import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class libraryMenuTest {

    //  custom action and star ratings
    @Test
    void testStarSymbolsPositive() {
        libraryMenu menu = new libraryMenu();

        // valid numbers/raiting converts into star symbols
        assertTrue(menu.starSymbols(5.0).contains("★★★★★"));

        // no book rating case: a 0.0 rating outputs a message
        assertEquals("There was no book rating provided.", menu.starSymbols(0.0));
    }

    @Test
    void testRatingOverFiveNegative() {
        // user tries to enter star rating 10 out of 5
        String simulatedInputs = "1\ntest.txt\n2\nFake Title\nFirst\nLast\nGenre\nSubGenre\n1\nSummary\n10\n\n8\n";
        System.setIn(new ByteArrayInputStream(simulatedInputs.getBytes()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        libraryMenu.main(new String[]{});

        String consoleOutput = outputStream.toString();

        // rating over 10 is caught and message outputs prompting user to try again
        assertTrue(consoleOutput.contains("Please enter a rating from 0 stars to 5 stars."));
    }

    // user must start with menu option 1 to interact with menu unless they are exiting
    @Test
    void testMainMenuGuardRailsNegative() {
        // besides exiting (option 8) user must load a library before using main menu
        System.setIn(new ByteArrayInputStream("2\n8\n".getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        libraryMenu.main(new String[]{});

        // verifying user error and directs to choose option 1 before using book options
        assertTrue(outputStream.toString().contains("Please select menu option [1] to load a digital library"));
    }


    @Test
    void testMenuGuardRailInvalidInputNegative() {

        System.setIn(new ByteArrayInputStream("abc\n8\n".getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        libraryMenu.main(new String[]{});

        String consoleOutput = outputStream.toString();

        // Asserts that the menu's guard rail catches the non-integer text and warns the user
        assertTrue(consoleOutput.contains("Please enter a valid option from the menu."));
    }
}