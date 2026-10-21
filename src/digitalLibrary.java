import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class digitalLibrary {
    private ArrayList<bookDetails> bookShelf;
    private final String digitalShelf = "TRA.txt";

    public digitalLibrary() {
        bookShelf = new ArrayList<bookDetails>();
        // loading book details from shelf (file)
        loadBookShelfFromFile();
    }

    // 5 digit int bookID
    public bookDetails addNewBook(bookDetails book) {
        // only a 5 digit book idea with leading 10000
        int uniqueBookID = 10000 + bookShelf.size();
        book.setBookID(uniqueBookID);
        bookShelf.add(book);
        // saving the new book details list to shelf (file)
        saveBookShelfToFile();
        return book;
    }

    // if book exists using bookId
    public bookDetails getBookByID(int bookID) {
        for (bookDetails book : bookShelf) {
            if (book.getBookID() == bookID)
                // if the book exists
                return book;
        }
        // if the book does NOT exist
        return null;
    }

    // removing books
    public boolean removeBookByID(int bookID) {
        bookDetails removeBook = getBookByID(bookID);
        if (removeBook != null) {
            bookShelf.remove(removeBook);
            // update database when a book is removed
            saveBookShelfToFile();
            return true;
        }
        return false;
    }

    // each line in text file holds the book details information
    public boolean saveBookShelfToFile() {
        try (BufferedWriter writer = new BufferedWriter (new FileWriter(digitalShelf))) {
            for (bookDetails book : bookShelf) {
                writer.write(
                        book.getBookID() + "|" +
                                book.getBookTitle() + "|" +
                                book.getAuthorFirstName() + "|" +
                                book.getAuthorLastName() + "|" +
                                book.getBookGenre() + "|" +
                                book.getBookSubGenre() + "|" +
                                book.getReadingStatus() + "|" +
                                book.getBookSummary() + "|" +
                                book.getBookRating());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("There was an error writing to file.");
            return false;
        }
    }

    // adding a new book to the bookShelf
    public boolean loadBookShelfFromFile() {
        // to avoid the output There was an error writing to file, verify the file exists first then return nothing
        java.io.File file = new java.io.File(digitalShelf);
        // if file NOT exists
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader (new FileReader(digitalShelf))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

            // all book details fields
                if  (fields.length == 9) {
                    int bookID = Integer.parseInt(fields[0]);
                    String bookTitle = fields[1];
                    String authorFirstName = fields[2];
                    String authorLastName = fields[3];
                    String bookGenre = fields[4];
                    String bookSubGenre = fields[5];
                    String readingStatus = fields[6];
                    String bookSummary = fields[7];
                    double bookRating = Double.parseDouble(fields[8]);

                    bookDetails savedBook = new bookDetails(bookTitle, authorFirstName, authorLastName, bookGenre, bookSubGenre, readingStatus, bookSummary, bookRating, bookID);
                    bookShelf.add(savedBook);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("There was an error reading file.");
            return false;
        }
    }

    // if shelf is empty
    public boolean ifShelfIsEmpty() {
        return bookShelf.isEmpty();
    }

    // viewing full library
    public int viewDigitalLibrary() {
        if (bookShelf.isEmpty()) {
            System.out.println("There are no books currently in the library.");
            return 0;
        }
        // will print out the book details like the title an author's name
        int count = 0;
        for (bookDetails book : bookShelf) {
            System.out.println("Book ID: " + book.getBookID() + ", " + book.getBookTitle() +
                    ", written by " + book.getAuthorFirstName() + " " + book.getAuthorLastName() +
                    " is a " + book.getBookGenre() + "/"+ book.getBookSubGenre() + " novel" +
                    ", it has a book rating of " + book.getBookRating() + "/5 Stars" +
                    ", and the book summary:\n" + book.getBookSummary() +
                    "the reading status is: " + book.getReadingStatus() + "\n");
            count++;
        }
        return count;
    }

    // sorting by author's last name
    public int sortLibraryByAuthorLastName() {
        if (bookShelf.size() > 1) {
            bookShelf.sort((book1, book2) -> {
                int compareAuthorLastName = book1.getAuthorLastName().compareToIgnoreCase(book2.getAuthorLastName());
                // Fallback to last name if first names are identical
                if (compareAuthorLastName == 0) {
                    return book1.getAuthorFirstName().compareToIgnoreCase(book2.getAuthorFirstName());
                }
                return compareAuthorLastName;
            });
            // saving the sort order to the file
            saveBookShelfToFile();
        }
        return bookShelf.size();
    }
}
