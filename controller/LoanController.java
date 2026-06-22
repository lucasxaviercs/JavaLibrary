package controller;

import exception.BookAlreadyOnLoanException;

import model.Book;
import model.Loan;
import model.Patron;
import persistence.FileManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Controller responsible for managing library book loans.
 * Handles checkout, checkin, validation rules, and state persistence.
 */
public class LoanController {
    
    private List<Loan> loans; // List of all loan transactions
    private FileManager fileManager; // Handles saving and loading data from disk
    private BookController bookController; // Accesses book data and validates available stock
    private PatronController patronController; // Accesses user data and validates borrowing status

    /*
     * Constructor for the LoanController.
     * Initializes dependencies and loads existing records into memory.
     */
    public LoanController(FileManager fileManager, BookController bookController, PatronController patronController) {
        this.fileManager = fileManager;
        this.bookController = bookController;
        this.patronController = patronController;
        this.loans = FileManager.loadLoans(bookController.getAllBooks(), patronController.getAllPatrons());
    }

    /*
     * Returns a copy of the loan records.
     * Prevents external modifications to the internal memory list.
     */
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans); // Copy of the original list
    }

    /*
     * Returns the direct reference to the internal loans list.
     * Useful for shared state management across controllers.
     */ 
    public List<Loan> getLoansReference(){
        return loans;
    }

    /*
     * Retrieves a list of all currently active (unreturned) loans.
     * Uses the Stream to filter the main list.
     */
    public List<Loan> getActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getIsReturned() == false) // Book not returned
                .collect(Collectors.toList()); // Get books that passed the filter and return into a new list
    }

    /*
     * Processes a book checkout for a specific patron.
     * Validates entities, generates a sequential ID, and updates states.
     */
    public void checkOut(int patronId, String isbn) throws BookAlreadyOnLoanException {
        Patron patron = patronController.findById(patronId);
        if (patron == null) { 
            throw new IllegalArgumentException("Patron with ID " + patronId + " not found.");
        }

        Book book = bookController.findByIsbn(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found.");
        }

        if (book.isAvailable() == false) {
            throw new BookAlreadyOnLoanException(isbn);
        }

        int newId;
        if (loans.isEmpty() == true) {
            newId = 1;
        } else {
            int maxId = 0;

            for(Loan loan : loans) {
                if (loan.getId() > maxId) {
                    maxId = loan.getId();
                }
            }

            newId = maxId + 1;
        }

        // Sets the loan date to the current date
        LocalDate loanDate = LocalDate.now();
        // Calculates the due date by adding 14 days to the loan date
        LocalDate dueDate = loanDate.plusDays(14);

        // Creates the new Loan object with the generated data and marks it as active
        Loan newLoan = new Loan(newId, book, patron, loanDate, dueDate, false);
        bookController.checkOutBook(isbn); // Decreases the books available copies
        loans.add(newLoan);

        FileManager.saveLoans(loans); // Persists the updated loans list to the disk
    }

    /*
     * Processes the return of a borrowed book.
     * Marks the loan as returned and restores the books availability.
     */
    public void checkIn(int loanId) {
        // Searches for the loan by its ID
        Loan loanToReturn = loans.stream()
                                .filter(l -> l.getId() == loanId) // FIlters for the exact ID match
                                .findFirst() // Retrieves the first matching element
                                .orElse(null); // Returns null if no match is found

        if (loanToReturn == null) { 
            throw new IllegalArgumentException("Loan with ID " + loanId + " not found.");
        }

        if (loanToReturn.getIsReturned() == true) {
            throw new IllegalArgumentException("Loan is already returned.");
        }

        // Update the states
        loanToReturn.setReturned(true); // Updates the loan state to reflect the return
        bookController.checkInBook(loanToReturn.getBook().getIsbn()); // Restores the books available copies

        FileManager.saveLoans(loans); // Persists the updated loans list to the disk
    }
}