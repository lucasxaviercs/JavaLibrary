package JavaLibrary.model;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;

    // Constructors
    public Book(String title, String author, String isbn, int totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies; // Initially, all copies are available
    }

    public Book(String title, String author, String isbn, int totalCopies, int availableCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters and Setters
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }

    public String getAuthor(){ return author; }
    public void setAuthor(String author){ this.author = author; }

    public String getIsbn(){ return isbn; }
    public void setIsbn(String isbn){ this.isbn = isbn; }

    public int getTotalCopies(){ return totalCopies; }
    public void setTotalCopies(int totalCopies){ 
        this.totalCopies = totalCopies; 
    }

    public int getAvailableCopies(){ return availableCopies; }
    public void setAvailableCopies(int availableCopies){ 
        this.availableCopies = availableCopies; 
    }

    // Functions
    // used when a patron returns a book to the library
    public void checkIn(){
        if(availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    // used when a patron checks out a book from the library
    public void checkOut(){
        if(availableCopies > 0) {
            availableCopies--;
        }
    }

    // checks if the book is available for checkout
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    // created this method to help debug and display book information
    @Override
    public String toString() {
        return title + " by " + author + " (ISBN: " + isbn + ") - Available Copies: " + availableCopies + "/" + totalCopies;
    }
}