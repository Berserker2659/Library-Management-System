package library;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import book.Book;
public class LibraryUser extends LibrarySystem {
        private String name;
        private double fees;
    
        public LibraryUser(String name) {
            this.name = name;
            fees = 0.0;
        }
    
        public LibraryUser() {
            this.name = "";
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
        public void borrowBook(Book book) {
             if (book.isAvailable()) {
            book.setAvailable(false);
            book.setBorrowedBy(this.getName());
          } else {
            System.out.println("Sorry, the book is not available.");
        }
    }

    public void returnBook(Book book) {
        book.setAvailable(true);
        book.setBorrowedBy("");
        // Calculate and track fees
        LocalDate dueDate = book.getDueDate();
        LocalDate returnDate = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        if (daysLate > 0) {
            double lateFee = daysLate * 10.0;
            this.fees += lateFee;
            System.out.println("You have been charged a late fee of " + lateFee + " rupees.");
        }
    }
    public String getReturnInfo(Book book) {
        LocalDate dueDate = book.getDueDate();
        long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

        if (daysLate > 0) {
            double lateFee = daysLate * 10.0;
            return "The selected book is already borrowed. It will be returned on " + dueDate + ".\nLate fee: " + lateFee + " rupees.";
        } else {
            return "The selected book is already borrowed. It will be returned on " + dueDate;
        }
    }
    
    public double getFees() {
        return fees;
    }

    public void payFees(double amount) {
        if (amount >= fees) {
            fees = 0.0;
            System.out.println("Thank you for your payment. Your remaining balance is " + amount + " rupees.");
        } else {
            fees -= amount;
            System.out.println("Thank you for your payment. Your remaining balance is " + fees + " rupees.");
        }
    }



    }
    


