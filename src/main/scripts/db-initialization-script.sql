/*
 * This script is for initializing/cleaning the database that WebBoard will be using.
 * To use this script, it is necessary for you to be logged in through MYSQL Server shell
 * on the user that you intend to connect to.
 * This script will remove all the tables and create a new User table, initializing
 * with two users (user:password and student:password)
 */

create schema webworks;
create schema testdb;

use webworks;
drop table answer; drop table question; drop table course; drop table questionoption; 
drop table studentsolution; drop table submission;
drop table usercourse; drop table user;
drop table assignment;

CREATE TABLE User (UserId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, 
Username VARCHAR(250) NOT NULL, Password VARCHAR(100) NOT NULL, UserPrivilegeLevel INT NOT NULL, 
FirstName TEXT NOT NULL, LastName TEXT NOT NULL);

/*
 * Inserting test users
 */
insert User VALUES (1, "user", "password", 2, "Admin", "Admin");
insert User VALUES (2, "student", "password", 1, "Student", "Student");

use testdb;
drop table answer; drop table question; drop table course; drop table questionoption; 
drop table studentsolution; drop table submission;
drop table usercourse; drop table user;
drop table assignment;

CREATE TABLE User (UserId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, 
Username VARCHAR(250) NOT NULL, Password VARCHAR(100) NOT NULL, UserPrivilegeLevel INT NOT NULL, 
FirstName TEXT NOT NULL, LastName TEXT NOT NULL);

/*
 * Inserting test users
 */
insert User VALUES (1, "user", "password", 2, "Admin", "Admin");
insert User VALUES (2, "student", "password", 1, "Student", "Student");