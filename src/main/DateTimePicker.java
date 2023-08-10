package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Series of Controls laid horizontally for the user to pick a Date and Time.
 * <p>
 * Date is selected from a calendar and time is selected from three dropdowns: hour, 
 * minutes, and AM or PM. 
 */
public class DateTimePicker extends HBox {
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final DateTimeFormatter INPUT_FORMATTER = 
            DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final int DATE_LENGTH = LocalDate.now().toString().length();
    private static final DateTimeFormatter DATE_AND_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate date;
    private LocalTime time;
    
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
    public DateTimePicker(Stage ownerStage, double padding) {
        date = null;
        time = null;

        // Selects date
        datePicker = new DatePicker();
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public LocalDate fromString(String formattedString) {
                try {
                    return LocalDate.from(INPUT_FORMATTER.parse(formattedString));
                } catch (DateTimeParseException parseException) {
                    return null;
                }
            }

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                }
                return INPUT_FORMATTER.format(localDate);
            }
        });

        datePicker.setOnAction((ActionEvent dateSelection) -> {
            System.out.println(datePicker.getValue());
            date = datePicker.getValue();
        });

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

    public static LocalDateTime parse(String dateTimeString) {
        LocalDateTime dateTime;

        if (dateTimeString.length() > DATE_LENGTH) {
            // has date and time
            dateTime = LocalDateTime.parse(dateTimeString, DATE_AND_TIME_FORMATTER);
        } else if (dateTimeString.length() == DATE_LENGTH) {
            // only date, use midnight
            dateTime = LocalDateTime.of(LocalDate.parse(dateTimeString, DATE_FORMATTER),
                    LocalTime.of(0, 0, 0, 0)); // midnight
        } else {
            throw new IllegalArgumentException("Unknown format: " + dateTimeString);
        }

        return dateTime;
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

    public LocalDateTime getDateTime() {
        if (date == null && time == null) {
            return null;    
        } else if (time == null) {
            return LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        }
        
        return LocalDateTime.of(date, time);
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
    public void setDateTime(LocalDateTime dueDate) {
        datePicker.setValue(
            LocalDate.of(dueDate.getYear(), dueDate.getMonth(), dueDate.getDayOfMonth()));

        int hour = dueDate.getHour();
        int minutes = dueDate.getMinute();

        if (hour == 0 && minutes == 0) {
            hourPicker.setValue("");
            minutesPicker.setValue("");
            amPmPicker.setValue("");
        } else {
            String amPm = "AM";
            if (hour == 0) {
                hour = 12;
            } else if (hour > 12) {
                hour -= 12;
                amPm = "PM";
            }

            hourPicker.setValue(Integer.toString(hour));
            minutesPicker.setValue(Integer.toString(minutes));
            amPmPicker.setValue(amPm);
        }
    }
}
