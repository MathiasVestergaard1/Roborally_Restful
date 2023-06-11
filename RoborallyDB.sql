DROP VIEW IF EXISTS Games;
DROP VIEW IF EXISTS Saves;
DROP VIEW IF EXISTS Boards;
DROP TABLE IF EXISTS checkpoint;
DROP TABLE IF EXISTS Player;
DROP TABLE IF EXISTS Obstacle;
DROP TABLE IF EXISTS Boardsettings;

CREATE TABLE Boardsettings (
Name VARCHAR(255) not null,
`Type` ENUM('Board', 'Save', 'Game') not null,
Width int(11) not null,
Height int(11) not null,
Phase ENUM('PROGRAMMING', 'ACTIVATION'),
currentPlayer int(11),
currentProgram int(11),
PRIMARY KEY (Name)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE Player (
Heading ENUM('NORTH', 'WEST', 'SOUTH', 'EAST') not null,
ID int(11) not null,
X int(11) not null,
Y int (11) not null,
playerHand VARCHAR(50) not null,
playerProgram VARCHAR(50) not null,
Board VARCHAR(255) not null,
FOREIGN KEY (Board) REFERENCES Boardsettings (Name),
PRIMARY KEY (Heading, ID, X, Y, playerHand, playerProgram, Board)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE Obstacle (
Heading ENUM('NORTH', 'WEST', 'SOUTH', 'EAST') not null,
X int(11) not null,
Y int (11) not null,
`Type` ENUM('Wall','Gear','Conveyor','Checkpoint') not null,
Board VARCHAR(255) not null,
FOREIGN KEY (Board) REFERENCES Boardsettings (Name),
PRIMARY KEY (Heading, X, Y, `Type`, Board)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE checkpoint (
X int(11) not null,
Y int(11) not null,
Board VARCHAR(255) not null,
FOREIGN KEY (Board) REFERENCES Boardsettings (Name),
PRIMARY KEY (X, Y, Board)
) DEFAULT CHARSET=utf8mb4;

CREATE VIEW Boards AS
SELECT *
FROM Boardsettings
WHERE `Type`	 = 'Board';

CREATE VIEW Saves AS
SELECT *
FROM Boardsettings
WHERE `Type` = 'Save';

CREATE VIEW Games AS
SELECT *
FROM Boardsettings
WHERE `Type` = 'Game';