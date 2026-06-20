package persistence;

import JavaLibrary.model.Book;
import JavaLibrary.model.Patron;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Book;

public class FileManager {

    private static final String BOOKS_FILE = "books.csv";
    private static final String PATRONS_FILE = "patrons.csv";
    private static final String BOOKS_FILE = "data/books.csv";

    // reads Books csv data and loads it on a Books List
    public List<Book> loadBooks(){
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
            System.out.println("Error: failed to load books csv file");
        }

        return books;
    }

    // saves the Book list on the Books csv file
    public void saveBooks(List<Book> books){
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            pw.println("title,author,isbn,totalCopies,availableCopies");

            for (Book b : books){
                String line = b.getTitle() + "," + b.getAuthor() + "," + b.getIsbn() + "," + b.getTotalCopies() + "," + b.getAvailableCopies();
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error: failed to save books in csv file");
            e.printStackTrace();
        }
    }

    public static void savePatrons(List<Patron> patrons) throws IOException{
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PATRONS_FILE))){
            bw.write("id;name;contact");
            bw.newLine();

            for(Patron p : patrons){
                bw.write(String.join(";", String.valueOf(p.getId()), p.getName(), p.getContact()));

                bw.newLine();
            }
        }
    }

    public static List<Patron> loadPatrons() throws IOException{
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
        }

        return patrons;
    }

    
}
