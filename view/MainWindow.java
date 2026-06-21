package view;

import controller.BookController;
import controller.LoanController;
import controller.PatronController;
import exception.PersistenceException;
import javax.swing.*;
import persistence.FileManager;

public class MainWindow extends JFrame {
    public MainWindow (){
        setTitle("JavaLibrary");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        FileManager fileManager = new FileManager();

        try {
            BookController bookController = new BookController(fileManager);
            PatronController patronController = new PatronController(fileManager);
            LoanController loanController = new LoanController(fileManager, bookController, patronController);
            patronController.setLoans(loanController.getLoansReference());

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Books", new BooksPanel(bookController));
            tabs.addTab("Patrons", new PatronsPanel(patronController));
            tabs.addTab("Loans", new LoanPanel(loanController));
            add(tabs);
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(this, "Could not load data files: " + ex.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
