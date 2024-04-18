import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import book.Book;

public class MemberViewController {
    private Main mainApp;
    private Connection conn;

    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, Integer> isbnCol;
    @FXML
    private TableColumn<Book, String> bookNameCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> availabilityCol;
    @FXML
    private TableColumn<Book, String> conditionCol;
    @FXML
    private TableColumn<Book, String> aisleRowCol;
    @FXML
    private TextField searchByNameTF;
    @FXML
    private TextField searchByAuthorTF;
    @FXML
    private TextField searchByIsbnTF;
    @FXML
    private ChoiceBox<String> SelectedBookToBorrow;
    @FXML
    private Button borrowBttn;
    @FXML
    private Button returnBttn;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private ChoiceBox<String> notReturnedChoiceBox;
    @FXML
    private Button searchByNamebttn;
    @FXML
    private Button searchByAuthorBttn;
    @FXML
    private Button searchByISBNbtttn;
    @FXML
    private Button showAvailableBooksBttn;
    @FXML
    private Text loggedInAsText;
    @FXML
    private Text loggedInLabel;
    @FXML
    private Text moneyOwedTxt;
    @FXML
    private Button LogoutBttn;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void initialize() {
        try {
            conn = DatabaseConnection.getConnection();
            isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            bookNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
            availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availabilityAsString"));
            conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));
            aisleRowCol.setCellValueFactory(new PropertyValueFactory<>("aisleRow"));

            loadBookData();
            loadAvailableBooks();
            updateNotReturnedBooks();
            updateLoggedInUserDisplay();

            if (UserSession.getInstance() != null) {
                int memberID = getMemberID();
                PreparedStatement stmt = conn.prepareStatement("SELECT ISBN FROM ISSUE WHERE returnDate IS NULL AND memberID =?");
                stmt.setInt(1, memberID);
                ResultSet rs = stmt.executeQuery();
                notReturnedChoiceBox.getItems().clear();
                while (rs.next()) {
                    notReturnedChoiceBox.getItems().add(rs.getString("ISBN"));
                }
                notReturnedChoiceBox.setValue(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLoggedInUserDisplay() {
        UserSession session = UserSession.getInstance();
        loggedInAsText.setText(session.getUsername());
        moneyOwedTxt.setText(String.format("%.2f", session.getOwesMoney()));
    }
    

@FXML
private void loadBookData() throws SQLException {
    bookTableView.getItems().clear();
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM BOOK");  
    ResultSet rs = stmt.executeQuery();

    while (rs.next()) {
        Book book = new Book(
            rs.getString("ISBN"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getInt("isAvail"),  // Adjust based on actual data type
            rs.getString("book_condition"),
            rs.getString("aisle_row")
        );
        bookTableView.getItems().add(book);
    }
}


private void loadAvailableBooks() throws SQLException {
    SelectedBookToBorrow.getItems().clear();
    PreparedStatement stmt = conn.prepareStatement("SELECT ISBN, title FROM BOOK WHERE isAvail = 1");
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
        String bookEntry = rs.getString("ISBN") + " - " + rs.getString("title");
        SelectedBookToBorrow.getItems().add(bookEntry);
    }
}

    private int getMemberID() throws SQLException {
        String memberUsername = UserSession.getInstance().getUsername();
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM MEMBER WHERE username =?");
        stmt.setString(1, memberUsername);
    
        ResultSet rs = stmt.executeQuery();
    
        if (rs.next()) {
            return rs.getInt("id");
        } else {
            throw new SQLException("Failed to retrieve member ID.");
        }
    }

    
    @FXML
private void handleBorrowBook() {
    try {
        String selectedItem = SelectedBookToBorrow.getValue();
        if (selectedItem == null || returnDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please select a book and enter a return date.");
            alert.showAndWait();
            return;
        }

        // Extract ISBN from the selected item
        String isbn = selectedItem.split(" - ")[0];

        int memberID = getMemberID();
        LocalDate returnDate = returnDatePicker.getValue();

        // Insert into ISSUE table
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO ISSUE (ISBN, memberID, issueDate, returnDate) VALUES (?, ?, CURDATE(), ?)");
        stmt.setString(1, isbn); // Use the extracted ISBN
        stmt.setInt(2, memberID);
        stmt.setDate(3, Date.valueOf(returnDate));
        stmt.executeUpdate();

        // Update BOOK table to set availability to unavailable
        PreparedStatement stmt2 = conn.prepareStatement("UPDATE BOOK SET isAvail = 0 WHERE ISBN = ?");
        stmt2.setString(1, isbn);
        stmt2.executeUpdate();

        loadBookData(); // Refresh the book data on table

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Book Borrowed");
        alert.setContentText("You have successfully borrowed the book.");
        alert.showAndWait();

        SelectedBookToBorrow.setValue(null);
        returnDatePicker.setValue(null);

    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to Borrow Book");
        alert.setContentText("An error occurred while borrowing the book. Please try again later.");
        alert.showAndWait();
    }
}

@FXML
private void handleReturnBook() {
    updateNotReturnedBooks();
    String selectedBook = notReturnedChoiceBox.getValue();
    if (selectedBook == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Missing Information");
        alert.setContentText("Please select a book to return.");
        alert.showAndWait();
        return;
    }

    // Extract ISBN from the selected item
    String isbn = selectedBook.split(" - ")[0];

    try {
        // Find the book in the ISSUE table that has not been returned yet
        PreparedStatement stmt = conn.prepareStatement("SELECT ISBN FROM ISSUE WHERE ISBN = ? AND memberID = ? AND returnDate IS NULL");
        stmt.setString(1, isbn);
        stmt.setInt(2, getMemberID());
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Update the ISSUE table to set the returnDate to the current date
            PreparedStatement stmt2 = conn.prepareStatement("UPDATE ISSUE SET returnDate = CURDATE() WHERE ISBN = ? AND memberID = ?");
            stmt2.setString(1, isbn);
            stmt2.setInt(2, getMemberID());
            stmt2.executeUpdate();

            // Update the BOOK table to set the book as available
            PreparedStatement stmt3 = conn.prepareStatement("UPDATE BOOK SET isAvail = 1 WHERE ISBN = ?");
            stmt3.setString(1, isbn);
            stmt3.executeUpdate();

            loadBookData(); // Refresh the book data table
            updateNotReturnedBooks(); // Refresh the not returned books choice box

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Book Returned");
            alert.setContentText("You have successfully returned the book.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Operation Failed");
            alert.setContentText("Could not find the book record. Please try again.");
            alert.showAndWait();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Database Error");
        alert.setContentText("An error occurred while updating the book return status. Please check your connection and try again.");
        alert.showAndWait();
    }
}
    @FXML
    private void searchByName() {
        String name = searchByNameTF.getText();

        if (name == null || name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please enter a name to search for.");
            alert.showAndWait();
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM BOOK WHERE title LIKE ?");
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            bookTableView.getItems().clear();

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String bookName = rs.getString("title");
                String author = rs.getString("author");
                String condition = rs.getString("book_condition");
                String aisleRow = rs.getString("aisle_row");

                Book book = new Book(isbn, bookName, author, 1, condition, aisleRow);
                bookTableView.getItems().add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Search");
            alert.setContentText("An error occurred while searching for books. Please try again later.");
            alert.showAndWait();
        }
    }

    @FXML
    private void searchByAuthor() {
        String author = searchByAuthorTF.getText();

        if (author == null || author.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please enter an author to search for.");
            alert.showAndWait();
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM BOOK WHERE author LIKE ?");
            stmt.setString(1, "%" + author + "%");
            ResultSet rs = stmt.executeQuery();

            bookTableView.getItems().clear();

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String bookName = rs.getString("title");
                String bookAuthor = rs.getString("author");
                int available = rs.getInt("isAvail");
                String condition = rs.getString("book_condition");
                String aisleRow = rs.getString("aisle_row");

                Book book = new Book(isbn, bookName, bookAuthor, available , condition, aisleRow);
                bookTableView.getItems().add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Search");
            alert.setContentText("An error occurred while searching for books. Please try again later.");
            alert.showAndWait();
        }
    }

    @FXML
    private void searchByISBN() {
        String isbn = searchByIsbnTF.getText();

        if (isbn == null || isbn.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please enter an ISBN to search for.");
            alert.showAndWait();
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM BOOK WHERE ISBN = ?");
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            bookTableView.getItems().clear();

            if (rs.next()) {
                String bookName = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("isAvail");
                String condition = rs.getString("book_condition");
                String aisleRow = rs.getString("aisle_row");

                Book book = new Book(isbn, bookName, author, available , condition, aisleRow);
                bookTableView.getItems().add(book);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Not Found");
                alert.setHeaderText("No Book Found");
                alert.setContentText("No book was found with the provided ISBN.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Search");
            alert.setContentText("An error occurred while searching for books. Please try again later.");
            alert.showAndWait();
        }
    }

    @FXML
    private void showOnlyAvailableBooks() {
        try {
            bookTableView.getItems().clear();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM BOOK WHERE isAvail = 'available'");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String bookName = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("isAvail");
                String condition = rs.getString("book_condition");
                String aisleRow = rs.getString("aisle_row");

                Book book = new Book(isbn, bookName, author, 1 , condition, aisleRow);
                bookTableView.getItems().add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Data");
            alert.setContentText("An error occurred while loading data from the database. Please try again later.");
            alert.showAndWait();
        }
    }

    private void updateNotReturnedBooks() {
        try {
            int memberID = getMemberID(); // Fetches the member ID correctly
            if (conn == null || conn.isClosed()) {
                System.out.println("Database connection is not valid or closed.");
                return;
            }
    
            // Query to select books that have not been returned yet
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT ISSUE.ISBN, BOOK.title " +
                "FROM ISSUE JOIN BOOK ON ISSUE.ISBN = BOOK.ISBN " +
                "WHERE ISSUE.memberID = ?"
            );
            stmt.setInt(1, memberID);
            ResultSet rs = stmt.executeQuery();
    
            notReturnedChoiceBox.getItems().clear();
            boolean foundBooks = false;
            while (rs.next()) {
                foundBooks = true;
                String bookEntry = rs.getString("ISBN") + " - " + rs.getString("title");
                notReturnedChoiceBox.getItems().add(bookEntry);
            }
    
            if (!foundBooks) {
                System.out.println("No currently borrowed books found for member ID: " + memberID); // Debugging output
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Load Not Returned Books");
            alert.setContentText("An error occurred while loading the list of not returned books. Please try again later.");
            alert.showAndWait();
        }
    }
    

    @FXML
    private void Logout() {
        try {
            mainApp.loadLoginView();
        } catch (Exception e) {
            e.printStackTrace();
        };
    }
}








