CREATE DATABASE student_system;
USE student_system;
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100),
    dob DATE,
    age INT,
    course VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(100),
    password VARCHAR(50),
    behaviour INT DEFAULT 0
);
CREATE TABLE marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20),
    semester VARCHAR(20),
    subject VARCHAR(50),
    marks INT,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);
SHOW DATABASES;
