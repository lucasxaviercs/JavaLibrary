package view;

import controller.PatronController;
import exception.PatronHasActiveLoansException;
import exception.PersistenceException;
import model.Patron;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;


/*
 * GUI panel for managing library patrons.
 * Allows adding, editing, deleting and searching patrons
 */
public class PatronsPanel extends JPanel implements ActionListener {
    private PatronController controller; // Controller that handles business logic

    // Table to display patrons
    private JTable patronsTable;
    private DefaultTableModel tableModel;

    // Search field
    private JTextField searchField;

    // Buttons for actions
    private JButton addPatronButton;
    private JButton updatePatronButton;
    private JButton removePatronButton;
    private JButton historyButton;

    /*
     * Constructor initializes UI and loads data
     */
    public PatronsPanel(PatronController controller){
        this.controller = controller;
        initComponents();
        initListeners();
        refreshTable(controller.getAllPatrons());
    }

    // Initialize UI components
    private void initComponents(){
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact"}, 0);
        patronsTable = new JTable(tableModel);

        searchField = new JTextField();
        addPatronButton = new JButton("Add Patron");
        updatePatronButton = new JButton("Edit");
        removePatronButton = new JButton("Delete");
        historyButton = new JButton("View History");

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Search: "), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(addPatronButton);
        buttonsPanel.add(updatePatronButton);
        buttonsPanel.add(removePatronButton);
        buttonsPanel.add(historyButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(patronsTable), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

    }

    // Initializes event listeners
    private void initListeners(){
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e){ search(); }
            @Override
            public void removeUpdate(DocumentEvent e){ search(); }
            @Override
            public void changedUpdate(DocumentEvent e){ search(); }
        });

        addPatronButton.addActionListener(this);
        updatePatronButton.addActionListener(this);
        removePatronButton.addActionListener(this);
        historyButton.addActionListener(this);
    }

    // Refreshes table with a list of patrons
    private void refreshTable(List<Patron> patrons){
        tableModel.setRowCount(0);
        for(Patron patron : patrons){
            tableModel.addRow(new Object[]{patron.getId(), patron.getName(), patron.getContact()});
        }
    }

    // Performs search based on input text
    private void search(){
        String query = searchField.getText();
        List<Patron> result;

        if(query.trim().isEmpty()){
            result = controller.getAllPatrons();
        }else{
            result = controller.searchPatrons(query);
        }
        
        refreshTable(result);
    }

    // Opens dialog to add a new patron
    private void openAddDialog() {

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Patron", true);
    
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
    
        // Panel of the form
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
    
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
    
        // Buttons
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
    
        // Layout
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    
        confirmButton.addActionListener(ev -> {
            try {
                controller.addPatron(nameField.getText(), contactField.getText());
    
                refreshTable(controller.getAllPatrons());
                dialog.dispose();
    
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    
            } catch (PersistenceException ex) {
                JOptionPane.showMessageDialog(dialog, "Could not save patron to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(ev -> dialog.dispose());
    
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Opens dialog to edit selected patron
    private void openUpdateDialog() {
        int selectedRow = patronsTable.getSelectedRow();
    
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a patron to edit!");
            return;
        }
    
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Patron patron = controller.findById(id);
    
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Patron", true);
    
        JTextField nameField = new JTextField(patron.getName());
        JTextField contactField = new JTextField(patron.getContact());
    
        // Panel of the form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        formPanel.add(new JLabel("ID:"));
        formPanel.add(new JLabel(String.valueOf(id)));
    
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
    
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
    
        // Buttons
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
    
        // Layout
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    
        confirmButton.addActionListener(ev -> {
            try {
                controller.editPatron(id, nameField.getText(), contactField.getText());
    
                refreshTable(controller.getAllPatrons());
                dialog.dispose();
    
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    
            } catch (PersistenceException ex) {
                JOptionPane.showMessageDialog( dialog, "Could not save patron to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(ev -> dialog.dispose());
    
        dialog.setSize(420, 220);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Handles button clicks
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == addPatronButton){
            openAddDialog();
        }

        if(e.getSource() == updatePatronButton){
            openUpdateDialog();
        }

        if(e.getSource() == removePatronButton){
            int selectedRow = patronsTable.getSelectedRow();

            if(selectedRow == -1){
                JOptionPane.showMessageDialog(this, "Select a patron to delete!");

                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                controller.deletePatron(id);
                refreshTable(controller.getAllPatrons());
            } catch(PatronHasActiveLoansException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }catch(PersistenceException ex){
                JOptionPane.showMessageDialog(this, "Could not save to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if(e.getSource() == historyButton){
            int selectedRow = patronsTable.getSelectedRow();

            if(selectedRow == -1){
                JOptionPane.showMessageDialog(this, "Select a patron to view their history!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int patronId = (int) tableModel.getValueAt(selectedRow, 0);
            String patronName = (String) tableModel.getValueAt(selectedRow, 1);
            
            java.util.List<model.Loan> history = controller.getPatronHistory(patronId);

            if(history.isEmpty()){
                JOptionPane.showMessageDialog(this, patronName + " has no loan history.", "Patron History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder("History for " + patronName + ":\n\n");
            for(model.Loan l : history){
                String status = l.getIsReturned() ? "Returned" : "Active";
                sb.append("- ").append(l.getBook().getTitle())
                  .append(" | Due: ").append(l.getDueDate())
                  .append(" | Status: ").append(status).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 200));

            JOptionPane.showMessageDialog(this, scrollPane, "Patron History", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    


}