CREATE TABLE amortisation_schedules (
    id int NOT null AUTO_INCREMENT,
    period int NOT NULL,
    payment DOUBLE(255, 2) NOT NULL,
    principle DOUBLE(255, 2) NOT NULL,
    interest DOUBLE(255, 2) NOT NULL,
    periodic_balance DOUBLE(255, 2) NOT NULL,
    account_id int NOT NULL,
    asset_price DOUBLE(255, 2) NOT NULL,
    balloon DOUBLE(255, 2),
    deposit DOUBLE(255, 2),
    interest_year DOUBLE(255, 5) NOT NULL,
    PRIMARY KEY (id)
);
