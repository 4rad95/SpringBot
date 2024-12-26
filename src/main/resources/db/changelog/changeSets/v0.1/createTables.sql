-- liquibase formatted sql

-- changeset 4rad95:create_table_statistic
CREATE TABLE Statistic (id INT AUTO_INCREMENT NOT NULL, startDateTime int NULL, duration int NULL, symbols VARCHAR(15) NULL, type ENUM('LONG','SHORT') );

-- changeset 4rad95:create_table_symbols
CREATE TABLE Symbols (symbols VARCHAR(25) NOT NULL, lowbuy VARCHAR(25) NOT NULL, highbuy VARCHAR(25) NOT NULL, imbbuy VARCHAR(25), lowsell VARCHAR(25) NOT NULL, highsell VARCHAR(25) NOT NULL, imbsell VARCHAR(25));
--CREATE TABLE Symbols (id INT AUTO_INCREMENT NOT NULL, symbols VARCHAR(25) NOT NULL);

-- changeset 4rad95:create_table_variant
CREATE TABLE Variant(id INT AUTO_INCREMENT NOT NULL, time TIMESTAMP, type VARCHAR(5), symbol VARCHAR(20),price VARCHAR(25),enterprice VARCHAR(25), stop VARCHAR(25), proffit VARCHAR(25) )

-- changeset 4rad95:create_table_openPosition
CREATE TABLE OpenPosition(id INT AUTO_INCREMENT NOT NULL, time TIMESTAMP, type VARCHAR(5), symbol VARCHAR(20),idBinance VARCHAR(20))