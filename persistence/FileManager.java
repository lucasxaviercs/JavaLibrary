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
            return books; // arquivo ainda não existe, lista vazia é o esperado
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // read the header first so the next reads are data only

            while((line = br.readLine()) != null){
                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                String title = parts[0];
                String author = parts[1];
                String isbn = parts[2];
                int totalCopies = Integer.parseInt(parts[3]);
                int availableCopies = Integer.parseInt(parts[4]);

                Book book = new Book(title, author, isbn, totalCopies, availableCopies);
                books.add(book);
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to load books from file", e);
        }

        return books;
    }

    // saves the Book list on the Books csv file
    public void saveBooks(List<Book> books) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            pw.println("title,author,isbn,totalCopies,availableCopies");

            for (Book b : books){
                String line = b.getTitle() + "," + b.getAuthor() + "," + b.getIsbn() + "," + b.getTotalCopies() + "," + b.getAvailableCopies();
                pw.println(line);
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to save books on file", e);
        }
    }

    public static void savePatrons(List<Patron> patrons) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PATRONS_FILE))){
            bw.write("id;name;contact");
            bw.newLine();

            for(Patron p : patrons){
                bw.write(String.join(";", String.valueOf(p.getId()), p.getName(), p.getContact()));

                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to save patrons on file", e);
        }
    }

    public static List<Patron> loadPatrons() {
        List<Patron> patrons = new ArrayList<>();

        File f = new File(PATRONS_FILE);
        if(!f.exists()){
            return patrons;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine();

            String line;
            while((line = br.readLine()) != null){
                if(line.isBlank()){
                    continue;
                }

                String[] parts = line.split(";", -1);
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String contact = parts[2];

                Patron patron = new Patron(id, name, contact);
                patrons.add(patron);
                
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to load patrons from file", e);
        }

        return patrons;
    }

    public static List<Loan> loadLoans(List<Book> books, List<Patron> patrons) {
        List<Loan> loans = new ArrayList<>();
        
        File file = new File(LOANS_FILE);
        if (!file.exists()) {
            return loans;
        }

        try (BufferedReader br = new BufferedReader( new FileReader(file) ) ) {
            br.readLine(); // Ignore the header

            String line;
            while ( (line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                int patronId = Integer.parseInt(parts[1]);
                String bookIsbn = parts[2];
                LocalDate loanDate = LocalDate.parse(parts[3]);
                LocalDate dueDate = LocalDate.parse(parts[4]);
                boolean isReturned = Boolean.parseBoolean(parts[5]);

                // Search the complete object in the lists
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

    public static void saveLoans(List<Loan> loans) {
        File file = new File(LOANS_FILE);
        file.getParentFile().mkdirs(); // Ensure the file path exists

        try (BufferedWriter bw = new BufferedWriter( new FileWriter(file) ) ) {
            bw.write("id,patronId,bookIsbn,loanDate,dueDate,isReturned"); // Header
            bw.newLine();
            
            for (Loan l : loans) {
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
