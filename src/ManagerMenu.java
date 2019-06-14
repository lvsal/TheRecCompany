import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.LinkedList;


public class ManagerMenu {
    private JTabbedPane itemMenu;
    private JPanel managerPanel;
    private JPanel itemPanel;
    private JTable customerTable;
    private JButton btnRefresh;
    private JButton btnLogout;


    private JTabbedPane tabbedPane1;
    private JTable saleTable;
    private JTable rentTable;
    private JButton buttonAdd;
    private JScrollPane scrollPanel;
    private JTable appointmentTable;
    private JButton signOutButton;
    private JLabel nameLabel;
    //private JButton requestButton;
    private JFrame frame = new JFrame("Manager Menu");
    private DefaultTableModel model;
    private DefaultTableModel model2;
    private DefaultTableModel model3;

    private final static String customerInfo[] = {"Customer ID", "Name", "Phone", "Address", "City", "Province"};

    private final static String saleInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String rentInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String atvInfo[] = {"VIN", "Model", "Price", "Stock", "Ground"};
    private final static String bicycleInfo[] = {"Serial No.", "Model", "Price", "Stock", "Ground"};


    private final static String sqlURL = "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2";
    private final static String sqlUsername = "h7euKF3cs2";
    private final static String sqlPassword = "2QL01X7xCG";

    private final static String queryCust = "Select * from CUSTOMER";

    private final static String querySaleInfo = "Select * from FOR_SALE";
    private final static String queryRentInfo = "Select * from FOR_RENT";

    private PreparedStatement ps;


    private Connection con;
    private ManagerMenu menu;

    public ManagerMenu() {

        menu = this;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        con = createConnection();
        displayItems();
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
                        con.close();
                    } catch (Exception ec) {
                        ec.printStackTrace(new java.io.PrintStream(System.out));
                    }
                    System.exit(0);
                }
            }
        };

        frame.addWindowListener(exitListener);

        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                con = createConnection();
                refreshItems();
                refreshCustomers();
            }
        });
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure that you want to logout?",
                        "Logout Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    Login login = new Login();
                    frame.dispose();
                }
            }
        });


        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    int row = customerTable.rowAtPoint(e.getPoint());
                    int key = (int) customerTable.getValueAt(row, 0);
                    EditCustInfo edit = new EditCustInfo(con, key, menu);
                    e.consume();
                }
            }
        });

        // TODO: Open Item Edit Menu
        saleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = saleTable.rowAtPoint(e.getPoint());
                    int key = (int) saleTable.getValueAt(row, 0);
                    EditItem item1 = new EditItem(con, key, menu, "sale");
                    e.consume();
                }
            }
        });

        // TODO: Open Item Edit Menu
        rentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = rentTable.rowAtPoint(e.getPoint());
                    int key = (int) rentTable.getValueAt(row, 0);
                    EditItem item2 = new EditItem(con, key, menu, "rent");
                    e.consume();
                }
            }
        });
        buttonAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                EditItem newItem = new EditItem(con, menu);
            }
        });
    }

    public void createUIComponents() {
        model = new DefaultTableModel(0, 0);
        for (String s : customerInfo) {
            model.addColumn(s);
        }
        customerTable = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model2 = new DefaultTableModel(0, 0);
        for (String sa : saleInfo) {
            model2.addColumn(sa);
        }
        saleTable = new JTable(model2) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        model3 = new DefaultTableModel(0, 0);
        for (String r : rentInfo) {
            model3.addColumn(r);
        }

        rentTable = new JTable(model3) {
            public boolean isCellEditable(int row, int column) { return false; }
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

    private Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(sqlURL, sqlUsername, sqlPassword);
            return con;
        } catch (Exception e) {
            e.printStackTrace(new java.io.PrintStream(System.out));
        }
        return null;
    }

    private void displayCustomers() {
        try {
            ps = con.prepareStatement(queryCust);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
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

    protected void refreshItems() {
        model2.setRowCount(0);
        model3.setRowCount(0);
        displayItems();
    }

    private void displayItems() {
        try {
            ps = con.prepareStatement(querySaleInfo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(3), rs.getDouble(2), rs.getInt(4)
                };
                model2.addRow(toAdd);
            }
            ps = con.prepareStatement(queryRentInfo);
            rs = ps.executeQuery();
            while(rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(3), rs.getDouble(2), rs.getInt(4)
                };
                model3.addRow(toAdd);
            }


        } catch (Exception e) {
            e.printStackTrace(new java.io.PrintStream(System.out));
        }
    }

    protected void refreshCustomers() {
        model.setRowCount(0);
        displayCustomers();
    }

}
