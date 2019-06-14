import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;
//import javax.swing.JFrame;

public class CreateCustomer {
    private JPanel panel1;
    private JPasswordField pass;
    private JFormattedTextField username;
    private JTextField namefield;
    private JTextField phonefield;
    private JTextField provfield;
    private JTextField streetfield;
    private JTextField cityfield;
    private JButton createAccountButton;
    private JFrame frame = new JFrame("Create Account");

    final static String sqlsignup = "insert into CUSTOMER (CustName, CustPhone, CustProv, CustStreet, CustCity, username) values (?, ?, ?, ?, ?, ?)";
    final static String sql_infosign = "insert into USERINFO (username, password, type) values (?, ?, ?)";
//    final static String getCustID = "select c.CustomerID from CUSTOMER as c where c.CustName=?";

    private PreparedStatement ps;
    private PreparedStatement ps1;
    private PreparedStatement ps2;
    private Connection con;

    public CreateCustomer(Connection connection) {


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        con = connection;


        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure that you want to close the application?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(exitListener);

        createAccountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                try {
                    String user = username.getText();
                    String passd = String.valueOf(pass.getPassword());
                    String custname = namefield.getText();

                    ps = con.prepareStatement(sqlsignup);
                    ps.setString(1, namefield.getText());
                    ps.setString(2, phonefield.getText());
                    ps.setString(3, provfield.getText());
                    ps.setString(4, streetfield.getText());
                    ps.setString(5, cityfield.getText());
                    ps.setString(6, user);

                    ps1 = con.prepareStatement(sql_infosign);
                    ps1.setString(1, user);
                    ps1.setString(2, passd);
                    ps1.setString(3, "Customer");

                    ps.executeUpdate();
                    ps1.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Successfully created an account");
                    con.close();
                    frame.dispose();
                } catch (Exception c) {
                    error_message("Could not create an account :( Try again ");
                    c.printStackTrace(new PrintStream(System.out));
                }

            }
        });
    }


    private Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2", "h7euKF3cs2", "2QL01X7xCG");
            return con;
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
        return null;
    }

    private static void error_message(String error) {
        JOptionPane.showMessageDialog(null, error, "Error!", JOptionPane.INFORMATION_MESSAGE);
    }

}
