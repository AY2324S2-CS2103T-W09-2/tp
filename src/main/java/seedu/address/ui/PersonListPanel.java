package seedu.address.ui;

import java.util.Comparator;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ScrollPane personScrollPane; // Direct reference to the ScrollPane, if needed

    @FXML
    private VBox personContainer; // Direct reference to the VBox containing the persons

    /**
     * Displays the person list panel.
     * @param persons The person list to update.
     */
    public PersonListPanel(ObservableList<Person> persons) {
        super(FXML);
        String style = "-fx-background-color: #333;";
        personScrollPane.setStyle(style);
        personContainer.setStyle(style);
        displayPersons(persons);
    }

    /**
     * Displays a list of persons.
     * @param persons The list of persons to be displayed.
     */
    public void displayPersons(ObservableList<Person> persons) {
        personContainer.getChildren().clear(); // Clear existing persons
        Label title = new Label("Key Employees");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-label-padding: 10;");
        personContainer.getChildren().add(title);

        if (persons.isEmpty()) {
            logger.info("No person displayed in key employee box");
            // Add a placeholder to fill the background color
            Label placeholderLabel = new Label("No key employees to display");
            placeholderLabel.setStyle("-fx-text-fill: white;");
            placeholderLabel.setMaxWidth(Double.MAX_VALUE); // Make the label fill the width
            placeholderLabel.setAlignment(Pos.CENTER);
            personContainer.getChildren().add(placeholderLabel);
        } else {
            logger.info("Displaying persons in key employee box");
            // Display persons with alternating background colors
            for (int i = 0; i < persons.size(); i++) {
                Person person = persons.get(i);
                Label name = new Label(String.format("%d. ", i + 1) + person.getName().toString());
                Label email = new Label(person.getEmail().toString());
                FlowPane descriptionsPane = new FlowPane(Orientation.VERTICAL);
                VBox personBox = new VBox();

                // Set alternating background colors
                String style = i % 2 == 0 ? "-fx-background-color: #444; -fx-padding: 5;"
                        : "-fx-background-color: #555; -fx-padding: 5;";

                descriptionsPane.setPrefWrapLength(Region.USE_PREF_SIZE);
                descriptionsPane.setMaxWidth(Region.USE_PREF_SIZE); // Set maxWidth to USE_PREF_SIZE
                descriptionsPane.setVgap(4);
                descriptionsPane.setHgap(4);
                name.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-wrap-text: true");
                email.setStyle("-fx-text-fill: white; -fx-wrap-text: true");
                personBox.setStyle(style);

                person.getDescriptions().stream()
                        .sorted(Comparator.comparing(description -> description.descriptionName))
                        .forEach(description -> {
                            Label label = new Label(description.descriptionName);
                            label.setStyle("-fx-text-fill: white; -fx-wrap-text: true");
                            descriptionsPane.getChildren().add(label);
                        });

                personBox.getChildren().addAll(name, email, descriptionsPane);
                personContainer.getChildren().add(personBox);
            }
        }
    }
}
