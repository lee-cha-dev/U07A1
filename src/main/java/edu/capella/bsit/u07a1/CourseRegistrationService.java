package edu.capella.bsit.u07a1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

// THIS FILE WAS USED TO PRACTICE CREATING DEV COMMENTS THAT ARE
// MORE ALIGNED WITH INDUSTRY STANDARDS/EXPECTATIONS. THE OTHER FILES
// WILL EVENTUALLY MIRROR THIS APPROACH

public class CourseRegistrationService {
    protected EntityManager em;

    /**
     * CONSTRUCTOR THAT ACCEPTS CONFIGURATION OVERRIDES FOR ENTITYMANAGERFACTORY CREATION.
     * THIS ALLOWS FOR DYNAMIC DATABASE CONNECTION SETTINGS RATHER THAN HARD-CODED VALUES IN THE PERSISTENCE.XML.
     * THE CONFIGURATION OVERRIDES ARE PASSED AS A MAP AND USED TO CREATE THE ENTITYMANAGERFACTORY AND ENTITYMANAGER.
     * 
     * @param configOverrides A MAP OF CONFIGURATION PROPERTIES TO OVERRIDE DEFAULT SETTINGS IN PERSISTENCE.XML.
     */
    public CourseRegistrationService(Map<String, String> configOverrides) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CourseRegistrationService", configOverrides);
        this.em = emf.createEntityManager();
    }

    /**
     * CONSTRUCTOR THAT ACCEPTS AN ENTITYMANAGER DIRECTLY.
     * THIS CAN BE USED WHEN AN ENTITYMANAGER IS ALREADY CREATED AND CONFIGURED EXTERNALLY.
     * 
     * @param em THE ENTITYMANAGER TO BE USED BY THE SERVICE.
     */
    public CourseRegistrationService(EntityManager em) {
        this.em = em;
    }

    // SYNCHRONOUS

    /**
     * CREATES A NEW COURSE ENTITY IN THE DATABASE.
     * THIS METHOD INITIATES A TRANSACTION, PERSISTS THE NEW COURSE ENTITY TO THE DATABASE, AND COMMITS THE TRANSACTION.
     * IT IS DESIGNED TO BE SYNCHRONOUS AND WILL BLOCK UNTIL THE OPERATION COMPLETES.
     * 
     * @param courseCode THE UNIQUE CODE IDENTIFYING THE COURSE.
     * @param creditHours THE NUMBER OF CREDIT HOURS THE COURSE CARRIES.
     * @return THE PERSISTED COURSE ENTITY.
     */
    public Course createCourse(String courseCode, int creditHours) {
        Course course = new Course(courseCode, creditHours);
        em.getTransaction().begin();
        em.persist(course);
        em.getTransaction().commit();
        return course;
    }

    /**
     * RETRIEVES ALL COURSES FROM THE DATABASE.
     * UTILIZES HIBERNATE QUERY LANGUAGE (HQL) TO SELECT ALL COURSE ENTITIES, ORDERED BY THEIR COURSE CODE.
     * THIS OPERATION IS SYNCHRONOUS AND WILL BLOCK UNTIL COMPLETED.
     * 
     * @return A LIST OF ALL COURSE ENTITIES IN THE DATABASE.
     */
    public List<Course> getAllCourses() {
        String hql = "SELECT crs FROM Course crs ORDER BY courseCode";
        TypedQuery<Course> query = em.createQuery(hql, Course.class);
        return query.getResultList();
    }

    /**
     * CREATES A NEW COURSE REGISTRATION IN THE DATABASE FOR A GIVEN LEARNER.
     * BEGINS A TRANSACTION TO PERSIST A NEW REGISTEREDCOURSE ENTITY, COMMITTING THE TRANSACTION UPON SUCCESS.
     * THIS METHOD IS SYNCHRONOUS AND BLOCKS UNTIL THE DATABASE OPERATION IS COMPLETE.
     * 
     * @param learnerID THE ID OF THE LEARNER REGISTERING FOR THE COURSE.
     * @param courseCode THE CODE OF THE COURSE TO REGISTER.
     * @param creditHours THE NUMBER OF CREDIT HOURS FOR THE COURSE.
     */
    public void createCourseRegistration(String learnerID, String courseCode, int creditHours) {
        RegisteredCourse registration = new RegisteredCourse(learnerID, courseCode, creditHours);
        em.getTransaction().begin();
        em.persist(registration);
        em.getTransaction().commit();
    }

    /**
     * RETRIEVES ALL COURSE REGISTRATIONS FOR A GIVEN LEARNER ID FROM THE DATABASE.
     * PERFORMS AN HQL QUERY TO FIND ALL REGISTEREDCOURSE ENTITIES ASSOCIATED WITH THE LEARNER ID.
     * THIS OPERATION IS SYNCHRONOUS AND WILL BLOCK UNTIL THE QUERY COMPLETES AND RESULTS ARE RETURNED.
     * 
     * @param id THE LEARNER ID WHOSE COURSE REGISTRATIONS ARE BEING REQUESTED.
     * @return A LIST OF REGISTEREDCOURSE ENTITIES FOR THE SPECIFIED LEARNER ID.
     */
    public List<RegisteredCourse> getAllCourseRegistrations(String id) {
        String hql = "SELECT reg FROM RegisteredCourse reg WHERE learnerID = :id";
        TypedQuery<RegisteredCourse> query = em.createQuery(hql, RegisteredCourse.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    // ASYNCHRONOUS

    /**
     * ASYNCHRONOUSLY RETRIEVES ALL COURSES FROM THE DATABASE.
     * THIS METHOD EXECUTES THE QUERY ON A BACKGROUND THREAD PROVIDED BY A NEWCACHEDTHREADPOOL TO AVOID BLOCKING THE MAIN THREAD.
     * THE HIBERNATE QUERY LANGUAGE (HQL) STATEMENT 'SELECT CRS FROM COURSE CRS ORDER BY COURSECODE' IS USED TO RETRIEVE ALL COURSES,
     * ORDERED BY THEIR COURSE CODE. THE RESULT IS A LIST OF COURSE ENTITIES.
     *
     * @return A COMPLETABLEFUTURE THAT, WHEN COMPLETED, PROVIDES A LIST OF ALL COURSE ENTITIES FROM THE DATABASE.
     */
    public CompletableFuture<List<Course>> getAllCoursesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            String hql = "SELECT crs FROM Course crs ORDER BY courseCode";
            TypedQuery<Course> query = em.createQuery(hql, Course.class);
            return query.getResultList();
        }, Executors.newCachedThreadPool());
    }

    /**
     * ASYNCHRONOUSLY CREATES A COURSE REGISTRATION IN THE DATABASE.
     * THIS METHOD PERFORMS THE DATABASE OPERATION ON A BACKGROUND THREAD PROVIDED BY A NEWCACHEDTHREADPOOL TO AVOID UI BLOCKING.
     * IT BEGINS A TRANSACTION, PERSISTS THE NEW REGISTEREDCOURSE ENTITY, AND COMMITS THE TRANSACTION.
     *
     * @param learnerID THE LEARNER'S ID TO BE ASSOCIATED WITH THE COURSE REGISTRATION.
     * @param courseCode THE COURSE CODE OF THE COURSE TO BE REGISTERED.
     * @param creditHours THE NUMBER OF CREDIT HOURS FOR THE COURSE.
     * @return A COMPLETABLEFUTURE REPRESENTING THE COMPLETION OF THE ASYNCHRONOUS OPERATION.
     */
    public CompletableFuture<Void> createCourseRegistrationAsync(String learnerID, String courseCode, int creditHours) {
        return CompletableFuture.runAsync(() -> {
            RegisteredCourse registration = new RegisteredCourse(learnerID, courseCode, creditHours);
            em.getTransaction().begin();
            em.persist(registration);
            em.getTransaction().commit();
        }, Executors.newCachedThreadPool());
    }

    /**
     * ASYNCHRONOUSLY RETRIEVES ALL COURSE REGISTRATIONS FOR A GIVEN LEARNER ID FROM THE DATABASE.
     * THIS METHOD USES A BACKGROUND THREAD PROVIDED BY A NEWCACHEDTHREADPOOL TO PERFORM THE DATABASE OPERATION ASYNCHRONOUSLY.
     * THE HQL QUERY 'SELECT REG FROM REGISTEREDCOURSE REG WHERE LEARNERID = :ID' IS USED TO FIND ALL REGISTRATIONS FOR THE SPECIFIED LEARNER ID.
     *
     * @param id THE LEARNER ID FOR WHICH TO RETRIEVE ALL COURSE REGISTRATIONS.
     * @return A COMPLETABLEFUTURE THAT, WHEN COMPLETED, PROVIDES A LIST OF REGISTEREDCOURSE ENTITIES FOR THE SPECIFIED LEARNER ID.
     */
    public CompletableFuture<List<RegisteredCourse>> getAllCourseRegistrationsAsync(String id) {
        return CompletableFuture.supplyAsync(() -> {
            String hql = "SELECT reg FROM RegisteredCourse reg WHERE learnerID = :id";
            TypedQuery<RegisteredCourse> query = em.createQuery(hql, RegisteredCourse.class);
            query.setParameter("id", id);
            return query.getResultList();
        }, Executors.newCachedThreadPool());
    }
}

