create table customer
(
  id serial not null
    constraint customer_pkey
    primary key,
  firstname varchar(255) default NULL::character varying,
  lastname varchar(255) default NULL::character varying,
  birthdate timestamp
)
