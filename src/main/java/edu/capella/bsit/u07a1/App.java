package edu.capella.bsit.u07a1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


/**
 * MAIN APPLICATION CLASS FOR THE JAVAFX FXML APPLICATION.
 * THIS CLASS EXTENDS JAVAFX.APPLICATION AND OVERRIDES THE START METHOD TO LAUNCH THE APPLICATION UI.
 * IT LOADS THE FXML FOR THE MAIN WINDOW, SETS UP THE SCENE, AND CONFIGURES THE PRIMARY STAGE.
 */
public class App extends Application {
    // INITIALIZES THE FXMLLoader WITH THE PATH TO THE FXML FILE.
    private FXMLLoader fxmlLoader;

    /**
     * STARTS THE APPLICATION BY LOADING THE FXML FILE, CREATING THE SCENE, AND SHOWING THE PRIMARY STAGE.
     * THIS METHOD ALSO SETS THE APPLICATION WINDOW'S TITLE, ICON, AND STYLESHEET.
     *
     * @param stage THE PRIMARY STAGE FOR THIS APPLICATION, ONTO WHICH THE APPLICATION SCENE CAN BE SET.
     */
    @Override
    public void start(Stage stage) {
        // LOAD IN THE FXML SOURCE
        fxmlLoader = new FXMLLoader(getClass().getResource("/edu/capella/bsit/u07a1/course_registration.fxml"));
        try {
            // CREATES A SCENE FOR THE PRIMARY STAGE WITH SPECIFIED DIMENSIONS.
            Scene scene = new Scene(fxmlLoader.load(), 550, 600);
            // APPLIES CSS STYLESHEET TO THE SCENE.
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/edu/capella/bsit/u07a1/course_registration.css")).toExternalForm());
            // SETS THE TITLE, SCENE, AND ICON FOR THE STAGE, AND DISPLAYS IT.
            stage.setTitle("Course Registration");
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/edu/capella/bsit/u07a1/icon.png")).toExternalForm()));
            stage.show();
        } catch (IOException ex) {
            // HANDLES IO EXCEPTIONS RELATED TO LOADING THE FXML FILE.
            System.err.println("Error Loading FXML: " + ex.getMessage());
        }
    }

    /**
     * INVOKED WHEN THE APPLICATION SHOULD STOP, AND PROVIDES A CONVENIENT PLACE TO PREPARE FOR APPLICATION EXIT AND
     * DESTROY RESOURCES. THIS METHOD CALLS stopApplication() TO CLEAN UP RESOURCES USED BY THE CONTROLLER.
     */
    @Override
    public void stop() {
        CourseRegistrationController controller = fxmlLoader.getController();
        controller.stopApplication();
    }

    /**
     * THE MAIN ENTRY POINT FOR ALL JAVAFX APPLICATIONS. THE START METHOD IS CALLED AFTER THE INIT METHOD HAS RETURNED,
     * AND AFTER THE SYSTEM IS READY FOR THE APPLICATION TO BEGIN RUNNING.
     *
     * @param args COMMAND LINE ARGUMENTS PASSED TO THE APPLICATION. NOT USED IN THIS APPLICATION.
     */
    public static void main(String[] args) {
        launch();
    }

}