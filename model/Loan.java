package model;

import java.time.LocalDate;

public class Loan {
    private int id;
    private Book book;
    private Patron patron;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private boolean isReturned;

    // Constructor
    Loan() (int id, Book book, Patron patron, LocalDate loanDate, LocalDate dueDate, boolean isReturned) {
        this.id = id;
        this.book = book;
        this.patron = patron;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.isReturned = false; // Default when created a loan
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
}