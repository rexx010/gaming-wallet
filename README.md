#  Gaming Wallet Service

A backend service for a gaming/tournament application that automates wallet
funding (Pay-In) and cash prize withdrawals (Payout) using the **Spotflow API**.

Built with Java 21 · Spring Boot 3.5 · JPA/Hibernate · Flyway · Supabase (Postgres)

---

## Architecture

The codebase follows a **modular layered architecture**. The most important
design rule: the `spotflow/` module is the *only* module allowed to know
Spotflow's endpoint paths, field names, or HTTP details. Everything else calls
clean Java interfaces.

```
src/main/java/com/gamingwallet/
│
├── spotflow/               ← Integration layer (isolated from business logic)
│   ├── client/             ← Raw HTTP calls to Spotflow (RestClient)
│   ├── service/            ← Translates domain concepts → Spotflow API shapes
│   ├── dto/                ← Spotflow request/response DTOs
│   └── config/             ← RestClient bean + SpotflowProperties
│
├── wallet/                 ← Core business logic (Pay-In / Payout)
│   ├── controller/         ← REST endpoints
│   ├── service/            ← Orchestration (calls spotflow service + transaction service)
│   └── dto/                ← Request/Response DTOs
│
├── webhook/                ← Idempotent webhook handler
│   ├── controller/         ← POST /webhooks/spotflow
│   └── service/            ← Idempotency gate + wallet credit
│
├── transaction/            ← Transaction entity, repository, service
├── user/                   ← User entity, repository, controller
├── scheduler/              ← Reconciliation background worker
└── common/                 ← Shared utilities, exceptions, ApiResponse wrapper
```

---

## How the Four Flows Work

### 1. Pay-In — `POST /wallet/fund`
1. Look up (or reject) the user
2. Generate a unique reference and save a `PENDING` transaction locally *first*
3. Call Spotflow `POST /virtual-accounts/temporary` to get a temporary bank account
4. Return the account details to the client — **the wallet is NOT credited here**

Only proof of actual money arriving (a webhook, or the reconciliation fallback)
is allowed to credit the wallet.

### 2. Webhook — `POST /webhooks/spotflow` (the idempotency gate)

Payment providers deliver webhooks **at least once, not exactly once**. If
your server is slow or returns a non-2xx, Spotflow retries — and a naive
`balance += amount` on every delivery would double-credit the user.

**The fix:** Spotflow's unique event `id` is inserted into the
`processed_webhooks` table, which has a `UNIQUE` constraint on `event_id`.
The entire operation (INSERT event + credit wallet + mark SUCCESS) runs in one
`@Transactional` method. A retried delivery hits a unique constraint violation,
which is caught and treated as "already handled" — no double credit possible.

### 3. Payout — `POST /wallet/withdraw`
1. **Atomic conditional debit** via SQL:
   `UPDATE users SET wallet_balance = wallet_balance - :amount WHERE id = :id AND wallet_balance >= :amount`
   If 0 rows updated → insufficient funds. This prevents race conditions that a
   Java-level read-check-write would have.
2. Save a `PENDING` PAYOUT transaction with the same reference
3. Call Spotflow `POST /transfers`
4. If Spotflow call fails outright → reverse the debit (compensating action)

### 4. Webhook Fallback — Reconciliation Scheduler
Runs every 5 minutes (`@Scheduled(fixedRate = 300000)`). Finds all `PENDING`
transactions older than 1 hour:

- **PAYOUT** → polls `GET /transfers/reference/{reference}` and syncs the real status (marking `SUCCESS`, `FAILED` with refund, etc.)
- **PAYIN** → marks `ABANDONED` (the virtual account has expired; no money arrived)

---

## Setup

### Prerequisites
- Java 21
- Maven 3.9+
- A Supabase project (free tier is fine)
- A Spotflow sandbox account

### Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | Supabase JDBC URL (see below) |
| `DB_USERNAME` | Supabase DB username |
| `DB_PASSWORD` | Supabase DB password |
| `SPOTFLOW_SECRET_KEY` | Your Spotflow secret key (`sk_test_...`) |

**Getting your Supabase JDBC URL:**
Go to your Supabase project → Settings → Database → Connection string → JDBC.
Use the **Transaction pooler** URL (port `6543`), not the direct connection.
It looks like:
```
jdbc:postgresql://aws-0-eu-west-1.pooler.supabase.com:6543/postgres
```

### Run Locally

```bash
# Set environment variables (Mac/Linux)
export DB_URL="jdbc:postgresql://..."
export DB_USERNAME="postgres.yourprojectref"
export DB_PASSWORD="yourpassword"
export SPOTFLOW_SECRET_KEY="sk_test_..."

mvn spring-boot:run
```

Flyway automatically runs all migrations in `src/main/resources/db/migration/`
on startup. No manual SQL needed.

### Or use `application-local.properties`

Create/edit `src/main/resources/application-local.properties` (already in
`.gitignore`) and put your values there for local development:

```properties
spring.datasource.url=jdbc:postgresql://...
spring.datasource.username=postgres.yourref
spring.datasource.password=yourpassword
spotflow.secret-key=sk_test_...
```

---

## API Reference

### Users

| Method | Path | Description |
|---|---|---|
| `POST` | `/users` | Create a user |
| `GET` | `/users/{id}` | Get user by id (includes wallet balance) |

**POST /users**
```json
{ "fullName": "Damilare Abiodun" }
```

### Wallet

| Method | Path | Description |
|---|---|---|
| `POST` | `/wallet/fund` | Generate a virtual account for Pay-In |
| `POST` | `/wallet/withdraw` | Initiate a withdrawal (Payout) |
| `GET` | `/wallet/withdraw/{reference}` | Poll the real status of a withdrawal |

**POST /wallet/fund**
```json
{
  "userId": 1,
  "amount": 5000.00
}
```
Response:
```json
{
  "reference": "uuid-here",
  "status": "PENDING",
  "message": "Transfer the exact amount into the account below.",
  "accountNumber": "7706438396",
  "bankName": "Testbank MFB",
  "accountName": "Damilare Abiodun"
}
```

**POST /wallet/withdraw**
```json
{
  "userId": 1,
  "amount": 2000.00,
  "accountNumber": "0123456789",
  "accountName": "Damilare Abiodun",
  "bankCode": "058",
  "narration": "Prize withdrawal"
}
```

### Webhooks

| Method | Path | Description |
|---|---|---|
| `POST` | `/webhooks/spotflow` | Spotflow payment event receiver |

Spotflow calls this endpoint automatically. Configure the URL in your Spotflow
dashboard as: `https://your-app.onrender.com/webhooks/spotflow`

---

## Deploying to Render

See the Deployment section in this README for step-by-step instructions.

---

## Key Design Decisions

### Money: `BigDecimal` not `double`
Wallet balances use `BigDecimal` (Java) and `NUMERIC(19,2)` (Postgres). `double`
and `float` cannot represent most decimal fractions exactly in binary — `0.1 + 0.2`
is literally `0.30000000000000004` in floating point, which is unacceptable for money.

### Race condition prevention via SQL
The balance debit uses `WHERE wallet_balance >= :amount` inside the `UPDATE`
itself, not as a prior read in Java. This makes the check and the write atomic
under Postgres row-level locking.

### @Transactional does not span HTTP calls
No method holds a database transaction open during a Spotflow API call. Doing
so would exhaust HikariCP's connection pool under moderate load (10 connections
× 3s Spotflow latency = blocked for everyone).

### Spring AOP self-invocation fix
`WalletTransactionHelper` is a separate `@Service` bean holding all
`@Transactional` atomic writes. This avoids the Spring proxy bypass that happens
when a method calls another `@Transactional` method on `this` — which silently
skips the transaction entirely.
