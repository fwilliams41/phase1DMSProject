import javax.management.InstanceNotFoundException;
import java.net.UnknownServiceException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

public class libraryMenu {

    public String displayMenu() {
        System.out.println("//".repeat(17));
        System.out.println(flair.BOLD_PINK + "Welcome to The Reader's Archive!" + flair.RESET);
        System.out.println("Please select an option below:");
        System.out.println(flair.BOLD_CYAN + "[1]. " + flair.RESET + "Create a new digital library or load a digital library.");
        System.out.println(flair.BOLD_CYAN + "[2]. " + flair.RESET + "Add a book.");
        System.out.println(flair.BOLD_CYAN + "[3]. " + flair.RESET + "Update book details.");
        System.out.println(flair.BOLD_CYAN + "[4]. " + flair.RESET + "Remove a book.");
        System.out.println(flair.BOLD_CYAN + "[5]. " + flair.RESET + "View digital library.");
        System.out.println(flair.BOLD_CYAN + "[6]. " + flair.RESET + "Sort digital library.");
        System.out.println(flair.BOLD_CYAN + "[7]. " + flair.RESET + "Delete digital library.");
        System.out.println(flair.BOLD_CYAN + "[8]. " + flair.RESET + "Exit.");
        return (flair.BOLD_PINK + "The Reader's Archive Menu has been displayed!" + flair.RESET);
    }

    public static void main(String[] args) {
        libraryMenu menu = new libraryMenu();
        // links to digitalLibrary class to view the digital library
        digitalLibrary library = new digitalLibrary();

// scanner for user input
        Scanner userInput = new Scanner(System.in);
        boolean mainMenuLoop = true;

        while (mainMenuLoop) {
            menu.displayMenu();
            System.out.print(flair.BOLD_PINK + "What would you like to do?" + flair.RESET + " : ");

            if (!userInput.hasNextInt()) {
                System.out.println(flair.BOLD_RED + "Please enter a valid option from the menu." + flair.RESET);
                userInput.nextLine();
                continue;
            }

            int userDecision = userInput.nextInt();
            userInput.nextLine();

            if (userDecision >= 2 && userDecision <= 7 && library.getActiveLibraryFile().isBlank()) {
                System.out.println(flair.BOLD_RED + "\nPlease select menu option [1] to load a digital library or create a new one!\n" + flair.RESET);
                continue;
            }

            switch (userDecision) {
                // main menu option 1 creating a new digital library OR loading a digital library
                case 1:
                    System.out.print("\n-*-*- Create a New Digital Library or Load a Digital Library -*-*-\n");
                    int totalLibraries = library.displayLibraries();

                    System.out.print("\nPlease enter the name of the digital library: ");
                    String libraryName = userInput.nextLine().trim();

                    if (libraryName.isBlank()) {
                        System.out.println(flair.BOLD_RED + "Please enter a valid library name.");
                        break;
                    }

                    if (!libraryName.endsWith(".txt")) {
                        libraryName = libraryName + ".txt";
                    }

                    // validating user input if the digital library already exists
                    String checkLibraryName = libraryName;
                    if (!checkLibraryName.endsWith(".txt")) {
                        checkLibraryName = checkLibraryName + ".txt";
                    }

                    java.io.File dir = new java.io.File(System.getProperty("user.dir"));
                    java.io.File[] existingFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));

                    boolean duplicateFound = false;
                    String matchedExistingName = "";

                    if (existingFiles != null) {
                        for (java.io.File file : existingFiles) {
                            if (file.getName().equalsIgnoreCase(checkLibraryName)) {
                                duplicateFound = true;
                                matchedExistingName = file.getName();
                                break;
                            }
                        }
                    }

                    // if it already exists simply output the file name and load it
                    if (duplicateFound) {
                        // match file name to user input
                        libraryName = matchedExistingName;
                        library.loadBookShelfFromFile(libraryName);
                        System.out.println("\nDigital library exists, loading " + flair.BOLD_PINK + libraryName.replace(".txt", "") + flair.RESET + "...\n");
                        break;
                    }

                    // if an only if there is a new digital library being created
                    boolean digitalLibraryExists;
                    if (libraryName.endsWith(".txt")) {
                        digitalLibraryExists = library.loadBookShelfFromFile(libraryName);
                    } else {
                        digitalLibraryExists = library.loadBookShelfFromFile(libraryName + ".txt");
                    }

                    // initializing new digital library file
                    try {
                        String trueFileName = libraryName.endsWith(".txt") ? libraryName : libraryName + ".txt";
                        java.io.File blankFile = new java.io.File(System.getProperty("user.dir"), trueFileName);
                        if (!blankFile.exists()) {
                            blankFile.createNewFile();
                        }
                    } catch (IOException e) {
                        System.out.println("System warning: File initialization failed.");
                    }
                    System.out.println("This new digital library " + flair.BOLD_PINK + libraryName.replace(".txt", "") + flair.RESET + " has been created. Your digital library is ready to be used!\n");

                    if (!libraryName.endsWith(".txt")) {
                        libraryName = libraryName + ".txt";
                    }
                    // end of case 1
                    break;

                // main menu option 2 adding a book
                case 2:
                    System.out.println("\n-*-*- Adding a book to your digital library -*-*-");

                    // getting the user input book title
                    System.out.println("Please enter the book title: ");
                    String bookTitle = userInput.nextLine();

                    // getting the user input author first name
                    System.out.println("Please enter the author's first name: ");
                    String authorFirstName = userInput.nextLine();

                    // getting the user input author last name
                    System.out.println("Please enter the author's last name: ");
                    String authorLastName = userInput.nextLine();

                    // getting the user input book genre.
                    System.out.println("Please enter the book genre: ");
                    String bookGenre = userInput.nextLine();

                    // getting the user input book sub-genre
                    System.out.println("Please enter the book sub-genre: ");
                    String bookSubGenre = userInput.nextLine();

                    // getting the user input reading status, instead of typing the word, make it a menu option choosing from 1-4 int
                    int readingStatusChoice = 0;
                    boolean validReadingStatus = false;

                    while (!validReadingStatus) {
                        System.out.println(flair.BOLD_CYAN + "[1]. " + flair.RESET + "To Be Read.");
                        System.out.println(flair.BOLD_CYAN + "[2]. " + flair.RESET + "Currently Reading.");
                        System.out.println(flair.BOLD_CYAN + "[3]. " + flair.RESET + "Did Not Finish.");
                        System.out.println(flair.BOLD_CYAN + "[4]. " + flair.RESET + "Finished Reading.");
                        System.out.println("Please choose the book's reading status [1-4]: ");

                        try {
                            readingStatusChoice = userInput.nextInt();
                            userInput.nextLine();

                            if (readingStatusChoice >= 1 && readingStatusChoice <= 4) {
                                validReadingStatus = true;
                            } else {
                                System.out.println(flair.BOLD_RED + "Please enter a valid option." + flair.RESET);
                            }
                        } catch (InputMismatchException e) {
                            System.out.println(flair.BOLD_RED + "Please enter a valid option." + flair.RESET);
                            userInput.nextLine();
                        }
                    }
                    String readingStatus = "";
                    switch (readingStatusChoice) {
                        case 1:
                            readingStatus = "To Be Read";
                            break;
                        case 2:
                            readingStatus = "Currently Reading";
                            break;
                        case 3:
                            readingStatus = "Did Not Finish";
                            break;
                        case 4:
                            readingStatus = "Finished Reading";
                            break;
                    }

                    // getting optional user input book summary
                    System.out.println("Please enter the book summary (optional): ");
                    String bookSummary = userInput.nextLine();

                    if (bookSummary.isBlank()) {
                        bookSummary = "There was no book summary provided.";
                    }

                    // getting optional user input book rating
                    double bookRating = 0.0;
                    boolean validBookRating = false;

                    while (!validBookRating) {
                        System.out.println("How would you rate the book out of 5 stars? (optional): ");
                        String optionalBookRating = userInput.nextLine();

                        if (optionalBookRating.isBlank()) {
                            bookRating = 0.0;
                            validBookRating = true;

                        } else {
                            try {
                                double rating = Double.parseDouble(optionalBookRating);
                                if (rating >= 0.0 && rating <= 5.0) {
                                    bookRating = rating;
                                    validBookRating = true;

                                } else {
                                    System.out.println("Please enter a rating from 0 stars to 5 stars.");
                                }
                            } catch (Exception e) {
                                System.out.println(flair.BOLD_RED + "Please enter a valid book rating." + flair.RESET);
                            }
                        }
                    }
                    // putting book details in order
                    bookDetails newBook = new bookDetails(bookTitle, authorFirstName, authorLastName, bookGenre, bookSubGenre, readingStatus, bookSummary, bookRating, 0);
                    // then adding the new book to the digital library database
                    library.addNewBook(newBook);

                    // output the book details
                    System.out.println("\nThe book, " + bookTitle + " written by " + authorFirstName + " " + authorLastName + " has been added to the library.\n"
                            + "Here are the book details:"
                            + "\n⊹ Reading Status: " + readingStatus
                            + "\n⊹ Book Summary: " + bookSummary
                            + "\n⊹ Book Rating: " + menu.starSymbols(bookRating) + "\n"
                    );
                    // end of case 2
                    break;

                // main menu option 3 updating book details
                case 3:
                    System.out.println("\n-*-*- Update Book Details -*-*-");
                    if (library.ifShelfIsEmpty()) {
                        System.out.println(flair.BOLD_RED + "There are no books currently in the library.\n" + flair.RESET);
                        break;
                    }
                    // list the books in the digital library so the user can see what they currently have
                    library.viewDigitalLibrary();
                    System.out.println("//".repeat(17));

                    bookDetails updatingBook = null;
                    boolean lookupBook = true;

                    while (lookupBook) {
                        System.out.println("\nPlease enter the 5-digit Book ID: ");
                        String bookIDInput = userInput.nextLine().trim();

                        if (bookIDInput.equals("0")) {
                            System.out.println("Going back to the main menu!");
                            break;
                        }

                        try {
                            int newBookID = Integer.parseInt(bookIDInput);
                            if (newBookID < 10000 || newBookID > 99999) {
                                System.out.println(flair.BOLD_RED + "Please enter a valid Book ID." + flair.RESET);
                                continue;
                            }

                            updatingBook = library.getBookByID(newBookID);
                            if (updatingBook == null) {
                                System.out.println(flair.BOLD_RED + "There is no book with the book ID: " + newBookID + flair.RESET);

                            } else {
                                lookupBook = false;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(flair.BOLD_RED + "Please enter a valid 5-digit Book ID number." + flair.RESET);
                        }
                    }

                    // loop to give the user options on what they would like to update
                    if (updatingBook != null) {
                        boolean updateBookMenu = true;
                        while (updateBookMenu) {
                            // give the update options for user to choose from
                            System.out.println(flair.BOLD_PINK +"\nWhat would you like to update?" + flair.RESET);
                            System.out.println(flair.BOLD_CYAN + "[1]. " + flair.RESET + "Book title.");
                            System.out.println(flair.BOLD_CYAN + "[2]. " + flair.RESET + "Author's first name.");
                            System.out.println(flair.BOLD_CYAN + "[3]. " + flair.RESET + "Author's last name.");
                            System.out.println(flair.BOLD_CYAN + "[4]. " + flair.RESET + "Book genre.");
                            System.out.println(flair.BOLD_CYAN + "[5]. " + flair.RESET + "Book sub-genre.");
                            System.out.println(flair.BOLD_CYAN + "[6]. " + flair.RESET + "Reading status.");
                            System.out.println(flair.BOLD_CYAN + "[7]. " + flair.RESET + "Book Summary.");
                            System.out.println(flair.BOLD_CYAN + "[8]. " + flair.RESET + "Book Rating");
                            System.out.println(flair.BOLD_CYAN + "[9]. " + flair.RESET + "Exit");
                            // prompts user to choose an options
                            System.out.println("Please choose an option: ");

                            String userInputOption = userInput.nextLine().trim();
                            int updateBookOption = 0;

                            try {
                                updateBookOption = Integer.parseInt(userInputOption);
                            } catch (NumberFormatException e) {
                                System.out.println(flair.BOLD_RED + "Please enter a valid option." + flair.RESET);
                                continue;
                            }

                            switch (updateBookOption) {
                                case 1:
                                    System.out.println("Please enter the new book title: ");
                                    updatingBook.setBookTitle(userInput.nextLine());
                                    System.out.println("The new title is: " + updatingBook.getBookTitle());

                                    break;

                                case 2:
                                    System.out.println("Please enter the new author's first name: ");
                                    updatingBook.setAuthorFirstName(userInput.nextLine());
                                    System.out.println("The new author's first name is: " + updatingBook.getAuthorFirstName());

                                    break;

                                case 3:
                                    System.out.println("Please enter the new author's last name: ");
                                    updatingBook.setAuthorLastName(userInput.nextLine());
                                    System.out.println("The new author's last name is: " + updatingBook.getAuthorLastName());

                                    break;

                                case 4:
                                    System.out.println("Please enter the new book genre: ");
                                    updatingBook.setBookGenre(userInput.nextLine());
                                    System.out.println("The new book genre is: " + updatingBook.getBookGenre());

                                    break;

                                case 5:
                                    System.out.println("Please enter the new book sub-genre: ");
                                    updatingBook.setBookSubGenre(userInput.nextLine());
                                    System.out.println("The new book sub-genre is: " + updatingBook.getBookSubGenre());

                                    break;

                                case 6:
                                    // making sure the reading status is a valid 1-4 integer input
                                    int newReadingStatusChoice = 0;
                                    boolean validNewReadingStatus = false;

                                    while (!validNewReadingStatus) {
                                        System.out.println(flair.BOLD_CYAN + "[1]. " + flair.RESET + "To Be Read.");
                                        System.out.println(flair.BOLD_CYAN + "[2]. " + flair.RESET + "Currently Reading.");
                                        System.out.println(flair.BOLD_CYAN + "[3]. " + flair.RESET + "Did Not Finish.");
                                        System.out.println(flair.BOLD_CYAN + "[4]. " + flair.RESET + "Finished Reading.");
                                        System.out.print("Please choose the book's reading status [1-4]: ");

                                        try {
                                            newReadingStatusChoice = userInput.nextInt();
                                            userInput.nextLine();

                                            if (newReadingStatusChoice >= 1 && newReadingStatusChoice <= 4) {
                                                validNewReadingStatus = true; // Breaks out of the validation loop
                                            } else {
                                                System.out.println(flair.BOLD_RED + "Please enter a valid option." + flair.RESET);
                                            }
                                        } catch (InputMismatchException e) {
                                            System.out.println(flair.BOLD_RED + "Please enter a valid option." + flair.RESET);
                                            userInput.nextLine();
                                        }
                                    }

                                    // Map the number selection to the exact abbreviation strings expected by your class
                                    String newReadingStatusText = "";
                                    switch (newReadingStatusChoice) {
                                        case 1:
                                            newReadingStatusText = "To Be Read";
                                            break;
                                        case 2:
                                            newReadingStatusText = "Currently Reading";
                                            break;
                                        case 3:
                                            newReadingStatusText = "Did Not Finish";
                                            break;
                                        case 4:
                                            newReadingStatusText = "Finished Reading";
                                            break;
                                    }

                                    updatingBook.setReadingStatus(newReadingStatusText);
                                    System.out.println("The reading status has been updated to: " + updatingBook.getReadingStatus());
                                    break;

                                case 7:
                                    // entering a book summary is optional
                                    System.out.println("Please enter the new book summary (optional): ");
                                    String newBookSummary = userInput.nextLine();
                                    if (newBookSummary.isBlank()) {
                                        newBookSummary = "There was no book summary provided.";
                                    }
                                    updatingBook.setBookSummary(newBookSummary);
                                    System.out.println("The new book summary is: " + updatingBook.getBookSummary());
                                    break;

                                case 8:
                                    double newBookRating = 0.0;
                                    boolean validNewBookRating = false;

                                    while (!validNewBookRating) {
                                        System.out.println("Please enter the new book rating (optional): ");
                                        String newBookRatingStr = userInput.nextLine();

                                        if (newBookRatingStr.isBlank()) {
                                            newBookRating = 0.0;
                                            validNewBookRating = true;
                                        } else {
                                            try {
                                                double newRating = Double.parseDouble(newBookRatingStr);
                                                if (newRating >= 0.0 && newRating <= 5.0) {
                                                    newBookRating = newRating;
                                                    validNewBookRating = true;
                                                } else {
                                                    System.out.println(flair.BOLD_RED + "Please enter a valid book rating." + flair.RESET);
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println(flair.BOLD_RED + "Please enter a valid book rating." + flair.RESET);
                                            }
                                        }
                                    }
                                    updatingBook.setBookRating(newBookRating);
                                    System.out.println("The new book rating is: " + menu.starSymbols(newBookRating));

                                    break;
                                case 9:
                                    System.out.println("Heading back to the main menu!");
                                    library.saveBookShelfToFile();
                                    updateBookMenu = false;
                                    break;

                                default:
                                    System.out.println("Please choose a menu option.");
                                    break;
                            }
                        }
                    }
                    // end of case 3
                    break;

                // main menu option 4 removing a book
                case 4:
                    System.out.println("\n-*-*- Removing A Book -*-*-");

                    if (library.ifShelfIsEmpty()) {
                        System.out.println(flair.BOLD_RED + "There are no books currently in the library.\n" + flair.RESET);
                        break;
                    }

                    boolean removeBookLoop = false;
                    while (!removeBookLoop) {
                        if (library.ifShelfIsEmpty()) {
                            // will return to the main menu
                            System.out.println(flair.BOLD_RED + "There are no books currently in the library.\n" + flair.RESET);
                            break;
                        }

                        System.out.println("The books currently in your digital library: ");
                        library.viewDigitalLibrary();
                        System.out.println("Which book would you like to remove? Please enter its 5-digit Book ID to remove the book: ");

                        // checking if the book exists using the bookID
                        if (userInput.hasNextInt()) {
                            int removeBookID = userInput.nextInt();
                            userInput.nextLine();

                            bookDetails removeBook = library.getBookByID(removeBookID);

                            if (removeBook == null) {
                                System.out.println(flair.BOLD_RED + "The book with a book ID: " + flair.RESET + removeBookID + flair.BOLD_RED + ", does not exist." + flair.RESET);
                            } else {
                                System.out.println("The book with a book ID: " + removeBook.getBookID() + ", and the title: "
                                        + removeBook.getBookTitle() + " has been removed from the digital library.\n");
                                // removes the book from the bookShelf array.
                                library.removeBookByID(removeBookID);
                            }
                        } else {
                            System.out.println(flair.BOLD_RED + "Please enter a valid book ID." + flair.RESET);
                            userInput.nextLine();
                            continue;
                        }

                        if (library.ifShelfIsEmpty()) {
                            System.out.println("The last book in your digital library has been removed.");
                            library.saveBookShelfToFile();
                            removeBookLoop = true;
                            break;
                        }

                        boolean validRemoveBookLoop = false;
                        while (!validRemoveBookLoop) {
                            System.out.print(flair.BOLD_PINK + "\nWould you like to remove another book?\n"+ flair.RESET);
                            System.out.println(flair.BOLD_CYAN + "[1]. " + flair.RESET + "Yes");
                            System.out.println(flair.BOLD_CYAN + "[2]. " + flair.RESET + "No");
                            System.out.println(flair.BOLD_PINK + "\nPlease enter a valid option: " + flair.RESET+ flair.RESET);

                            if (userInput.hasNextInt()) {
                                int userContinueRemoveBook = userInput.nextInt();
                                userInput.nextLine();

                                if (userContinueRemoveBook == 1) {
                                    validRemoveBookLoop = true;
                                } else if (userContinueRemoveBook == 2) {
                                    System.out.println("The digital library has been updated! ");
                                    library.saveBookShelfToFile();
                                    validRemoveBookLoop = true;
                                    removeBookLoop = true;
                                } else {
                                    System.out.println(flair.BOLD_RED + "\nPlease enter a valid option: " + flair.RESET);
                                }
                            } else {
                                System.out.println(flair.BOLD_RED + "\nPlease enter a valid option: " + flair.RESET);
                                userInput.nextLine();
                            }
                        }
                    }
                    // the end of case 4
                    break;

                // main menu option 5 viewing library
                case 5:
                    System.out.println("\n-*-*- Viewing Your Digital Library -*-*-\n");
                    library.viewDigitalLibrary();
                    System.out.println();
                    // end of case 5
                    break;

                // main menu option 6 sorting library
                case 6:
                    System.out.println("\n-*-*- Sorting Your Digital Library -*-*-");

                    if (library.ifShelfIsEmpty()) {
                        System.out.println(flair.BOLD_RED + "There are no books currently in the library.\n" + flair.RESET);
                        break;
                    }

                    // array sorting from digital library
                    library.sortLibraryByAuthorLastName();
                    System.out.println("Your library has been successfully sorted alphabetically by Author's Last Name (A-Z)!");

                    // prints out the organized digital library
                    library.viewDigitalLibrary();
                    // end of case 6
                    break;

                // main menu option 7 deleting digital library
                case 7:
                    String openFile = library.getActiveLibraryFile();
                    String displayLibraryName = openFile.replace(".txt", "");


                    System.out.println(flair.BOLD_RED + "\nWARNING: " + flair.RESET + "You are about to permanently delete the library file '" + displayLibraryName + "'!");
                    System.out.print("Are you absolutely sure you want to delete it? Type 'YES' to confirm: ");

                    String userConfirmed = userInput.nextLine().trim();

                    if (userConfirmed.equalsIgnoreCase("YES")) {
                        Path filePath = Paths.get(openFile);
                        try {
                            if (Files.deleteIfExists(filePath)) {
                                library.deleteDigitalLibrary();
                                library.resetActiveLibrary();
                                System.out.println("The library has been successfully deleted!\n");
                            } else {
                                System.out.println(openFile.replace(".txt", "") + " could not be deleted, it does not exist.\n");
                            }
                    } catch (IOException e) {
                        // Updated to report the actual failure reason (e.g., file lock or permissions)
                        System.out.println(openFile + " could not be deleted.\n");
                    }
                } else {
                        System.out.println("\nYes was not entered, deletion process has been canceled, returning to main menu!");
                    }
                    // end of case 7
                    break;

                // main menu option 8 exit
                case 8:
                    System.out.println("Thank you for using The Reader's Archive!");
                    mainMenuLoop = false;
                    break;

                default:
                    System.out.println("Please choose a menu option.");
                    break;

            }
            // end of switch cases for main menu

        }
        // end of mainMenuLoop
        userInput.close();
    }
// ------------------------------------------------------------------------------------------ //
    // // converting book rating string to star symbols // //
    public String starSymbols(double bookRating) {
        if (bookRating == 0.0) {
            return "There was no book rating provided.";
        }

        String starSymbol = "";
        int fullStars = (int) bookRating;
        double leftOver = bookRating - fullStars;
        // for a full star rating like 5 stars or 3 stars
        for (int i = 0; i < fullStars; i++) {
            starSymbol += "★";
        }

        // for half a star rating lik 4.5 or 3.5 stars
        boolean halfStars = false;
        if (leftOver >= 0.5) {
            starSymbol += "⯪";
            halfStars = true;
        }

        // for any rating below a full 5 star gets an empty star symbol
        int starSymbolCount;
        if (halfStars) {
            starSymbolCount = fullStars + 1;
        } else {
            starSymbolCount = fullStars;
        }
        for (int i = starSymbolCount; i < 5; i++) {
            starSymbol += "☆";
        }
        return starSymbol + "(" + bookRating + " out of 5 stars)";
    }
}