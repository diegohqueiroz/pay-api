-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

insert into users(id, name, password, email, type) values ('user001-4126-4ca4-a412-307c25d64499', 'Anderson Silva', '12345678','anderson@silva.com', 1);
insert into users(id, name, password, email, type) values ('user002-4126-4ca4-a412-307c25d64499', 'Bruno Silva', '12345678','bruno@silva.com', 1);
insert into users(id, name, password, email, type) values ('user003-4126-4ca4-a412-307c25d64499', 'Carlos Silva', '12345678','carlos@silva.com', 1);
insert into users(id, name, password, email, type) values ('user004-4126-4ca4-a412-307c25d64499', 'Danilo Silva', '12345678','danilo@silva.com', 1);
insert into users(id, name, password, email, type) values ('user005-4126-4ca4-a412-307c25d64499', 'Amado LTDA', '1111111111','contato@amado.com', 2);