import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class digitalLibrary {
    private String activeLibraryFile = "";
    private ArrayList<bookDetails> bookShelf;

    public digitalLibrary() {
        bookShelf = new ArrayList<bookDetails>();
    }

    public int displayLibraries() {
        // finding and listing digital library files using universal user.dir and searching user's directory
        String digitalLibraryPath = System.getProperty("user.dir");
        java.io.File dir = new java.io.File(System.getProperty("user.dir"));
        java.io.File[] existingFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (existingFiles ==  null || existingFiles.length == 0) {
            System.out.println("\nNo libraries found");
            return 0;
        }
        // listing all digital library archives for user to view when loading or creating a new digital library through main menu option 1
        System.out.println("\n-*-*- Current Digital Library Archives -*-*-");
        int count = 1;
        for (java.io.File file : existingFiles) {
            String displayName = file.getName().replace(".txt", "");

            if (file.getName().equals(this.activeLibraryFile) || file.getAbsolutePath().equals(this.activeLibraryFile)) {
                System.out.println("[" + count + "]" + " ⊹ " + displayName + " (Active Collection) ✦");
            } else {
                System.out.println("[" + count + "]" + " ⊹ " + displayName);
            }
            count++;
        }
            return existingFiles.length;
        }

    // 5 digit int bookID, with a random number instead of leading with 10000
    public bookDetails addNewBook(bookDetails book) {
        int uniqueID = 0;
        boolean isDuplicateID;

        do {
            uniqueID = ThreadLocalRandom.current().nextInt(10000, 100000);
            isDuplicateID = false;
            for (bookDetails currentBook : bookShelf) {
                if (currentBook.getBookID() == uniqueID) {
                    isDuplicateID = true;
                    break;
                }
            }
        } while (isDuplicateID);
        book.setBookID(uniqueID);
        bookShelf.add(book);
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

        if (activeLibraryFile == null || activeLibraryFile.isEmpty()) {
            System.out.println("There is no active library file selected!\n");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter (new FileWriter(activeLibraryFile))) {
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
    public boolean loadBookShelfFromFile(String fileName) {
        this.bookShelf.clear();
        this.activeLibraryFile = fileName;

        // to avoid the output There was an error writing to file, verify the file exists first then return nothing
        java.io.File file = new java.io.File(activeLibraryFile);

        if (!file.exists()) {
            return true;
        }

        try (BufferedReader reader = new BufferedReader (new FileReader(activeLibraryFile))) {
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
            System.out.println("⊹ Book ID: " + book.getBookID() +
                    "\n⊹ Title: " + book.getBookTitle() +
                    "\n⊹ Written by: " + book.getAuthorFirstName() + " " + book.getAuthorLastName() +
                    "\n⊹ Genre and Subgenre:" + book.getBookGenre() + ", "+ book.getBookSubGenre() +
                    "\n⊹ Star Rating: " + book.getBookRating() + " / 5 Stars" +
                    "\n⊹ Reading Status: " + book.getReadingStatus() +
                    "\n⊹ Summary: " + book.getBookSummary() + "\n");
            count++;
        }
        return count;
    }

    // sorting by author's last name
    public int sortLibraryByAuthorLastName() {
        if (bookShelf.size() > 1) {
            bookShelf.sort((book1, book2) -> {
                int compareAuthorLastName = book1.getAuthorLastName().compareToIgnoreCase(book2.getAuthorLastName());

                // fallback to sorting books by author first name if their last names are identical
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

    // deleting digital library
    public void deleteDigitalLibrary() {
        this.bookShelf.clear();
    }
    // which library is active
    public String getActiveLibraryFile() {
        return this.activeLibraryFile;
    }
    // reset active library
    public void resetActiveLibrary() {
        this.activeLibraryFile = "";
    }
}
