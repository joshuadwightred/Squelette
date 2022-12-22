CREATE TABLE PREFIX_Restaurant
(
    Restaurant VARCHAR(100) PRIMARY KEY,
    Ville      VARCHAR(100),
    Capacite   INTEGER
);

CREATE TABLE PREFIX_Employee
(
    Identifiant INTEGER PRIMARY KEY,
    Nom         VARCHAR(100),
    Categorie   VARCHAR(100)
);

CREATE TABLE PREFIX_Reservation
(
    Restaurant  VARCHAR(100) REFERENCES PREFIX_Restaurant,
    Identifiant INTEGER REFERENCES PREFIX_Employee,
    Date        DATE,
    PRIMARY KEY (Identifiant, Date)
);

CREATE TABLE PREFIX_Tarif
(
    Restaurant VARCHAR(100) REFERENCES PREFIX_Restaurant,
    Categorie  VARCHAR(100),
    Prix       DECIMAL(10, 2),
    PRIMARY KEY (Restaurant, Categorie)
);

INSERT INTO PREFIX_Restaurant
VALUES ('Nimporte', 'Dunkerque', 75),
       ('Etoile Resto U', 'Rennes', 300),
       ('La cantine des Illuminatis', 'Bourg-La-Reinne', 500),
       ('Escale Normande', 'Sottevast', 2);

INSERT INTO PREFIX_Employee
VALUES (0, 'Kevin Tollemer', 'Etudiant'),
       (1, 'Alexandre Leconte', 'Etudiant'),
       (2, 'Leo Robert', 'Etudiant'),
       (3, 'Jean Lassalle', 'Paysan'),
       (4, 'Jeremie Michon', 'Paysan'),
       (5, 'Jean Massiet', 'Illuminati'),
       (6, 'Usul', 'Illuminati'),
       (7, 'Ulf Mark Schneider', 'Seigneur du mal'),
       (8, 'Geoffroy Roux de Bézieux', 'Seigneur du mal'),
       (9, 'Simon Postec', 'Seigneur du mal'),
       (10, 'Gerald Darmanin', 'Ministre mis en examen'),
       (11, 'Eric Dupond-Moretti', 'Ministre mis en examen');

INSERT INTO PREFIX_Tarif
VALUES ('Nimporte', 'Etudiant', 3.30),
       ('Nimporte', 'Illuminati', 28.12),
       ('Nimporte', 'Paysan', 32.99),
       ('Nimporte', 'Seigneur du mal', 84.69),
       ('Nimporte', 'Ministre mis en examen', 0.00),
       ('Etoile Resto U', 'Etudiant', 1.00),
       ('Etoile Resto U', 'Illuminati', 950.00),
       ('Etoile Resto U', 'Paysan', 8.00),
       ('Etoile Resto U', 'Seigneur du mal', 666.69),
       ('Etoile Resto U', 'Ministre mis en examen', 0.00),
       ('La cantine des Illuminatis', 'Etudiant', 3.30),
       ('La cantine des Illuminatis', 'Illuminati', 0.05),
       ('La cantine des Illuminatis', 'Paysan', 8.00),
       ('La cantine des Illuminatis', 'Seigneur du mal', 666.69),
       ('La cantine des Illuminatis', 'Ministre mis en examen', 0.00),
       ('Escale Normande', 'Etudiant', 3.30),
       ('Escale Normande', 'Illuminati', 950.00),
       ('Escale Normande', 'Paysan', 30.00),
       ('Escale Normande', 'Seigneur du mal', 666.69),
       ('Escale Normande', 'Ministre mis en examen', 0.00);

INSERT INTO PREFIX_Reservation
VALUES ('Escale Normande', 10, '2022-12-11'),
       ('Escale Normande', 10, '2022-12-12'),
       ('Escale Normande', 10, '2022-12-13'),
       ('La cantine des Illuminatis', 10, '2022-12-14'),
       ('La cantine des Illuminatis', 7, '2022-12-11'),
       ('La cantine des Illuminatis', 7, '2022-12-12'),
       ('Escale Normande', 2, '2022-12-11'),
       ('La cantine des Illuminatis', 11, '2022-12-11'),
       ('Etoile Resto U', 0, '2022-12-11'),
       ('Etoile Resto U', 0, '2022-12-12'),
       ('Etoile Resto U', 0, '2022-12-13'),
       ('Etoile Resto U', 0, '2022-12-14'),
       ('Etoile Resto U', 1, '2022-12-11'),
       ('Etoile Resto U', 1, '2022-12-12'),
       ('Etoile Resto U', 1, '2022-12-13'),
       ('Etoile Resto U', 1, '2022-12-14'),
       ('La cantine des Illuminatis', 5, '2022-12-11'),
       ('Nimporte', 5, '2022-12-07');
