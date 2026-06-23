package view;
 
import controller.BookController;
import controller.LoanController;
import controller.PatronController;
import exception.PersistenceException;
import persistence.FileManager;
 
import javax.swing.*;
import java.awt.*;
 
public class MainWindow extends JFrame {
 
    public MainWindow() {
        setTitle("JavaLibrary");
        setSize(980, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
        FileManager fileManager = new FileManager();
 
        try {
            BookController bookCtrl = new BookController(fileManager);
            PatronController patronCtrl = new PatronController(fileManager);
            LoanController loanCtrl = new LoanController(fileManager, bookCtrl, patronCtrl);
            patronCtrl.setLoans(loanCtrl.getLoansReference());
 
            // CardLayout troca qual painel está visível
            JPanel content = new JPanel(new CardLayout());
            content.add(new BooksPanel(bookCtrl), "Books");
            content.add(new PatronsPanel(patronCtrl), "Patrons");
            content.add(new LoanPanel(loanCtrl), "Loans");
 
            add(buildSidebar(content), BorderLayout.WEST);
            add(content, BorderLayout.CENTER);
 
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private JPanel buildSidebar(JPanel content) {
        CardLayout cl = (CardLayout) content.getLayout();
    
        JButton btnBooks = new JButton("Books");
        JButton btnPatrons = new JButton("Patrons");
        JButton btnLoans = new JButton("Loans");
    
        // Configuração visual dos botões
        JButton[] buttons = {btnBooks, btnPatrons, btnLoans};
    
        for (JButton btn : buttons) {

            Color normalColor = new Color(60, 63, 65);
            Color hoverColor = new Color(80, 120, 180);
        
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 15));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(normalColor);
            btn.setForeground(Color.WHITE);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            btn.setBorderPainted(false);
            btn.setOpaque(true);
        
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
        
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(hoverColor);
                }
        
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(normalColor);
                }
            });
        }
    
        btnBooks.addActionListener(e -> cl.show(content, "Books"));
        btnPatrons.addActionListener(e -> cl.show(content, "Patrons"));
        btnLoans.addActionListener(e -> cl.show(content, "Loans"));
    
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(43, 43, 43));
    
        JLabel title = new JLabel("JavaLibrary");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(30));
    
        sidebar.add(btnBooks);
        sidebar.add(Box.createVerticalStrut(10));
    
        sidebar.add(btnPatrons);
        sidebar.add(Box.createVerticalStrut(10));
    
        sidebar.add(btnLoans);
    
        return sidebar;
    }
}