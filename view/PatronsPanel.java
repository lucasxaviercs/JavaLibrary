package view;

import controller.PatronController;
import exception.PatronHasActiveLoansException;
import exception.PersistenceException;
import model.Patron;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class PatronsPanel extends JPanel implements ActionListener {
    private PatronController controller;
    private JTable patronsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addPatronButton;
    private JButton updatePatronButton;
    private JButton removePatronButton;

    public PatronsPanel(PatronController controller){
        this.controller = controller;
        initComponents();
        initListeners();
        refreshTable(controller.getAllPatrons());
    }

    private void initComponents(){
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact"}, 0);
        patronsTable = new JTable(tableModel);

        searchField = new JTextField();
        addPatronButton = new JButton("Add Patron");
        updatePatronButton = new JButton("Edit");
        removePatronButton = new JButton("Delete");

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Search: "), BorderLayout.WEST);
        top.add(searchField, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(addPatronButton);
        buttonsPanel.add(updatePatronButton);
        buttonsPanel.add(removePatronButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(patronsTable), BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

    }

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
    }

    private void refreshTable(List<Patron> patrons){
        tableModel.setRowCount(0);
        for(Patron patron : patrons){
            tableModel.addRow(new Object[]{patron.getId(), patron.getName(), patron.getContact()});
        }
    }

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

    private void openAddDialog(){
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Patron", true);
        dialog.setLayout(new GridLayout(3, 2));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        dialog.add(new JLabel("Name: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Contact: "));
        dialog.add(contactField);

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        dialog.add(confirmButton);
        dialog.add(cancelButton);

        confirmButton.addActionListener(ev -> {
            try{
                controller.addPatron(nameField.getText(), contactField.getText());
                refreshTable(controller.getAllPatrons());
                dialog.dispose();
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (PersistenceException ex){
                JOptionPane.showMessageDialog(dialog, "Could not save patron to file" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(ev-> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    private void openUpdateDialog(){
        int selectedRow = patronsTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Select a patron to edit!");
            
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Patron patron = controller.findById(id);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Patron", true);
        dialog.setLayout(new GridLayout(3, 2));

        JTextField nameField = new JTextField(patron.getName());
        JTextField contactField = new JTextField(patron.getContact());

        dialog.add(new JLabel("ID:"));
        dialog.add(new JLabel(String.valueOf(id)));
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new Label("Contact:"));
        dialog.add(contactField);

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        dialog.add(confirmButton);
        dialog.add(cancelButton);

        confirmButton.addActionListener(ev -> {
            try{
                controller.editPatron(id, nameField.getText(), contactField.getText());
                refreshTable(controller.getAllPatrons());
                dialog.dispose();
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (PersistenceException ex){
                JOptionPane.showMessageDialog(dialog, "Could not save patron to file. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(ev -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        
    }

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
    }

    
    


}
