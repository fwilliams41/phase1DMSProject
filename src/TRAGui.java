import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * Francine Williams
 * CEN 3024 - Software Development 1
 * November 9, 2026
 * TRAGui.java
 *
 * The Reader's Archive is a digital library manager. The user can create or open a library,
 * add, edit and remove books, sort the library by author's last name, and delete a library.
 * This class is the graphical (Swing) version of the program and contains the main method that
 * starts it.
 *
 * For Phase 4 of the DMS project, the libraries and books are kept in a MySQL database,
 * the user types in their own server address, username, and password when the program starts.
 * Books are chosen by clicking them in the table instead of typing their ID, and all updates appear in pop up windows.
 */

public class TRAGui {
    // GUI colour scheme
    private static final Color blushPink = new Color(255, 209, 227);
    private static final Color berryPink = new Color(156, 26, 88);
    private static final Color creamWhite = new Color(255, 248, 243);
    private static final Color softRowPink = new Color(255, 236, 244);
    private static final Color inkBrown = new Color(74, 44, 42);
    private static final Color gridPink = new Color(232, 168, 196);

    // fonts for the title, subtitle, and everything else
    private static final Font titleFont = new Font("Arial Rounded MT Bold", Font.PLAIN, 30);
    private static final Font taglineFont = new Font("Calibri", Font.ITALIC, 16);
    private static final Font bodyFont = new Font("Calibri", Font.PLAIN, 14);
    private static final Font bodyBoldFont = new Font("Calibri", Font.BOLD, 14);

    // font calibri does not have star symbols, so the star ratings uses another font with star symbols
    private static final Font starFont = pickStarFont();

    private static final String[] readingStatusOptions = {
            "To Be Read",
            "Currently Reading",
            "Did Not Finish",
            "Finished Reading"
    };
    private static final String noSummaryText = "There was no book summary provided.";
    private static final int idColumn = 0;
    private static final int ratingColumn = 7;

    // to make texts centered and work with font calibri texts were moved a bit to compensate
    private static final int textNudge = 3;

    private libraryDatabase library;
    private JFrame menuFrame;
    private JLabel libraryNameLabel;

    // moved the book count label to below the table instead of in the pop up windows
    private JLabel bookCountLabel;

    private JTable digitalLibraryTable;
    private DefaultTableModel digitalLibraryModel;

    private JButton addBookButton;
    private JButton editBookButton;
    private JButton removeBookButton;
    private JButton sortLibraryButton;
    private JButton deleteLibraryButton;

// ------------------------------------------------------------------------------------------ //

    public TRAGui() {
        library = new libraryDatabase();

        menuFrame = new JFrame("The Reader's Archive");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // closes the database connection when the window is closed
        menuFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                library.disconnect();
            }
        });

        menuFrame.setLayout(new BorderLayout());
        menuFrame.getContentPane().setBackground(creamWhite);

        // window icon (the green pixel book image)
        ImageIcon windowIcon = loadPixelImage(32);
        if (windowIcon != null) {
            menuFrame.setIconImage(windowIcon.getImage());
        }

        menuFrame.add(buildHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(12, 0));
        contentPanel.setBackground(creamWhite);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 12, 14));
        JPanel toolbar = buildToolbar();
        contentPanel.add(toolbar, BorderLayout.NORTH);
        contentPanel.add(buildTable(), BorderLayout.CENTER);

        // book count shown under the table
        bookCountLabel = new JLabel(" ");
        bookCountLabel.setFont(bodyBoldFont);
        bookCountLabel.setForeground(berryPink);
        bookCountLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 0));
        contentPanel.add(bookCountLabel, BorderLayout.SOUTH);
        menuFrame.add(contentPanel, BorderLayout.CENTER);

        menuFrame.add(buildStatusBar(), BorderLayout.SOUTH);

        // keeps window from getting smaller/narrower as the buttons were being hidden before
        menuFrame.pack();
        Insets frameEdges = menuFrame.getInsets();
        int minimumWidth = toolbar.getPreferredSize().width + 28 + 20 + frameEdges.left + frameEdges.right;
        menuFrame.setMinimumSize(new Dimension(minimumWidth, 560));
        menuFrame.setSize(Math.max(1050, minimumWidth), 660);
        menuFrame.setLocationRelativeTo(null);

        updateButtonStates();
        menuFrame.setVisible(true);
    }

// ------------------------------------------------------------------------------------------ //

    /*
     * method: buildHeader
     * parameters: none
     * return: JPanel
     * purpose: builds the pink banner at the top of the window with the title and sub-title
     */

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(berryPink);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        JLabel titleLabel = new JLabel("The Reader's Archive", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        JLabel taglineLabel = new JLabel("your very own digital library!", JLabel.CENTER);
        taglineLabel.setFont(taglineFont);
        taglineLabel.setForeground(blushPink);
        titleBlock.add(titleLabel);
        titleBlock.add(taglineLabel);
        header.add(titleBlock, BorderLayout.CENTER);

        return header;
    }

    /*
     * method: buildToolbar
     * parameters: NONE
     * return: JPanel
     * purpose: builds the row with the open library name on the left and the buttons in the
     *          middle of the window, the name wraps onto a second line only when it runs out of space
     */

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setBackground(creamWhite);
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 0, 10, 0));

        JButton openLibraryButton = makeButton("Open Library");
        addBookButton = makeButton("Add Book");
        editBookButton = makeButton("Edit Book");
        removeBookButton = makeButton("Remove Book");
        sortLibraryButton = makeButton("Sort by Author");
        deleteLibraryButton = makeButton("Delete Library");

        openLibraryButton.addActionListener(e -> openLibrary());
        addBookButton.addActionListener(e -> addBook());
        editBookButton.addActionListener(e -> editSelectedBook());
        removeBookButton.addActionListener(e -> removeSelectedBook());
        sortLibraryButton.addActionListener(e -> sortLibrary());
        deleteLibraryButton.addActionListener(e -> deleteLibrary());

        editBookButton.setToolTipText("Please select a book in the table first (or just double-click it)");
        removeBookButton.setToolTipText("Please select a book in the table first (or press Delete)");

        // row of buttons, kept together on ONE row
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setOpaque(false);
        buttonRow.add(openLibraryButton);
        buttonRow.add(Box.createHorizontalStrut(20));
        buttonRow.add(addBookButton);
        buttonRow.add(Box.createHorizontalStrut(8));
        buttonRow.add(editBookButton);
        buttonRow.add(Box.createHorizontalStrut(8));
        buttonRow.add(removeBookButton);
        buttonRow.add(Box.createHorizontalStrut(20));
        buttonRow.add(sortLibraryButton);
        buttonRow.add(Box.createHorizontalStrut(20));
        buttonRow.add(deleteLibraryButton);

        // which library is open label put to far left of buttons
        libraryNameLabel = new JLabel("", JLabel.LEFT);
        libraryNameLabel.setFont(bodyBoldFont);
        libraryNameLabel.setForeground(berryPink);
        libraryNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        setLibraryNameText("Not connected");

        // the buttons always sit in the middle of the window, separate from the 'which library is open' label
        toolbar.setLayout(new LayoutManager() {
            private static final int gapBeforeButtons = 12;

            @Override public void addLayoutComponent(String name, Component component) { }
            @Override public void removeLayoutComponent(Component component) { }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                Insets edges = parent.getInsets();
                Dimension buttons = buttonRow.getPreferredSize();

                // room for at least libraryNameWidth on the left AND the same on the right so it stays centred
                int width = edges.left + edges.right + buttons.width + 2 * (libraryNameWidth + gapBeforeButtons);
                int height = edges.top + edges.bottom
                        + Math.max(buttons.height, libraryNameLabel.getPreferredSize().height);
                return new Dimension(width, height);
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return preferredLayoutSize(parent);
            }

            @Override
            public void layoutContainer(Container parent) {
                Insets edges = parent.getInsets();
                int innerWidth = parent.getWidth() - edges.left - edges.right;
                int innerHeight = parent.getHeight() - edges.top - edges.bottom;

                Dimension buttons = buttonRow.getPreferredSize();
                int buttonsX = edges.left + (innerWidth - buttons.width) / 2;
                buttonRow.setBounds(buttonsX, edges.top + (innerHeight - buttons.height) / 2,
                        buttons.width, buttons.height);

                // long library names would get ... at the end, fixes the wrapping for any library name
                int nameWidth = Math.max(libraryNameWidth, buttonsX - edges.left - gapBeforeButtons);
                libraryNameSpace = nameWidth;
                wrapLibraryName(nameWidth);
                int nameHeight = libraryNameLabel.getPreferredSize().height;
                libraryNameLabel.setBounds(edges.left, edges.top + (innerHeight - nameHeight) / 2,
                        nameWidth, nameHeight);
            }
        });

        toolbar.add(libraryNameLabel);
        toolbar.add(buttonRow);
        return toolbar;
    }

    /*
     * method: buildTable
     * parameters: NONE
     * return: JScrollPane
     * purpose: builds the library table (striped rows, grid lines, star ratings) and sets up
     *          selecting a book, double-clicking to edit and the delete key to remove
     */

    private JScrollPane buildTable() {
        String[] tableColumns = {
                "ID",
                "Title",
                "Author",
                "Genre",
                "Sub-Genre",
                "Reading Status",
                "Summary",
                "Rating"
        };

        digitalLibraryModel = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        digitalLibraryTable = new JTable(digitalLibraryModel) {

            // keeps the grid lines and design for library table
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int rowHeight = getRowHeight();
                int filledHeight = getRowCount() * rowHeight;
                if (filledHeight >= getHeight()) {
                    return;
                }

                int row = getRowCount();
                for (int y = filledHeight; y < getHeight(); y += rowHeight, row++) {
                    g.setColor(row % 2 == 0 ? Color.WHITE : softRowPink);
                    g.fillRect(0, y, getWidth(), rowHeight);
                    g.setColor(getGridColor());
                    g.drawLine(0, y + rowHeight - 1, getWidth(), y + rowHeight - 1);
                }

                g.setColor(getGridColor());
                int columnEdge = 0;
                for (int column = 0; column < getColumnCount(); column++) {
                    columnEdge += getColumnModel().getColumn(column).getWidth();
                    g.drawLine(columnEdge - 1, filledHeight, columnEdge - 1, getHeight());
                }
            }
        };

        digitalLibraryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        digitalLibraryTable.setAutoCreateRowSorter(true);
        digitalLibraryTable.setRowHeight(28);
        digitalLibraryTable.setFont(bodyFont);
        digitalLibraryTable.setForeground(inkBrown);
        digitalLibraryTable.setBackground(Color.WHITE);
        digitalLibraryTable.setFillsViewportHeight(true);
        digitalLibraryTable.setShowGrid(true);
        digitalLibraryTable.setGridColor(gridPink);
        digitalLibraryTable.setSelectionBackground(berryPink);
        digitalLibraryTable.setSelectionForeground(Color.WHITE);

        digitalLibraryTable.getTableHeader().setReorderingAllowed(false);

        // column names are centred and moved down to sit evenly
        digitalLibraryTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(bodyBoldFont);
                setBackground(blushPink);
                setForeground(berryPink);

                // normal java header
                setBorder(BorderFactory.createCompoundBorder(
                        UIManager.getBorder("TableHeader.cellBorder"),
                        BorderFactory.createEmptyBorder(2 + textNudge, 4, 2, 4)));
                return this;
            }
        });

        // white and pink striped rows with padding
        digitalLibraryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            // star ratings use a different font, so those cells are centred by measuring the letters
            private boolean measuringToCentre = false;

            @Override
            public Insets getInsets() {
                return measuringToCentre ? centredInsets(this, super.getInsets()) : super.getInsets();
            }

            @Override
            public Insets getInsets(Insets reuse) {

                // labels use this one when drawing, so it needs the same padding
                return getInsets();
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                if (!isSelected) {
                    cell.setBackground(row % 2 == 0 ? Color.WHITE : softRowPink);
                    cell.setForeground(inkBrown);
                }

                // star symbols have their own font
                boolean hasStars = (value != null) && value.toString().matches(".*[★☆⯪].*");
                setFont(hasStars ? starFont : bodyFont);

                measuringToCentre = hasStars;

                setBorder(hasStars ? BorderFactory.createEmptyBorder(3, 8, 3, 8)
                        : BorderFactory.createEmptyBorder(textNudge, 8, 0, 8));

                // hovering over a cell shows its full text, in case of long summaries or titles
                String cellText = (value == null) ? "" : value.toString();
                if (cellText.isBlank()) {
                    setToolTipText(null);
                } else if (cellText.length() > 50) {

                    // wraps long text
                    String safeText = cellText.replace("&", "&amp;").replace("<", "&lt;");
                    setToolTipText("<html><p width='280'>" + safeText + "</p></html>");
                } else {
                    setToolTipText(cellText);
                }
                return cell;
            }
        });

        // summary column gets the most space (it is priority to account for long summaries) every other column works with the left over space
        int[] columnWidths = {0, 160, 130, 80, 115, 115, 520, 0};
        for (int i = 0; i < columnWidths.length; i++) {
            if (columnWidths[i] > 0) {
                digitalLibraryTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }
        }

        // ID and Rating columns stay exactly as wide as their contents need
        fixColumnWidth(idColumn, bodyFont, "99999");
        fixColumnWidth(ratingColumn, starFont, ratingText(5.0), ratingText(4.5), ratingText(0.5));

        // selecting a row is what drives Edit / Remove now
        digitalLibraryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // to edit a book, you can double-click it
        digitalLibraryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && digitalLibraryTable.getSelectedRow() != -1) {
                    editSelectedBook();
                }
            }
        });

        // delete key removes the selected book
        digitalLibraryTable.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("DELETE"), "removeSelectedBook");
        digitalLibraryTable.getActionMap().put("removeSelectedBook", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedBook();
            }
        });

        JScrollPane tableScroll = new JScrollPane(digitalLibraryTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(blushPink, 2));
        return tableScroll;
    }

    /*
     * method: fixColumnWidth
     * parameters: int column, Font font, String... widestTexts
     * return: VOID
     * purpose: locks a column to the width of its widest text so it does not take space from the
     *          other columns (used for the ID and Rating columns)
     */

    private void fixColumnWidth(int column, Font font, String... widestTexts) {
        FontMetrics metrics = digitalLibraryTable.getFontMetrics(font);
        String header = String.valueOf(digitalLibraryTable.getColumnModel().getColumn(column).getHeaderValue());
        int width = digitalLibraryTable.getFontMetrics(bodyBoldFont).stringWidth(header);
        for (String text : widestTexts) {
            width = Math.max(width, metrics.stringWidth(text));
        }

        // padding on both sides plus a little extra room
        width += 8 + 8 + 10;
        javax.swing.table.TableColumn tableColumn = digitalLibraryTable.getColumnModel().getColumn(column);
        tableColumn.setMinWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setPreferredWidth(width);
    }

    /*
     * method: buildStatusBar
     * parameters: NONE
     * return: JPanel
     * purpose: builds the plain pink bar along the bottom (updates now appear as pop ups instead)
     */

    private JPanel buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(blushPink);
        statusBar.setPreferredSize(new Dimension(0, 30));
        return statusBar;
    }

    /*
     * method: makeButton
     * parameters: String text
     * return: JButton
     * purpose: makes a toolbar button with the app's colours and font, the rounded outline
     *          is drawn in roundedButtonUI
     */

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(blushPink);
        button.setForeground(berryPink);
        button.setFont(bodyFont);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // padding, the rounded outline is drawn in roundedButtonUI
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return button;
    }

    /*
     * method: connectToDatabase
     * parameters: NONE
     * return: boolean
     * purpose: asks the user for their MySQL server address, username and password and connects
     *          to the theReadersArchive database, keeps asking (with what was typed) until it connects
     *          or the user cancels, returns true if connected
     */

    private boolean connectToDatabase() {
        JTextField serverField = new centredTextField(22);
        JTextField usernameField = new centredTextField(22);
        JPasswordField passwordField = new JPasswordField(22);

        JPanel connectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        connectPanel.add(new JLabel("Please enter your MySQL details to open The Reader's Archive database:"), constraints);
        constraints.gridwidth = 1;

        String[] labelTexts = {
                "Server address",
                "Username",
                "Password"
        };

        JComponent[] fields = {
                serverField,
                usernameField,
                passwordField
        };

        int row = 1;
        for (int i = 0; i < fields.length; i++) {
            constraints.gridy = row++;
            constraints.gridx = 0;
            constraints.weightx = 0;
            JLabel label = new JLabel(labelTexts[i]);

            // moves the label down a little to line up with the calibri text
            label.setBorder(BorderFactory.createEmptyBorder(textNudge, 0, 0, 0));
            connectPanel.add(label, constraints);
            constraints.gridx = 1;
            constraints.weightx = 1;
            connectPanel.add(fields[i], constraints);

            if (fields[i] == serverField) {

                // just an example, the user has to type their own server address
                constraints.gridy = row++;
                JLabel serverHint = new JLabel("for example: localhost:3306");
                serverHint.setFont(bodyFont);
                connectPanel.add(serverHint, constraints);
            }
        }

        Object[] connectButtons = {"Connect", "Cancel"};
        while (true) {
            Object choice = showResizableDialog(connectPanel, "Connect to Database", JOptionPane.OK_CANCEL_OPTION, connectButtons);
            if (!"Connect".equals(choice)) {
                return false;
            }

            String serverAddress = serverField.getText().trim();
            String username = usernameField.getText().trim();
            if (serverAddress.isBlank() || username.isBlank()) {
                showUpdate("Connect to Database", "Please enter the server address and username.");
                continue;
            }

            try {
                library.connect(serverAddress, username, new String(passwordField.getPassword()));
                setLibraryNameText("No library open");
                updateButtonStates();
                return true;
            } catch (SQLException ex) {
                showDatabaseError("connect to the database", ex);
            }
        }
    }

    /*
     * method: openLibrary
     * parameters: NONE
     * return: VOID
     * purpose: connects to the database first if needed, then lets the user pick an existing
     *          library or type a new name to create one, and loads it into the table
     */

    private void openLibrary() {
        if (!library.isDatabaseConnected() && !connectToDatabase()) {
            return;
        }

        JComboBox<String> libraryPicker = new JComboBox<>();
        libraryPicker.setEditable(true);
        try {
            for (String libraryName : library.getLibraryNames()) {
                libraryPicker.addItem(libraryName);
            }
        } catch (SQLException ex) {
            showDatabaseError("load the list of libraries", ex);
            return;
        }

        // starts empty instead of filling in the first library
        libraryPicker.setSelectedIndex(-1);

        JPanel pickerPanel = new JPanel(new BorderLayout(0, 6));
        pickerPanel.add(new JLabel("Choose a library, or type a new name to create one:"), BorderLayout.NORTH);
        pickerPanel.add(libraryPicker, BorderLayout.CENTER);

        Object choice = showResizableDialog(pickerPanel, "Open Library", JOptionPane.OK_CANCEL_OPTION, null);
        if (!Integer.valueOf(JOptionPane.OK_OPTION).equals(choice)) {
            return;
        }

        Object pickedItem = libraryPicker.getEditor().getItem();
        String libraryName = (pickedItem == null) ? "" : pickedItem.toString().trim();
        if (libraryName.isBlank()) {
            return;
        }

        boolean isNewLibrary;
        try {
            isNewLibrary = library.openLibrary(libraryName);
        } catch (SQLException ex) {
            showDatabaseError("open the library", ex);
            return;
        }

        // the database gives back the name the way it was first created
        String displayName = library.getActiveLibraryName();
        digitalLibraryTable.getRowSorter().setSortKeys(null);
        tableRefresh();
        updateButtonStates();
        setLibraryNameText("Current Library: " + displayName);
        showUpdate("Open Library", (isNewLibrary ? "Created" : "Opened") + " \"" + displayName + "\".");
    }

    /*
     * method: addBook
     * parameters: NONE
     * return: VOID
     * purpose: shows the book form and saves the new book to the open library in the database
     */

    private void addBook() {
        if (!libraryIsOpen()) {
            showUpdate("Add a Book", "Open a library first, then you can add books :)");
            return;
        }
        bookForm form = new bookForm(null);
        if (!form.showDialog("Add a Book", "Add Book")) {
            return;
        }

        bookDetails newBook = new bookDetails(
                form.bookTitle(),
                form.authorFirstName(),
                form.authorLastName(),
                form.bookGenre(),
                form.bookSubGenre(),
                form.readingStatus(),
                form.bookSummary(),
                form.bookRating(),
                // book ID is given by the library
                0
        );

        try {
            library.addNewBook(newBook);
        } catch (SQLException ex) {
            showDatabaseError("add the book", ex);
            return;
        }

        tableRefresh();
        selectBookByID(newBook.getBookID());
        showUpdate("Book Added", "Added \"" + newBook.getBookTitle() + "\" to your library.");
    }

    /*
     * method: editSelectedBook
     * parameters: NONE
     * return: VOID
     * purpose: shows the book form filled in with the selected book's details and saves any
     *          changes to the database
     */

    private void editSelectedBook() {
        bookDetails selectedBook = getSelectedBook();
        if (selectedBook == null) {
            return;
        }
        bookForm form = new bookForm(selectedBook);
        if (!form.showDialog("Edit Book Details", "Save Changes")) {
            return;
        }

        selectedBook.setBookTitle(form.bookTitle());
        selectedBook.setAuthorFirstName(form.authorFirstName());
        selectedBook.setAuthorLastName(form.authorLastName());
        selectedBook.setBookGenre(form.bookGenre());
        selectedBook.setBookSubGenre(form.bookSubGenre());
        selectedBook.setReadingStatus(form.readingStatus());
        selectedBook.setBookSummary(form.bookSummary());
        selectedBook.setBookRating(form.bookRating());

        try {
            library.updateBook(selectedBook);
        } catch (SQLException ex) {
            showDatabaseError("save the changes", ex);
            return;
        }
        tableRefresh();
        selectBookByID(selectedBook.getBookID());
        showUpdate("Book Updated", "Saved changes to \"" + selectedBook.getBookTitle() + "\".");
    }

    /*
     * method: removeSelectedBook
     * parameters: NONE
     * return: VOID
     * purpose: asks the user to confirm, then removes the selected book from the database
     */

    private void removeSelectedBook() {
        bookDetails selectedBook = getSelectedBook();
        if (selectedBook == null) {
            return;
        }
        boolean confirmed = askYesNo("Remove \"" + selectedBook.getBookTitle() + "\" by "
                + selectedBook.getAuthorFirstName() + " " + selectedBook.getAuthorLastName()
                + " from your library?", "Remove Book");
        if (!confirmed) {
            return;
        }

        try {
            library.removeBookByID(selectedBook.getBookID());
        } catch (SQLException ex) {
            showDatabaseError("remove the book", ex);
            return;
        }
        tableRefresh();
        showUpdate("Book Removed", "Removed \"" + selectedBook.getBookTitle() + "\" from your library.");
    }

    /*
     * method: sortLibrary
     * parameters: NONE
     * return: VOID
     * purpose: custom feature: sorts the library alphabetically by author's last name
     */

    private void sortLibrary() {
        if (!libraryIsOpen() || digitalLibraryModel.getRowCount() == 0) {
            showUpdate("Sort by Author", "This library is empty, add some books to sort it!");
            return;
        }

        library.sortLibraryByAuthorLastName();

        // clears any sorting from clicking the column headers so the table shows the library's order
        digitalLibraryTable.getRowSorter().setSortKeys(null);
        tableRefresh();
        showUpdate("Sort by Author", "Sorted by author's last name, A-Z.");
    }

    /*
     * method: deleteLibrary
     * parameters: NONE
     * return: VOID
     * purpose: asks the user to confirm, then permanently deletes the open library and its
     *          books from the database
     */

    private void deleteLibrary() {
        if (!libraryIsOpen()) {
            return;
        }
        String displayName = library.getActiveLibraryName();
        boolean confirmed = askYesNo("This will permanently delete the library \""
                + displayName + "\" and every book in it.\nWould you like to proceed?", "Delete Library");
        if (!confirmed) {

            // nothing else to do as the user chose NO
            return;
        }

        try {
            library.deleteActiveLibrary();
        } catch (SQLException ex) {
            showDatabaseError("delete the library", ex);
            return;
        }

        tableRefresh();
        updateButtonStates();
        setLibraryNameText("No library open");
        showUpdate("Library Deleted", "Deleted \"" + displayName + "\". Open or create another library to continue.");
    }

    /*
     * method: showDatabaseError
     * parameters: String action, SQLException ex
     * return: VOID
     * purpose: tells the user what could not be done and why, with a clearer message when the
     *          MySQL driver has not been added to the project
     */

    private void showDatabaseError(String action, SQLException ex) {
        String reason = ex.getMessage();
        if (reason != null && reason.contains("No suitable driver")) {
            reason = "The MySQL driver (MySQL Connector/J) has not been added to the project.";
        }
        showUpdate("Database Error", "Could not " + action + ".\n" + reason);
    }

    /*
     * method: getSelectedBook
     * parameters: NONE
     * return: bookDetails
     * purpose: gets the selected book using its ID from the table (the user never types the ID),
     *          returns null if no book is selected
     */

    private bookDetails getSelectedBook() {
        int viewRow = digitalLibraryTable.getSelectedRow();
        if (viewRow == -1) {
            return null;
        }

        // still works when the table is sorted
        int modelRow = digitalLibraryTable.convertRowIndexToModel(viewRow);
        int bookID = Integer.parseInt(String.valueOf(digitalLibraryModel.getValueAt(modelRow, idColumn)));
        try {
            return library.getBookByID(bookID);
        } catch (SQLException ex) {
            showDatabaseError("load the book", ex);
            return null;
        }
    }

    /*
     * method: selectBookByID
     * parameters: int bookID
     * return: VOID
     * purpose: selects the book with this ID in the table and scrolls to it
     *          (used after adding or editing so the user can see the book)
     */

    private void selectBookByID(int bookID) {
        for (int modelRow = 0; modelRow < digitalLibraryModel.getRowCount(); modelRow++) {
            if (String.valueOf(digitalLibraryModel.getValueAt(modelRow, idColumn)).equals(String.valueOf(bookID))) {
                int viewRow = digitalLibraryTable.convertRowIndexToView(modelRow);
                if (viewRow != -1) {
                    digitalLibraryTable.setRowSelectionInterval(viewRow, viewRow);
                    digitalLibraryTable.scrollRectToVisible(digitalLibraryTable.getCellRect(viewRow, 0, true));
                }
                return;
            }
        }
    }

    /*
     * method: updateButtonStates
     * parameters: NONE
     * return: VOID
     * purpose: turns buttons on or off: add, sort and delete need an open library,
     *          edit and remove need a selected book
     */

    private void updateButtonStates() {
        boolean libraryOpen = libraryIsOpen();
        boolean bookSelected = digitalLibraryTable.getSelectedRow() != -1;
        addBookButton.setEnabled(libraryOpen);
        sortLibraryButton.setEnabled(libraryOpen);
        deleteLibraryButton.setEnabled(libraryOpen);
        editBookButton.setEnabled(bookSelected);
        removeBookButton.setEnabled(bookSelected);
    }

    /*
     * method: showPopup
     * parameters: Object message, String title, int optionType, Object[] buttons, boolean resizable
     * return: Object
     * purpose: every pop up goes through here so they all look the same, returns the button that
     *          was clicked (null if the pop up was closed with the X)
     */

    private Object showPopup(Object message, String title, int optionType, Object[] buttons, boolean resizable) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, optionType,
                null, buttons, (buttons == null) ? null : buttons[0]);
        JDialog dialog;

        // makes the pop up's title bar plain pink
        pinkTheme.buildingTitleBar = true;
        try {
            dialog = optionPane.createDialog(menuFrame, title);
        } finally {
            pinkTheme.buildingTitleBar = false;
        }
        dialog.setResizable(resizable);

        // waits here until a button is clicked or the pop up is closed
        dialog.setVisible(true);
        dialog.dispose();
        return optionPane.getValue();
    }

    /*
     * method: showResizableDialog
     * parameters: JComponent content, String title, int optionType, Object[] buttons
     * return: Object
     * purpose: shows a pop up with a form or picker inside that can be resized by dragging the edges,
     *          returns the button that was clicked
     */

    private Object showResizableDialog(JComponent content, String title, int optionType, Object[] buttons) {
        return showPopup(content, title, optionType, buttons, true);
    }

    /*
     * method: askYesNo
     * parameters: String question, String title
     * return: boolean
     * purpose: asks the user a yes or no question, returns true if yes was clicked
     */

    private boolean askYesNo(String question, String title) {
        Object choice = showPopup(question, title, JOptionPane.YES_NO_OPTION, null, false);
        return Integer.valueOf(JOptionPane.YES_OPTION).equals(choice);
    }

    /*
     * method: showUpdate
     * parameters: String title, String message
     * return: VOID
     * purpose: shows an update pop up (book added, library deleted, etc.) that the user closes with OK
     */

    private void showUpdate(String title, String message) {
        showPopup(message, title, JOptionPane.DEFAULT_OPTION, null, false);
    }

    // padding for dropdown rows, centredInsets() decides how much goes above and below the text
    private static final javax.swing.border.Border dropdownRowPadding = BorderFactory.createEmptyBorder(2, 4, 2, 4);

    /*
     * method: centredInsets
     * parameters: JComponent component, Insets normal
     * return: Insets
     * purpose: calibri sits too high on windows and too low on other computers,this measures
     *          the letters and moves the padding around until the text is in the middle
     *          (used by the text boxes and dropdown rows)
     */

    static Insets centredInsets(JComponent component, Insets normal) {
        int height = component.getHeight();
        if (height <= 0 || component.getFont() == null) {
            return normal;
        }
        FontMetrics metrics = component.getFontMetrics(component.getFont());
        double capHeight = component.getFont()
                .createGlyphVector(metrics.getFontRenderContext(), "H").getVisualBounds().getHeight();
        int verticalPadding = normal.top + normal.bottom;
        int innerHeight = height - verticalPadding;

        // where the bottom of the letters should be to look centred
        double targetBaseline = (height + capHeight) / 2.0;

        int bestTop = normal.top;
        double bestGap = Double.MAX_VALUE;
        for (int top = 0; top <= verticalPadding; top++) {

            // tries each amount of padding on top to see which one gets closest
            double baseline = top + (innerHeight - metrics.getHeight()) / 2.0 + metrics.getAscent();
            double gap = Math.abs(baseline - targetBaseline);
            if (gap < bestGap) {
                bestGap = gap;
                bestTop = top;
            }
        }
        return new Insets(bestTop, normal.left, verticalPadding - bestTop, normal.right);
    }

    /*
     * method: centredFont
     * parameters: Font font
     * return: Font
     * purpose: for text java places by itself (like the pop up titles), moves the font up or down
     *          so calibri looks centred
     */

    static Font centredFont(Font font) {
        FontMetrics metrics = new JLabel().getFontMetrics(font);
        double capHeight = font.createGlyphVector(metrics.getFontRenderContext(), "H").getVisualBounds().getHeight();

        // how far the letters need to move to be in the middle
        double shift = (capHeight + metrics.getHeight()) / 2.0 - metrics.getAscent();
        return font.deriveFont(java.awt.geom.AffineTransform.getTranslateInstance(0, shift));
    }

    // smallest space the library name gets to the left of the buttons (window can not shrink past this)
    private static final int libraryNameWidth = 200;

    // the name before it gets wrapped
    private String libraryNameText = "";

    // width it was last wrapped to
    private int libraryNameWrapWidth = -1;

    // space the toolbar is giving the name right now
    private int libraryNameSpace = libraryNameWidth;

    /*
     * method: setLibraryNameText
     * parameters: String text
     * return: VOID
     * purpose: changes the library name shown in the toolbar, wrapping it to the space it actually
     *          has so the toolbar does not jump to two lines
     */

    private void setLibraryNameText(String text) {
        libraryNameText = text;

        // forces it to wrap again
        libraryNameWrapWidth = -1;

        // wraps the name to fit the space it has right now
        wrapLibraryName(libraryNameSpace);
        libraryNameLabel.revalidate();
    }

    /*
     * method: wrapLibraryName
     * parameters: int width
     * return: VOID
     * purpose: fits the library name into the width, only starting a new line when the next word
     *          does not fit (each word is measured with the real font)
     */

    private void wrapLibraryName(int width) {
        if (width == libraryNameWrapWidth) {

            // already wrapped for this width
            return;
        }
        libraryNameWrapWidth = width;

        // measures each word with the real font
        FontMetrics metrics = libraryNameLabel.getFontMetrics(libraryNameLabel.getFont());

        // the 10px indent plus a little extra room
        int availableWidth = width - 12;
        StringBuilder wrappedText = new StringBuilder("<html>");
        String currentLine = "";
        for (String word : libraryNameText.split(" ")) {
            String longerLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (!currentLine.isEmpty() && metrics.stringWidth(longerLine) > availableWidth) {
                wrappedText.append(htmlSafe(currentLine)).append("<br>");
                currentLine = word;
            } else {
                currentLine = longerLine;
            }
        }
        wrappedText.append(htmlSafe(currentLine)).append("</html>");
        libraryNameLabel.setText(wrappedText.toString());
    }

    /*
     * method: htmlSafe
     * parameters: String text
     * return: String
     * purpose: returns the text with characters like & and < changed so they are not read as html
     */

    private static String htmlSafe(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /*
     * method: libraryIsOpen
     * parameters: NONE
     * return: boolean
     * purpose: returns true if a library is currently open
     */

    private boolean libraryIsOpen() {
        return library.isLibraryOpen();
    }

    // star symbols come from starSymbols() in libraryMenu, "No rating" is shorter so it fits in the table
    private static final libraryMenu starMaker = new libraryMenu();

    /*
     * method: ratingText
     * parameters: double bookRating
     * return: String
     * purpose: returns the star symbols from starSymbols() in libraryMenu, or "No rating" for 0
     *          (shorter than the console wording so it fits in the table)
     */

    private static String ratingText(double bookRating) {
        return (bookRating == 0.0) ? "No rating" : starMaker.starSymbols(bookRating);
    }

    /*
     * method: pickStarFont
     * parameters: NONE
     * return: Font
     * purpose: returns the first installed font that has all three star symbols (★ ⯪ ☆),
     *          since calibri does not have them
     */

    private static Font pickStarFont() {
        String[] fontChoices = {
                "Segoe UI Symbol",
                "Segoe UI Emoji",
                "Dialog"
        };
        for (String fontName : fontChoices) {
            Font candidate = new Font(fontName, Font.PLAIN, 14);
            if (candidate.canDisplayUpTo("★⯪☆") == -1) {
                return candidate;
            }
        }
        return new Font("Dialog", Font.PLAIN, 14);
    }

    /*
     * method: cleanInput
     * parameters: String text
     * return: String
     * purpose: trims what the user typed and turns line breaks into spaces so every detail
     *          shows on one line in the table
     */

    private static String cleanInput(String text) {
        return text.replace("\r", " ").replace("\n", " ").trim();
    }

    /*
     * method: loadPixelImage
     * parameters: int size
     * return: ImageIcon
     * purpose: loads the pixel book image for the window icon and keeps the pixels sharp when
     *          it's resized, returns NULL if bookImage.png is not found
     */

    private ImageIcon loadPixelImage(int size) {
        File imageFile = new File(System.getProperty("user.dir"), "bookImage.png");
        if (!imageFile.exists()) {
            return null;
        }
        Image scaledImage = new ImageIcon(imageFile.getAbsolutePath()).getImage()
                .getScaledInstance(size, size, Image.SCALE_REPLICATE);
        return new ImageIcon(scaledImage);
    }

    /*
     * method: tableRefresh
     * parameters: NONE
     * return: VOID
     * purpose: fills the table with the books from the open library and updates the book count
     */

    private void tableRefresh() {
        digitalLibraryModel.setRowCount(0);
        ArrayList<bookDetails> bookShelf = new ArrayList<>();
        if (libraryIsOpen()) {
            try {
                bookShelf = library.getBookShelf();
            } catch (SQLException ex) {
                showDatabaseError("load the books", ex);
            }
        }
        for (bookDetails book : bookShelf) {
            digitalLibraryModel.addRow(new Object[]{
                    book.getBookID(),
                    book.getBookTitle(),
                    book.getAuthorFirstName() + " " + book.getAuthorLastName(),
                    book.getBookGenre(),
                    book.getBookSubGenre(),
                    book.getReadingStatus(),
                    book.getBookSummary(),
                    ratingText(book.getBookRating())
            });
        }
        updateBookCount();
    }

    /*
     * method: updateBookCount
     * parameters: NONE
     * return: VOID
     * purpose: shows "1 book" or "5 books" under the table, blank when no library is open
     *          (but still takes up its space so the table does not move)
     */

    private void updateBookCount() {
        if (!libraryIsOpen()) {
            bookCountLabel.setText(" ");
            return;
        }
        int bookCount = digitalLibraryModel.getRowCount();
        bookCountLabel.setText(bookCount == 1 ? "1 book" : bookCount + " books");
    }


    /*
     * class: bookForm
     * one form for adding AND editing books (instead of a pop up for each detail), used by addBook
     * and editSelectedBook, the small methods at the bottom (bookTitle, authorFirstName, etc.)
     * return what the user typed, cleaned up so it can be saved
     */

    private class bookForm {
        private final JTextField titleField = new centredTextField(24);
        private final JTextField firstNameField = new centredTextField(24);
        private final JTextField lastNameField = new centredTextField(24);
        private final JTextField genreField = new centredTextField(24);
        private final JTextField subGenreField = new centredTextField(24);
        private final JComboBox<String> readingStatusBox = new JComboBox<>(readingStatusOptions);
        private final JTextArea summaryArea = new JTextArea(4, 24);
        private final JComboBox<Double> ratingBox = new JComboBox<>();
        private final JPanel formPanel = new JPanel(new GridBagLayout());

        bookForm(bookDetails existingBook) {
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);

            // ratings from 0 (no rating) to 5 in half stars, shown as star symbols
            for (double rating = 0.0; rating <= 5.0; rating += 0.5) {
                ratingBox.addItem(rating);
            }

            // shows every option without scrolling
            ratingBox.setMaximumRowCount(11);
            ratingBox.setFont(starFont);
            ratingBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "" : ratingText((Double) value));

                    // stars need the symbol font, "No rating" stays calibri to match the table
                    boolean noRating = (value == null) || ((Double) value) == 0.0;
                    setFont(noRating ? bodyFont : starFont);
                    setBorder(dropdownRowPadding);
                    return this;
                }

                @Override
                public Insets getInsets() {

                    // keeps the text centred
                    return centredInsets(this, super.getInsets());
                }

                @Override
                public Insets getInsets(Insets reuse) {

                    // labels use this one when drawing, so it needs the same padding
                    return getInsets();
                }
            });

            if (existingBook != null) {
                titleField.setText(existingBook.getBookTitle());
                firstNameField.setText(existingBook.getAuthorFirstName());
                lastNameField.setText(existingBook.getAuthorLastName());
                genreField.setText(existingBook.getBookGenre());
                subGenreField.setText(existingBook.getBookSubGenre());
                readingStatusBox.setSelectedItem(existingBook.getReadingStatus());
                summaryArea.setText(existingBook.getBookSummary());
                double existingRating = existingBook.getBookRating();

                // older ratings that are not a half step (like 4.3) get added so they are not lost
                if (((DefaultComboBoxModel<Double>) ratingBox.getModel()).getIndexOf(existingRating) == -1) {
                    ratingBox.addItem(existingRating);
                }
                ratingBox.setSelectedItem(existingRating);
            }

            // the closed dropdown has its own font: symbol font for stars, calibri for "No rating"
            Runnable matchRatingFont = () -> {
                Double picked = (Double) ratingBox.getSelectedItem();
                ratingBox.setFont((picked == null || picked == 0.0) ? bodyFont : starFont);
            };
            matchRatingFont.run();
            ratingBox.addActionListener(e -> matchRatingFont.run());

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(4, 4, 4, 4);
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            // the last number is how much extra height a row gets when the window is resized,
            // only the summary box gets taller, everything else just gets wider
            addRow("Title *", titleField, 0, constraints, 0);
            addRow("Author's First Name *", firstNameField, 1, constraints, 0);
            addRow("Author's Last Name *", lastNameField, 2, constraints, 0);
            addRow("Genre *", genreField, 3, constraints, 0);
            addRow("Sub-Genre *", subGenreField, 4, constraints, 0);
            addRow("Reading Status", readingStatusBox, 5, constraints, 0);
            addRow("Summary", new JScrollPane(summaryArea), 6, constraints, 1);
            addRow("Book Rating", ratingBox, 7, constraints, 0);
        }

        /*
         * method: addRow
         * parameters: String labelText, JComponent field, int row, GridBagConstraints constraints, double rowGrowth
         * return: VOID
         * purpose: adds a label and its box to the form, rowGrowth is how much extra height the row
         *          gets when the window is resized (only the summary box grows taller)
         */

        private void addRow(String labelText, JComponent field, int row, GridBagConstraints constraints, double rowGrowth) {
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.weightx = 0;
            constraints.weighty = rowGrowth;
            constraints.fill = GridBagConstraints.HORIZONTAL;

            // labels line up with their box (summary label stays at the top of its taller box)
            constraints.anchor = (rowGrowth > 0) ? GridBagConstraints.NORTHWEST : GridBagConstraints.WEST;
            JLabel label = new JLabel(labelText);
            label.setFont(bodyBoldFont);
            label.setForeground(berryPink);

            // moves the label down a little to line up with the calibri text
            label.setBorder(BorderFactory.createEmptyBorder(textNudge, 0, 0, 0));
            formPanel.add(label, constraints);

            constraints.gridx = 1;
            constraints.weightx = 1;
            constraints.fill = (rowGrowth > 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
            formPanel.add(field, constraints);
        }

        /*
         * method: showDialog
         * parameters: String dialogTitle, String saveButtonText
         * return: boolean
         * purpose: shows the form and keeps it open (with what was typed) until it's filled in or
         *          cancelled, returns true if the user saved
         */

        boolean showDialog(String dialogTitle, String saveButtonText) {
            Object[] dialogButtons = {saveButtonText, "Cancel"};
            while (true) {
                Object choice = showResizableDialog(formPanel, dialogTitle, JOptionPane.OK_CANCEL_OPTION, dialogButtons);
                if (!saveButtonText.equals(choice)) {
                    return false;
                }
                String missingField = findMissingField();
                if (missingField == null) {
                    return true;
                }
                showUpdate("Missing Detail", missingField + " cannot be left blank. Please fill it in!");
            }
        }

        /*
         * method: findMissingField
         * parameters: NONE
         * return: String
         * purpose: returns the name of the first required field left blank, or null if they're all filled in
         */

        private String findMissingField() {
            if (bookTitle().isBlank()) return "Title";
            if (authorFirstName().isBlank()) return "Author's First Name";
            if (authorLastName().isBlank()) return "Author's Last Name";
            if (bookGenre().isBlank()) return "Genre";
            if (bookSubGenre().isBlank()) return "Sub-Genre";
            return null;
        }

        String bookTitle() { return cleanInput(titleField.getText()); }
        String authorFirstName() { return cleanInput(firstNameField.getText()); }
        String authorLastName() { return cleanInput(lastNameField.getText()); }
        String bookGenre() { return cleanInput(genreField.getText()); }
        String bookSubGenre() { return cleanInput(subGenreField.getText()); }
        String readingStatus() { return (String) readingStatusBox.getSelectedItem(); }

        String bookSummary() {
            String summary = cleanInput(summaryArea.getText());
            return summary.isBlank() ? noSummaryText : summary;
        }

        double bookRating() {
            Double pickedRating = (Double) ratingBox.getSelectedItem();
            return (pickedRating == null) ? 0.0 : pickedRating;
        }
    }

    /*
     * method: applyDialogTheme
     * parameters: NONE
     * return: VOID
     * purpose: gives every pop up (open library, the book form, confirmations) the app's colours,
     *          fonts, rounded buttons and pink dropdowns, has to run before any window is made
     */

    private static void applyDialogTheme() {

        // swaps java's blue/grey theme for the pink one (borders, scrollbars, title bars)
        try {
            javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new pinkTheme());
            UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {

            // keeps the default look if the pink theme can not be used
        }

        // pop ups draw their own title bar so it can be pink instead of the system's grey one
        JDialog.setDefaultLookAndFeelDecorated(true);

        // pop up titles: calibri moved down by the measured amount so it's centred in the title bar
        UIManager.put("InternalFrame.titleFont", centredFont(bodyBoldFont));

        // clear, bold X
        UIManager.put("InternalFrame.closeIcon", new closeIcon());

        // title bar stays the same pink and berry text when you click away from a pop up
        UIManager.put("inactiveCaption", blushPink);
        UIManager.put("inactiveCaptionText", berryPink);
        UIManager.put("inactiveCaptionBorder", berryPink);

        // simple pink outline around every pop up (instead of java's frame with the corner marks)
        UIManager.put("RootPane.plainDialogBorder",
                new javax.swing.plaf.BorderUIResource.LineBorderUIResource(berryPink, 2));

        UIManager.put("OptionPane.background", creamWhite);
        UIManager.put("Panel.background", creamWhite);
        UIManager.put("OptionPane.messageForeground", berryPink);
        UIManager.put("OptionPane.messageFont", bodyBoldFont);
        UIManager.put("Label.foreground", berryPink);
        UIManager.put("Label.font", bodyBoldFont);

        UIManager.put("Button.background", blushPink);
        UIManager.put("Button.foreground", berryPink);
        UIManager.put("Button.font", bodyFont);
        UIManager.put("Button.border", BorderFactory.createEmptyBorder(6, 18, 6, 18));

        // every button (pop ups too) gets rounded corners
        UIManager.put("ButtonUI", roundedButtonUI.class.getName());

        // every dropdown gets a pink arrow button and outline
        UIManager.put("ComboBoxUI", pinkComboBoxUI.class.getName());

        UIManager.put("TextField.font", bodyFont);

        // room above and below the text, centredTextField decides how to split it
        UIManager.put("TextField.margin", new javax.swing.plaf.InsetsUIResource(2, 4, 2, 4));

        // dropdowns are centred in pinkComboBoxUI instead
        UIManager.put("ComboBox.padding", new javax.swing.plaf.InsetsUIResource(0, 2, 0, 2));

        // summary box in the book form
        UIManager.put("TextArea.font", bodyFont);

        // list that drops down from a dropdown
        UIManager.put("List.font", bodyFont);
        UIManager.put("ToolTip.font", bodyFont);
        UIManager.put("OptionPane.buttonFont", bodyFont);
        UIManager.put("TextField.foreground", inkBrown);
        UIManager.put("TextField.selectionBackground", berryPink);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("PasswordField.font", bodyFont);
        UIManager.put("PasswordField.margin", new javax.swing.plaf.InsetsUIResource(2, 4, 2, 4));
        UIManager.put("PasswordField.foreground", inkBrown);
        UIManager.put("PasswordField.selectionBackground", berryPink);
        UIManager.put("PasswordField.selectionForeground", Color.WHITE);
        UIManager.put("TextArea.selectionBackground", berryPink);
        UIManager.put("TextArea.selectionForeground", Color.WHITE);

        UIManager.put("ComboBox.font", bodyFont);
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", inkBrown);
        UIManager.put("ComboBox.selectionBackground", berryPink);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
    }


    /*
     * class: roundedButtonUI
     * draws every button in the app (pop ups too) with a rounded background and outline instead
     * of java's square buttons
     */

    public static class roundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        private static final int cornerRadius = 18;
        private static final Color hoverPink = softRowPink;
        private static final Color pressedPink = new Color(245, 170, 200);
        private static final Color disabledPink = new Color(205, 160, 180);

        /*
         * method: createUI
         * parameters: JComponent component
         * return: javax.swing.plaf.ComponentUI
         * purpose: java calls this to make the look for each button, the small arrow buttons
         *          (dropdowns, scrollbars) keep their normal style and everything else gets rounded
         */

        public static javax.swing.plaf.ComponentUI createUI(JComponent component) {

            // leaves the small arrow buttons (dropdowns, scrollbars) in their normal style
            if (component instanceof javax.swing.plaf.basic.BasicArrowButton
                    || component.getClass().getName().contains("ComboBox")) {
                return javax.swing.plaf.metal.MetalButtonUI.createUI(component);
            }
            return new roundedButtonUI();
        }

        /*
         * method: installDefaults
         * parameters: AbstractButton button
         * return: VOID
         * purpose: makes the button see-through around its rounded corners and turns on the hover colour
         */

        @Override
        protected void installDefaults(AbstractButton button) {
            super.installDefaults(button);

            // so the square corners behind it stay see-through
            button.setOpaque(false);

            // needed for the hover colour
            button.setRolloverEnabled(true);
        }

        /*
         * method: paint
         * parameters: Graphics g, JComponent component
         * return: VOID
         * purpose: draws the rounded background and outline (lighter when hovered, deeper when clicked),
         *          the X in pop up title bars gets the same hover colours but no rounded box
         */

        @Override
        public void paint(Graphics g, JComponent component) {
            AbstractButton button = (AbstractButton) component;
            ButtonModel buttonState = button.getModel();

            // the X button in a pop up's title bar: no rounded box, but the same hover and click
            // colours as the other buttons, filled inside the X's square
            if (button.getParent() != null && button.getParent().getClass().getName().contains("TitlePane")) {
                if (buttonState.isPressed() || buttonState.isRollover()) {
                    int iconX = (button.getWidth() - closeIcon.iconSize) / 2;
                    int iconY = (button.getHeight() - closeIcon.iconSize) / 2;
                    g.setColor(buttonState.isPressed() ? pressedPink : hoverPink);
                    g.fillRect(iconX + 1, iconY + 1, closeIcon.iconSize - 2, closeIcon.iconSize - 2);
                }
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // draws the X and its square on top
                super.paint(g, component);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fillColour;
            if (!button.isEnabled()) {
                fillColour = hoverPink;
            } else if (buttonState.isPressed()) {
                fillColour = pressedPink;
            } else if (buttonState.isRollover()) {
                fillColour = hoverPink;
            } else {
                fillColour = button.getBackground();
            }

            int width = button.getWidth();
            int height = button.getHeight();
            g2.setColor(fillColour);
            g2.fillRoundRect(1, 1, width - 2, height - 2, cornerRadius, cornerRadius);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(button.isEnabled() ? button.getForeground() : disabledPink);
            g2.drawRoundRect(1, 1, width - 3, height - 3, cornerRadius, cornerRadius);

            // draws the text on top
            super.paint(g2, component);
            g2.dispose();
        }

        /*
         * method: paintText
         * parameters: Graphics g, AbstractButton button, Rectangle textRect, String text
         * return: VOID
         * purpose: draws the button text centred by the height of the capital letters (calibri has extra
         *          space below the letters which pushes text up), plain colour when disabled
         */

        @Override
        protected void paintText(Graphics g, AbstractButton button, Rectangle textRect, String text) {
            g.setFont(button.getFont());
            g.setColor(button.isEnabled() ? button.getForeground() : disabledPink);

            // centred using the height of the capital letters instead of the font's height
            // (calibri has extra space below the letters which pushes text up)
            java.awt.font.FontRenderContext renderContext = ((Graphics2D) g).getFontRenderContext();
            double capHeight = button.getFont().createGlyphVector(renderContext, "H").getVisualBounds().getHeight();
            int baseline = (int) Math.round((button.getHeight() + capHeight) / 2.0);
            g.drawString(text, textRect.x, baseline);
        }
    }


    /*
     * class: pinkTheme
     * replaces the blues and greys java uses for borders, scrollbars, outlines and pop up title
     * bars with the app's pinks, each method just returns which pink to use for that part
     */

    public static class pinkTheme extends javax.swing.plaf.metal.DefaultMetalTheme {
        private static final javax.swing.plaf.ColorUIResource darkPink = new javax.swing.plaf.ColorUIResource(berryPink);
        private static final javax.swing.plaf.ColorUIResource midPink = new javax.swing.plaf.ColorUIResource(gridPink);
        private static final javax.swing.plaf.ColorUIResource lightPink = new javax.swing.plaf.ColorUIResource(blushPink);
        private static final javax.swing.plaf.ColorUIResource mutedPink = new javax.swing.plaf.ColorUIResource(205, 160, 180);
        private static final javax.swing.plaf.ColorUIResource cream = new javax.swing.plaf.ColorUIResource(creamWhite);

        @Override public String getName() { return "Reader's Archive Pink"; }

        // "primary" colours: active title bars, selections, focus outlines
        @Override protected javax.swing.plaf.ColorUIResource getPrimary1() { return darkPink; }
        @Override protected javax.swing.plaf.ColorUIResource getPrimary2() { return midPink; }
        @Override protected javax.swing.plaf.ColorUIResource getPrimary3() { return lightPink; }

        // "secondary" colours: borders, scrollbars, inactive title bars, backgrounds
        @Override protected javax.swing.plaf.ColorUIResource getSecondary1() { return mutedPink; }
        @Override protected javax.swing.plaf.ColorUIResource getSecondary2() { return midPink; }
        @Override protected javax.swing.plaf.ColorUIResource getSecondary3() { return cream; }

        @Override public javax.swing.plaf.ColorUIResource getWindowTitleForeground() { return darkPink; }
        @Override public javax.swing.plaf.ColorUIResource getWindowTitleInactiveForeground() { return mutedPink; }

        // java draws a dotted pattern on pop up title bars, so while a pop up is being made
        // (showPopup turns this on) every colour in the pattern is the same pink, which keeps the bar plain,
        // the rest of the time java uses its normal colours since other outlines need them
        static boolean buildingTitleBar = false;

        @Override public javax.swing.plaf.ColorUIResource getPrimaryControlHighlight() {
            return buildingTitleBar ? lightPink : super.getPrimaryControlHighlight();
        }
        @Override public javax.swing.plaf.ColorUIResource getPrimaryControlDarkShadow() {
            return buildingTitleBar ? lightPink : super.getPrimaryControlDarkShadow();
        }
        @Override public javax.swing.plaf.ColorUIResource getControlHighlight() {
            return buildingTitleBar ? lightPink : super.getControlHighlight();
        }
        @Override public javax.swing.plaf.ColorUIResource getControlDarkShadow() {
            return buildingTitleBar ? lightPink : super.getControlDarkShadow();
        }
        @Override public javax.swing.plaf.ColorUIResource getControl() {
            return buildingTitleBar ? lightPink : super.getControl();
        }
    }


    /*
     * class: centredTextField
     * text box that keeps calibri centred on any computer (see centredInsets)
     */

    public static class centredTextField extends JTextField {
        public centredTextField(int columns) { super(columns); }

        /*
         * method: getInsets
         * parameters: NONE
         * return: Insets
         * purpose: returns the padding split so the text sits in the middle (see centredInsets)
         */

        @Override
        public Insets getInsets() {
            return centredInsets(this, super.getInsets());
        }
    }


    /*
     * class: closeIcon
     * the X in pop up title bars: a bold berry pink cross that's easy to see
     */

    public static class closeIcon implements Icon {
        private static final int iconSize = 16;

        /*
         * method: paintIcon
         * parameters: Component component, Graphics g, int x, int y
         * return: VOID
         * purpose: draws the X and the little square outline around it
         */

        @Override
        public void paintIcon(Component component, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(berryPink);

            // little square outline around the X
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(x + 1, y + 1, iconSize - 2, iconSize - 2);

            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int inset = 5;
            g2.drawLine(x + inset, y + inset, x + iconSize - inset, y + iconSize - inset);
            g2.drawLine(x + iconSize - inset, y + inset, x + inset, y + iconSize - inset);
            g2.dispose();
        }

        @Override public int getIconWidth() { return iconSize; }
        @Override public int getIconHeight() { return iconSize; }
    }


    /*
     * class: pinkComboBoxUI
     * pink dropdowns: pink arrow button, berry outline and centred text
     */

    public static class pinkComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        /*
         * method: createUI
         * parameters: JComponent component
         * return: javax.swing.plaf.ComponentUI
         * purpose: java calls this to make the look for each dropdown
         */

        public static javax.swing.plaf.ComponentUI createUI(JComponent component) {
            return new pinkComboBoxUI();
        }

        /*
         * method: installDefaults
         * parameters: NONE
         * return: VOID
         * purpose: gives the dropdown a berry outline and lets the arrow button be its own (wider) size
         */

        @Override
        protected void installDefaults() {
            super.installDefaults();

            // allows the arrow button to be its own size
            squareButton = false;
            comboBox.setBorder(BorderFactory.createLineBorder(berryPink, 2));
        }

        /*
         * method: createRenderer
         * parameters: NONE
         * return: ListCellRenderer<Object>
         * purpose: each row of the list that opens (like your libraries in open library) and the closed
         *          box get centred so the text sits in the middle of the row
         */

        @Override
        protected ListCellRenderer<Object> createRenderer() {
            return new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setBorder(dropdownRowPadding);
                    return this;
                }

                @Override
                public Insets getInsets() {

                    // keeps the text centred
                    return centredInsets(this, super.getInsets());
                }

                @Override
                public Insets getInsets(Insets reuse) {

                    // labels use this one when drawing, it needs the same padding
                    return getInsets();
                }
            };
        }

        /*
         * method: createEditor
         * parameters: NONE
         * return: ComboBoxEditor
         * purpose: the box you type into (open library) is centred like the other text boxes
         */

        @Override
        protected ComboBoxEditor createEditor() {
            return new javax.swing.plaf.basic.BasicComboBoxEditor.UIResource() {
                @Override
                protected JTextField createEditorComponent() {

                    // keeps calibri font centred
                    JTextField typingBox = new centredTextField(9);
                    typingBox.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
                    return typingBox;
                }
            };
        }

        /*
         * method: createArrowButton
         * parameters: NONE
         * return: JButton
         * purpose: makes the pink arrow button (the default one is only 16px wide, this one is bigger)
         */

        @Override
        protected JButton createArrowButton() {

            // arguments: direction, background, shadow, arrow colour, and highlight
            JButton arrowButton = new javax.swing.plaf.basic.BasicArrowButton(
                    SwingConstants.SOUTH, blushPink, blushPink, berryPink, blushPink) {

                // the default arrow button is only 16px wide, this makes it bigger
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(30, 24);
                }
            };
            arrowButton.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, berryPink));
            arrowButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return arrowButton;
        }
    }

    /*
     * method: main
     * parameters: String[] args
     * return: VOID
     * purpose: starts the GUI version of The Reader's Archive
     */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // has to run before ANY window is made
            applyDialogTheme();
            TRAGui gui = new TRAGui();

            // asks for the database details once the window is showing
            SwingUtilities.invokeLater(gui::connectToDatabase);
        });
    }
}