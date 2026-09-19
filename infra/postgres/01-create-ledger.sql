-- M0-T2: database creation only. The audit ledger (HLD §9) lives in Postgres db
-- `ledger`. POSTGRES_DB=ledger already creates it on first init; this script is an
-- idempotent belt-and-braces create so the db exists even if POSTGRES_DB is unset.
-- NO TABLES OR TRIGGERS HERE: the append-only, hash-chained ledger schema and its
-- update/delete-denying trigger are M1-T4 (CLAUDE.md non-negotiable #7). Do not add
-- tables to this file.
SELECT 'CREATE DATABASE ledger'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ledger')\gexec
