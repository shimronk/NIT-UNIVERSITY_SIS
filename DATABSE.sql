CREATE DATABASE student_system;
USE student_system;
CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    age INT NOT NULL,
    course VARCHAR(100) NOT NULL,
    phone VARCHAR(10),

    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,

    behaviour_score INT DEFAULT 0,
    behaviour_status VARCHAR(20) DEFAULT 'GOOD'
);
CREATE TABLE users (
    username VARCHAR(100) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    role ENUM('ADMIN','STUDENT') NOT NULL
);
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMIN');
CREATE TABLE student_marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20),
    year INT,
    semester INT,
    subject VARCHAR(100),
    marks DOUBLE,

    FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON DELETE CASCADE
);
CREATE TABLE behaviour_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20),
    change_value INT,
    reason VARCHAR(255),
    record_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON DELETE CASCADE
);
CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50)
);
INSERT INTO admin (username, password)
VALUES ('admin', 'admin123');
USE student_system;

CREATE TABLE marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    marks INT NOT NULL,
    grade VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
        ON DELETE CASCADE
);
USE student_system;

CREATE TABLE behaviour (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    behaviour_type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
        ON DELETE CASCADE
);
ALTER TABLE behaviour_records
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    marks INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);
ALTER TABLE students
MODIFY behaviour_score INT DEFAULT 50,
MODIFY behaviour_status VARCHAR(30) DEFAULT 'GOOD';



DESCRIBE students;
DESCRIBE marks;
