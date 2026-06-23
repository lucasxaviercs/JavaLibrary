package view;

import controller.LoanController;
import exception.BookAlreadyOnLoanException;
import exception.PersistenceException;
import model.Loan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;


/*
 * GUI panel for managing library book loans.
 * Handles the display, search, checkout, and checkin operations.
 */
public class LoanPanel extends JPanel implements ActionListener {
    private LoanController controller; // Handles business logic for loan operations
    private JTable loansTable; // Displays the loan records in a grid format
    private DefaultTableModel tableModel;
    private JTextField searchField; // Captures user input for filtering the table
    private JButton checkOutButton; // Triggers the checkout dialog
    private JButton checkInButton; // Triggers the checkin process for a selected loan

    /*
     * Constructor initializes the UI, sets up event listeners, and loads initial data.
     */
    public LoanPanel(LoanController controller) {
        this.controller = controller;
        initComponents();
        initListeners();
        refreshTable(controller.getAllLoans());
    }

    /*
     * It brings together the graphic elements and defines the layout structure.
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        // Defines the table columns
        tableModel = new DefaultTableModel(new Object[]{"Loan ID", "Book Title", "Patron ID" ,"Patron Name", "Loan Date", "Due Date", "Status", "Fine ($)"}, 0);
        loansTable = new JTable(tableModel);

        searchField = new JTextField();
        checkOutButton = new JButton("Check Out Book");
        checkInButton = new JButton("Check In (Return)");

        // Groups the search components at the top of the panel
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Search (Book or Patron): "), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        // Groups the action buttons at the bottom of the panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(checkOutButton);
        buttonsPanel.add(checkInButton);

        // Adds the sections to the main panel
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(loansTable), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    /*
     * Binds event listeners to interactive components.
     */
    private void initListeners() {
        // Triggers the search method whenever the user types, deletes, or modifies text
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(); }
        });

        checkOutButton.addActionListener(this);
        checkInButton.addActionListener(this);
    }

    /*
     * Clears the current table and refills it with the current list of loans.
     */
    private void refreshTable(List<Loan> loans) {
        tableModel.setRowCount(0);
        for (Loan l : loans) {
            String status = l.getIsReturned() ? "Returned" : "Active";
            tableModel.addRow(new Object[]{
                    l.getId(),
                    l.getBook().getTitle(),
                    l.getPatron().getId(),
                    l.getPatron().getName(),
                    l.getLoanDate().toString(),
                    l.getDueDate().toString(),
                    status,
                    String.format("$%.2f", l.getFineAmount())
            });
        }
    }

    /*
     * Filters the displayed loans based on the text entered in the search field.
     */
    private void search() {
        String query = searchField.getText().trim().toLowerCase();
        List<Loan> result;

        if (query.isEmpty()) {
            result = controller.getAllLoans();
        } else {
            // Filters the list by checking if the query matches the book title or patron name
            result = controller.getAllLoans().stream()
                    .filter(l -> l.getBook().getTitle().toLowerCase().contains(query) || 
                                 l.getPatron().getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        refreshTable(result);
    }

    /*
     * Opens a modal dialog to collect patron ID and book ISBN for a new loan.
     */
    private void openCheckOutDialog() {

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Check Out Book", true);
    
        JTextField patronIdField = new JTextField();
        JTextField isbnField = new JTextField();
    
        // FORM
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        formPanel.add(new JLabel("Patron ID:"));
        formPanel.add(patronIdField);
    
        formPanel.add(new JLabel("Book ISBN:"));
        formPanel.add(isbnField);
    
        // Buttons
        JButton confirmButton = new JButton("Check Out");
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
    
        // Layout
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    
        confirmButton.addActionListener(ev -> {
            try {
                int patronId = Integer.parseInt(patronIdField.getText().trim());
                String isbn = isbnField.getText().trim();
    
                controller.checkOut(patronId, isbn);
                refreshTable(controller.getAllLoans());
    
                dialog.dispose();
    
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Patron ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
    
            } catch (BookAlreadyOnLoanException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
    
            } catch (PersistenceException ex) {
                JOptionPane.showMessageDialog(dialog, "Could not save loan to file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        dialog.setSize(420, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /*
     * Handles button click events for the panel.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkOutButton) {
            openCheckOutDialog();
        }

        if (e.getSource() == checkInButton) {
            // Identifies which row the user clicked in the table
            int selectedRow = loansTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a loan from the table to return it!");
                return;
            }

            int loanId = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                controller.checkIn(loanId);
                refreshTable(controller.getAllLoans());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            } catch (PersistenceException ex) {
                JOptionPane.showMessageDialog(this, "Could not save to file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}