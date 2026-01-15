package com.mycompany.sis.utill;

public class Session {

    private static String studentId;
    private static String role;

    public static void setStudent(String id) {
        studentId = id;
    }

    public static String getStudentId() {
        return studentId;
    }

    public static void clear() {
        studentId = null;
        role = null;
    }
}
