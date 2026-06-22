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

public class LoanPanel extends JPanel implements ActionListener {
    private LoanController controller;
    private JTable loansTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton checkOutButton;
    private JButton checkInButton;

    public LoanPanel(LoanController controller) {
        this.controller = controller;
        initComponents();
        initListeners();
        refreshTable(controller.getAllLoans());
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Loan ID", "Book Title", "Patron ID" ,"Patron Name", "Loan Date", "Due Date", "Status", "Fine ($)"}, 0);
        loansTable = new JTable(tableModel);

        searchField = new JTextField();
        checkOutButton = new JButton("Check Out Book");
        checkInButton = new JButton("Check In (Return)");

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Search (Book or Patron): "), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(checkOutButton);
        buttonsPanel.add(checkInButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(loansTable), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initListeners() {
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

    private void search() {
        String query = searchField.getText().trim().toLowerCase();
        List<Loan> result;

        if (query.isEmpty()) {
            result = controller.getAllLoans();
        } else {
            result = controller.getAllLoans().stream()
                    .filter(l -> l.getBook().getTitle().toLowerCase().contains(query) || 
                                 l.getPatron().getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        refreshTable(result);
    }

    private void openCheckOutDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Check Out Book", true);
        dialog.setLayout(new GridLayout(3, 2));

        JTextField patronIdField = new JTextField();
        JTextField isbnField = new JTextField();

        dialog.add(new JLabel("Patron ID:"));
        dialog.add(patronIdField);
        dialog.add(new JLabel("Book ISBN:"));
        dialog.add(isbnField);

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        dialog.add(confirmButton);
        dialog.add(cancelButton);

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

        cancelButton.addActionListener(ev -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkOutButton) {
            openCheckOutDialog();
        }

        if (e.getSource() == checkInButton) {
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