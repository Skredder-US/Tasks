package main;

import java.io.Serializable;
import java.util.Calendar;

/**
 * The {@code Task} class represents a single task, e.g. for a todo list. 
 * <p>
 * Tasks are represented with the following:
 * <ul>
 *   <li>Title</li> 
 *   <li>Description</li>
 *   <li>Due Date</li>
 *   <li>Whether the task is completed</li>
 * </ul>
 * 
 * Tasks have the following rules:
 * <ul>
 *   <li>Tasks cannot store null data.</li>
 *   <li>The title cannot be empty.</li>
 * </ul>
 * Attempting either of these will throw an exception.
 * <p>
 * {@code Task} objects have getters and setter but whether the task is completed is also a field.
 */
public class Task implements Comparable<Task>, Serializable {
    private static final String NULL_ERROR_MESSAGE = 
            "Tasks cannot store null data.";
    private static final String EMPTY_TITLE_ERROR_MESSAGE = 
            "Title cannot be empty.";

    private String title;
    private String description;
    private Calendar dueDate;
    
    /**
     * Indicates whether this task is completed.
     */
    public boolean isCompleted;

    /**
     * Initializes a newly created {@code Task} object so it represents the task with the 
     * specified information.
     * 
     * @param title title of the task.
     * @param description description of the task.
     * @param dueDate when the task is due.
     * @param isCompleted whether the task is completed.
     * 
     * @throws IllegalArgumentException if any parameter is null or if the specified title 
     * {@code String} is empty.
     */
    public Task(String title, String description, Calendar dueDate, boolean isCompleted) {
        if (title == null || description == null || dueDate == null) {
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        } else if (title.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_TITLE_ERROR_MESSAGE);
        }

        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
    }

    /**
     * Gets the title of this task.
     * @return A String representing the title of this task.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description of this task.
     * @return A String representing the description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the due date of this task.
     * @return A Calendar representing the due date of this task.
     */
    public Calendar getDueDate() {
        return dueDate;
    }

    /**
     * Gets whether this task is completed.
     * <p>
    * {@code isCompleted} is also an accessible field.
     * @return A boolean representing whether this task is completed.
     */
    public boolean getIsCompleted() {
        return isCompleted;
    }

    /**
     * Sets the title of this task.
     * @param title The value used to set the title.
     * 
     * @throws IllegalArgumentException if the specified {@code String} is null or empty.
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        } else if (title.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_TITLE_ERROR_MESSAGE);
        }

        this.title = title;
    }

    /**
     * Sets the description of this task.
     * @param description The value used to set the description.
     * 
     * @throws IllegalArgumentException if the specified {@code String} is null.
     */
    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        }

        this.description = description;
    }

    /**
     * Sets the due date of this task.
     * @param title The Calendar used to set the due date.
     * 
     * @throws IllegalArgumentException if the specified {@code Calendar} is null.
     */
    public void setDueDate(Calendar dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        }

        this.dueDate = dueDate;
    }

    /**
     * Set whether this task is completed.
     * <p>
     * {@code isCompleted} is also an accessible field.
     * @param isCompleted the value used to set whether this task is completed.
     */
    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * Converts this {@code Task} object into a {@code String} of the form:
     * <p>
     * {@snippet :
     * Title: t
     * Description: d
     * Due Date: dd
     * Is Completed: ic
     * }
     * Where:
     * <ul>
     *   <li>{@code t} is the title of this task.
     *   <li>{@code d} is the description of this task.
     *   <li>{@code dd} is the due date of this task.
     *   <li>{@code ic} is true when this task is completed, false otherwise.
     * </ul>
     */
    @Override
    public String toString() {
        return "Title: " + title + 
                "\nDescription: " + description + 
                "\nDue Date: " + dueDate.getTime() + 
                "\nIs Completed: " + isCompleted + "\n";
    }

    /**
     * Compares the due dates of the two {@code Task} objects. When both due dates are equal,
     * compares their titles lexicographically. 
     * @param anotherTask the {@code Task} to be compared
     * @return the value 0 if the due dates and titles are equal; a value less than 0 if the due 
     * date of this {@code Task} is before the argument's or if the due dates are equal and the 
     * title is lexicographically less than the argument's; and a value greater than 0 if the due 
     * date of this {@code Task} is after the due date the argument's or if the due dates are equal  
     * and the title is lexicographically greater than the argument's.
     */
    @Override
    public int compareTo(Task anotherTask) {
        int dueDateComparison = dueDate.compareTo(anotherTask.dueDate);
        if (dueDateComparison == 0) {
            return title.compareTo(anotherTask.title);
        }
        return dueDateComparison;
    }
}
