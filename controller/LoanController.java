package controller;

import exception.BookAlreadyOnLoanException;
import model.Book;
import model.Loan;
import model.Patron;
import persistence.FileManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
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

        try {
            this.loans = FileManager.loadLoans(bookController.getAllBooks(), patronController.getAllPatrons());
        }
        catch (IOException e) {
            System.out.println("Error loading loans from file: " + e.getMessage());
            this.loans = new ArrayList<>();
        }
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans); // copy of the original list
    }

    public List<Loan> getActiveLoans() {
        return loans.stream()
                .filter(loan -> loan.getIsReturned() == false); // book not returned
                .collect(Collector.toList()); // get books that passed the filter and return into a new list
    }







}
