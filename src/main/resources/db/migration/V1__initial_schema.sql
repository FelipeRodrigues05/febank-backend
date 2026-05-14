CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255)        NOT NULL,
    document    VARCHAR(14)         NOT NULL UNIQUE,
    email       VARCHAR(255)        NOT NULL UNIQUE,
    password    VARCHAR(255)        NOT NULL,
    type        VARCHAR(50)         NOT NULL,
    created_at  DATETIME(6),
    updated_at  DATETIME(6)
);

CREATE TABLE IF NOT EXISTS clients (
    id            VARCHAR(36)  PRIMARY KEY,
    client_id     VARCHAR(255) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    active        TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME(6),
    updated_at    DATETIME(6)
);

CREATE TABLE IF NOT EXISTS accounts (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    number     VARCHAR(255),
    type       VARCHAR(50)  NOT NULL,
    balance    DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status     VARCHAR(50)  NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS cards (
    id              VARCHAR(36) PRIMARY KEY,
    account_id      BIGINT       NOT NULL,
    card_type       VARCHAR(50)  NOT NULL,
    card_status     VARCHAR(50)  NOT NULL,
    limit_available DECIMAL(10,2),
    used_limit      DECIMAL(10,2),
    number          VARCHAR(255),
    cvv             VARCHAR(255),
    expiration_date DATE,
    created_at      DATETIME(6),
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS card_bills (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id         VARCHAR(36)   NOT NULL,
    month           INT           NOT NULL,
    year            INT           NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    minimum_payment DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    due_date        DATE,
    status          VARCHAR(50)   NOT NULL,
    closed_at       DATETIME(6),
    paid_at         DATETIME(6),
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    CONSTRAINT fk_card_bills_card FOREIGN KEY (card_id) REFERENCES cards (id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id  BIGINT        NOT NULL,
    type        VARCHAR(50)   NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    category    VARCHAR(50),
    status      VARCHAR(50)   NOT NULL,
    executed_at DATETIME(6),
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS transfers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_account_id BIGINT        NOT NULL,
    to_account_id   BIGINT        NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    description     VARCHAR(255),
    status          VARCHAR(50)   NOT NULL,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    CONSTRAINT fk_transfers_from FOREIGN KEY (from_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transfers_to   FOREIGN KEY (to_account_id)   REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS pix_keys (
    id         VARCHAR(36)  PRIMARY KEY,
    account_id BIGINT       NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    pix_key    VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME(6),
    CONSTRAINT fk_pix_keys_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS pix_contacts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id      BIGINT       NOT NULL,
    alias           VARCHAR(255) NOT NULL,
    pix_key         VARCHAR(255) NOT NULL,
    transfer_count  INT          NOT NULL DEFAULT 0,
    created_at      DATETIME(6),
    CONSTRAINT fk_pix_contacts_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE IF NOT EXISTS savings_boxes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT        NOT NULL,
    name       VARCHAR(255)  NOT NULL,
    image_url  VARCHAR(1024) NOT NULL,
    balance    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_savings_boxes_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);
