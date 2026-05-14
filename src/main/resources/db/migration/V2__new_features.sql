CREATE TABLE IF NOT EXISTS kyc_records (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT      NOT NULL UNIQUE,
    status           VARCHAR(50) NOT NULL,
    submitted_at     DATETIME(6),
    reviewed_at      DATETIME(6),
    rejection_reason VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS aml_flags (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id     BIGINT        NOT NULL,
    transaction_id BIGINT,
    reason         VARCHAR(500)  NOT NULL,
    amount         DECIMAL(10,2) NOT NULL,
    status         VARCHAR(50)   NOT NULL,
    created_at     DATETIME(6),
    reviewed_at    DATETIME(6)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    action      VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    ip_address  VARCHAR(45),
    created_at  DATETIME(6)
);

CREATE TABLE IF NOT EXISTS otp_codes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    code       VARCHAR(6)  NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used       TINYINT(1)  NOT NULL DEFAULT 0,
    created_at DATETIME(6)
);

CREATE TABLE IF NOT EXISTS trusted_devices (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT       NOT NULL,
    device_fingerprint VARCHAR(255) NOT NULL,
    device_name        VARCHAR(255),
    expires_at         DATETIME(6)  NOT NULL,
    created_at         DATETIME(6)
);

CREATE TABLE IF NOT EXISTS operation_limits (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    limit_type VARCHAR(50)   NOT NULL,
    max_amount DECIMAL(10,2) NOT NULL,
    updated_at DATETIME(6),
    CONSTRAINT uq_operation_limits_user_type UNIQUE (user_id, limit_type)
);

CREATE TABLE IF NOT EXISTS virtual_cards (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id    VARCHAR(36) NOT NULL UNIQUE,
    expires_at DATE        NOT NULL,
    active     TINYINT(1)  NOT NULL DEFAULT 1,
    created_at DATETIME(6),
    CONSTRAINT fk_virtual_cards_card FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS card_installment_plans (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id              VARCHAR(36)   NOT NULL,
    card_bill_id         BIGINT,
    total_amount         DECIMAL(10,2) NOT NULL,
    installment_amount   DECIMAL(10,2) NOT NULL,
    total_installments   INT           NOT NULL,
    paid_installments    INT           NOT NULL DEFAULT 0,
    description          VARCHAR(255)  NOT NULL,
    created_at           DATETIME(6),
    updated_at           DATETIME(6),
    CONSTRAINT fk_installment_plans_card      FOREIGN KEY (card_id)      REFERENCES cards (id),
    CONSTRAINT fk_installment_plans_card_bill FOREIGN KEY (card_bill_id) REFERENCES card_bills (id)
);

CREATE TABLE IF NOT EXISTS cashback_records (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id            VARCHAR(36)   NOT NULL,
    transaction_amount DECIMAL(10,2) NOT NULL,
    cashback_amount    DECIMAL(10,2) NOT NULL,
    credited           TINYINT(1)    NOT NULL DEFAULT 0,
    created_at         DATETIME(6),
    CONSTRAINT fk_cashback_records_card FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS chargebacks (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id                 VARCHAR(36)   NOT NULL,
    original_transaction_id BIGINT        NOT NULL,
    amount                  DECIMAL(10,2) NOT NULL,
    reason                  VARCHAR(500)  NOT NULL,
    status                  VARCHAR(50)   NOT NULL,
    reviewed_at             DATETIME(6),
    created_at              DATETIME(6),
    CONSTRAINT fk_chargebacks_card FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS limit_increase_requests (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id          VARCHAR(36)   NOT NULL,
    requested_limit  DECIMAL(10,2) NOT NULL,
    current_limit    DECIMAL(10,2) NOT NULL,
    status           VARCHAR(50)   NOT NULL,
    reviewed_at      DATETIME(6),
    created_at       DATETIME(6),
    CONSTRAINT fk_limit_increase_requests_card FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS pix_limits (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id               BIGINT        NOT NULL UNIQUE,
    daily_limit              DECIMAL(10,2) NOT NULL,
    single_transaction_limit DECIMAL(10,2) NOT NULL,
    night_limit              DECIMAL(10,2) NOT NULL,
    updated_at               DATETIME(6),
    CONSTRAINT fk_pix_limits_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS pix_qr_codes (
    id          VARCHAR(36)    PRIMARY KEY,
    account_id  BIGINT         NOT NULL,
    type        VARCHAR(50)    NOT NULL,
    pix_key     VARCHAR(255),
    amount      DECIMAL(10,2),
    description VARCHAR(255),
    payload     VARCHAR(2048),
    active      TINYINT(1)     NOT NULL DEFAULT 1,
    expires_at  DATETIME(6),
    created_at  DATETIME(6),
    CONSTRAINT fk_pix_qr_codes_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS pix_devolutions (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_transfer_id  BIGINT        NOT NULL,
    requester_account_id  BIGINT        NOT NULL,
    amount                DECIMAL(10,2) NOT NULL,
    reason                VARCHAR(500),
    status                VARCHAR(50)   NOT NULL,
    reviewed_at           DATETIME(6),
    created_at            DATETIME(6)
);

CREATE TABLE IF NOT EXISTS pix_saques (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id        BIGINT        NOT NULL,
    type              VARCHAR(50)   NOT NULL,
    total_amount      DECIMAL(10,2) NOT NULL,
    withdrawal_amount DECIMAL(10,2) NOT NULL,
    merchant_account_id BIGINT,
    created_at        DATETIME(6),
    CONSTRAINT fk_pix_saques_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS scheduled_pix (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_account_id BIGINT        NOT NULL,
    pix_key        VARCHAR(255)  NOT NULL,
    amount         DECIMAL(10,2) NOT NULL,
    description    VARCHAR(255),
    scheduled_date DATE          NOT NULL,
    status         VARCHAR(50)   NOT NULL,
    executed_at    DATETIME(6),
    created_at     DATETIME(6),
    CONSTRAINT fk_scheduled_pix_from_account FOREIGN KEY (from_account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS boletos (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    issuer_account_id BIGINT        NOT NULL,
    payer_document    VARCHAR(14),
    amount            DECIMAL(10,2) NOT NULL,
    description       VARCHAR(255),
    boleto_code       VARCHAR(100)  NOT NULL UNIQUE,
    status            VARCHAR(50)   NOT NULL,
    due_date          DATE          NOT NULL,
    paid_at           DATETIME(6),
    paid_by_account_id BIGINT,
    created_at        DATETIME(6),
    updated_at        DATETIME(6)
);

CREATE TABLE IF NOT EXISTS investment_products (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                  VARCHAR(255)  NOT NULL,
    type                  VARCHAR(50)   NOT NULL,
    annual_rate           DECIMAL(10,6) NOT NULL,
    cdi_percentage        DECIMAL(10,4),
    minimum_amount        DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    minimum_days_to_redeem INT          NOT NULL DEFAULT 0,
    ir_exempt             TINYINT(1)    NOT NULL DEFAULT 0,
    active                TINYINT(1)    NOT NULL DEFAULT 1,
    created_at            DATETIME(6)
);

CREATE TABLE IF NOT EXISTS investment_positions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id      BIGINT        NOT NULL,
    product_id      BIGINT        NOT NULL,
    invested_amount DECIMAL(10,2) NOT NULL,
    current_amount  DECIMAL(10,2) NOT NULL,
    applied_at      DATETIME(6),
    redeemed_at     DATETIME(6),
    active          TINYINT(1)    NOT NULL DEFAULT 1,
    CONSTRAINT fk_investment_positions_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_investment_positions_product FOREIGN KEY (product_id) REFERENCES investment_products (id)
);

CREATE TABLE IF NOT EXISTS loans (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id                BIGINT        NOT NULL,
    type                      VARCHAR(50)   NOT NULL,
    requested_amount          DECIMAL(10,2) NOT NULL,
    approved_amount           DECIMAL(10,2),
    interest_rate             DECIMAL(10,6),
    total_installments        INT           NOT NULL,
    paid_installments         INT           NOT NULL DEFAULT 0,
    installment_amount        DECIMAL(10,2),
    status                    VARCHAR(50)   NOT NULL,
    credit_score_at_application INT,
    created_at                DATETIME(6),
    updated_at                DATETIME(6),
    CONSTRAINT fk_loans_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS loan_installments (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id            BIGINT        NOT NULL,
    installment_number INT           NOT NULL,
    amount             DECIMAL(10,2) NOT NULL,
    due_date           DATE          NOT NULL,
    paid_at            DATETIME(6),
    paid               TINYINT(1)    NOT NULL DEFAULT 0,
    created_at         DATETIME(6),
    CONSTRAINT fk_loan_installments_loan FOREIGN KEY (loan_id) REFERENCES loans (id)
);

CREATE TABLE IF NOT EXISTS open_finance_consents (
    id             VARCHAR(36)  PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    client_id      VARCHAR(255) NOT NULL,
    permissions    VARCHAR(500),
    status         VARCHAR(50)  NOT NULL,
    expires_at     DATETIME(6)  NOT NULL,
    authorised_at  DATETIME(6),
    revoked_at     DATETIME(6),
    created_at     DATETIME(6)
);

CREATE TABLE IF NOT EXISTS scheduled_payments (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_account_id    BIGINT        NOT NULL,
    type               VARCHAR(50)   NOT NULL,
    amount             DECIMAL(10,2) NOT NULL,
    target_identifier  VARCHAR(255),
    description        VARCHAR(255),
    scheduled_date     DATE          NOT NULL,
    status             VARCHAR(50)   NOT NULL,
    executed_at        DATETIME(6),
    failure_reason     VARCHAR(500),
    created_at         DATETIME(6)
);
