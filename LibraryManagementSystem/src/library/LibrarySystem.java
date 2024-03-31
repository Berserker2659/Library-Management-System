package library;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import library.LibraryUser;
import book.Book;


public class LibrarySystem {
    private List<Book> catalog;
    private List<LibraryUser> users;

    public LibrarySystem() {
        catalog = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void addBook(Book book) {
        catalog.add(book);
    }

    public void addUser(LibraryUser user) {
        users.add(user);
    }

    public void removeBook(Book book) {
        catalog.remove(book);
    }

    public boolean checkBookAvailability(Book book) {
        return book.isAvailable();
    }
    public List<LibraryUser> getUsers() {
        return users;
    }
    public List<Book> listBooksInAlphabeticalOrder() {
        return catalog.stream().sorted(Comparator.comparing(Book::getTitle)).collect(Collectors.toList());
    }   

    public String findBookLocation(Book book) {
        return book.getAisleRow();
    }
}
