package edu.capella.bsit.u07a1;

import jakarta.persistence.*;

/**
 * ENTITY REPRESENTATION OF A COURSE OFFERING. THIS CLASS MAPS TO THE "COURSE_OFFERINGS" TABLE IN THE DATABASE.
 * IT INCLUDES PROPERTIES FOR THE COURSE CODE, CREDIT HOURS, AND A FLAG INDICATING WHETHER A STUDENT IS REGISTERED FOR THE COURSE.
 */
@Entity
@Table(name="course_offerings")
public class Course {
    // UNIQUE IDENTIFIER FOR THE COURSE. MAPPED TO THE "COURSE_CODE" COLUMN IN THE DATABASE.
    @Id
    @Column(name="course_code")
    private String courseCode;

    // NUMBER OF CREDIT HOURS ASSIGNED TO THE COURSE. MAPPED TO THE "CREDIT_HOURS" COLUMN IN THE DATABASE.
    @Column(name="credit_hours")
    private int creditHours;

    // TRANSIENT PROPERTY TO INDICATE WHETHER THE COURSE IS REGISTERED FOR. NOT PERSISTED IN THE DATABASE.
    @Transient
    private boolean isRegisteredFor;

    /**
     * DEFAULT CONSTRUCTOR REQUIRED BY THE JPA SPECIFICATION. USED BY THE PERSISTENCE FRAMEWORK TO CREATE INSTANCES OF THE ENTITY.
     */
    public Course() { }

    /**
     * CONSTRUCTS A NEW COURSE INSTANCE WITH SPECIFIED COURSE CODE AND CREDIT HOURS.
     * INITIALLY, THE COURSE IS NOT REGISTERED FOR.
     *
     * @param courseCode THE UNIQUE CODE IDENTIFYING THE COURSE.
     * @param creditHours THE NUMBER OF CREDIT HOURS THE COURSE CARRIES.
     */
    public Course(String courseCode, int creditHours) {
        this.courseCode = courseCode;
        this.creditHours = creditHours;
        this.isRegisteredFor = false;
    }

    // SETTERS AND GETTERS FOR THE CLASSES'S PROPERTIES.
    public void setIsRegisteredFor(boolean trueOrFalse){
        this.isRegisteredFor = trueOrFalse;
    }

    public boolean getIsRegisteredFor() {
        return this.isRegisteredFor;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    /**
     * RETURNS A STRING REPRESENTATION OF THE COURSE, COMBINING COURSE CODE AND CREDIT HOURS.
     *
     * @return A STRING REPRESENTING THE COURSE.
     */
    @Override
    public String toString(){
        return String.format("%s (%d)", courseCode, creditHours);
    }

}
