CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    second_name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    second_surname VARCHAR(255) NOT NULL
);

CREATE TABLE currency (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    abbreviation VARCHAR(3) NOT NULL
);

CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    id_holder INT,
    amount DECIMAL NOT NULL,
    id_currency INT NOT NULL,
    CONSTRAINT fk_customer_account
      FOREIGN KEY(id_holder)
      REFERENCES customer(id),
    CONSTRAINT fk_currency_account
      FOREIGN KEY(id_currency)
      REFERENCES currency(id)
);

CREATE TABLE transfer (
    id SERIAL PRIMARY KEY,
    id_origin_account INT NOT NULL,
    id_destination_account INT NOT NULL,
    amount DECIMAL NOT NULL,
    id_currency INT NOT NULL,
    datetime TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_origin_account_transfer
      FOREIGN KEY(id_origin_account)
      REFERENCES account(id),
    CONSTRAINT fk_destination_account_transfer
      FOREIGN KEY(id_destination_account)
      REFERENCES account(id),
    CONSTRAINT fk_currency_transfer
      FOREIGN KEY(id_currency)
      REFERENCES currency(id)
);
