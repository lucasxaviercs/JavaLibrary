package persistence;

import exception.PersistenceException;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import model.Loan;
import model.Patron;

public class FileManager {

    
    private static final String BOOKS_FILE = "data/books.csv";
    private static final String PATRONS_FILE = "data/patrons.csv";
    private static final String LOANS_FILE = "data/loans.csv";

    // reads Books csv data and loads it on a Books List
    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);

        if (!file.exists()) {
            return books; // if the file does not exist, we return an empty list
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // read the header first so the next reads are data only

            while((line = br.readLine()) != null){
                if (line.isBlank()) continue;

                String[] parts = line.trim().split(","); // we split to separate the fields in the csv file
                String title = parts[0].trim();
                String author = parts[1].trim();
                String isbn = parts[2].trim();
                int totalCopies = Integer.parseInt(parts[3].trim());
                int availableCopies = Integer.parseInt(parts[4].trim());

                Book book = new Book(title, author, isbn, totalCopies, availableCopies);
                books.add(book);
            }
        } catch (IOException e) {
            // the IOException is handled by throwing PersistenceException so that we caught it in a JOptionPane error message in the GUI
            throw new PersistenceException("Failed to load books from file", e);
        }

        return books;
    }

    // saves the Book list on the Books csv file
    public void saveBooks(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            pw.println("title,author,isbn,totalCopies,availableCopies");

            // for each book on the list, we assemble the csv line with "," separator
            for (Book b : books){
                String line = b.getTitle() + "," + b.getAuthor() + "," + b.getIsbn() + "," + b.getTotalCopies() + "," + b.getAvailableCopies();
                pw.println(line);
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to save books on file", e);
        }
    }

    // Saves the list of patrons into the CSV file
    public void savePatrons(List<Patron> patrons) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PATRONS_FILE))){
            bw.write("id;name;contact"); // Writes the file header
            bw.newLine();

            // Writes each patron as a line in the file
            for(Patron p : patrons){
                bw.write(String.join(";", String.valueOf(p.getId()), p.getName(), p.getContact()));

                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to save patrons on file", e);
        }
    }

    // Loads patrons form the CSV file
    public List<Patron> loadPatrons() {
        List<Patron> patrons = new ArrayList<>();

        File f = new File(PATRONS_FILE);

        // If file does not exist, return empty list
        if(!f.exists()){
            return patrons;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine(); // Skip header line

            String line;
            while((line = br.readLine()) != null){
                // Ignore empty lines
                if(line.isBlank()){
                    continue;
                }

                // Split line into fields
                String[] parts = line.split(";", -1);
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String contact = parts[2].trim();

                // Create patron object and add to list
                Patron patron = new Patron(id, name, contact);
                patrons.add(patron);
                
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to load patrons from file", e);
        }

        return patrons;
    }

    /*
     * Loads loan records from the CSV file.
     */
    public static List<Loan> loadLoans(List<Book> books, List<Patron> patrons) {
        List<Loan> loans = new ArrayList<>(); // Initializes an empty list to store the loaded loans
        
        File file = new File(LOANS_FILE);
        if (!file.exists()) {
            return loans;
        }

        try (BufferedReader br = new BufferedReader( new FileReader(file) ) ) {
            br.readLine(); // Ignore the header

            String line;
            // Reads the file line by line until the end
            while ( (line = br.readLine()) != null) {
                if (line.isBlank()) continue; // Skips any blank lines to prevent parsing errors

                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                int patronId = Integer.parseInt(parts[1].trim());
                String bookIsbn = parts[2].trim();
                LocalDate loanDate = LocalDate.parse(parts[3].trim());
                LocalDate dueDate = LocalDate.parse(parts[4].trim());
                boolean isReturned = Boolean.parseBoolean(parts[5].trim());

                // Uses auxiliary methods to find the actual objects in memory
                Patron foundPatron = findPatronById(patrons, patronId);
                Book foundBook = findBookByIsbn(books, bookIsbn);

                // Only add if find the book and the patron
                if (foundPatron != null && foundBook != null) {
                    loans.add( new Loan(id, foundBook, foundPatron, loanDate, dueDate, isReturned));
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to load loans from file", e);
        }

        return loans;
    }

    /*
     * Saves the list of loans to the CSV file.
     */
    public static void saveLoans(List<Loan> loans) {
        File file = new File(LOANS_FILE);
        file.getParentFile().mkdirs(); // Ensure the file path exists

        try (BufferedWriter bw = new BufferedWriter( new FileWriter(file) ) ) {
            // Writes the CSV header defining the column structure
            bw.write("id,patronId,bookIsbn,loanDate,dueDate,isReturned");
            bw.newLine();
            
            // Iterates through every loan in the memory list
            for (Loan l : loans) {
                // Formats the loan attributes into a single comma-separated string
                String line = l.getId() + "," + 
                              l.getPatron().getId() + "," + 
                              l.getBook().getIsbn() + "," + 
                              l.getLoanDate() + "," + 
                              l.getDueDate() + "," + 
                              l.getIsReturned();

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to save loans to file", e);
        }
    }

    // Aux. methods
    private static Patron findPatronById(List<Patron> patrons, int id) {
        for (Patron p : patrons) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    private static Book findBookByIsbn(List<Book> books, String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) return b;
        }
        return null;
    }
}