package book;


import java.time.LocalDate;


public class Book {
        private String title;
        private String author;
        private int ISBN;
        private boolean isAvailable;
        private String condition;
        private String borrowedBy;
        private String aisleRow;
        
        public Book(String title, String author, int ISBN, boolean isAvailable, String condition,String aisleRow){
            this.title = title;
            this.author = author;
            this.ISBN = ISBN;
            this.isAvailable = isAvailable;
            this.condition = condition;
            this.aisleRow = aisleRow;
        }
     
        public Book() {
            this.title = "";
            this.author = "";
            this.ISBN = 0;
            this.isAvailable = false;
            this.condition = "";
            this.borrowedBy = "";
            this.aisleRow = "";
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
    
        public int getISBN() {
            return ISBN;
        }
    
        public void setISBN(int ISBN) {
            this.ISBN = ISBN;
        }
    
        public boolean isAvailable() {
            return isAvailable;
        }

        public boolean getisAvailable(){
            return isAvailable;
        }
    
        public void setAvailable(boolean available) {
            isAvailable = available;
        }
    
        public String getCondition() {
            return condition;
        }
    
        public void setCondition(String condition) {
            this.condition = condition;
        }
    
        public String getBorrowedBy() {
            return borrowedBy;
        }
    
        public void setBorrowedBy(String borrowedBy) {
            this.borrowedBy = borrowedBy;
        }
    
        public String getAisleRow() {
            return aisleRow;
        }
        public LocalDate getDueDate() {
        LocalDate currentDate = LocalDate.now();
         LocalDate dueDate = currentDate.plusDays(14);
            return dueDate;
}

    
        public void setAisleRow(String aisleRow) {
            this.aisleRow = aisleRow;
        }
        public void returnBook() {
            if (!isAvailable) {
                isAvailable = true;
                borrowedBy = "";
            } else {
                System.out.println("The book is already available.");
            }
        }
        
    }
    

