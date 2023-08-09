package main;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
     * Of the form: {@code (date) (hour):(minutes) (AM or PM)}, with the corresponding values in place. 
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

        boolean hasNoTime = hour == null || minutes == null || amPm == null ||
                hour.isEmpty() || minutes.isEmpty() || amPm.isEmpty();

        if (date == null) {
            if (hasNoTime) {
                return ""; // no data
            }

            date = LocalDate.now(); // only time
        } else if (hasNoTime) {
            return date.toString(); // only date
        }
        
        // return date and time
        return date.toString() + " " + hour + ":" + minutes + " " + amPm;
    }

    /**
     * Sets the date of this {@code DateTimePicker} from a text string such as {@code 2023-08-09}.
     * <p>
     * Specified {@code String} must represent a valid date and is parsed using 
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE}.
     * 
     * @param date the text to parse such as "2023-08-09", not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public void setDate(String date) {
        datePicker.setValue(LocalDate.parse(date));
    }

    /**
     * Sets the time of this {@code DateTimePicker} from a text string such as {@code 9:57 AM}.
     * <p>
     * Specified {@code String} must be in the format: 
     * {@code (hour):(minutes) (AM or PM)}, with the corresponding values in place.
     * 
     * @param time the text to parse such as "9:57 AM", not null
     */
    public void setTime(String time) {
        int colonIndex = time.indexOf(":");
        int spaceIndex = time.indexOf(" ");
        
        hourPicker.setValue(time.substring(0, colonIndex));
        minutesPicker.setValue(time.substring(colonIndex + 1, spaceIndex));
        amPmPicker.setValue(time.substring(spaceIndex + 1, time.length()));
    }
}
