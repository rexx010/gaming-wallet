# ⚠️ Secret Cleanup — Do This Before Pushing to GitHub

Your Supabase password and Spotflow API keys were previously in files that may
have been committed. Follow these steps BEFORE pushing to GitHub.

## Step 1 — Rotate your credentials (most important)

Since the keys were visible, assume they're compromised even if you haven't
pushed to GitHub yet. Rotate them now:

**Supabase password:**
Supabase dashboard → Settings → Database → Reset database password

**Spotflow keys:**
Spotflow dashboard → API Keys → Regenerate (or create new, then delete old)

Then update your new values in `application-local.properties`.

## Step 2 — Make sure application-local.properties is gitignored

Run this command in your project root:

```bash
git check-ignore -v src/main/resources/application-local.properties
```

If it prints a line like `.gitignore:6:*-local.properties`, it's ignored. Good.
If it prints nothing, the file is NOT ignored — stop and check your .gitignore.

## Step 3 — Remove the file from git tracking if it was ever committed

```bash
git rm --cached src/main/resources/application-local.properties
git commit -m "chore: untrack application-local.properties (contains secrets)"
```

CONCEPT — `git rm --cached`:
`git rm` removes a file. The `--cached` flag means "remove it from git's
index (what git is tracking) but leave the actual file on disk". Without
`--cached`, git would delete the file from your hard drive too.

## Step 4 — If you already pushed credentials to GitHub

You must rewrite git history using git-filter-repo (the modern replacement
for BFG Repo Cleaner):

```bash
pip install git-filter-repo
git filter-repo --path src/main/resources/application-local.properties --invert-paths
git push --force
```

Then go to GitHub → Settings → Danger Zone → and also clear GitHub's cache.

CONCEPT — Why force push works here:
`--force` overwrites the remote branch with your rewritten local history.
Normally force-pushing is dangerous (you can overwrite others' work), but
when cleaning secrets from a solo assessment repo, it's the correct tool.
