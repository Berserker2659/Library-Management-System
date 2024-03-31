    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.input.MouseEvent;
    import library.LibraryUser;
    import java.time.LocalDate;
    import java.time.temporal.ChronoUnit;
    import java.util.List;
    import book.Book;
    import library.LibrarySystem;
    import library.LibraryUser;
    


    public class MainViewController {
        @FXML
        private ChoiceBox<String> notReturnedChoiceBox;
        @FXML
        private Button returnButton;
        @FXML
        private Button addBookBttn;
        @FXML
        protected TableView<Book> bookTableView;
        @FXML
        private TextField bookNameTextField;
        @FXML
        private TextField bookAuthorTextField;
        @FXML
        private TextField bookIsbnTextField;
        @FXML
        private ChoiceBox<String> choiceBox;
        @FXML
        private TextField bookAisleRowTextField;
        @FXML
        private TextField PersonNameTextField;
        @FXML
        private DatePicker returnDatePicker;
        @FXML
        private Main mainApp;
        @FXML
        public ObservableList<Book> observableBooks;
        @FXML
        private TableColumn<Book, String> bookNameCol;
        @FXML
        private TableColumn<Book, String> authorCol; 
        @FXML
        private TableColumn<Book, Integer> isbnCol;
        @FXML
        private TableColumn<Book, Boolean> availabilityCol;
        @FXML
        private TableColumn<Book, String> conditionCol;
        @FXML
        private TableColumn<Book, String> borrowedByCol;
        @FXML
        private TableColumn<Book, String> aisleRowCol;

        public void setMainApp(Main mainApp) {
                this.mainApp = mainApp;
                List<Book> sortedBooks = mainApp.getLibrarySystem().listBooksInAlphabeticalOrder();
                observableBooks = FXCollections.observableArrayList(sortedBooks);
                bookTableView.setItems(observableBooks);
        
        }

        public void initialize(){

            choiceBox.setItems(FXCollections.observableArrayList("New", "Ok", "Bad"));
            bookNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
            isbnCol.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
            availabilityCol.setCellValueFactory(new PropertyValueFactory<>("available"));
            availabilityCol.setCellFactory(column -> {
                return new TableCell<Book, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
            
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            // Format the boolean as a yes/no string
                            setText(item ? "Yes" : "No");
                        }
                    }
                };
            });
            conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));
            borrowedByCol.setCellValueFactory(new PropertyValueFactory<>("borrowedBy"));
            aisleRowCol.setCellValueFactory(new PropertyValueFactory<>("aisleRow"));        
                
        }

    
    @FXML
    private void handleAddBook() throws IllegalArgumentException {
        try {
            String title = bookNameTextField.getText();
            String author = bookAuthorTextField.getText();
            int isbn = Integer.parseInt(bookIsbnTextField.getText());
            String aisleRow = bookAisleRowTextField.getText();

            String condition = choiceBox.getSelectionModel().getSelectedItem().toString();

            if (title.isEmpty() || author.isEmpty() || aisleRow.isEmpty() || condition == null) {
                throw new IllegalArgumentException("Please fill in all required fields.");
            }

            Book newBook = new Book(title, author, isbn, true, condition,aisleRow);

            mainApp.getLibrarySystem().addBook(newBook);

            System.out.println("Book Availibility: "+newBook.isAvailable());

            observableBooks.add(newBook);

            System.out.println(observableBooks.size()); 

            bookNameTextField.clear();
            bookAuthorTextField.clear();
            bookIsbnTextField.clear();
            bookAisleRowTextField.clear();
            choiceBox.getSelectionModel().clearSelection();

            bookTableView.refresh();
            updateNotReturnedChoiceBooks(); 

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("AddBook Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleBorrowBook() {
        try {
            String borrowerName = PersonNameTextField.getText();
            LocalDate returnDate = returnDatePicker.getValue();

            if (borrowerName.isEmpty() || returnDate == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Please provide both the borrower's name and a return date.");
                alert.showAndWait();
                return;
            }
            

            LibraryUser borrower = new LibraryUser(borrowerName);
            mainApp.getLibrarySystem().addUser(borrower);

            Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                if (selectedBook.isAvailable()) {
                    borrower.borrowBook(selectedBook);
                    bookTableView.refresh();
                    PersonNameTextField.clear();
                    returnDatePicker.getEditor().clear();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Book Borrowed");
                    alert.setHeaderText(null);
                    alert.setContentText("The book has been borrowed successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Book Not Available");
                    alert.setHeaderText(null);
                    alert.setContentText("The selected book is already borrowed.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Book Selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a book to borrow.");
                alert.showAndWait();
            }
            
            updateNotReturnedChoiceBooks();

        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while processing your request.");
            alert.showAndWait();
        }
    }


    @FXML
    private void displayReturnInfo() {
    Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
    if(!selectedBook.isAvailable()) {
        LibraryUser borrower = new LibraryUser();
        String returnInfo = borrower.getReturnInfo(selectedBook);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Not Available");
        alert.setHeaderText(null);
        alert.setContentText(returnInfo);

        alert.showAndWait();
    }
    }

    @FXML
    public void updateNotReturnedChoiceBooks() {
        ObservableList<Book> notReturnedBooks = observableBooks.filtered(book -> !book.isAvailable());
        ObservableList<String> bookTitles = FXCollections.observableArrayList();
        for (Book book : notReturnedBooks) {
            bookTitles.add(book.getTitle());
        }
        notReturnedChoiceBox.setItems(bookTitles);
        notReturnedChoiceBox.getSelectionModel().clearSelection();

    }


    @FXML
    private void handleReturnBook() {
    try {
        String selectedBookTitle = notReturnedChoiceBox.getSelectionModel().getSelectedItem();

        if (selectedBookTitle != null) {
            Book selectedBook = observableBooks.stream().filter(book -> book.getTitle().equals(selectedBookTitle)).findFirst().orElse(null);
            if (selectedBook != null && !selectedBook.isAvailable()) {
                selectedBook.setAvailable(true);
                String borrowerName = selectedBook.getBorrowedBy();

                LibraryUser borrower = findUserByName(borrowerName);

                if (borrower != null) {
                    borrower.returnBook(selectedBook);
                    double lateFee = borrower.getFees();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Book Return Information");
                    alert.setHeaderText(null);

                    LocalDate dueDate = selectedBook.getDueDate();

                    if (lateFee > 0) {
                        alert.setContentText("The book was due on " + dueDate + ".\nLate fee: " + lateFee + " rupees.");
                    } else {
                        alert.setContentText("The book was due on " + dueDate + ". No late fee.");
                    }

                    alert.showAndWait();
                }

                updateNotReturnedChoiceBooks();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Book Not Borrowed");
                alert.setHeaderText(null);
                alert.setContentText("The selected book is already available and not borrowed.");
                alert.showAndWait();
            }
        } else {
            // Handle no book selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Book Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a book to return.");
            alert.showAndWait();
        }
        bookTableView.refresh();

    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("An error occurred: " + e.getMessage());
        alert.showAndWait();
    }
}

private LibraryUser findUserByName(String name) {
    for (LibraryUser user : mainApp.getLibrarySystem().getUsers()) {
        if (user.getName().equals(name)) {
            return user;
        }
    }
    return null; 
}
        
    }
