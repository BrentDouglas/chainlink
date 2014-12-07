create schema if not exists app;

create table if not exists app.numbers (
  id bigint auto_increment primary key not null,
  item int not null,
  quantity int not null
);

create table if not exists app.orders (
  id bigint auto_increment primary key not null,
  orderID int not null,
  item int not null,
  quantity int not null
);

create table if not exists app.inventory (
  id bigint auto_increment primary key not null,
  orderID int not null,
  itemID int not null,
  quantity int not null
);