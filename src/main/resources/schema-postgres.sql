DROP table IF EXISTS customer;
CREATE TABLE customer
(
    id serial PRIMARY KEY,
    firstname varchar(255),
    lastname varchar(255),
    birthdate timestamp
);
