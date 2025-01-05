-- liquibase formatted sql

-- changeset 4rad95:create_table_statistic
CREATE TABLE Statistic (id INT AUTO_INCREMENT NOT NULL, startDateTime VARCHAR(25) not null , duration VARCHAR(25) NULL, symbols VARCHAR(15) NULL, type ENUM('LONG','SHORT'),pnl VARCHAR(15) not null , comission VARCHAR(15) not null );

-- changeset 4rad95:create_table_symbols
CREATE TABLE Symbols (symbols VARCHAR(25) NOT NULL, lowbuy VARCHAR(25) NOT NULL, highbuy VARCHAR(25) NOT NULL, imbbuy VARCHAR(25), lowsell VARCHAR(25) NOT NULL, highsell VARCHAR(25) NOT NULL, imbsell VARCHAR(25));
--CREATE TABLE Symbols (id INT AUTO_INCREMENT NOT NULL, symbols VARCHAR(25) NOT NULL);

-- changeset 4rad95:create_table_variant
CREATE TABLE Variant(id INT AUTO_INCREMENT NOT NULL, time TIMESTAMP, type VARCHAR(5), symbol VARCHAR(20),price VARCHAR(25),enterprice VARCHAR(25), stop VARCHAR(25), proffit VARCHAR(25) )

-- changeset 4rad95:create_table_openPosition
--CREATE TABLE OpenPosition(id INT AUTO_INCREMENT NOT NULL, time TIMESTAMP, type VARCHAR(5), symbol VARCHAR(20),idBinance VARCHAR(20), stopId VARCHAR(25), stopClientId VARCHAR(25), profitId VARCHAR(25), profitClientId VARCHAR(25))
CREATE TABLE OpenPosition(id INT AUTO_INCREMENT NOT NULL, time TIMESTAMP, type VARCHAR(5), symbol VARCHAR(20),idBinance VARCHAR(20), stopId VARCHAR(25) NOT NULL , profitId VARCHAR(25) NOT NULL,  profit2Id VARCHAR(25))

-- changeset 4rad95:create_table_logUpdate
CREATE TABLE LogUpdate (id INT AUTO_INCREMENT NOT NULL, time TIMESTAMP NOT NULL , msg VARCHAR(100) NOT NULL )
