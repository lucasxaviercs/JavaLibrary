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
        if (title == null){
            System.out.println("Error: you cannot add a book with no title!");
            return;
        } 

        if (author == null) {
            System.out.println("Error: you cannot add a book with no author!");
            return;
        }

        if(isbn == null) {
            System.err.println("Error: you cannot add a book with no isbn!");
            return;
        }

        if (totalCopies <= 0){
            System.out.println("Error: books should have a positive quantity!");
            return;
        }

        boolean isNew = books.stream().noneMatch(b -> b.getIsbn().equals(isbn));

        if(!isNew) {
            System.out.println("Warning: there's already a book with this isbn.");
            return;
        }

        Book newBook = new Book(title, author,  isbn, totalCopies);
        books.add(newBook);
        fileManager.saveBooks(books);
        System.out.println("Log: book added with success!");
    }

    public void removeBook(String isbn){
        boolean bookExists = books.stream().anyMatch(b -> b.getIsbn().equals(isbn));

        if (!bookExists) {
            System.out.println("Error: the book you're trying to remove does not exist!");
            return;
        }

        books.removeIf(b -> b.getIsbn().equals(isbn));
        fileManager.saveBooks(books);
        System.out.println("Log: book removed successfully!");
    }

    public void updateBook(String isbn, String newTitle, String newAuthor, int newTotalCopies){
        Book b = findByIsbn(isbn);

        if (b == null) {
            System.out.println("Error: the book you're trying to update does not exist!");
        }

        if (newTitle != null && !newTitle.isBlank()) {
            b.setTitle(newTitle);
        }
        if (newAuthor != null && !newAuthor.isBlank()) {
            b.setAuthor(newAuthor);
        }

        if (newTotalCopies > 0) {
            b.setTotalCopies(newTotalCopies);
        }

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