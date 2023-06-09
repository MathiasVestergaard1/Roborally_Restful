DROP TABLE IF EXISTS checkpoint;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS obstacle;
DROP TABLE IF EXISTS boardsettings;

CREATE TABLE boardsettings (
Name VARCHAR(255) not null,
`Type` ENUM('Board', 'Save', 'Game') not null,
Width int(11) not null,
Height int(11) not null,
Phase ENUM('PROGRAMMING', 'ACTIVATION'),
currentPlayer int(11),
currentStep int(11),
playerCount int(11) not null DEFAULT 1,
playerLimit int(11) not null DEFAULT 2,
stepMode VARCHAR(255) not null DEFAULT "false",
originBoard VARCHAR(255) not null DEFAULT Name,
PRIMARY KEY (Name)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE player (
Heading ENUM('NORTH', 'WEST', 'SOUTH', 'EAST') not null,
ID int(11) not null,
X int(11) not null,
Y int (11) not null,
playerHand VARCHAR(150) not null DEFAULT '[]',
playerProgram VARCHAR(150) not null DEFAULT '[]',
Points int(11) not null DEFAULT 0,
Board VARCHAR(255) not null,
FOREIGN KEY (Board) REFERENCES boardsettings (Name) ON DELETE CASCADE,
PRIMARY KEY (Heading, ID, X, Y, playerHand, playerProgram, Board)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE obstacle (
Heading ENUM('NORTH', 'WEST', 'SOUTH', 'EAST') not null,
X int(11) not null,
Y int (11) not null,
`Type` ENUM('Wall','Gear','Conveyor','Checkpoint') not null,
Board VARCHAR(255) not null,
FOREIGN KEY (Board) REFERENCES boardsettings (Name) ON DELETE CASCADE, 
PRIMARY KEY (Heading, X, Y, `Type`, Board)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE checkpoint (
X int(11) not null,
Y int(11) not null,
Board VARCHAR(255) not null,
FOREIGN KEY (Board) REFERENCES boardsettings (Name) ON DELETE CASCADE,
PRIMARY KEY (X, Y, Board)
) DEFAULT CHARSET=utf8mb4;