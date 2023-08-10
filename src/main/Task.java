package main;

import java.io.Serializable;
import java.time.LocalDate;

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
 *   <li>Tasks cannot store null titles or description.</li>
 *   <li>The title cannot be empty.</li>
 * </ul>
 * Attempting either of these will throw an exception.
 * <p>
 * {@code Task} objects have getters and setter but whether the task is completed is also a field.
 */
public class Task implements Serializable {
    private static final String NULL_ERROR_MESSAGE = 
            "Tasks cannot store null titles or description.";
    private static final String EMPTY_TITLE_ERROR_MESSAGE = 
            "Title cannot be blank.";

    private String title;
    private String description;

    /**
     * The due date of this task.
     */
    public LocalDate dueDate; 
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
     * @throws IllegalArgumentException if title or description are null or if title is blank.
     */
    public Task(String title, String description, LocalDate dueDate, boolean isCompleted) {
        if (title == null || description == null) {
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        } else if (title.isBlank()) {
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
     * <p>
    * {@code dueDate} is also an accessible field.
     * @return A LocalDate representing the due date of this task.
     */
    public LocalDate getDueDate() {
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
     * @param dueDate The Calendar used to set the due date.
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Sets whether this task is completed.
     * <p>
     * {@code isCompleted} is also an accessible field.
     * @param isCompleted the value used to set whether this task is completed.
     */
    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
