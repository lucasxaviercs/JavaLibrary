public class BookController {
    private List<Book> books;

    public void addBook(String title, String author, String isbn, int totalCopies) {
    }

    public void removeBook(String isbn){
    }

    public void updateBook(String isbn, String newTitle, String newAuthor, int newTotalCopies){
    }

    public List<Book> searchBooks(String query){}

    public List<Book> getAllBooks(){}

    public Book findByIsbn(String isbn){}

    public void checkOutBook(String isbn){}

    public void checkInBook(String isbn){}
}