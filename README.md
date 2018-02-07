# Welcome to WebBoard!
WebBoard is a desktop application for submission and (auto-)grading of student assignments. The primary purpose
of WebBoard is as a better alternative to WeBWorK and supply a slick and easy-to-use interface for 
instructors to release assignments and for students to submit their work.

WebBoard provides a very simple interface for students to submit their assignments and see their assignment
marks. Professors get access to an administrative interface that allows professors to create courses, manage
students, release assignments and view assignment statistics.

WebBoard is written using Java with a MySQL back-end.

## Source
The source files are located in the `src` directory of the root folder.

The external libraries that are required to be added to the class path are found under `src/lib`.

The main Java code is found under `src/main/java`.

The tests are found under `src/test/java`.
Automated tests are found user `src/test/java/acceptance`.

Resources that the application require (such as style sheets and images) are found under `src/main/resources`.

## Credits
WebBoard's development was almost completely done by Brendan Zhang. Others who contributed include:

Qiyan Lu, Ahmed Sirajuddin, Julian Speedie

## Setting up DB
The application is set up to use a local MySQL database. For it to work, you will need to have MYSQL Server 
installed, have a database set up and change the config.properties to match the settings you have on your 
machine. 

In config.properties, JDBC_URL must be changed to `jdbc\:mysql\://localhost/_NAME_?useSSL\=false` 
where `_NAME_` is the name of the MYSQL database that you are using. JDBC_USERNAME and JDBC_PASSWORD correspond
to the credentials for the user that the application will be using to connect to your database. 

By default, this is set to 'testuser':'password'. The user must have admin privileges for the database you are using. 
The config file also features a property called TEST_MODE. When TEST_MODE is set to 1, the application will 
use the properties TEST_JDBC_URL, TEST_JDBC_USERNAME and TEST_JDBC_PASSWORD instead. By default, TEST_MODE is
set to 1 when running the unit tests.

## Development Setup
All project dependencies are found in the `lib` directory.
The project requires Apache Ant for building.
To build the project simply run the `build.xml` file located in the `build` directory with the default target.

Example build steps:

```sh
cd build
ant -buildfile build.xml
```
