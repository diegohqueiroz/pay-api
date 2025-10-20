-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

insert into users(id, name, password, email, type) values (1, 'Anderson Silva', '12345678','anderson@silva.com', 1);
insert into users(id, name, password, email, type) values (2, 'Bruno Silva', '12345678','bruno@silva.com', 1);
insert into users(id, name, password, email, type) values (3, 'Carlos Silva', '12345678','carlos@silva.com', 1);
insert into users(id, name, password, email, type) values (4, 'Danilo Silva', '12345678','danilo@silva.com', 1);
insert into users(id, name, password, email, type) values (5, 'Amado LTDA', '1111111111','contato@amado.com', 2);

ALTER SEQUENCE users_seq RESTART WITH 6;

insert into accounts(id, balance, user_id) values ('account001-4126-4ca4-a412-307c25d64499', 1000 , 1);
insert into accounts(id, balance, user_id) values ('account002-4126-4ca4-a412-307c25d64499', 2000 , 2);
insert into accounts(id, balance, user_id) values ('account003-4126-4ca4-a412-307c25d64499', 3000 , 3);
insert into accounts(id, balance, user_id) values ('account004-4126-4ca4-a412-307c25d64499', 4000 , 4);
insert into accounts(id, balance, user_id) values ('account005-4126-4ca4-a412-307c25d64499', 5000 , 5);

insert into transactions(id, value, createdAt, account_destination_id, account_source_id, type) VALUES('transac001-4126-4ca4-a412-307c25d64499', 1000, now(), 'account001-4126-4ca4-a412-307c25d64499', null, 2);
insert into transactions(id, value, createdAt, account_destination_id, account_source_id, type) VALUES('transac002-4126-4ca4-a412-307c25d64499', 2000, now(), 'account002-4126-4ca4-a412-307c25d64499', null, 2);
insert into transactions(id, value, createdAt, account_destination_id, account_source_id, type) VALUES('transac003-4126-4ca4-a412-307c25d64499', 3000, now(), 'account003-4126-4ca4-a412-307c25d64499', null, 2);
insert into transactions(id, value, createdAt, account_destination_id, account_source_id, type) VALUES('transac004-4126-4ca4-a412-000000000001', 5000, now(), 'account004-4126-4ca4-a412-307c25d64499', null, 2);
insert into transactions(id, value, createdAt, account_destination_id, account_source_id, type) VALUES('transac004-4126-4ca4-a412-000000000002', 1000, now(), 'account004-4126-4ca4-a412-307c25d64499', 'account005-4126-4ca4-a412-307c25d64499', 1);
insert into transactions(id, value, createdAt, account_destination_id, account_source_id, type) VALUES('transac005-4126-4ca4-a412-307c25d64499', 4000, now(), 'account005-4126-4ca4-a412-307c25d64499', null, 2);

