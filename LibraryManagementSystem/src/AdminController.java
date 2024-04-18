import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import library.*;
import book.Book;

public class AdminController {
    @FXML private AnchorPane AdminAnchorPane;
    @FXML private Button logoutBttn;
    @FXML private Button refreshMemberBttn;
    @FXML private Button refreshBookBttn;
    @FXML private Button refreshAdminBttn;   
    @FXML private TableView<MemberViewClass> memberTable;
    @FXML private TableColumn<MemberViewClass, Integer> memberIDColumn;
    @FXML private TableColumn<MemberViewClass, String> memberNameColumn;
    @FXML private TableColumn<MemberViewClass, String> memberUserNameColumn;
    @FXML private TableColumn<MemberViewClass, String> memberPasswordColumn;
    @FXML private TableColumn<MemberViewClass, String> memberBookInPossessionColumn;
    @FXML private TableColumn<MemberViewClass, String> memberReturnBookDateColumn;
    @FXML private TableColumn<MemberViewClass, Double> memberMoneyOwedColumn;
    @FXML private TextField addNewMemberName;
    @FXML private TextField addNewUserName;
    @FXML private TextField addNewMemberPassword;
    @FXML private Button addNewMemberBttn;
    @FXML private Button deleteUserBttn;
    @FXML private TextField deleteMemberTextField;
    @FXML private Button searchBttn;
    @FXML private Button sortMemberByMoneyOwedBttn;
    @FXML private Button sortMemberByNameBttn;
    @FXML private Button sortMemberByDateBttn;

    // Book Info Tab
    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book, Integer> isbnCol;
    @FXML private TableColumn<Book, String> bookNameCol;
    @FXML private TableColumn<Book, String> authorCol;
    @FXML private TableColumn<Book, Integer> availabilityCol;
    @FXML private TableColumn<Book, String> conditionCol;
    @FXML private TableColumn<Book, String> aisleRowCol;
    @FXML private TextField isbnNewBook;
    @FXML private TextField bookNameNewBook;
    @FXML private TextField authorNameNewBook;
    @FXML private TextField aisleRowNewBook;
    @FXML private Button addNewBookBttn;
    @FXML private TextField isbnDeleteBook;
    @FXML private TextField bookNameDeleteBook;
    @FXML private Button removeBookBttn;

    // Admin Info Tab
    @FXML private TableView<Admin> adminTable;
    @FXML private TableColumn<Admin, Integer> adminIDColumn;
    @FXML private TableColumn<Admin, String> adminUserNameColumn;
    @FXML private TableColumn<Admin, String> adminPasswordColumn;
    @FXML private TextField addNewAdminUserName;
    @FXML private TextField addNewAdminPassword;
    @FXML private TextField addNewAdminName;
    @FXML private Button addNewAdminBttn;
    @FXML private TextField removeAdminUserName;
    @FXML private Button removeAdminBttn;

    //Search Variables
    @FXML private TextField searchID;
    @FXML private TextField searchName;
    @FXML private TextField searchISBN;
    @FXML private TextField searchBookName;
    private Connection conn;
    
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {
        try {
            conn = DatabaseConnection.getConnection();
            initializeMemberTable();
            initializeBookTable();
            initializeAdminTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
private void sortMemberByMoneyOwed(ActionEvent event) {
    String sql = "SELECT m.id, m.name, m.username, m.password, m.owes_money, b.title, i.issueDate, i.returnDate " +
                 "FROM MEMBER m " +
                 "LEFT JOIN ISSUE i ON m.id = i.memberID " +
                 "LEFT JOIN BOOK b ON i.ISBN = b.ISBN " +
                 "ORDER BY m.owes_money DESC";
    memberTable.setItems(executeMemberQuery(sql));
}

@FXML
private void sortMemberByName(ActionEvent event) {
    String sql = "SELECT m.id, m.name, m.username, m.password, m.owes_money, b.title, i.issueDate, i.returnDate " +
                 "FROM MEMBER m " +
                 "LEFT JOIN ISSUE i ON m.id = i.memberID " +
                 "LEFT JOIN BOOK b ON i.ISBN = b.ISBN " +
                 "ORDER BY m.name ASC";
    memberTable.setItems(executeMemberQuery(sql));
}

@FXML
private void sortMemberByDateBttn(ActionEvent event) {
    String sql = "SELECT m.id, m.name, m.username, m.password, m.owes_money, b.title, i.issueDate, i.returnDate " +
                 "FROM MEMBER m " +
                 "LEFT JOIN ISSUE i ON m.id = i.memberID " +
                 "LEFT JOIN BOOK b ON i.ISBN = b.ISBN " +
                 "ORDER BY i.returnDate DESC";
    memberTable.setItems(executeMemberQuery(sql));
}

private ObservableList<MemberViewClass> executeMemberQuery(String sql) {
    ObservableList<MemberViewClass> members = FXCollections.observableArrayList();
    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            double owes_money = rs.getDouble("owes_money");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String bookTitle = rs.getString("title");
            Date issueDate = rs.getDate("issueDate");
            Date returnDate = rs.getDate("returnDate");
            members.add(new MemberViewClass(id, name, owes_money, username, password, bookTitle, issueDate, returnDate));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return members;
}


    private void initializeMemberTable() {
        try {
        memberIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberMoneyOwedColumn.setCellValueFactory(new PropertyValueFactory<>("owes_money"));
        memberUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        memberPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        memberBookInPossessionColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        memberReturnBookDateColumn.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        memberReturnBookDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        
            memberTable.setItems(loadMemberData());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initializeBookTable() {
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));
        availabilityCol.setCellFactory(column -> new TableCell<Book, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : (item == 1 ? "Available" : "Unavailable"));
            }
        });
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));
        aisleRowCol.setCellValueFactory(new PropertyValueFactory<>("aisleRow"));
        try {
            bookTableView.setItems(loadBookData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeAdminTable() {
        adminIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adminUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        adminPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminTable.setItems(loadAdminData());
    }

    @FXML
    private void addMember(ActionEvent event){
        try {
            String memberName = addNewMemberName.getText();
            System.out.println(memberName);
            String memberUsername = addNewUserName.getText();
            System.out.println(memberUsername);
            String memberPassword = addNewMemberPassword.getText();
            System.out.println(memberPassword);

            addMemberToDatabase(memberName,memberUsername,memberPassword);
            loadMemberData();
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMemberToDatabase(String name, String username, String password) throws SQLException {
        String sql = "INSERT INTO MEMBER (name, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();
        }
    }

    private ObservableList<Admin> loadAdminData() {
        ObservableList<Admin> admins = FXCollections.observableArrayList();
        String sql = "SELECT id, userName, password FROM ADMIN";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                Admin admin = new Admin(id, userName, password);
                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    @FXML
    private void deleteUser(ActionEvent event) {
        try {
            int memberID = Integer.parseInt(deleteMemberTextField.getText());
            deleteMemberFromDatabase(memberID);
            loadMemberData();
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewBook(ActionEvent event) {
        try {
            String isbn = isbnNewBook.getText();
            String title = bookNameNewBook.getText();
            String author = authorNameNewBook.getText();
            String aisleRow = aisleRowNewBook.getText();
            addBookToDatabase(isbn, title, author, aisleRow);
            loadBookData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeBook(ActionEvent event) {
        try {
            String isbn = isbnDeleteBook.getText();
            deleteBookFromDatabase(isbn);
            loadBookData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<MemberViewClass> loadMemberData() throws SQLException {
        ObservableList<MemberViewClass> members = FXCollections.observableArrayList();
        String sql = "SELECT m.id, m.name, m.username,m.password ,m.owes_money, b.title, i.issueDate, i.returnDate FROM MEMBER m " +
                     "LEFT JOIN ISSUE i ON m.id = i.memberID " +
                     "LEFT JOIN BOOK b ON i.ISBN = b.ISBN";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double owes_money = rs.getDouble("owes_money");
                String bookTitle = rs.getString("title");
                String username = rs.getString("username");
                String password = rs.getString("password");
                Date issueDate = rs.getDate("issueDate");
                Date returnDate = rs.getDate("returnDate");
                // Modify your Member class to include these additional fields
                MemberViewClass member = new MemberViewClass(id, name, owes_money, username, password, bookTitle, issueDate, returnDate);
                members.add(member);
            }
        }
        return members;
    }
    

    private ObservableList<Book> loadBookData() throws SQLException {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT ISBN, title, author, isAvail, book_condition, aisle_row FROM BOOK";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int  isAvail = rs.getInt("isAvail");
                String condition = rs.getString("book_condition");
                String aisleRow = rs.getString("aisle_row");
                Book book = new Book(isbn, title, author, isAvail, condition, aisleRow);
                books.add(book);
            }
        }
        return books;
    }
   
    @FXML
    private void deleteMemberFromDatabase(int memberID) throws SQLException {
        String sql = "DELETE FROM MEMBER WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberID);
            stmt.executeUpdate();
        }
    }

    private ObservableList<MemberViewClass> searchMemberInDatabase(String id, String name, String bookName, String isbn) throws SQLException {
        ObservableList<MemberViewClass> members = FXCollections.observableArrayList();
        String sql = "SELECT m.id, m.name, m.username,m.password ,m.owes_money, b.title, i.issueDate, i.returnDate FROM MEMBER m " +
                     "JOIN ISSUE i ON m.id = i.memberID " +
                     "JOIN BOOK b ON i.ISBN = b.ISBN " +
                     "WHERE (? IS NULL OR m.id = ?) AND (? IS NULL OR m.name = ?) AND (? IS NULL OR b.title = ?) AND (? IS NULL OR b.ISBN = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, id);
            stmt.setString(3, name);
            stmt.setString(4, name);
            stmt.setString(5, bookName);
            stmt.setString(6, bookName);
            stmt.setString(7, isbn);
            stmt.setString(8, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int memberId = rs.getInt("id");
                String memberName = rs.getString("name");
                String username = rs.getString("username");
                String password = rs. getString("password");
                double owes_money = rs.getDouble("owes_money");
                String bookTitle = rs.getString("title");
                Date issueDate = rs.getDate("issueDate");
                Date returnDate = rs.getDate("returnDate");
                MemberViewClass member = new MemberViewClass(memberId, memberName, owes_money,username,password, bookTitle, issueDate, returnDate);
                members.add(member);
            }
        }
        return members;
    }
    @FXML
    private void addBookToDatabase(String isbn, String title, String author, String aisleRow) throws SQLException {
        String sql = "INSERT INTO BOOK (isbn, title, author, isAvail, book_condition, aisle_row) VALUES (?, ?, ?, 1, 'NEW', ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, aisleRow);
            stmt.executeUpdate();
        }
    }
    
    @FXML
    private void searchMember(ActionEvent event) {
        try {
            // Prepare components for dynamic SQL
            List<String> conditions = new ArrayList<>();
            List<Object> parameters = new ArrayList<>();
    
            if (!searchID.getText().isEmpty()) {
                conditions.add("m.id = ?");
                parameters.add(Integer.parseInt(searchID.getText()));
            }
            if (!searchName.getText().isEmpty()) {
                conditions.add("m.name LIKE ?");
                parameters.add("%" + searchName.getText() + "%");
            }
            if (!searchBookName.getText().isEmpty()) {
                conditions.add("b.title LIKE ?");
                parameters.add("%" + searchBookName.getText() + "%");
            }
            if (!searchISBN.getText().isEmpty()) {
                conditions.add("b.ISBN = ?");
                parameters.add(searchISBN.getText());
            }
    
            if (conditions.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Input Error");
                alert.setContentText("Please fill at least one search field!");
                alert.showAndWait();
            } else {
                String sql = "SELECT m.id, m.name, m.username, m.password, m.owes_money, b.title, i.issueDate, i.returnDate " +
                             "FROM MEMBER m " +
                             "LEFT JOIN ISSUE i ON m.id = i.memberID " +
                             "LEFT JOIN BOOK b ON i.ISBN = b.ISBN " +
                             "WHERE " + String.join(" AND ", conditions);
                ObservableList<MemberViewClass> searchResult = executeMemberSearch(sql, parameters);
                memberTable.setItems(searchResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<MemberViewClass> executeMemberSearch(String sql, List<Object> parameters) throws SQLException {
        ObservableList<MemberViewClass> members = FXCollections.observableArrayList();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(new MemberViewClass(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("owes_money"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("title"),
                    rs.getDate("issueDate"),
                    rs.getDate("returnDate")
                ));
            }
        }
        return members;
    }

    @FXML
    private void deleteBookFromDatabase(String isbn) throws SQLException {
        String sql = "DELETE FROM BOOK WHERE isbn like ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

// Add a new admin to the database
    @FXML
    private void addNewAdmin(ActionEvent event) {
        String userName = addNewAdminUserName.getText();
        String password = addNewAdminPassword.getText();
        addAdminToDatabase(userName, password);
        initializeAdminTable();
    }

// Delete an admin from the database
    @FXML
    private void removeAdmin(ActionEvent event) {
        String userName = removeAdminUserName.getText();
        deleteAdminFromDatabase(userName);
        initializeAdminTable();
    }
    @FXML
    private void addAdminToDatabase(String userName, String password) {
        String sql = "INSERT INTO ADMIN (userName, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteAdminFromDatabase(String userName) {
        String sql = "DELETE FROM ADMIN WHERE userName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// Search for a book by title
@FXML
private void searchBookByTitle(ActionEvent event) {
    String title = searchBookName.getText();
    ObservableList<Book> searchResult = searchBookByTitleInDatabase(title);
    bookTableView.setItems(searchResult);
}

private ObservableList<Book> searchBookByISBNInDatabase(String isbn) {
    ObservableList<Book> books = FXCollections.observableArrayList();
    String sql = "SELECT isbn, title, author, isAvail, book_condition, aisle_row FROM BOOK WHERE isbn LIKE ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, isbn);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String bookISBN = rs.getString("isbn");
            String title = rs.getString("title");
            String author = rs.getString("author");
            int isAvail = rs.getInt("isAvail");
            String condition = rs.getString("book_condition");
            String aisleRow = rs.getString("aisle_row");
            Book book = new Book(bookISBN, title, author, isAvail, condition, aisleRow);
            books.add(book);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return books;
}

private ObservableList<Book> searchBookByTitleInDatabase(String title) {
    ObservableList<Book> books = FXCollections.observableArrayList();
    String sql = "SELECT isbn, title, author, isAvail, book_condition, aisle_row FROM BOOK WHERE title LIKE ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, "%" + title + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String isbn = rs.getString("isbn");
            String bookTitle = rs.getString("title");
            String author = rs.getString("author");
            int isAvail = rs.getInt("isAvail");
            String condition = rs.getString("book_condition");
            String aisleRow = rs.getString("aisle_row");
            Book book = new Book(isbn, bookTitle, author, isAvail, condition, aisleRow);
            books.add(book);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return books;
}

@FXML
private void handleLogout(ActionEvent event) {
    try {
        // Assuming Main has a method to load the login view
        mainApp.loadLoginView();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

@FXML
private void handleRefresh(ActionEvent event) {
    try {
        // Re-initialize to refresh data
        initializeMemberTable();
        initializeBookTable();
        initializeAdminTable();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}