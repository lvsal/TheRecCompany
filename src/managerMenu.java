import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.*;
import java.util.LinkedList;


public class managerMenu {
    private JTabbedPane itemMenu;
    private JPanel managerPanel;
    private JPanel itemPanel;
    private JTable table1;
    private JTable customerTable;


    private JTabbedPane tabbedPane1;
    private JScrollPane scrollPanel;
    private JTable appointmentTable;
    private JButton signOutButton;
    private JLabel nameLabel;
    //private JButton requestButton;
    private JFrame frame = new JFrame("Doctor Console");
    private DefaultTableModel model;
    private DefaultTableModel model2;

    private final static String customerInfo[] = {"Customer ID", "Name", "Phone", "Address", "City", "Province"};




    public managerMenu() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);


        displayCustomers();
        frame.setContentPane(managerPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure that you want to close the application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    try {
                        int x = 1;
                        //con.close();
                    } catch (Exception ec) {
                        ec.printStackTrace(new java.io.PrintStream(System.out));
                    }
                    System.exit(0);
                }
            }
        };

        frame.addWindowListener(exitListener);





    }

    public void createUIComponents() {
        model = new DefaultTableModel(0, 0);
        //model2 = new DefaultTableModel(0, 0);
        for (String s : customerInfo) {
            model.addColumn(s);
        }
        customerTable = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        /*
        for (String s : appts) {
            model2.addColumn(s);
        }
        appointmentTable = new JTable(model2) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };*/
    }

    private void displayCustomers() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2", "h7euKF3cs2", "2QL01X7xCG");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from CUSTOMER");
            while(rs.next()) {
                System.out.println(rs.getInt(1) + " " +rs.getString(2)+ " "+ rs.getString(3)+" "+ rs.getString(5)+
                       " "+ rs.getString(6) + " " +  rs.getString(4));
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(5),
                            rs.getString(6), rs.getString(4)
                };
                model.addRow(toAdd);
            }

        } catch (Exception e) {
            e.printStackTrace(new java.io.PrintStream(System.out));
        }
    }
}
