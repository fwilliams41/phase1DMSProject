# The Reader's Archive

Francine Williams

CEN 3024 - Software Development 1

Phase 4

The Reader's Archive is your very own digital library! You can create OR open a library, add, edit and remove books, sort your library by the author's last name, and delete a library.
For Phase 4 of the DMS project, the GUI version (TRAGui) keeps EVERY library and book in a MySQL database instead of .txt files.

---------------
## What you need

- Java and IntelliJ IDEA
- MySQL Server, and your own MySQL username and password
- DataGrip (or another MySQL program like MySQL Workbench) to run the database script
- MySQL Connector/J, the MySQL driver that lets Java talk to the database

---------------
## 1. Make the database

1. Open DataGrip and connect to your MySQL server.
2. Open `theReadersArchive.sql` (it is in this project folder) and run the whole thing.
3. This creates the `theReadersArchive` database with two tables, `digitalLibraries` and `books`, and a sample library called **dataSamples** with 20 books already in it.
4. To check it worked, run `SELECT COUNT(*) FROM theReadersArchive.books;` and it should say 20.

---------------
## 2. Add the MySQL driver
1. Download Connector/J from https://dev.mysql.com/downloads/connector/j/ (**Platform Independent** > the ZIP file).
2. Unzip it and put the `mysql-connector-j` .jar file in the project's `lib` folder.
3. In IntelliJ go to **File > Project Structure > Libraries**, click **+**, pick **Java**, choose the .jar file, then click **Apply** and **OK**.

---------------
## 3. Run it
1. Run `TRAGui` (it has the main method for the GUI).
2. When the **Connect to Database** pop up shows up, type in:
    - **Server address:** your MySQL server and port, for example `localhost:3306`
    - **Username** and **Password:** your own MySQL login
3. Click **Connect**, then **Open Library** and pick **dataSamples**, OR type a new name to make a new library.

Nothing about your server, username or password is saved in the code, you type it in EVERY time.

---------------
## How to use it
- Add Book: fill in the form, anything with a * has to be filled in.
- Edit Book: click a book in the table and click Edit Book, OR just double-click the book.
- Remove Book: click a book and click Remove Book, OR press Delete.
- Sort by Author: sorts the library by the author's last name A-Z.
- Click a column name: to sort the table by that column.
- Delete Library: deletes the open library and ALL of its books for good.

Every change is saved to the database straight away, so you can refresh the tables in DataGrip to see it.