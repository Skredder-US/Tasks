package main;

import java.time.LocalDate;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;

/**
 * Series of Controls laid horizontally for the user to pick a Date and Time.
 * <p>
 * Date is selected from a calendar and time is selected from three dropdowns: hour, 
 * minutes, and AM or PM. 
 */
public class DateTimePicker extends HBox {
    private final DatePicker datePicker;
    private final ChoiceBox<String> hourPicker;
    private final ChoiceBox<String> minutesPicker;
    private final ChoiceBox<String> amPmPicker;

    /**
     *
     * Initializes a newly created {@code DateTimePicker} object to be displayed and handle users
     * selecting date and/or time.
     * 
     * @param padding 
     */
    public DateTimePicker(double padding) {
        // Selects date
        datePicker = new DatePicker();

        // Selects hour
        hourPicker = new ChoiceBox<String>();
        ObservableList<String> hours = hourPicker.getItems();
        hours.add("");
        for (int i = 1; i <= 12; i++) {
            hours.add(Integer.toString(i));
        }

        // Selects minutes in increments of 5.
        minutesPicker = new ChoiceBox<String>();
        ObservableList<String> minutes = minutesPicker.getItems();
        minutes.add("");
        for (int i = 0; i <= 55; i += 5) {
            String minuteAsString = "";
            // handle :00 and :05
            if (i < 10) {
                minuteAsString = "0";
            }
            minuteAsString += Integer.toString(i);

            minutes.add(minuteAsString);
        }

        // Selects AM or PM
        amPmPicker = new ChoiceBox<String>();
        amPmPicker.getItems().addAll("", "AM", "PM");

        // Horizontally stack the controls
        setSpacing(padding / 2);
        setPadding(new Insets(padding, 0, 0, 0));
        getChildren().addAll(datePicker, hourPicker, minutesPicker, amPmPicker);
    }

    /**
     * Returns a {@code String} representing the user's selection of date and time.
     * <p>
     * Of the form: (date) (hour):(minutes) (AM or PM), with the corresponding values in place. 
     * <p>
     * Date and/or time can be unset.
     * 
     * @return a string representation of this selected date and time.
     */
    @Override
    public String toString() {
        LocalDate date = datePicker.getValue();
        String hour = hourPicker.getValue();
        String minutes = minutesPicker.getValue();
        String amPm = amPmPicker.getValue();

        if (date == null) {
            if (hour == null || minutes == null || amPm == null) {
                return ""; // no data
            } 

            date = LocalDate.now(); // only time
        } else if (hour == null || minutes == null || amPm == null) {
            return date.toString(); // only date
        }
        
        // return date and time
        return date.toString() + " " + hour + ":" + minutes + " " + amPm;
        
    }
}
