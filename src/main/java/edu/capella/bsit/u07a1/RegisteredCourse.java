package edu.capella.bsit.u07a1;

import jakarta.persistence.*;
import javafx.fxml.FXML;

/**
 * ENTITY REPRESENTATION OF A LEARNER REGISTRATION FOR A COURSE.
 * THIS CLASS MAPS TO THE "LEARNER_REGISTRATION" TABLE IN THE DATABASE.
 * IT INCLUDES PROPERTIES FOR THE REGISTRATION ID, LEARNER ID, COURSE CODE, AND CREDIT HOURS.
 */
@Entity
@Table(name="learner_registration")
public class RegisteredCourse {

    // UNIQUE IDENTIFIER FOR THE REGISTRATION. AUTO-INCREMENTED TO MIRROR SQL DATABASE FUNCTIONALITY.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="registration_id")
    private int registrationID;

    // THE ID OF THE LEARNER. MAPPED TO THE "LEARNER_ID" COLUMN IN THE DATABASE.
    @FXML
    @Column(name="learner_id")
    private String learnerID;

    // THE CODE OF THE COURSE FOR WHICH THE REGISTRATION IS MADE. MAPPED TO THE "COURSE_CODE" COLUMN.
    @Column(name="course_code")
    private String courseCode;

    // THE NUMBER OF CREDIT HOURS FOR THE REGISTERED COURSE. MAPPED TO THE "CREDIT_HOURS" COLUMN.
    @Column(name="credit_hours")
    private int creditHours;

    /**
     * DEFAULT CONSTRUCTOR REQUIRED BY JPA.
     */
    public RegisteredCourse() { }

    /**
     * CONSTRUCTS A REGISTEREDCOURSE ENTITY WITH SPECIFIED LEARNER ID, COURSE CODE, AND CREDIT HOURS.
     *
     * @param learnerID THE IDENTIFIER OF THE LEARNER.
     * @param courseCode THE CODE OF THE COURSE.
     * @param credits THE NUMBER OF CREDIT HOURS FOR THE COURSE.
     */
    public RegisteredCourse(String learnerID, String courseCode, int credits) {
        this.learnerID = learnerID;
        this.courseCode = courseCode;
        this.creditHours = credits;
    }

    // GETTERS AND SETTERS FOR THE CLASS'S PROPERTIES.
    public int getRegistrationID() {
        return registrationID;
    }

    public String getLearnerID() {
        return learnerID;
    }

    public void setLearnerID(String learnerID) {
        this.learnerID = learnerID;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getCreditHours(){
        return creditHours;
    }

    public void setCreditHours(int credits){
        this.creditHours = credits;
    }

    /**
     * RETURNS A STRING REPRESENTATION OF THE REGISTERED COURSE, COMBINING LEARNER ID, COURSE CODE, AND CREDIT HOURS.
     *
     * @return A STRING REPRESENTING THE REGISTERED COURSE.
     */
    @Override
    public String toString(){
        return String.format("%s: %s (%d)", learnerID, courseCode, creditHours);
    }
}