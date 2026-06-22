package view;

import controller.BookController;
import exception.PersistenceException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.Book;

public class BooksPanel extends JPanel implements ActionListener, ComponentListener {
    private BookController controller;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addBookButton;
    private JButton updateBookButton;
    private JButton removeBookButton;

    // the constructor assigns a BookController so that it can perform actions in Books
    public BooksPanel(BookController controller){
        this.controller = controller;
        initComponents(); // call method to initialize all the components
        initListeners(); // call method to create listeners
        refreshTable(controller.getAllBooks()); // gets all books to appear in the table
    }

    private void initComponents(){
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[] {"ISBN", "Title", "Author", "Total Copies", "Available Copies"}, 0);
        booksTable = new JTable(tableModel);

        searchField = new JTextField(); 
        addBookButton = new JButton("Add Book");
        updateBookButton = new JButton("Edit");
        removeBookButton = new JButton("Delete");

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Buscar: "), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(addBookButton);
        buttonsPanel.add(updateBookButton);
        buttonsPanel.add(removeBookButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(booksTable),BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initListeners(){
        // for every action on the search bar, it will search the books
        searchField.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e){ search(); }

            @Override
            public void removeUpdate(DocumentEvent e){ search(); }

            @Override
            public void changedUpdate(DocumentEvent e){ search(); }
        });

        addBookButton.addActionListener(this);
        removeBookButton.addActionListener(this);
        updateBookButton.addActionListener(this);

        this.addComponentListener(this);
    }

    // returns the table with the books on the list given
    private void refreshTable(List<Book> books){
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getIsbn(), b.getTitle(), b.getAuthor(), b.getTotalCopies(), b.getAvailableCopies()});
        }
    }

    // return a book list given a search and calls refreshTable
    private void search(){
        List<Book> result = null;
        String query = searchField.getText();
        
        if(query.isBlank()){
            result = controller.getAllBooks();
        } else {
            result = controller.searchBooks(query);
        }

        refreshTable(result);
    }

    // when adding a book we need a form for the user to input data
    private void openAddDialog() {
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Book", true);
    dialog.setLayout(new GridLayout(5, 2));

    JTextField titleField = new JTextField();
    JTextField authorField = new JTextField();
    JTextField isbnField = new JTextField();
    JTextField totalCopiesField = new JTextField();

    dialog.add(new JLabel("Title:"));
    dialog.add(titleField);
    dialog.add(new JLabel("Author:"));
    dialog.add(authorField);
    dialog.add(new JLabel("ISBN:"));
    dialog.add(isbnField);
    dialog.add(new JLabel("Total copies:"));
    dialog.add(totalCopiesField);

    JButton confirmButton = new JButton("Confirm");
    JButton cancelButton = new JButton("Cancel");
    dialog.add(confirmButton);
    dialog.add(cancelButton);

    confirmButton.addActionListener(ev -> {
        try {
            // gets user input
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();
            int totalCopies = Integer.parseInt(totalCopiesField.getText());

            // calls controller to perform the action
            controller.addBook(title, author, isbn, totalCopies);
            refreshTable(controller.getAllBooks());
            dialog.dispose(); 
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Total copies must be a valid number", "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(dialog, "Could not save the book to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    });

    cancelButton.addActionListener(ev -> dialog.dispose());

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}
    // when updating a book we need a form for the user to input data
    private void openUpdateDialog() {
        int selectedLine = booksTable.getSelectedRow();

        if (selectedLine == -1){
            JOptionPane.showMessageDialog(this, "Select a book to edit!");
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedLine, 0);
        Book b = controller.findByIsbn(isbn);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setLayout(new GridLayout(5, 2));

        JTextField titleField = new JTextField(b.getTitle());
        JTextField authorField = new JTextField(b.getAuthor());
        JTextField totalCopiesField = new JTextField(String.valueOf(b.getTotalCopies()));

        dialog.add(new JLabel("ISBN:"));
        dialog.add(new JLabel(isbn));
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        dialog.add(new JLabel("Total copies:"));
        dialog.add(totalCopiesField);

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        dialog.add(confirmButton);
        dialog.add(cancelButton);

        confirmButton.addActionListener(ev -> {
            try {
                // gets user input
                String title = titleField.getText();
                String author = authorField.getText();
                int totalCopies = Integer.parseInt(totalCopiesField.getText());

                // calls the controller to perform de action
                controller.updateBook(isbn, title, author, totalCopies);
                refreshTable(controller.getAllBooks());
                dialog.dispose();
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Total copies must be a valid number", "Error", JOptionPane.ERROR_MESSAGE);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (PersistenceException ex) {
                JOptionPane.showMessageDialog(dialog, "Could not save the book to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        });

        cancelButton.addActionListener(ev -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == addBookButton){
            openAddDialog();
        } 
        
        if (e.getSource() == removeBookButton){
            // to remove a book, the user must first select a book row 
            int selectedLine = booksTable.getSelectedRow();

            if (selectedLine == -1){
                JOptionPane.showMessageDialog(this, "Select a book to delete!");
                return;
            }

            String isbn = (String) tableModel.getValueAt(selectedLine, 0);

            try {
                controller.removeBook(isbn);
                refreshTable(controller.getAllBooks());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (PersistenceException ex) {
                JOptionPane.showMessageDialog(this, "Could not save the book to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 

        if (e.getSource() == updateBookButton){
            openUpdateDialog();
        }
    }   

    @Override
    public void componentShown(ComponentEvent e) {
        // refreshes the table when the user comes back to the Book tab
        refreshTable(controller.getAllBooks());
    }

    @Override
    public void componentResized(ComponentEvent e) {  }

    @Override
    public void componentMoved(ComponentEvent e) {  }


    @Override
    public void componentHidden(ComponentEvent e) {  }
}