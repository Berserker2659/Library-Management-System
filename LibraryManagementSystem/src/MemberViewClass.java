import java.util.Date;

public class MemberViewClass {
    private int id;
    private String name;
    private double owes_money;
    private String username; // Added
    private String password; // Added
    private String bookTitle;
    private Date issueDate;
    private Date returnDate;

    public MemberViewClass(int id, String name, double owes_money, String username, String password, String bookTitle, Date issueDate, Date returnDate) {
        this.id = id;
        this.name = name;
        this.owes_money = owes_money;
        this.username = username;
        this.password = password;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOwes_money() {
        return owes_money;
    }

    public void setOwes_money(double owes_money) {
        this.owes_money = owes_money;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
