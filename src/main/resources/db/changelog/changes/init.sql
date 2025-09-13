--liquibase formatted sql

--changeset admin:1
CREATE TABLE payments
(
                    payment_id    SERIAL PRIMARY KEY,
                    order_id      BIGINT  NOT NULL,
                    user_id       BIGINT  NOT NULL,
                    status        VARCHAR(32) NOT NULL,
                    timestamp TIMESTAMP,
                    payment_amount DECIMAL(32,2)

);
--rollback DROP TABLE users;