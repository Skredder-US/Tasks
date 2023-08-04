package main;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * This class handles user input for the task manager via the console/terminal. 
 * <p>
 * I'm keeping this class just in case, the UI will handle user input.
 */
@Deprecated
public class TaskManagerConsole {
    public static void main(String[] args) {
        System.out.println("Task Manager allows you to create, update, and track tasks.");

        Scanner console = new Scanner(System.in);
        String[] commands = {"exit"};
        String userCommand = getUserCommand(console, commands);

        // do something with userCommand
    }

    /**
     * Returns the valid command inputted by the user via the console/terminal.
     * @param console {@code Scanner} on user input stream.
     * @param commands Array of available commands.
     * @return The valid command inputted by the user.
     */
    private static String getUserCommand(Scanner inputScanner, String[] commands) {
        String userInput = getUserInput(inputScanner, commands);

        Stream<String> commandsStream = Arrays.stream(commands);
        while (!commandsStream.anyMatch(userInput::equals)) {
            System.out.println("Unknown command.");
            System.out.println();
            userInput = getUserInput(inputScanner, commands);
        }

        return userInput; // matches a command
    }

    /**
     * Lists available commands then prompts the user for input then returns this user input 
     * (trimmed). There is no guarentee the user input will be an available command.
     * @param console {@code Scanner} on user input stream.
     * @param commands Array of available commands.
     * @return Trimmed user input via specified {@code Scanner}.
     */
    private static String getUserInput(Scanner console, String[] commands) {
        System.out.print("Commands: ");

        if (commands.length > 0) {
            System.out.print(commands[0]);
        }
        for (int i = 1; i < commands.length; i++) {
            System.out.print(", " + commands[i]);
        }

        System.out.println();
        System.out.print("Input: ");

        return console.nextLine().trim();
    }
}
