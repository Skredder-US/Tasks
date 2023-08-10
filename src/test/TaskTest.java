package test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import main.Task;
import org.junit.Test;

/**
 * Unit tests for the {@code Task} class. 
 * <p>
 * Many tests implicitly test the getter methods.
 */
public class TaskTest {
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final LocalDate DUE_DATE = LocalDate.now();
    private static final boolean IS_COMPLETED = false;

    @Test (expected = IllegalArgumentException.class)
    public void constructorNullTitle() {
        new Task(null, DESCRIPTION, DUE_DATE, IS_COMPLETED);
    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorNullDescription() {
        new Task(TITLE, null, DUE_DATE, IS_COMPLETED);
    }

    public void constructorNullDueDate() {
        Task task = new Task(TITLE, DESCRIPTION, null, IS_COMPLETED);
        assertEquals(null, task.dueDate);

    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorEmptyTitle() {
        new Task("", DESCRIPTION, DUE_DATE, IS_COMPLETED);
    }

    @Test
    public void constructorTitle() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        assertEquals(TITLE, task.getTitle());
    }

    @Test
    public void constructorDescription() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        assertEquals(DESCRIPTION, task.getDescription());
    }

    @Test
    public void constructorDueDate() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        assertEquals(DUE_DATE, task.dueDate);
    }

    @Test
    public void constructorIsCompleted() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        assertEquals(IS_COMPLETED, task.getIsCompleted());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setTitleNull() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        task.setTitle(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setTitleEmpty() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        task.setTitle("");
    }

    @Test
    public void setTitle() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        
        String newTitle = "new title";
        task.setTitle(newTitle);

        assertEquals(newTitle, task.getTitle());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setDescriptionNull() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        task.setDescription(null);
    }

    @Test
    public void setDescription() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);

        String newDescription = "new description";
        task.setDescription(newDescription);

        assertEquals(newDescription, task.getDescription());
    }
    
    @Test
    public void setDescriptionEmpty() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);

        String emptyDescription = "";
        task.setDescription(emptyDescription);

        assertEquals(emptyDescription, task.getDescription());
    }

    public void setDueDateNull() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        task.setDueDate(null);

        assertEquals(null, task.dueDate);
    }

    @Test
    public void setDueDate() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);

        LocalDate newDueDate = LocalDate.now(); // diff time
        task.setDueDate(newDueDate);

        assertEquals(newDueDate, task.dueDate);
    }

    @Test
    public void setIsCompleted() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, false);
        task.setIsCompleted(true);
        assert(task.isCompleted);
    }
}
