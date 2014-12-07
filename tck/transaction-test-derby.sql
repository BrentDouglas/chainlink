connect 'jdbc:derby:databases/orderDB;create=true';

create schema app;

create table app.numbers (
  id bigint primary key not null generated always as identity (start with 1, increment by 1),
  item int not null,
  quantity int not null
);

create table app.orders (
  id bigint primary key not null generated always as identity (start with 1, increment by 1),
  orderID int not null,
  item int not null,
  quantity int not null
);

create table app.inventory (
  id bigint primary key not null generated always as identity (start with 1, increment by 1),
  orderID int not null,
  itemID int not null,
  quantity int not null
);