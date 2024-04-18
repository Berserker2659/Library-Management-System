package book;
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int isAvailable; // Change to int
    private String condition;
    private String aisleRow;

    public Book(String isbn, String title, String author, int isAvailable, String condition, String aisleRow) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.condition = condition;
        this.aisleRow = aisleRow;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public String getAvailabilityAsString() {
        return isAvailable == 1 ? "Available" : "Unavailable";
    }
    public void setIsAvailable(int available) {
        isAvailable = available;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAisleRow() {
        return aisleRow;
    }

    public void setAisleRow(String aisleRow) {
        this.aisleRow = aisleRow;
    }
}