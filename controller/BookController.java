package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Book;
import persistence.FileManager;

public class BookController {
    private List<Book> books = new ArrayList<>();
    private FileManager fileManager;

    public BookController(FileManager fileManager) {
        this.fileManager = fileManager;
        this.books = fileManager.loadBooks();
    }

    public void addBook(String title, String author, String isbn, int totalCopies) {
        if (title == null || title.isBlank()){
            throw new IllegalArgumentException("You cannot add a book with no title!");
        } 

        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("You cannot add a book with no author!");
        }

        if(isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("You cannot add a book with no isbn!");
        }

        if (totalCopies <= 0){
            throw new IllegalArgumentException("Books must have a positive quantity!");
        }

        boolean isNew = books.stream().noneMatch(b -> b.getIsbn().equals(isbn));

        if(!isNew) {
            throw new IllegalArgumentException("There's already a book with this isbn.");
        }

        Book newBook = new Book(title, author,  isbn, totalCopies);
        books.add(newBook);
        fileManager.saveBooks(books);
    }

    public void removeBook(String isbn){
        boolean bookExists = books.stream().anyMatch(b -> b.getIsbn().equals(isbn));

        if (!bookExists) {
            throw new IllegalArgumentException("The book you're trying to remove does not exist!");
        }

        books.removeIf(b -> b.getIsbn().equals(isbn));
        fileManager.saveBooks(books);
    }

    public void updateBook(String isbn, String newTitle, String newAuthor, int newTotalCopies){
        Book b = findByIsbn(isbn);

        if (b == null) {
            throw new IllegalArgumentException("The book you're trying to update does not exist!");
        }

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("A book must have a title.");
        }
        if (newAuthor == null || newAuthor.isBlank()) {
            throw new IllegalArgumentException("A book must have a author.");
        }
        if (newTotalCopies <= 0) {
            throw new IllegalArgumentException("A book must have a positive quantity!");
        }

        int copiesOnLoan = b.getTotalCopies() - b.getAvailableCopies();
        if (newTotalCopies < copiesOnLoan) {
            throw new IllegalArgumentException(
                "Cannot set total copies below " + copiesOnLoan + " — that many are currently on loan.");
        }

        b.setTitle(newTitle);
        b.setAuthor(newAuthor);
        b.setTotalCopies(newTotalCopies);
        b.setAvailableCopies(newTotalCopies - copiesOnLoan);

        fileManager.saveBooks(books);
    }

    public List<Book> searchBooks(String query){
        String lower = query.toLowerCase();
        return books.stream().filter(b -> b.getIsbn().toLowerCase().contains(lower) || b.getTitle().toLowerCase().contains(lower) || b.getAuthor().toLowerCase().contains(lower)).collect(Collectors.toList());
    }

    public List<Book> getAllBooks(){
        return books;
    }

    public Book findByIsbn(String isbn){
        return books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    public void checkOutBook(String isbn){
        Book b = findByIsbn(isbn);
        if (b != null) {
            b.checkOut();
            fileManager.saveBooks(books);
        }
    }

    public void checkInBook(String isbn){
         Book b = findByIsbn(isbn);
        if (b != null) {
            b.checkIn();
            fileManager.saveBooks(books);
        }
    }
}