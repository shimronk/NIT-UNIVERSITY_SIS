package com.mycompany.sis.auth.model;

import java.time.LocalDate;
import java.time.Period;

public class Student {

    private String studentId;
    private String fullName;
    private LocalDate dob;
    private int age;
    private String course;
    private String phone;
    private String email;
    private String password;
    private int behaviourScore;
    private String behaviourStatus;

    public Student(String studentId, String fullName, LocalDate dob,
                   String course, String phone) {

        this.studentId = studentId;
        this.fullName = fullName;
        this.dob = dob;
        this.course = course;
        this.phone = phone;

        this.age = Period.between(dob, LocalDate.now()).getYears();
        this.email = studentId.toLowerCase() + "@nituniversity.lk";
        this.password = generatePassword(fullName, dob.getYear());

        this.behaviourScore = 0;
        this.behaviourStatus = "GOOD";
    }

    private String generatePassword(String name, int year) {
        String[] parts = name.split(" ");
        String initials = "";
        for (String p : parts) {
            initials += p.charAt(0);
        }
        return initials.toUpperCase() + year;
    }

    // GETTERS
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public LocalDate getDob() { return dob; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getBehaviourScore() { return behaviourScore; }
    public String getBehaviourStatus() { return behaviourStatus; }
}
