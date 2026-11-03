import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class digitalLibraryTest {

    private digitalLibrary library;
    private bookDetails dummyBook1;

    @BeforeEach
    void setUp() {
        library = new digitalLibrary();
        library.loadBookShelfFromFile("unitTest.txt");
        library.deleteDigitalLibrary();

        dummyBook1 = new bookDetails("Test Title", "John", "Doe", "Fiction", "Sci-Fi", "To Be Read", "Summary", 4.5, 0);
    }

// -----------------------------------------------------------------------------------------------
    // file opening
    // positive
    @Test
    void testFileCanBeOpenedPositive() {
        assertTrue(library.loadBookShelfFromFile("unitTest.txt"));
    }

    // negative
    @Test
    void testFileCanBeOpenedNegative() {
        // Load an invalid/non-existent file name layout
        library.loadBookShelfFromFile("nonExistentFile.txt");

        // a non-existent file shouldn't load any books into memory
        assertTrue(library.ifShelfIsEmpty(), "A non-existent file layout should leave the book shelf completely empty.");
    }

// -----------------------------------------------------------------------------------------------
    // adding a book
    // positive
    @Test
    void testObjectsCanBeAddedPositive() {
        assertTrue(library.ifShelfIsEmpty());
        library.addNewBook(dummyBook1);
        // verifies adding books
        assertFalse(library.ifShelfIsEmpty());
    }

    // negative
    @Test
    void testObjectsCanBeAddedNegative() {
        // Verify the database is empty before starting
        assertTrue(library.ifShelfIsEmpty());

        // attempting to find a book that was never added to the database layout
        bookDetails missingBook = library.getBookByID(777777);

        // assert message that book does not exist
        assertNull(missingBook, "non-existent book should return null.");
        assertTrue(library.ifShelfIsEmpty(), "shelf should remain completely empty.");
    }

// -----------------------------------------------------------------------------------------------
    // removing a book
    @Test
    void testObjectRemovalPositiveAndNegative() {
        library.addNewBook(dummyBook1);
        int assignedId = dummyBook1.getBookID();

        // successfully removes book
        library.removeBookByID(assignedId);
        assertNull(library.getBookByID(assignedId));

        // trying to remove an invalid or non-existent book ID returns error message/null
        assertNull(library.getBookByID(99999));
    }

    // updating book details
    @Test
    void testObjectUpdatingPositiveAndNegative() {
        library.addNewBook(dummyBook1);
        int assignedId = dummyBook1.getBookID();

        // valid book ID successful
        bookDetails bookToUpdate = library.getBookByID(assignedId);
        assertNotNull(bookToUpdate);
        bookToUpdate.setBookTitle("Updated Title");
        assertEquals("Updated Title", library.getBookByID(assignedId).getBookTitle());

        // cannot enter an invalid book ID
        bookDetails nonExistentBook = library.getBookByID(88888);
        assertNull(nonExistentBook);
    }
}