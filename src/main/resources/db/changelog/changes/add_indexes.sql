--liquibase formatted sql
--includeAll path:db/changelog/changes

--changeset admin:2
CREATE INDEX payment_amount_index ON payments (payment_amount)
--rollback DROP INDEX payment_amount_index;