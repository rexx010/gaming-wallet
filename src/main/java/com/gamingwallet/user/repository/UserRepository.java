package com.gamingwallet.user.repository;

import com.gamingwallet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Atomic conditional debit.
     *
     * CONCEPT: the WHERE clause re-checks the balance at the instant of the
     * UPDATE (under Postgres row lock) — not when Java last read it. Two
     * concurrent withdrawals both try to update the same row; Postgres
     * serialises them, and only the first finds wallet_balance >= amount.
     * The second sees 0 rows updated, which we treat as insufficient funds.
     * This eliminates the read-check-write race without needing a pessimistic
     * lock annotation on the entity.
     *
     * Returns the number of rows updated (1 = success, 0 = insufficient balance).
     */
    @Modifying
    @Query("UPDATE User u SET u.walletBalance = u.walletBalance - :amount " +
           "WHERE u.id = :userId AND u.walletBalance >= :amount")
    int debitBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * Refund path: used when a Spotflow disbursement call fails after we've
     * already debited locally. Reverses the debit so no money is lost.
     */
    @Modifying
    @Query("UPDATE User u SET u.walletBalance = u.walletBalance + :amount WHERE u.id = :userId")
    int creditBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
