import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Francine Williams
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * libraryDatabase.java
 * Phase 4 of the DMS project.
 * Instead of reading and writing .txt files, the libraries and books are kept in the theReadersArchive MySQL database built with DataGrip.
 * Every change to the book or library is saved to the database.
 */

public class libraryDatabase {
    private static final String databaseName = "theReadersArchive";
    private static final String bookColumns = "bookID, bookTitle, authorFirstName, authorLastName, bookGenre, bookSubGenre, " +
            "readingStatus, bookSummary, bookRating";

    private Connection connection;
    private int activeLibraryID = -1;
    private String activeLibraryName = "";
    private boolean sortedByAuthor = false;

    /*
     * method: connect
     * parameters: String serverAddress, String username, String password
     * return: VOID
     * purpose: connects to theReadersArchive database on the server the user entered
     */

    public void connect(String serverAddress, String username, String password) throws SQLException {
        String databaseURL = "jdbc:mysql://" + serverAddress + "/" + databaseName;
        Connection newConnection = DriverManager.getConnection(databaseURL, username, password);
        disconnect();
        connection = newConnection;
        closeLibrary();
    }

    /*
     * method: disconnect
     * parameters: NONE
     * return: VOID
     * purpose: closes connection to database when program closes
     */

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // there is nothing else to do since the program is closing
            }
            connection = null;
        }
    }

    /*
     * method: isDatabaseConnected
     * parameters: NONE
     * return: boolean
     * purpose: return true if there is a connection to the database
     */

    public boolean isDatabaseConnected() {
        try  {
            return connection != null && !connection.isClosed();
        } catch (SQLException ex) {
            return false;
        }
    }

    /*
     * method: getLibraryNames
     * parameters: NONE
     * return: ArrayList<String>
     * purpose: returns the name of EVERY library in the database
     */

    public ArrayList<String> getLibraryNames() throws SQLException {
        ArrayList<String> libraryNames = new ArrayList<>();

        try (PreparedStatement query = connection.prepareStatement("SELECT libraryName FROM digitalLibraries ORDER BY libraryName")) {
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                libraryNames.add(resultSet.getString("libraryName"));
            }
        }
        return libraryNames;
    }

    /*
     * method: openLibrary
     * parameters: String libraryName
     * return: boolean
     * purpose: opens the library with this name OR creates a new one if it does not exist
     *          returns TRUE if a new library was created
     */

    public boolean openLibrary(String libraryName) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT * FROM digitalLibraries WHERE libraryName = ?")) {
            query.setString(1, libraryName);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()) {
                setActiveLibrary(resultSet.getInt("libraryID"), resultSet.getString("libraryName"));
                return false;
            }
        }

        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO digitalLibraries (libraryName) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, libraryName);
            insert.executeUpdate();
            ResultSet newKeys = insert.getGeneratedKeys();
            newKeys.next();
            setActiveLibrary(newKeys.getInt(1), libraryName);
            return true;
        }
    }

    /*
     * method: deleteActiveLibrary
     * parameters: NONE
     * return: VOID
     * purpose: deletes the active library and its books
     */

    public void deleteActiveLibrary() throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM digitalLibraries WHERE libraryID = ?")) {
            delete.setInt(1, activeLibraryID);
            delete.executeUpdate();
        }
        closeLibrary();
    }

    /*
     * method: getBookShelf
     * parameters: NONE
     * return: ArrayList<bookDetails>
     * purpose: returns every single book in the opened library in the order they were added
     *          OR by the author's last name AFTER the library has been sorted
     */

    public ArrayList<bookDetails> getBookShelf() throws SQLException {
        ArrayList<bookDetails> bookShelf = new ArrayList<>();
        String bookOrder = sortedByAuthor ? "authorLastName, authorFirstName" : "orderAsAdded";

        try (PreparedStatement query = connection.prepareStatement("SELECT "+ bookColumns + " FROM books WHERE libraryID = ? ORDER BY " + bookOrder)) {
            query.setInt(1, activeLibraryID);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                bookShelf.add(readBook(resultSet));
            }
        }
        return bookShelf;
    }

    /*
     * method: getBookByID
     * parameters: int bookID
     * return: bookDetails
     * purpose: returns the book with its ID from the opened library
     *          OR NULL if there is not an ID
     */

    public bookDetails getBookByID(int bookID) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT " + bookColumns + " FROM books WHERE bookID = ? AND libraryID = ?")) {
            query.setInt(1, bookID);
            query.setInt(2, activeLibraryID);
            ResultSet resultSet = query.executeQuery();
            return resultSet.next() ? readBook(resultSet) : null;
        }
    }

    /*
     * method: addNewBook
     * parameters: bookDetails book
     * return: bookDetails
     * purpose: the book is given a random 5-digit ID and saves it to the opened library
     *          returns the book with its NEW ID
     */

    public bookDetails addNewBook(bookDetails book) throws SQLException {
        int uniqueID;
        do {
            uniqueID = ThreadLocalRandom.current().nextInt(10000, 100000);
        } while (bookIDExists(uniqueID));
        book.setBookID(uniqueID);

        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO books (" + bookColumns + ", libraryID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            insert.setInt(1, book.getBookID());
            insert.setString(2, book.getBookTitle());
            insert.setString(3, book.getAuthorFirstName());
            insert.setString(4, book.getAuthorLastName());
            insert.setString(5, book.getBookGenre());
            insert.setString(6, book.getBookSubGenre());
            insert.setString(7, book.getReadingStatus());
            insert.setString(8, book.getBookSummary());
            insert.setDouble(9, book.getBookRating());
            insert.setInt(10, activeLibraryID);
            insert.executeUpdate();
        }
        return book;
    }

    /*
     * method: updateBook
     * parameters: bookDetails book
     * return: VOID
     * purpose: saves the edited book details that is already in the database
     */

    public void updateBook(bookDetails book) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE books SET bookTitle = ?, authorFirstName = ?, authorLastName = ?, bookGenre = ?, "
                        + "bookSubGenre = ?, readingStatus = ?, bookSummary = ?, bookRating = ? "
                        + "WHERE bookID = ? AND libraryID = ?")) {

            update.setString(1, book.getBookTitle());
            update.setString(2, book.getAuthorFirstName());
            update.setString(3, book.getAuthorLastName());
            update.setString(4, book.getBookGenre());
            update.setString(5, book.getBookSubGenre());
            update.setString(6, book.getReadingStatus());
            update.setString(7, book.getBookSummary());
            update.setDouble(8, book.getBookRating());
            update.setInt(9, book.getBookID());
            update.setInt(10, activeLibraryID);
            update.executeUpdate();
        }
    }

    /*
     * method: removeBookByID
     * parameters: int bookID
     * return: boolean
     * purpose: deletes the book with this book ID from the opened library,
     *          returns TRUE if it was removed
     */

    public boolean removeBookByID(int bookID) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("DELETE FROM books WHERE bookID = ? AND libraryID = ?")) {
            delete.setInt(1, bookID);
            delete.setInt(2, activeLibraryID);
            return delete.executeUpdate() > 0;
        }
    }

    /*
     * method: sortLibraryByAuthorLastName
     * parameters: NONE
     * return: VOID
     * purpose: sorting custom feature, sorts the table by author's last name A-Z
     */

    public void sortLibraryByAuthorLastName() {
        sortedByAuthor = true;
    }

    /*
     * method: isLibraryOpen
     * parameters: NONE
     * return: boolean
     * purpose: returns TRUE if a library is currently opened
     */

    public boolean isLibraryOpen() {
        return activeLibraryID != -1;
    }

    /*
     * method: getActiveLibraryName
     * parameters: NONE
     * return: String
     * purpose: returns the opened library's name the way it was first created
     *          example: My Cozy Library was created first, typed my cozy library returns My Cozy Library to Current Library text
     */

    public String getActiveLibraryName() {
        return activeLibraryName;
    }

    /*
     * method: setActiveLibrary
     * parameters: int libraryID, String libraryName
     * return: VOID
     * purpose: remembers which library is opened and its books are sorted in the order they were added
     */

    private void setActiveLibrary(int libraryID, String libraryName) {
        activeLibraryID = libraryID;
        activeLibraryName = libraryName;
        sortedByAuthor = false;
    }

    /*
     * method: closeLibrary
     * parameters: NONE
     * return: VOID
     * purpose: "forgets" the opened library after it is deleted OR when connecting to a database
     */

    private void closeLibrary() {
        setActiveLibrary(-1, "");
    }

    /*
     * method: bookIDExists
     * parameters: int bookID
     * return: boolean
     * purpose: returns TRUE IF any book in the database already used this ID
     */

    private boolean bookIDExists(int bookID) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("SELECT 1 FROM books WHERE bookID = ?")) {
            query.setInt(1, bookID);
            return query.executeQuery().next();
        }
    }

    /*
     * method: readBook
     * parameters: ResultSet resultSet
     * return: bookDetails
     * purpose: changes current row of a query into a bookDetails object
     */

    private bookDetails readBook(ResultSet resultSet) throws SQLException {
        return new bookDetails(
                resultSet.getString("bookTitle"),
                resultSet.getString("authorFirstName"),
                resultSet.getString("authorLastName"),
                resultSet.getString("bookGenre"),
                resultSet.getString("bookSubGenre"),
                resultSet.getString("readingStatus"),
                resultSet.getString("bookSummary"),
                resultSet.getDouble("bookRating"),
                resultSet.getInt("bookID")
        );
    }


}
