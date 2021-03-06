/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Símon Örn Reynisson <sor7@hi.is>
 */
public class SearchpageController implements Initializable {

    Stage stage;  //Refers to the current window
    SearchController search; //This will do the searching. 
    @FXML
    private DatePicker firstDate;
    @FXML
    private DatePicker secondDate;
    @FXML
    private TextField departingFrom;
    @FXML
    private TextField arrivingTo;
    @FXML
    private CheckBox isOneWay;
    @FXML
    private GridPane offersGrid;
    @FXML
    private Button searchButton;
    @FXML
    private GridPane allSearchFields;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addDateListeners();

        //Creates a binding which disables the search button if the fields are not filled out.
        searchButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return departingFrom.getText().length() == 0 || arrivingTo.getText().length() == 0
                    || firstDate.getValue() == null || (!isOneWay.isSelected() && secondDate.getValue() == null);
        }, arrivingTo.textProperty(), departingFrom.textProperty(), firstDate.valueProperty(), secondDate.valueProperty(), isOneWay.selectedProperty()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Adds a listener to the second date picker to make dates that are before
     * the date chosen in the first one unpickable, and vice versa.
     */
    private void addDateListeners() {
        //This prevents the user from choosing a return date that's sooner than
        //the outgoing departure date. 
        final Callback<DatePicker, DateCell> dayCellFactoryLatterPicker
                = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (firstDate.getValue() != null && item.isBefore(
                                firstDate.getValue().plusDays(1))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #eeeeee;");
                        }
                    }
                };
            }
        };

        final Callback<DatePicker, DateCell> dayCellFactoryPriorPicker
                = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (secondDate.getValue() != null && item.isAfter(
                                secondDate.getValue().plusDays(-1))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #eeeeee;");
                        }
                    }
                };
            }
        };
        secondDate.setDayCellFactory(dayCellFactoryLatterPicker);
        firstDate.setDayCellFactory(dayCellFactoryPriorPicker);

        //This will set the next date fields to 5 days later than the first one
        // automatically. I didn't like it. 
//        firstDate.valueProperty().addListener((ov, oldValue, newValue) -> {
//            secondDate.setValue(newValue.plusDays(7));
//        });
    }

    /**
     * Changes the window to the booking site, and passes the info for the
     * flights found to the controller for the booking site.
     *
     * @param event Irrelevant
     */
    @FXML
    private void searchHandler(ActionEvent event) {
        //Just a test, this can be deleted later. 
        LocalDate departing = firstDate.getValue();
        LocalDate returnDate = secondDate.getValue();
        String departure = departingFrom.getText();
        String arrival = arrivingTo.getText();
  
        
        
        Parent root;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookingPage.fxml"));
            root = (Parent) loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            BookingPageController controller = (BookingPageController) loader.getController();
            controller.passFlightData(departing, departure, returnDate, arrival,allSearchFields);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
