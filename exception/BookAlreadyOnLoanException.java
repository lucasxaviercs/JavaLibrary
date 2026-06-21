package exception;

public class BookAlreadyOnLoanException extends RuntimeException {
    public BookAlreadyOnLoanException(String isbn) {
        super("The book with ISBN " + isbn + " has no available copies for checkout.");
    }
}