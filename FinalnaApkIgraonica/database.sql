-- ================================================================
-- VAŽNO ZA OVAJ PROJEKAT
-- Ovaj SQL samo pravi bazu ako ne postoji.
-- Tabele NE pravimo ručno: Hibernate ih kreira/ažurira iz @Entity klasa
-- zato što je spring.jpa.hibernate.ddl-auto=update.
-- Ako promeniš Java model, prvo pusti aplikaciju da Hibernate uradi update.
-- ================================================================

-- BlaBla Igraonica - lokalna MySQL baza
--
-- Ova skripta namerno pravi SAMO praznu bazu.
-- Tabele kreira i održava Hibernate na osnovu Java @Entity klasa
-- zato što je u application.properties podešeno:
-- spring.jpa.hibernate.ddl-auto=update
--
-- Nemoj ručno praviti stare tabele iz prethodnih verzija projekta,
-- jer mogu da se razlikuju od aktuelnog Java modela.

CREATE DATABASE IF NOT EXISTS blabla_igraonica
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE blabla_igraonica;
