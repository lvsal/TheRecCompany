import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.sql.*;

public class InstructorMenu {


    private JFrame frame = new JFrame("Instructor Menu");
    private DefaultTableModel model;
    private DefaultTableModel model2;
    private DefaultTableModel model3;
    private DefaultTableModel model4;
    private DefaultTableModel model5;

    private Connection con;
    private ResultSet rs;
    private JPanel panel1;
    private JButton btnRefresh;
    private JButton btnLog;
    private JPanel teachingTab;
    private JTable table1;
    private JTable table2;
    private JPanel courseTab;


    public InstructorMenu(Connection con) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
/*
        con = connection;
        displayItems();
        displayCustomers();
        displayEmployee();
        displayCourses();
        frame.setContentPane(managerPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
*/
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure that you want to close the application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    try {
                        con.close();
                    } catch (Exception ec) {
                        ec.printStackTrace(new PrintStream(System.out));
                    }
                    System.exit(0);
                }
            }
        };

        frame.addWindowListener(exitListener);
    }

}
