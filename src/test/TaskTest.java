package test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
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
    private static final Calendar DUE_DATE = Calendar.getInstance(); // current time
    private static final boolean IS_COMPLETED = false;

    @Test (expected = IllegalArgumentException.class)
    public void constructorNullTitle() {
        new Task(null, DESCRIPTION, DUE_DATE, IS_COMPLETED);
    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorNullDescription() {
        new Task(TITLE, null, DUE_DATE, IS_COMPLETED);
    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorNullDueDate() {
        new Task(TITLE, DESCRIPTION, null, IS_COMPLETED);
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
        assertEquals(DUE_DATE, task.getDueDate());
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

    @Test (expected = IllegalArgumentException.class)
    public void setDueDateNull() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        task.setDueDate(null);
    }

    @Test
    public void setDueDate() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);

        Calendar newDueDate = Calendar.getInstance(); // diff time
        task.setDueDate(newDueDate);

        assertEquals(newDueDate, task.getDueDate());
    }

    @Test
    public void setIsCompleted() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, false);
        task.setIsCompleted(true);
        assert(task.isCompleted);
    }

    @Test 
    public void toStringTest() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);

        String expected = "Title: " + TITLE +
                "\nDescription: " + DESCRIPTION +
                "\nDue Date: " + DUE_DATE.getTime() +
                "\nIs Completed: " + IS_COMPLETED + "\n";
        assertEquals(expected, task.toString());
    }

    @Test
    public void compareToLess() {
        Task taskBefore = new Task(TITLE, DESCRIPTION, Calendar.getInstance(), IS_COMPLETED);
        
        Calendar dueDate = Calendar.getInstance(); 
        dueDate.add(Calendar.SECOND, 1);
        Task taskAfter = new Task(TITLE, DESCRIPTION, dueDate, IS_COMPLETED);
        
        assert(taskBefore.compareTo(taskAfter) < 0);
    }

    @Test
    public void compareToMore() {
        Task taskBefore = new Task(TITLE, DESCRIPTION, Calendar.getInstance(), IS_COMPLETED);
        
        Calendar dueDate = Calendar.getInstance(); 
        dueDate.add(Calendar.SECOND, 1);
        Task taskAfter = new Task(TITLE, DESCRIPTION, dueDate, IS_COMPLETED);

        assert(taskAfter.compareTo(taskBefore) > 0);
    }

    @Test
    public void compareToTitleLess() {
        Task task = new Task(TITLE, DESCRIPTION, Calendar.getInstance(), IS_COMPLETED);
        Task taskTitleGreater = new Task(TITLE + 'a', DESCRIPTION, Calendar.getInstance(),
                IS_COMPLETED);

        assert(task.compareTo(taskTitleGreater) < 0);
    }

    @Test
    public void compareToTitleMore() {
        Task task = new Task(TITLE, DESCRIPTION, Calendar.getInstance(), IS_COMPLETED);
        Task taskTitleGreater = new Task(TITLE + 'a', DESCRIPTION, Calendar.getInstance(),
                IS_COMPLETED);

        assert(taskTitleGreater.compareTo(task) > 0);
    }

    @Test
    public void compareToEqual() {
        Task task = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);
        Task taskSame = new Task(TITLE, DESCRIPTION, DUE_DATE, IS_COMPLETED);

        assert(task.compareTo(taskSame) == 0);
    }
}
