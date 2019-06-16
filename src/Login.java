import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;


public class Login extends JDialog {
    private JButton btnLogin;
    private JButton btnCreate;
    private JPasswordField textPassword;
    private JFormattedTextField textUser;
    private JFrame frame = new JFrame("Login");
    private JPanel outer;

    private final static String sqlURL = "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2?useSSL=false";
    private final static String sqlUsername = "h7euKF3cs2";
    private final static String sqlPassword = "2QL01X7xCG";

    final static String sqlLogin = "select u.type from USERINFO as u where u.username = ? AND u.password = ?";
    private PreparedStatement ps;
    private Connection con;

    public Login() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
        frame.setContentPane(this.outer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        con = createConnection();


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
                    } catch (Exception e2) {
                        System.out.println("Program closed without needing to close connection.");
                    }
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(exitListener);

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String username = textUser.getText().toLowerCase();
                String password = String.valueOf(textPassword.getPassword());
                String type = "none";
                try {
                    ps = con.prepareStatement(sqlLogin);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) type = rs.getString(1);

                    if (type.equals("Manager") || type.equals("Admin")) {
                        ManagerMenu menu = new ManagerMenu(con, type);
                        frame.dispose();
                    } else if (type.equals("Employee")) {
                        EmployeeMenu inst = new EmployeeMenu(con, username, type);
                        frame.dispose();
                    } else if (type.equals("Instructor")) {
                        EmployeeMenu inst = new EmployeeMenu(con, username, type);
                        frame.dispose();
                    } else if (type.equals("Customer")) {
                        CustomerMenu cust = new CustomerMenu(username, con);
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.");
                    }

                } catch (Exception e2) {
                    e2.printStackTrace(new PrintStream(System.out));
                }
            }
        });
        btnCreate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                CreateCustomer create = new CreateCustomer(con);
            }
        });
    }

    public Login(Connection con) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
        frame.setContentPane(this.outer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        this.con = con;


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
                    } catch (Exception e2) {
                        System.out.println("Program closed without needing to close connection.");
                    }
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(exitListener);

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String username = textUser.getText();
                String password = String.valueOf(textPassword.getPassword());
                String type = "none";
                try {
                    ps = con.prepareStatement(sqlLogin);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) type = rs.getString(1);

                    if (type.equals("Manager") || type.equals("Admin")) {
                        ManagerMenu menu = new ManagerMenu(con, type);
                        frame.dispose();
                    } else if (type.equals("Employee")) {
                        EmployeeMenu inst = new EmployeeMenu(con, username, type);
                        frame.dispose();
                    } else if (type.equals("Instructor")) {
                        EmployeeMenu inst = new EmployeeMenu(con, username, type);
                        frame.dispose();
                    } else if (type.equals("Customer")) {
                        CustomerMenu cust = new CustomerMenu(username, con);
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.");
                    }

                } catch (Exception e2) {
                    e2.printStackTrace(new PrintStream(System.out));
                }
            }
        });
        btnCreate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                CreateCustomer create = new CreateCustomer(con);
            }
        });
    }


    private Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    sqlURL, sqlUsername, sqlPassword);
            return con;
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
        return null;
    }


}
