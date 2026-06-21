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

public class LoanController {
    
    private List<Loan> loans;
    private FileManager fileManager;
    private BookController bookController;
    private PatronController patronController;

    public LoanController(FileManager fileManager, BookController bookController, PatronController patronController) {
        this.fileManager = fileManager;
        this.bookController = bookController;
        this.patronController = patronController;
        this.loans = FileManager.loadLoans(bookController.getAllBooks(), patronController.getAllPatrons());
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans); // copy of the original list
    }

    public List<Loan> getLoansReference(){
        return loans;
    }

    public List<Loan> getActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getIsReturned() == false) // book not returned
                .collect(Collectors.toList()); // get books that passed the filter and return into a new list
    }

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

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        // create the loan and decrease the copy
        Loan newLoan = new Loan(newId, book, patron, loanDate, dueDate, false);
        bookController.checkOutBook(isbn);
        loans.add(newLoan);

        FileManager.saveLoans(loans);
    }

    public void checkIn(int loanId) {
        Loan loanToReturn = loans.stream()
                                .filter(l -> l.getId() == loanId)
                                .findFirst()
                                .orElse(null);

        if (loanToReturn == null) {
            throw new IllegalArgumentException("Loan with ID " + loanId + " not found.");
        }

        if (loanToReturn.getIsReturned() == true) {
            throw new IllegalArgumentException("Loan is already returned.");
        }

        // update the states
        loanToReturn.setReturned(true);
        bookController.checkInBook(loanToReturn.getBook().getIsbn());

        FileManager.saveLoans(loans);
    }
}