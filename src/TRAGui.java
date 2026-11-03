import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;

public class TRAGui {
    private digitalLibrary library;
    private JFrame menuFrame;
    private JLabel statusLabel;

    private JTextArea libraryDisplay;
    // table style for data display
    private JTable digitalLibraryTable;
    private javax.swing.table.DefaultTableModel digitalLibraryModel;


    public TRAGui() {
        library = new digitalLibrary();

        // main menu window
        menuFrame = new JFrame("The Reader's Archive");
        menuFrame.setSize(950, 620);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // window centering
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setLayout(new BorderLayout());
        menuFrame.setResizable(true);

        setupMenu();
        menuFrame.setVisible(true);

    }

    // menu design
    private void setupMenu() {
        libraryDisplay = new JTextArea();
        libraryDisplay.setEditable(false);
        libraryDisplay.setLineWrap(true);
        libraryDisplay.setWrapStyleWord(true);
        libraryDisplay.setFont(new Font("Dialog", Font.PLAIN, 14));
        libraryDisplay.setRows(3);
        libraryDisplay.setText("Welcome to The Reader's Archive!\n" + "To start load an library or create a new one ~");

        // library data table
        // table for library data
        String[] tableColumns = {"Book ID", "Book Title", "Author", "Genre", "Sub-Genre", "Reading Status", "Summary", "Book Rating"};
        digitalLibraryModel = new javax.swing.table.DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        digitalLibraryTable = new JTable(digitalLibraryModel);
        digitalLibraryTable.setRowHeight(24);
        digitalLibraryTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        digitalLibraryTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        digitalLibraryTable.getTableHeader().setReorderingAllowed(false);
        digitalLibraryTable.setAutoCreateRowSorter(true);

        // width of columns
        int[] columnWidths = {65, 150, 120, 80, 95, 115, 220, 70};
        for (int i = 0; i < columnWidths.length; i++) {
            digitalLibraryTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // library data display area
        JPanel libraryPanel = new JPanel(new BorderLayout(0, 5));
        libraryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        libraryPanel.add(new JScrollPane(digitalLibraryTable), BorderLayout.CENTER);
        libraryPanel.add(new JScrollPane(libraryDisplay), BorderLayout.SOUTH);
        menuFrame.add(libraryPanel, BorderLayout.CENTER);

        // bottom of display and flair
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.setPreferredSize(new Dimension(500, 200));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // buttons panel on left side
        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 0, 4));

        JButton createLoadLibrary = new JButton("1. Create or Load A library.");
        JButton addBook = new JButton("2. Add A Book.");
        JButton updateBookDetails = new JButton("3. Update Book Details.");
        JButton removeBook = new JButton("4. Remove A Book.");
        JButton viewLibrary = new JButton("5. View Library");
        JButton sortLibrary = new JButton("6. Sort Library");
        JButton deleteLibrary = new JButton("7. Delete Library.");
        JButton exitTRA = new JButton("8. Exit.");

        // pink buttons!!
        Color pinkButtons = new Color(255, 209, 227);
        Color darkPink = new Color(156, 26, 88);
        JButton[] menuButtons = {createLoadLibrary, addBook, updateBookDetails, removeBook, viewLibrary, sortLibrary, deleteLibrary, exitTRA};
        for (JButton menuButton : menuButtons) {
            menuButton.setBackground(pinkButtons);
            menuButton.setForeground(darkPink);
            menuButton.setFont(new Font("Dialog", Font.PLAIN, 14));
            menuButton.setOpaque(true);
            menuButton.setFocusable(false);
            menuButton.setBorder(BorderFactory.createLineBorder(darkPink, 2));
        }



        buttonPanel.add(createLoadLibrary); // 1
        buttonPanel.add(addBook); // 2
        buttonPanel.add(updateBookDetails); // 3
        buttonPanel.add(removeBook); // 4
        buttonPanel.add(viewLibrary); // 5
        buttonPanel.add(sortLibrary); // 6
        buttonPanel.add(deleteLibrary); // 7
        buttonPanel.add(exitTRA); // 8

        // push button to LEFT side of bottom panel
        bottomPanel.add(buttonPanel);

        // flair aspect, cute book pixel art
        JPanel bookImage = new JPanel(new BorderLayout());
        bookImage.setBackground(Color.WHITE); // temp
        // placeholder

        JLabel bookImageLabel;
        java.io.File imageFile = new java.io.File(System.getProperty("user.dir"), "bookImage.png");
        // scale image since it's a pixel image
        if (imageFile.exists()) {
            Image scaleImage = new ImageIcon(imageFile.getAbsolutePath()).getImage().getScaledInstance(160, 160, Image.SCALE_REPLICATE);
            bookImageLabel = new JLabel(new ImageIcon(scaleImage), JLabel.CENTER);
        } else {
            bookImageLabel = new JLabel("Book Image", JLabel.CENTER);
        }
        bookImage.add(bookImageLabel, BorderLayout.CENTER);
        bottomPanel.add(bookImage);

        // combine bottom layout to data frame
        menuFrame.add(bottomPanel, BorderLayout.SOUTH);

        // option 1. loading or creating a library file
        createLoadLibrary.addActionListener(e -> {
            java.io.File dir = new java.io.File(System.getProperty("user.dir"));
            java.io.File[] exitingFiles = dir.listFiles((dir1, fileName) -> fileName.endsWith(".txt"));

            StringBuilder availableLibraryFiles = new StringBuilder("Existing Library Archives\n");
            if (exitingFiles != null && exitingFiles.length > 0) {
                for (java.io.File file : exitingFiles) {
                    availableLibraryFiles.append(" - ").append(file.getName().replace(".txt", "")).append("\n");
                }
            } else {
                availableLibraryFiles.append("No library archives were found. Please create a new library or enter an existing name!\n");
            }
            availableLibraryFiles.append("Please enter a valid library name.");

            String libraryName = JOptionPane.showInputDialog(menuFrame, availableLibraryFiles.toString());
            if (libraryName == null || libraryName.trim().isBlank())
                return;

            libraryName = libraryName.trim();
            if (!libraryName.endsWith(".txt"))
                libraryName = libraryName + ".txt";
            boolean libraryExists = false;
            if (exitingFiles != null) {
                for (java.io.File file : exitingFiles) {
                    if (file.getName().equals(libraryName)) {
                        libraryName = file.getName();
                        libraryExists = true;
                        break;
                    }
                }
            }
            if (!libraryExists) {
                try {
                    new java.io.File(System.getProperty("user.dir"), libraryName).createNewFile();
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(menuFrame, "This library file could not be created.");
                    return;
                }
            }
            library.loadBookShelfFromFile(libraryName);
            tableRefresh();

            // confirmation output
            libraryDisplay.setText("Successfully Loaded the Library: " + libraryName.replace(".txt", ""));
        });

        // option 2. adding a book
        addBook.addActionListener(e -> {
            if (library.getActiveLibraryFile().isBlank()) {
                libraryDisplay.setText("Please select a library from menu option [1] to view your digital library :)!");
                return;
            }
            String bookTitle = askRequiredInput("Please Enter the Book Title: ", "");
            if (bookTitle == null)
                return;

            String authorFirstName = askRequiredInput("Please Enter the Author's First Name: ", "");
            if (authorFirstName == null)
                return;

            String authorLastName = askRequiredInput("Please Enter the Author's Last Name: ", "");
            if (authorLastName == null)
                return;

            String bookGenre = askRequiredInput("Please Enter the Book's Genre: ", "");
            if (bookGenre == null)
                return;

            String bookSubGenre = askRequiredInput("Please Enter the Book's Sub-Genre: ", "");
            if (bookSubGenre == null)
                return;


            // drop down menu option for reading status
            String[] readingStatusOption = {
                    "To Be Read",
                    "Currently Reading",
                    "Did Not Finish",
                    "Finished Reading"
            };

            String readingStatusChoice = (String) JOptionPane.showInputDialog(
                    menuFrame, "Please Select a Reading Status:\n", "Reading Status",
                    JOptionPane.QUESTION_MESSAGE, null, readingStatusOption, readingStatusOption[0]
            );
            if (readingStatusChoice == null)
                return;

            // optional summary
            String bookSummary = JOptionPane.showInputDialog(menuFrame, "Please Enter the Book Summary: ");
            if (bookSummary == null)
                return;
            bookSummary = bookSummary.replace("|", "/");
            if (bookSummary.trim().isBlank()) {
                bookSummary = "A book summary was not provided.";
            }

            // optional book rating wrapped the same in console
            double bookRating = 0.0;
            boolean validBookRating = false;

            while(!validBookRating) {
                String optionalBookRating = JOptionPane.showInputDialog(menuFrame, "Please Enter the Book Rating: ");
                if (optionalBookRating == null)
                    return;
                if (optionalBookRating.isBlank()) {
                    validBookRating = true;
                } else {
                    try {
                        double bRating = Double.parseDouble(optionalBookRating.trim());
                        if (bRating >= 0.0 && bRating <= 5.0) {
                            bookRating = bRating;
                            validBookRating = true;
                        } else {
                            JOptionPane.showMessageDialog(menuFrame, "Please enter a book rating from 0 stars to 5 stars.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(menuFrame, "Please enter a valid book rating from 0 stars to 5 stars.");
                    }
                }
            }
            bookDetails newBook = new bookDetails(
                    bookTitle,
                    authorFirstName,
                    authorLastName,
                    bookGenre,
                    bookSubGenre,
                    readingStatusChoice,
                    bookSummary,
                    bookRating,
                    0 // book ID tracking
            );
            library.addNewBook(newBook);
            tableRefresh();

            // data display window flair
            libraryDisplay.setText(
                    "==============================================\n" +
                            "               BOOK ADDED SUCCESSFULLY        \n" +
                            "==============================================\n" +
                            "⊹ Book Title: " + bookTitle + "\n" +
                            "⊹ Author: " + authorFirstName + " " + authorLastName + "\n" +
                            "⊹ Genre: " + bookGenre + " (" + bookSubGenre + ")\n" +
                            "⊹ Reading Status: " + readingStatusChoice + "\n" +
                            "--- \n" +
                            "⊹ Summary: " + bookSummary + "\n" +
                            "⊹ Book Rating: " + bookRating + " out of 5 stars"
            );
        });
        // option 3. updating a book's details
        updateBookDetails.addActionListener(e -> {
            if (library.getActiveLibraryFile().isBlank()) {
                libraryDisplay.setText("Please select a library from menu option [1] to view your digital library :)!");
                return;
            }
            if (library.ifShelfIsEmpty()) {
                libraryDisplay.setText("This library is currently empty, please add some books edit a book's details!");
                return;
            }

            // taking 5 digit book id
            String bookIDInput = JOptionPane.showInputDialog(menuFrame, "Please Enter the Book ID: ");
            if (bookIDInput == null)
                return;

            try {
                int bookID = Integer.parseInt(bookIDInput.trim());
                bookDetails updateBook = library.getBookByID(bookID);

                if (updateBook == null) {
                    JOptionPane.showMessageDialog(menuFrame, "The book with ID " + bookID + " does not exist.");
                    return;
                }


                // drop down menu for editing book details
                String[] updateBookOptions = {
                        "Edit Book Title",
                        "Edit Author First Name",
                        "Edit Author Last Name",
                        "Edit Book Genre",
                        "Edit Book Sub-Genre",
                        "Edit Reading Status",
                        "Edit Book Summary",
                        "Edit Book Rating",

                };

                String editChoice = (String) JOptionPane.showInputDialog(
                        menuFrame,
                        "What would you like to update?",
                        "Update Details Menu",
                        JOptionPane.QUESTION_MESSAGE, null, updateBookOptions, updateBookOptions[0]
                );

                if (editChoice == null) {
                    return;
                }
                switch (editChoice) {
                    case "Edit Book Title" -> {
                        String newTitle = askRequiredInput("Please Enter the Book Title:", updateBook.getBookTitle());
                        if (newTitle != null) updateBook.setBookTitle(newTitle);
                    }
                    case "Edit Author First Name" -> {
                        String newFirstName = askRequiredInput("Please Enter the Author First Name:", updateBook.getAuthorFirstName());
                        if (newFirstName != null) updateBook.setAuthorFirstName(newFirstName);
                    }
                    case "Edit Author Last Name" -> {
                        String newLastName = askRequiredInput("Please Enter the Author Last Name:", updateBook.getAuthorLastName());
                        if (newLastName != null) updateBook.setAuthorLastName(newLastName);
                    }
                    case "Edit Book Genre" -> {
                        String newGenre = askRequiredInput("Please Enter the Book Genre:", updateBook.getBookGenre());
                        if (newGenre != null) updateBook.setBookGenre(newGenre);
                    }
                    case "Edit Book Sub-Genre" -> {
                        String newSubGenre = askRequiredInput("Please Enter the Book Sub-Genre:", updateBook.getBookSubGenre());
                        if (newSubGenre != null) updateBook.setBookSubGenre(newSubGenre);
                    }
                    case "Edit Reading Status" -> {
                        String[] readingStatusOption = {"To Be Read", "Currently Reading", "Did Not Finish", "Finished Reading"};
                        String newReadingStatus = (String) JOptionPane.showInputDialog(menuFrame, "Please Select a Reading Status:",
                                "Reading Status Selector", JOptionPane.QUESTION_MESSAGE, null, readingStatusOption, updateBook.getReadingStatus());
                        if (newReadingStatus != null) updateBook.setReadingStatus(newReadingStatus);
                    }
                    case "Edit Book Summary" -> {
                        String newSummary = JOptionPane.showInputDialog(menuFrame, "Please Enter the Book Summary:", updateBook.getBookSummary());
                        if (newSummary != null) newSummary = newSummary.replace("|", "/");
                        if (newSummary != null) updateBook.setBookSummary(newSummary.isBlank() ? "There was no book summary provided." : newSummary);
                    }
                    case "Edit Book Rating" -> {
                        String newBookRating = JOptionPane.showInputDialog(menuFrame, "Please Enter the Book Rating:", updateBook.getBookRating());
                        if (newBookRating != null) {
                            try {
                                double bRating = Double.parseDouble(newBookRating);
                                if (bRating >= 0.0 && bRating <= 5.0) {
                                    updateBook.setBookRating(bRating);
                                } else {
                                    JOptionPane.showMessageDialog(menuFrame, "Please enter a valid Book Rating!");
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(menuFrame, "Please enter a valid Book Rating!");
                            }
                        }
                    }
                }
                tableRefresh();
                library.saveBookShelfToFile();
                libraryDisplay.setText(
                        "==============================================\n" +
                                "              BOOK DETAILS UPDATED            \n" +
                                "==============================================\n" +
                                "Successfully updated the book details for \n" +
                                "Book ID: " + bookIDInput + " [" + editChoice + "]\n\n" +
                                "Click '5. View Library' to view the updated details."
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(menuFrame, "Please enter a valid numeric 5-digit ID.");
            }
        });
// option 4. removing a book
        removeBook.addActionListener(e -> {
            if (library.getActiveLibraryFile().isBlank()) {
                libraryDisplay.setText("Please select a library from menu option [1] to view your digital library :)!");
                return;
            }
            if (library.ifShelfIsEmpty()) {
                libraryDisplay.setText("This library is currently empty, please add some books to remove any!");
                return;
            }
            String bookIDInput = JOptionPane.showInputDialog(menuFrame, "Please Enter the Book ID: ");
            if (bookIDInput == null)
                return;
            try {
                int removeBookID = Integer.parseInt(bookIDInput.trim());
                bookDetails removingBook = library.getBookByID(removeBookID);

                if (removingBook == null) {
                    JOptionPane.showMessageDialog(menuFrame, "The book with ID " + removeBookID + " does not exist.");
                } else {
                    library.removeBookByID(removeBookID);
                    tableRefresh();
                    library.saveBookShelfToFile();
                    libraryDisplay.setText(
                            "==============================================\n" +
                                    "               BOOK REMOVED                   \n" +
                                    "==============================================\n" +
                                    "Book Title: " + removingBook.getBookTitle() + "\n" +
                                    "Book ID: " + removeBookID + "\n\n" +
                                    "Has been successfully removed from your digital library."
                    );
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(menuFrame, "Please enter a valid Book ID!");
            }
        });
// option 5. viewing digital library
        viewLibrary.addActionListener(e -> {
            if (library.getActiveLibraryFile().isBlank()) {
                libraryDisplay.setText("Please select a library from menu option [1] to view your digital library :)!");
                return;
            }
            if (library.ifShelfIsEmpty()) {
                libraryDisplay.setText("This library is currently empty, please add some books to view your digital library :)!");
                return;
            }
            showLibrary("Your Digital Library");
        });
// option 6. sorting your library
        sortLibrary.addActionListener(e -> {
            if (library.getActiveLibraryFile().isBlank() || library.ifShelfIsEmpty()) {
                libraryDisplay.setText("This library is currently empty, please add some books to sort it!");
                return;
            }
            library.sortLibraryByAuthorLastName();
            showLibrary("Your Digital Library Sorted by Author's Last Name A-Z");
        });
// option 7. deleting a digital library
        deleteLibrary.addActionListener(e -> {
            if (library.getActiveLibraryFile().isBlank()) {
                libraryDisplay.setText("Please select a library from menu option [1] to view your digital library :)!");
                return;
            }
            String activeFile = library.getActiveLibraryFile().trim();
            int confirmYes = JOptionPane.showConfirmDialog(
                    menuFrame, "WARNING: You are about to permanently delete the library file, "
                            + activeFile.replace(".txt", "") + ". Would you like to proceed?",
                    "Confirm Digital Library Deletion: ", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirmYes == JOptionPane.YES_OPTION) {
                Path filePath = Paths.get(activeFile);
                try {
                    if (Files.deleteIfExists(filePath)) {
                        library.deleteDigitalLibrary();
                        library.resetActiveLibrary();
                        tableRefresh();
                        libraryDisplay.setText(
                                """
                                        ==============================================
                                                      LIBRARY DELETED                \s
                                        ==============================================
                                        The archive workspace file has been wiped permanently.
                                        Please create a new library or load another one."""
                        );
                    } else {
                        libraryDisplay.setText("Cancelling Deletion Process: File could not be found.");
                    }
                } catch (Exception ex) {
                    libraryDisplay.setText("Cancelling Deletion Process: File could not be found.");
                }
            } else {
                libraryDisplay.setText("Deletion Cancelled: Going back to main menu.");
            }
        });
// option 8. exiting whole application
        exitTRA.addActionListener(e -> System.exit(0));
    }

    private void showLibrary(String heading) {
        tableRefresh();
        libraryDisplay.setText(heading + " (" + digitalLibraryModel.getRowCount() + " books)");
    }

        private void tableRefresh() {
            digitalLibraryModel.setRowCount(0);
            for (bookDetails book : library.getBookShelf()) {
                // show "No rating" when the rating is 0, otherwise show the rating out of 5
                String bookRatingText;
                if (book.getBookRating() == 0.0) {
                    bookRatingText = "No rating";
                } else {
                    bookRatingText = book.getBookRating() + " / 5 stars";
                }
                digitalLibraryModel.addRow(new Object[]{
                        book.getBookID(),
                        book.getBookTitle(),
                        book.getAuthorFirstName() + " " + book.getAuthorLastName(),
                        book.getBookGenre(),
                        book.getBookSubGenre(),
                        book.getReadingStatus(),
                        book.getBookSummary(),
                        bookRatingText
                });
            }
            }

            // asks until something is entered something
            private String askRequiredInput(String message, String startingText) {
                String userInput = "";
                boolean validInput = false;

                while (!validInput) {
                    userInput = JOptionPane.showInputDialog(menuFrame, message, startingText);

                    // user pressed cancel
                    if (userInput == null) {
                        return null;
                    }

                    // | separates the fields in the save file, so it gets swapped out
                    userInput = userInput.replace("|", "/");
                    userInput = userInput.trim();

                    if (userInput.isBlank()) {
                        JOptionPane.showMessageDialog(menuFrame, "This field cannot be left blank. Please try again!");
                    } else {
                        validInput = true;
                    }
                }
                return userInput;
            }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(TRAGui::new);
    }
}