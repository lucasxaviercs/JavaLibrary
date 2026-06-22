package controller;

import model.Patron;
import model.Loan;
import persistence.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import exception.PatronHasActiveLoansException;


/*
 * Controller responsible for managing library patrons.
 * Handles add, edit, delete, search and persistence operations.
 */
public class PatronController {
    private List<Patron> patrons; // List of all registered patrons
    private List<Loan> loans; // List of all loans (used to check active loans)
    private int nextId; // Auto-increment ID for new patrons
    private FileManager fileManager; // Handles flie saving and loading

    // Constructor loads patrons from file and initializes data
    public PatronController(FileManager fileManager){
        this.fileManager = fileManager;
        this.patrons = fileManager.loadPatrons();
        this.loans = new ArrayList<>();
        // Sets next ID based on the highest existing ID
        nextId = patrons.stream().mapToInt(Patron::getId).max().orElse(0) + 1;
    }
    
    // Sets the lis of loans (used externally)
    public void setLoans(List<Loan> loans){
        this.loans = loans;
    }

    // Finds a patron by ID
    public Patron findById(int id){
        for(Patron patron : patrons){
            if(patron.getId() == id){
                return patron;
            }
        }
        return null;
    }

    // Adds a new patron
    public void addPatron(String name, String contact){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("The user's name can not be empty");
        }

        Patron patron = new Patron(nextId++, name.trim(), contact.trim());
        patrons.add(patron);
        fileManager.savePatrons(patrons);
    }

    // Edits an existing patron
    public void editPatron(int id, String newName, String newContact){
        Patron patron = findById(id);

        if(patron == null){
            throw new IllegalArgumentException("User id " + id + " not found.");
        }

        if(newName == null || newName.trim().isEmpty()){
            throw new IllegalArgumentException("The user's name can not be empty.");
        }

        patron.setName(newName.trim());
        patron.setContact(newContact.trim());
        fileManager.savePatrons(patrons);
    }

    // Deletes a patron (only if no active loans)
    public void deletePatron(int id) throws PatronHasActiveLoansException{
        Patron patron = findById(id);

        if(patron == null){
            throw new IllegalArgumentException("User id " + id + " not found.");
        }

        boolean hasActiveLoans = loans.stream().anyMatch(l -> !l.getIsReturned() && l.getPatron().getId() == id);

        if(hasActiveLoans){
            throw new PatronHasActiveLoansException(patron.getName());
        }

        patrons.remove(patron);
        fileManager.savePatrons(patrons);
    }

    // Searches patrons by name or ID
    public List<Patron> searchPatrons(String query){
        if(query == null || query.trim().isEmpty()){
            return new ArrayList<>(patrons);
        }

        String q = query.trim().toLowerCase();

        List<Patron> result = new ArrayList<>();
        
        for(Patron patron : patrons){
            if(patron.getName().toLowerCase().contains(q) || String.valueOf(patron.getId()).contains(q)){
                result.add(patron);
            }
        }

        return result;
    }

    public List<Loan> getPatronHistory(int patronId) {
        if (loans == null || loans.isEmpty()) {
            return new ArrayList<>();
        }
        
        return loans.stream()
                .filter(l -> l.getPatron().getId() == patronId)
                .collect(Collectors.toList());
    }

    // Returns all patrons
    public List<Patron> getAllPatrons(){
        return new ArrayList<>(patrons);
    }
}
