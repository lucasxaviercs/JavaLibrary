package exception;

public class PatronHasActiveLoansException extends Exception {

    public PatronHasActiveLoansException(String patronName){
        super("Cannot delete " + patronName + " have active loans.");
    }
    
}
