package edu.capella.bsit.u07a1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * CONTROLLER CLASS FOR COURSE REGISTRATION UI. HANDLES INTERACTION BETWEEN THE UI AND THE PERSISTENCE LAYER,
 * INCLUDING LOADING AND REGISTERING COURSES ASYNCHRONOUSLY.
 */
public class CourseRegistrationController {
    // *******************************************
    // SETUP HIBERNATE ACCESS -> IT WILL BE CONFIGURED & INITIALIZED IN THE CONSTRUCTOR
    // *******************************************
    CourseRegistrationService service;
    EntityManager em;
    EntityManagerFactory emf;
    ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(CourseRegistrationController.class.getName());
    private String currentUser = "";
    final int MAX_CREDIT_LOAD = 9;
    private final IntegerProperty totalCredit = new SimpleIntegerProperty(0);
    @FXML
    public HBox learnerHBox;
    @FXML
    public HBox creditsHBox;
    @FXML
    public Button learnerSignIn;
    @FXML
    public Label learnerID;
    @FXML
    public TextField learnerIDValue;
    @FXML
    public Label selectPromptLabel;
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<RegisteredCourse> regCourseList = FXCollections.observableArrayList();
    @FXML
    private TableView<Course> coursesTable = new TableView<>();
    @FXML
    public TableColumn<Course, String> courseIdColumn;
    @FXML
    public TableColumn<Course, Integer> creditsColumn;
    @FXML
    public Label registerTableLabel;
    @FXML
    private TableView<RegisteredCourse> registeredCoursesTable = new TableView<>();
    @FXML
    public TableColumn<RegisteredCourse, String> regCourseIdColumn;
    @FXML
    public TableColumn<RegisteredCourse,Integer> regCreditsColumn;
    @FXML
    public Label creditHourPromptLabel;
    @FXML
    public Label creditHoursLabel;
    @FXML
    public GridPane grid;
    @FXML
    public Label confirmPromptLabel;

    /**
     * INITIALIZES THE CONTROLLER BY SETTING UP DATABASE CONNECTION AND LOADING UI COMPONENTS.
     * CONFIGURATION OVERRIDES FOR THE ENTITYMANAGERFACTORY ARE SPECIFIED TO ALLOW DYNAMIC DATABASE SETTINGS.
     */
    public CourseRegistrationController(){
        // LOAD THE PROPERTIES FILE AND CREATE THE SERVICE IN THE CONSTRUCTOR
        // CREATE A HASHMAP OF THE CONFIGS THAT WILL NEED TO BE OVERWRITTEN, POINTING TO THE PROPERTIES
        Map<String, String> configOverrides = new HashMap<>();

        // PROPERTIES TO LOAD IN THE `jdbc.properties` FILE, WHICH WILL BE USED TO GET THE CONFIG
        // DATA, SUCH AS THE URL, USERNAME, AND PASSWORD
        Properties props = new Properties();

        // TRY WITH RESOURCE TO LOAD THE RESOURCE FILE & THEN SET THE CONFIG OVERRIDES
        try (InputStream is = getClass().getResourceAsStream("/edu/capella/bsit/u07a1/jdbc.properties")) { // Adjust the path if necessary
            props.load(is);
            // ADD THE PROPERTIES THAT NEED TO BE OVERWRITTEN - URL, USERNAME, PASSWORD, ETC.
            configOverrides.put("jakarta.persistence.jdbc.url", props.getProperty("db.url"));
            configOverrides.put("jakarta.persistence.jdbc.user", props.getProperty("db.username"));
            configOverrides.put("jakarta.persistence.jdbc.password", props.getProperty("db.password"));

            // CREATE THE ENTITY MANAGER FACTORY WITH THE CONFIGURATION OVERRIDES (PASSES IN URL, USERNAME, & PASSWORD
            emf = Persistence.createEntityManagerFactory("CourseRegistrationService", configOverrides);
            em = emf.createEntityManager();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not load 'jdbc.properties'.", ex);
        }
        // SET THE SERVICE TO THE NEW COURSE REGISTRATION SERVICE WITH THE CONFIGS SET FOR THE PERSISTENCE.XML FILE
        this.service = new CourseRegistrationService(em);
    }

    /**
     * ASYNCHRONOUSLY LOADS ALL COURSES FROM THE DATABASE AND UPDATES THE COURSE LIST IN THE UI.
     */
    public void loadCoursesAsync() {
        service.getAllCoursesAsync().thenAcceptAsync(courses -> {
            Platform.runLater(() -> {
                // PLATFORM WILL UPDATE THE LIST OF COURSES AND COMBOBOX ONCE THEY COURSES HAVE BEEN RETRIEVED
                courseList.setAll(courses);
                logger.log(Level.CONFIG, "The following course were added:\n{0}", courseList);
            });
        });
    }

    /**
     * UPDATES THE CURRENT REGISTRATION STATUS FOR THE LEARNER, INCLUDING REGISTERED COURSES AND TOTAL CREDIT HOURS.
     */
    private void updateCurrentRegistration() {
        // GET THE CURRENT LEARNER ID, ASYNCHRONOUSLY GET ALL REGISTERED COURSES
        String learnerId = learnerIDValue.getText();
        service.getAllCourseRegistrationsAsync(learnerId).thenAcceptAsync(registeredCourses -> {
            Platform.runLater(() -> {
                // CLEAR THE PREVIOUS COURSE LIST AND UPDATE IT
                regCourseList.clear();
                regCourseList.addAll(registeredCourses);

                // CREATE A SET OF REGISTERED COURSE CODE THAT IS MAPED TO THE GET COURSE CODE METHOD
                Set<String> registeredCourseCodes = registeredCourses.stream()
                        .map(RegisteredCourse::getCourseCode)
                        .collect(Collectors.toSet());

                // RUN A FOR EACH TO SET IS REGISTERED TO TRUE IF THERE IS A MATCH IN THE SET
                courseList.forEach(course -> course.setIsRegisteredFor(registeredCourseCodes.contains(course.getCourseCode())));

                updateRegistrationUI(regCourseList);
            });
        });
    }

    /**
     * UPDATES THE UI TO REFLECT THE CURRENT REGISTRATION STATUS, INCLUDING UPDATING THE LIST OF REGISTERED COURSES
     * AND THE TOTAL CREDIT HOURS.
     *
     * @param registeredCourses THE LIST OF COURSES THE LEARNER IS CURRENTLY REGISTERED FOR.
     */
    private void updateRegistrationUI(ObservableList<RegisteredCourse> registeredCourses) {
        // RESET THE TOTAL CREDITS
        totalCredit.setValue(0);

        // UPDATE THE TOTAL CREDIT HOURS
        for (RegisteredCourse course : registeredCourses) {
            totalCredit.set(totalCredit.get() + course.getCreditHours());
        }
    }

    // METHOD TO CONFIGURE THE LOGGER
    private static void configureLogger(){
        // SETUP LOGGER
        LogManager.addFileHandlerToLogger(logger);
    }

    /**
     * HANDLES THE SIGN-IN ACTION FOR THE USER. THIS METHOD VALIDATES THE PROVIDED LEARNER ID AND UPDATES THE UI ACCORDINGLY.
     * IF THE LEARNER ID IS VALID, IT INITIATES THE PROCESS TO UPDATE THE CURRENT REGISTRATION INFORMATION.
     *
     * @param actionEvent THE EVENT TRIGGERED BY CLICKING THE SIGN-IN BUTTON.
     */
    public void signIn(ActionEvent actionEvent) {
        if (Objects.equals(learnerIDValue.getText(), "")){
            confirmPromptLabel.setTextFill(Color.RED);
            confirmPromptLabel.setText("Please enter a Learner ID.");
            logger.log(Level.WARNING, "Failed to enter a Learner ID.");
        } else {
            // UPDATE THE CURRENT USER - ALLOWS FOR VALIDATION IN COURSE
            // SELECTION FROM THE COMBOBOX
            currentUser = learnerIDValue.getText();

            // UPDATE REGISTRATION AND SET ALL COURSES TO NOT REGISTERED FOR
            for (Course c : courseList){
                c.setIsRegisteredFor(false);
            }
            updateCurrentRegistration();

            // USER FEEDBACK
            confirmPromptLabel.setTextFill(Color.GREEN);
            confirmPromptLabel.setText(String.format("Signed in with Learner ID: %s", learnerIDValue.getText()));
            logger.log(Level.INFO, String.format("Signed in with Learner ID: %s", learnerIDValue.getText()));
        }
    }

    /**
     * HANDLES ACTIONS PERFORMED ON THE COURSES TABLE, SUCH AS SELECTIONS MADE BY THE USER. THIS METHOD DETERMINES
     * WHETHER A COURSE CAN BE REGISTERED BASED ON THE CURRENT USER'S CREDIT LOAD AND REGISTRATION STATUS.
     * IT CHECKS IF THE USER HAS SIGNED IN AND VERIFIES THE COURSE HAS NOT BEEN PREVIOUSLY REGISTERED FOR.
     * UPON SUCCESSFUL REGISTRATION, IT UPDATES THE UI AND DATABASE ACCORDINGLY.
     *
     * @param choice THE COURSE SELECTED BY THE USER FOR REGISTRATION.
     */
    public void courseTableOnAction(Course choice) {
        // CHECK TO ENSURE THERE IS A LEARNER ID & THAT THE LEARNER SIGNED IN (PRESSED THE SIGN-IN BUTTON)
        if (!Objects.equals(learnerIDValue.getText(), "") & Objects.equals(learnerIDValue.getText(), currentUser)){
            // BOOLEAN TO CHECK IF COURSE HAS BEEN REGISTERED FOR BY
            // CHECKING IF THE CHOICE IS REGISTERED FOR
            boolean isRegistered = choice.getIsRegisteredFor();
            // BOOLEAN CHECK && CREDIT HOURS LESS THAN 9
            if (!isRegistered & totalCredit.get() < MAX_CREDIT_LOAD){
                // CHANGE CONFIRM PROMPT LABEL TO GREEN
                confirmPromptLabel.setTextFill(Color.GREEN);

                // UPDATES THE LABEL TO REFLECT SUCCESSFUL REGISTRATION
                confirmPromptLabel.setText(String.format("Successfully registered for %s!", choice));

                // UPDATES THE CURRENT CREDIT HOURS -> LABEL IS BOUND, AUTO UPDATES
                totalCredit.set(totalCredit.get() + choice.getCreditHours());

                // SET COURSE TO CURRENTLY REGISTERED FOR
                choice.setIsRegisteredFor(true);

                // WRITE THE REGISTERED COURSE TO THE DATABASE -> ASYNCHRONOUS CALL TO HIBERNATE SERVICE
                registerCourse(learnerIDValue.getText(), choice);
            } else if (isRegistered){
                // IF THE COURSE HAS ALREADY BEEN REGISTERED FOR, CHANGE THE COLOR FILL TO RED
                // AND DISPLAY CONFIRM PROMPT LABEL AS FAILED TO REGISTER
                confirmPromptLabel.setTextFill(Color.RED);
                confirmPromptLabel.setText(String.format("Failed to register duplicate class: %s.", choice));
                logger.log(Level.WARNING, String.format("Failed to register duplicate class: %s.", choice));
            } else {
                // IN THE EVENT THAT THE PREVIOUS CONDITIONALS FAIL, THE ONLY PATH LEFT IS
                // THE EVENT THAT THERE ARE CURRENTLY 9 CREDIT HOURS ALREADY REGISTERED FOR
                // SET FILL TO RED AND DISPLAY ONLY 9 CREDIT ARE ALLOWED TO BE REGISTERED FOR
                confirmPromptLabel.setTextFill(Color.RED);
                confirmPromptLabel.setText(String.format("Failed to register for %s, only %s credits are allowed.", choice, MAX_CREDIT_LOAD));
                logger.log(Level.WARNING, String.format("Failed to register for %s, only %s credits are allowed.", choice, MAX_CREDIT_LOAD));
            }
        } else if (Objects.equals(learnerIDValue.getText(), "")){
            confirmPromptLabel.setTextFill(Color.RED);
            confirmPromptLabel.setText("Please enter a Learner ID.");
            logger.log(Level.WARNING, "Failed to enter a Learner ID.");
        } else {
            confirmPromptLabel.setTextFill(Color.YELLOW);
            confirmPromptLabel.setText("Please sign in to continue.");
            logger.log(Level.WARNING, "Failed to sign in with Learner ID.");
        }
    }

    /**
     * ASYNCHRONOUSLY REGISTERS A COURSE FOR THE CURRENT USER. THIS METHOD INITIATES A BACKGROUND OPERATION TO
     * REGISTER A NEW COURSE UNDER THE USER'S LEARNER ID AND UPDATES THE UI UPON COMPLETION.
     *
     * @param learnerID THE IDENTIFIER FOR THE LEARNER.
     * @param choice THE COURSE TO BE REGISTERED.
     */
    public void registerCourse(String learnerID, Course choice) {
        service.createCourseRegistrationAsync(learnerID, choice.getCourseCode(), choice.getCreditHours())
                .thenRunAsync(() -> {
                    Platform.runLater(this::updateCurrentRegistration);
                });
    }

    /**
     * PREPARES THE APPLICATION FOR SHUTDOWN BY TERMINATING ACTIVE BACKGROUND OPERATIONS AND CLOSING DATABASE CONNECTIONS.
     * THIS METHOD ENSURES ALL RESOURCES ARE PROPERLY RELEASED BEFORE THE APPLICATION EXITS.
     */
    public void stopApplication() {
        // ATTEMPT TO STOP ALL ACTIVELY EXECUTING TASKS
        executorService.shutdownNow();
        // CLOSE THE ENTITY MANAGER & FACTORY
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * INITIALIZES UI COMPONENTS AND LOADS DATA AS REQUIRED AT THE START OF THE APPLICATION. THIS METHOD SETS UP
     * BINDINGS, LISTENERS, AND INITIATES ASYNCHRONOUS LOADING OF COURSES.
     */
    @FXML
    public void initialize() {
        // BIND THE TABLE COLUMNS SO THEY GROW WITH THE COURSE TABLE VIEW
        courseIdColumn.prefWidthProperty().bind(coursesTable.widthProperty().multiply(0.5)); // 50% WIDTH
        creditsColumn.prefWidthProperty().bind(coursesTable.widthProperty().multiply(0.5)); // 50% WIDTH
        regCourseIdColumn.prefWidthProperty().bind(registeredCoursesTable.widthProperty().multiply(0.5)); // 50% WIDTH
        regCreditsColumn.prefWidthProperty().bind(registeredCoursesTable.widthProperty().multiply(0.5)); // 50% WIDTH

        // BIND THE CREDIT HOURS LABEL TO THE TOTAL CREDIT INTEGER PROPERTY
        creditHoursLabel.textProperty().bind(totalCredit.asString());

        // SET THE COURSE ID & CREDITS COLUMNS VALUES
        regCourseIdColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getCourseCode()));
        regCreditsColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getCreditHours()));

        // SET THE COURSE ID & CREDITS COLUMNS VALUES
        courseIdColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getCourseCode()));
        creditsColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getCreditHours()));

        // BIND THE OBSERVABLE LISTS TO THE VIEW TABLES
        registeredCoursesTable.setItems(regCourseList);
        coursesTable.setItems(courseList);

        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // CALL THE METHOD TO HANDLE THE ADDITION OF THE SELECTED ROW TO THE REGISTERED COURSES LIST
                courseTableOnAction(newSelection);
            }
        });

        // PROGRAMMATICALLY SET THE REGISTERED TABLE'S PLACEHOLDER -> DOING IT
        // VIA FXML PRODUCES AN UNKNOWN ERROR (AT LEAST UNKNOWN TO THE COMPILER/LSP).
        Label placeholder = new Label("You are not registered for any courses.");
        placeholder.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        registeredCoursesTable.setPlaceholder(placeholder);

        // LOAD THE COURSES INTO THE OBSERVABLE LIST & CONFIG THE LOGGER
        loadCoursesAsync();
        configureLogger();
    }
}
