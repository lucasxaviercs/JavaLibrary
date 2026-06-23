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
    
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField totalCopiesField = new JTextField();
    
        // FORM
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
    
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
    
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
    
        formPanel.add(new JLabel("Total copies:"));
        formPanel.add(totalCopiesField);
    
        // BUTTONS
        JButton confirmButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
    
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.BLUE);
        confirmButton.setFocusPainted(false);
    
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.BLUE);
        cancelButton.setFocusPainted(false);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
    
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    
        confirmButton.addActionListener(ev -> {
            try {
                controller.addBook(titleField.getText(), authorField.getText(), isbnField.getText(), Integer.parseInt(totalCopiesField.getText()));
    
                refreshTable(controller.getAllBooks());
                dialog.dispose();
    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Total copies must be a number", "Error", JOptionPane.ERROR_MESSAGE);
    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        dialog.setSize(420, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    // when updating a book we need a form for the user to input data
    private void openUpdateDialog() {

        int selectedLine = booksTable.getSelectedRow();
    
        if (selectedLine == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to edit!");
            return;
        }
    
        String isbn = (String) tableModel.getValueAt(selectedLine, 0);
        Book b = controller.findByIsbn(isbn);
    
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
    
        JTextField titleField = new JTextField(b.getTitle());
        JTextField authorField = new JTextField(b.getAuthor());
        JTextField totalCopiesField = new JTextField(String.valueOf(b.getTotalCopies()));
    
        // FORM
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(new JLabel(isbn));
    
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
    
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
    
        formPanel.add(new JLabel("Total copies:"));
        formPanel.add(totalCopiesField);
    
        // BUTTONS
        JButton confirmButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
    
        confirmButton.setBackground(new Color(52, 152, 219));
        confirmButton.setForeground(Color.BLUE);
        confirmButton.setFocusPainted(false);
    
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.BLUE);
        cancelButton.setFocusPainted(false);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
    
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    
        confirmButton.addActionListener(ev -> {
            try {
                controller.updateBook(isbn, titleField.getText(), authorField.getText(), Integer.parseInt(totalCopiesField.getText()));
    
                refreshTable(controller.getAllBooks());
                dialog.dispose();
    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Total copies must be a number", "Error", JOptionPane.ERROR_MESSAGE);
    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        dialog.setSize(450, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
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