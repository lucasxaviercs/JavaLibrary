package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private int id;
    private Book book;
    private Patron patron;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private boolean isReturned;

    // Constructor
    public Loan(int id, Book book, Patron patron, LocalDate loanDate, LocalDate dueDate, boolean isReturned) {
        this.id = id;
        this.book = book;
        this.patron = patron;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.isReturned = isReturned;
    }

    // Getters
    public int getId() { return id; }
    public Book getBook() { return book; }
    public Patron getPatron() { return patron; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean getIsReturned() { return isReturned; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setBook(Book book) { this.book = book; }
    public void setPatron(Patron patron) { this.patron = patron; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setReturned(boolean isReturned) { this.isReturned = isReturned; }

    public double getFineAmount() {
        
        if (isReturned) { return 0.0; }
        
        LocalDate today = LocalDate.now();
        
        if (today.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            
            double dailyRate = 1.50; 
            
            return daysLate * dailyRate;
        }
        
        return 0.0; 
    }

    @Override
    public String toString () {
        String status = isReturned ? "Returned" : "Active";
        String dueInfo;

        if (dueDate != null) {
            dueInfo = "Due: " + dueDate;
        } else {
            dueInfo = "Due: N/A";
        }

        return "[ " + id + "| " + book.getTitle() + "| " + patron.getName() + "] - Due: " + dueDate + " | Status " + status;
    }
}