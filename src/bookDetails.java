public class bookDetails {
    private int bookID;
    private String bookTitle;
    private String authorFirstName;
    private String authorLastName;
    private String bookGenre;
    private String bookSubGenre;
    private String readingStatus;
    private String bookSummary;
    private double bookRating;

// initializing everything
    public bookDetails(String bookTitle, String authorFirstName, String authorLastName, String bookGenre, String bookSubGenre, String readingStatus, String bookSummary, double bookRating, int bookID) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
        this.bookGenre = bookGenre;
        this.bookSubGenre = bookSubGenre;
        this.readingStatus = readingStatus;
        this.bookSummary = bookSummary;
        this.bookRating = bookRating;
    }
// the setters and getters
    // setter and gett for book ID
    public int getBookID() {
        return bookID;
    }
    public int setBookID(int bookID) {
        this.bookID = bookID;
        return this.bookID;
    }

    // setter and getter for book title
    public String getBookTitle() {
        return bookTitle;
    }
    public String setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
        return this.bookTitle;
    }

    // setter and getter for author first name
    public String getAuthorFirstName() {
        return authorFirstName;
    }
    public String setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
        return this.authorFirstName;
    }

    // setter and getter for author last name
    public String getAuthorLastName() {
        return authorLastName;
    }
    public String setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
        return this.authorLastName;
    }

    // setter and getter for book genre
    public String getBookGenre() {
        return bookGenre;
    }
    public String setBookGenre(String bookGenre) {
        this.bookGenre = bookGenre;
        return this.bookGenre;
    }

    // setter and getter for book sub genre
    public String getBookSubGenre() {
        return bookSubGenre;
    }
    public String setBookSubGenre(String bookSubGenre) {
        this.bookSubGenre = bookSubGenre;
        return this.bookSubGenre;
    }

    // setter and getter for reading status
    public String getReadingStatus() {
        return readingStatus;
    }
    public String setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
        return this.readingStatus;
    }

    // setter and getter for book summary
    public String getBookSummary() {
        return bookSummary;
    }
    public String setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
        return this.bookSummary;
    }
    // setter and getter for book rating
    public double getBookRating() {
        return bookRating;
    }
    public double setBookRating(double bookRating) {
        this.bookRating = bookRating;
        return this.bookRating;
    }
}

