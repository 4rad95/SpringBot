-- liquibase formatted sql

-- changeset rad95:insert_statistic
insert into Statistic (startDateTime, duration, symbols, type)
values (12222, 3311, 'LINKUSDT','LONG' ),
       (11122,2222, 'BTCUSDT','SHORT');