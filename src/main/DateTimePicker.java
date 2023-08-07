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
        for (int i = 1; i <= 12; i++) {
            hours.add(Integer.toString(i));
        }

        // Selects minutes in increments of 5.
        minutesPicker = new ChoiceBox<String>();
        ObservableList<String> minutes = minutesPicker.getItems();
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
        amPmPicker.getItems().addAll("AM", "PM");

        // Horizontally stack the controls
        setSpacing(padding / 2);
        setPadding(new Insets(padding, 0, 0, 0));
        getChildren().addAll(datePicker, hourPicker, minutesPicker, amPmPicker);
    }

    /**
     * Returns a {@code String} representing the user's selection of date and/or time.
     * <p>
     * Of the form: (date) (hour):(minutes) (AM or PM). With the corresponding values in place. 
     * <p>
     * Unset data will be blank.
     * 
     * @return a string representation of this selected date and/or time.
     */
    @Override
    public String toString() {
        // dueDate or empty on null
        LocalDate date = datePicker.getValue();
        String dateString;
        if (date == null) {
            dateString = "";
        } else {
            dateString = date.toString();
        }
        
        // hour or empty on null
        String hour = hourPicker.getValue();
        if (hour == null) {
            hour = "";
        }
        
        // minutes or empty on null
        String minutes = minutesPicker.getValue();
        if (minutes == null) {
            minutes = "";
        }
                    
        // AM or PM or empty on null
        String amPm = amPmPicker.getValue();
        if (amPm == null) {
            amPm = "";
        }

        // formatting 
        String result = dateString + " " + hour;
        if (!hour.isEmpty()) {
            result += ":";

            if (minutes.isEmpty()) {
                result += "00";
            }
        }
        result += minutes + " " + amPm;

        // done
        return result.trim();
    }
}
