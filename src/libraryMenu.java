import java.net.UnknownServiceException;
import java.util.Scanner;

public class libraryMenu {

    public String displayMenu() {
        System.out.println("//".repeat(17));
        System.out.println("Welcome to The Reader's Archive!");
        System.out.println("Please select an option below:");
        System.out.println("[1]. Add a book.");
        System.out.println("[2]. Update book details.");
        System.out.println("[3]. Remove a book.");
        System.out.println("[4]. View digital library.");
        System.out.println("[5]. Sort digital library by author's last name.");
        System.out.println("[6]. Exit.");
        System.out.println("//".repeat(17));

        return "The Reader's Archive Menu has been displayed!";
    }

    public static void main(String[] args) {
        libraryMenu menu = new libraryMenu();
        digitalLibrary library = new digitalLibrary(); // links to digitalLibrary class to view the digital library

// scanner for user input
        Scanner userInput = new Scanner(System.in);
        boolean mainMenuLoop = true;

        while (mainMenuLoop) {
            menu.displayMenu();
            System.out.print("What would you like to do? : ");

            if (!userInput.hasNextInt()) {
                System.out.println("Please enter a valid option from the menu.");
                userInput.nextLine();
                continue;
            }

            int userDecision = userInput.nextInt();
            userInput.nextLine();

            switch (userDecision) {
                case 1:
                    System.out.println("\n" + "-*-*- Adding a book to your digital library -*-*-");

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

                    // getting the user input reading status, but the user must not be able to enter random words and capitalization should not matter.
                    String readingStatus = "";
                    boolean validReadingStatus = false;

                    while (!validReadingStatus) {
                        // examples of reading status format for user to enter.
                        System.out.println(
                                "Please enter the current reading status: (To Be Read, " +
                                        "Currently Reading, Finished Reading, or Did Not Finish DNF): "
                        );

                        if (userInput.hasNextLine()) {
                            readingStatus = userInput.nextLine();

                            if (readingStatus.equalsIgnoreCase("To Be Read") || readingStatus.equalsIgnoreCase("TBR")) {
                                readingStatus = "TBR";
                                validReadingStatus = true;

                            } else if (readingStatus.equalsIgnoreCase("Currently Reading")) {
                                readingStatus = "Currently Reading";
                                validReadingStatus = true;

                            } else if (readingStatus.equalsIgnoreCase("Finished Reading")) {
                                readingStatus = "Finished Reading";
                                validReadingStatus = true;

                            } else if (readingStatus.equalsIgnoreCase("Did Not Finish") || readingStatus.equalsIgnoreCase("DNF")) {
                                readingStatus = "DNF";
                                validReadingStatus = true;

                            } else {
                                System.out.println("Please enter a valid reading status:" +
                                        "To Be Read, Currently Reading, Finished Reading, or Did Not Finish DNF.");
                            }
                        }
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
                                System.out.println("Please enter a valid book rating.");
                            }
                        }
                    }
                    // putting book details in order
                    bookDetails newBook = new bookDetails(bookTitle, authorFirstName, authorLastName, bookGenre, bookSubGenre, readingStatus, bookSummary, bookRating, 0);
                    // then adding the new book to the digital library database
                    library.addNewBook(newBook);

                    // output the book details
                    System.out.println("//".repeat(17));
                    System.out.println("- The book " + bookTitle + " by " + authorFirstName + " " + authorLastName + " has been added to the library.");
                    System.out.println("- Reading Status: " + readingStatus);
                    System.out.println("- Book Summary: " + bookSummary);
                    System.out.println("- Book Rating: " + menu.starSymbols(bookRating));
                    System.out.println("//".repeat(17) + "\n");
                    // end of case 1
                    break;

                // updating a book's details by allowing the user to see their current books first in case they forgot the ID
                case 2:
                    System.out.println("\n" + "-*-*- Update Book Details -*-*-");
                    if (library.ifShelfIsEmpty()) {
                        System.out.println("There are no books currently in the library." + "\n");
                        break;
                    }
                    // list the books in the digital library
                    library.viewDigitalLibrary();
                    System.out.println("//".repeat(17));

                    bookDetails updatingBook = null;
                    boolean lookupBook = true;

                    while (lookupBook) {
                        System.out.println("\n" + "Please enter the 5-digit Book ID: ");
                        String bookIDInput = userInput.nextLine().trim();

                        if (bookIDInput.equals("0")) {
                            System.out.println("Going back to the main menu!");
                            break;
                        }

                        try {
                            int newBookID = Integer.parseInt(bookIDInput);
                            if (newBookID < 10000 || newBookID > 99999) {
                                System.out.println("Please enter a valid Book ID.");
                                continue;
                            }

                            updatingBook = library.getBookByID(newBookID);
                            if (updatingBook == null) {
                                System.out.println("There is no book with the book ID: " + newBookID);

                            } else {
                                lookupBook = false;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid 5-digit Book ID number.");
                        }
                    }

                    if (updatingBook != null) {
                        boolean updateBookMenu = true;
                        while (updateBookMenu) {
                            System.out.println("The book: " + updatingBook.getBookTitle() + " is currently in your digital library.");
                            // give the update options for user to choose from
                            System.out.println("What would you like to update?");
                            System.out.println("[1]. Book title.");
                            System.out.println("[2]. Author's first name.");
                            System.out.println("[3]. Author's last name.");
                            System.out.println("[4]. Book genre.");
                            System.out.println("[5]. Book sub-genre.");
                            System.out.println("[6]. Reading status.");
                            System.out.println("[7]. Book Summary.");
                            System.out.println("[8]. Book Rating");
                            System.out.println("[9]. Exit");
                            // prompts user to choose an options
                            System.out.println("Please choose an option: ");

                            String userInputOption = userInput.nextLine().trim();
                            int updateBookOption = 0;

                            try {
                                updateBookOption = Integer.parseInt(userInputOption);
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid option.");
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
                                    // making sure the reading status is a valid string input
                                    String newReadingStatus = "";
                                    boolean validNewReadingStatus = false;

                                    while (!validNewReadingStatus) {
                                        System.out.println(
                                                "Please enter the current reading status: (To Be Read, " +
                                                        "Currently Reading, Finished Reading, or Did Not Finish DNF): "
                                        );
                                        newReadingStatus = userInput.nextLine().trim();

                                        if (newReadingStatus.equalsIgnoreCase("To Be Read") || newReadingStatus.equalsIgnoreCase("TBR")) {
                                            updatingBook.setReadingStatus("TBR");
                                            validNewReadingStatus= true;

                                        } else if (newReadingStatus.equalsIgnoreCase("Currently Reading")) {
                                            updatingBook.setReadingStatus("Currently Reading");
                                            validNewReadingStatus= true;

                                        } else if (newReadingStatus.equalsIgnoreCase("Finished Reading")) {
                                            updatingBook.setReadingStatus("Finished Reading");
                                            validNewReadingStatus= true;

                                        } else if (newReadingStatus.equalsIgnoreCase("Did Not Finish") || newReadingStatus.equalsIgnoreCase("DNF")) {
                                            updatingBook.setReadingStatus("DNF");
                                            validNewReadingStatus= true;

                                        } else {
                                            System.out.println("Please enter a valid reading status.");
                                        }
                                    }
                                    System.out.println("The reading status has been: " + updatingBook.getReadingStatus());

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
                                                    System.out.println("Please enter a rating from 0 stars to 5 stars.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Please enter a valid book rating.");
                                            }
                                        }
                                    }
                                    updatingBook.setBookRating(newBookRating);
                                    System.out.println("The new book rating is: " + menu.starSymbols(newBookRating));

                                    break;
                                case 9:
                                    System.out.println("Heading back to the main menu!");
                                    // saves book updates in TRA.txt file
                                    library.saveBookShelfToFile();
                                    updateBookMenu = false;
                                    break;

                                default:
                                    System.out.println("Please choose a menu option.");
                                    break;
                            }
                        }
                    }
                break;
                // main menu options moving on to case 3: removing a book, case 4: viewing the library, and case 5 exiting the WHOLE application.
                case 3:
                    System.out.println("\n" + "-*-*- Removing A Book -*-*-");

                    if (library.ifShelfIsEmpty()) {
                        System.out.println("There are no books currently in the library." + "\n");
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
                            System.out.println("The book with ID " + removeBookID + " does not exist.");
                        } else {
                            System.out.println("The book with ID: " + removeBook.getBookID() + ", with the title "
                                    + removeBook.getBookTitle() + " has been removed from the digital library.");

                            // removes the book from the bookShelf array.
                            library.removeBookByID(removeBookID);
                        }
                    } else {
                        System.out.println("Please enter a valid book ID.");
                        userInput.nextLine();
                    }
                    // the end of case 3
                    break;

                // case 4: viewing library
                case 4:
                    System.out.println("\n" + "-*-*- Viewing Your Digital Library -*-*-");
                    library.viewDigitalLibrary();
                    System.out.println();
                    // end of case 4
                    break;

                case 5:
                    System.out.println("\n" + "-*-*- Sorting Your Digital Library -*-*-");

                    if  (library.ifShelfIsEmpty()) {
                        System.out.println("There are no books currently in the library." + "\n");
                        break;
                    }

                    // array sorting from digital library
                    library.sortLibraryByAuthorLastName();
                    System.out.println("Your library has been successfully sorted alphabetically by Author's Last Name (A-Z)!");

                    // prints out the organized digital library
                    library.viewDigitalLibrary();
                    System.out.println("//".repeat(17) + "\n");
                    break;

                // case 6: exiting whole application
                case 6:
                    System.out.println("Thank you for using The Reader's Archive!");
                    mainMenuLoop = false;
                    break;

                default:
                    System.out.println("Please choose a menu option.");
                    break;

            } // end of switch cases for main menu

        } // end of mainMenuLoop
        userInput.close();
    }

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