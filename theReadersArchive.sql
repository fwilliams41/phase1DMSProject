
CREATE DATABASE theReadersArchive
DEFAULT CHARACTER SET utf8mb4;
USE theReadersArchive;

CREATE TABLE digitalLibraries (
   libraryID INT AUTO_INCREMENT PRIMARY KEY,
   libraryName VARCHAR(100) NOT NULL UNIQUE
);

# the books table
CREATE TABLE books (
    bookID INT PRIMARY KEY,
    libraryID INT NOT NULL,
    bookTitle VARCHAR(300) NOT NULL,
    authorFirstName VARCHAR(100) NOT NULL,
    authorLastName VARCHAR(100) NOT NULL,
    bookGenre VARCHAR(100) NOT NULL,
    bookSubGenre VARCHAR(100) NOT NULL,
    readingStatus VARCHAR(30) NOT NULL,
    bookSummary TEXT NOT NULL,
    bookRating DECIMAL(2, 1) NOT NULL DEFAULT 0.0,
    orderAsAdded INT NOT NULL AUTO_INCREMENT UNIQUE,

# constraints and checks
  CONSTRAINT fiveDigitBookID
  CHECK (bookID BETWEEN 10000 AND 99999),

    CONSTRAINT bookRatingRange
    CHECK (bookRating BETWEEN 0.0 AND 5.0),

    CONSTRAINT bookLibrary
    FOREIGN KEY (libraryID)
    REFERENCES digitalLibraries (libraryID)
    ON DELETE CASCADE
);

# the sample library data
INSERT INTO digitalLibraries (libraryName) VALUES ('dataSamples');

# populating table with data samples
INSERT INTO books (bookID, libraryID, bookTitle, authorFirstName, authorLastName,
                   bookGenre, bookSubGenre, readingStatus, bookSummary, bookRating)

VALUES
    (10021, 1, 'The Turnglass', 'Gareth', 'Rubin', 'Fiction', 'Mystery', 'Currently Reading',
     'A dual-narrative mystery thriller told in two halves, read by flipping the book over.', 0.0),

    (10022, 1, 'Dune', 'Frank', 'Herbert', 'Sci-Fi', 'Epic Fantasy', 'Currently Reading',
     'A desert planet''s political struggles over the most valuable resource in the universe.', 0.0),

    (92481, 1, 'Pride and Prejudice', 'Jane', 'Austen', 'Romance', 'Classic Romance', 'Finished Reading',
     'There was no book summary provided.', 5.0),

    (15842, 1, 'Emily Wilde''s Encyclopaedia of Faeries', 'Heather', 'Fawcett', 'Fantasy', 'Cozy Fantasy', 'To Be Read',
     'A grumpy professor travels to a remote village to study its hidden faeries for her encyclopaedia.', 0.0),

    (56193, 1, 'Little Women', 'Louisa May', 'Alcott', 'Fiction', 'Classic Drama', 'Finished Reading',
     'There was no book summary provided.', 4.5),

    (63104, 1, 'The Poppy War', 'R.F.', 'Kuang', 'Fantasy', 'Historical Fantasy', 'Finished Reading',
     'A dark military fantasy inspired by 20th-century Chinese history.', 4.5),

    (45912, 1, 'Emma', 'Jane', 'Austen', 'Romance', 'Classic Satire', 'To Be Read',
     'There was no book summary provided.', 0.0),

    (28415, 1, 'Dune Messiah', 'Frank', 'Herbert', 'Sci-Fi', 'Epic Fantasy', 'Finished Reading',
     'The sequel following Paul Atreides as emperor, facing the cost of his own power.', 4.5),

    (91043, 1, 'The Dragon Republic', 'R.F.', 'Kuang', 'Fantasy', 'Historical Fantasy', 'Currently Reading',
     'A war orphan navigates political betrayal and civil war in the second Poppy War book.', 0.0),

    (38529, 1, 'Emily Wilde''s Map of the Otherlands', 'Heather', 'Fawcett', 'Fantasy', 'Cozy Fantasy', 'Finished Reading',
     'Emily sets off on another scholarly adventure to find a door into the faerie realm.', 4.5),

    (11742, 1, 'The Cruel Prince', 'Holly', 'Black', 'Fantasy', 'Enemies to Lovers YA', 'Currently Reading',
     'There was no book summary provided.', 0.0),

    (74918, 1, 'Wings of Starlight', 'Flame', 'O''Reilly', 'Fantasy', 'Romantasy', 'To Be Read',
     'An ethereal celestial fantasy filled with hidden courts and starlit magic.', 0.0),

    (88321, 1, 'The Burning God', 'R.F.', 'Kuang', 'Fantasy', 'Historical Fantasy', 'To Be Read',
     'The explosive final book of the Poppy War trilogy.', 0.0),

    (23659, 1, 'A Court of Thorns and Roses', 'Sarah J.', 'Maas', 'Fantasy', 'Romantic Fantasy', 'Currently Reading',
     'A human huntress is dragged into a magical faerie land after killing a wolf.', 0.0),

    (67412, 1, 'A Game of Thrones', 'George R.R.', 'Martin', 'Fiction', 'Epic Fantasy', 'To Be Read',
     'Noble families of Westeros fight for control of the Iron Throne.', 0.0),

    (52184, 1, 'Happy Place', 'Emily', 'Henry', 'Romance', 'Contemporary Romance', 'Finished Reading',
     'A broken-up couple pretend to still be together during their friends'' yearly holiday.', 4.5),

    (41038, 1, 'The Martian', 'Andy', 'Weir', 'Sci-Fi', 'Hard Science Fiction', 'Finished Reading',
     'An astronaut stranded on Mars must use science to survive until he can be rescued.', 4.5),

    (30419, 1, 'Icebreaker', 'Hannah', 'Grace', 'Romance', 'Sports Romance', 'Currently Reading',
     'A figure skater and a hockey captain collide when they are forced to share the ice.', 0.0),

    (79145, 1, 'Emily Wilde''s Compendium of Lost Tales', 'Heather', 'Fawcett', 'Fantasy', 'Cozy Fantasy', 'Finished Reading',
     'The final whimsical book in the Emily Wilde trilogy.', 4.5),

    (14382, 1, 'The Summer I Turned Pretty', 'Jenny', 'Han', 'Romance', 'Coming of Age YA', 'To Be Read',
     'A coming-of-age summer at the beach house where everything changes.', 0.0);