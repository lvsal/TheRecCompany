

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;
import java.util.Date;


public class EmployeeMenu {

    private PreparedStatement ps;

    private PreparedStatement ps2;

    private Connection con;
    private EmployeeMenu menu;

    private JTabbedPane tabbedPane1;
    private JPanel EmployeePanel;
    private JTabbedPane tabbedPane2;
    private JTabbedPane tabbedPane3;
    private JTabbedPane tabbedPane4;
    private JTable SaleTable;
    private JTable RentTable;
    private JTable AvailCourTable;
    private JTable PastCourTable;
    private JTable SaleTransTable;
    private JTable RentTransTable;
    private JTable CourseTransTable;
    private JButton refreshButton;
    private JButton logOutButton;

    private JFrame frame = new JFrame("Employee Menu");

    private DefaultTableModel model;
    private DefaultTableModel model2;
    private DefaultTableModel model3;
    private DefaultTableModel model4;
    private DefaultTableModel model5;
    private DefaultTableModel model6;
    private DefaultTableModel model7;


    private final static String saleInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String rentInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String AvailCourses[] = {"CourseID", "Name", "Fee", "InstructorID", "Ground"};
    private final static String PastCourses[] = {"CourseID", "Name", "Fee", "InstructorID", "Ground"};
    private final static String CourseTrans[] = {"CourseID", "CustomerID", "TransID", "Price", "TransDate"};
    private final static String RentTrans[] = {"ItemID", "CustomerID", "TransID", "Price", "Quantity", "TransDate"};
    private final static String SaleTrans[] = {"ItemID", "CustomerID", "TransID", "Price", "Quantity", "TransDate"};


    private final static String sqlURL = "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2";
    private final static String sqlUsername = "h7euKF3cs2";
    private final static String sqlPassword = "2QL01X7xCG";

    private final static String querygroundloc = "Select Location from GROUNDS where Ground=?";

    private final static String querySaleInfo = "Select * from FOR_SALE";
    private final static String queryRentInfo = "Select * from FOR_RENT";
    private final static String querySaleTrans = "Select * from TRANSACTION_SALE";
    private final static String queryRentTrans = "Select * from TRANSACTION_RENT";
    private final static String queryCourTrans = "Select * from TRANSACTION_COURSES";
    private final static String queryCourAvInfo = "Select * from COURSES where Available=1";
    private final static String queryCourPaInfo = "Select * from COURSES where Available=0";
    private final static String updateStockSale = "Update FOR_SALE set Stock=? where ItemID=?";
    private final static String updateRentSale = "Update FOR_RENT set Stock=? where ItemID=?";


    public EmployeeMenu() {
        menu = this;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        con = createConnection();
        displayItems();
        frame.setContentPane(EmployeePanel);
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
                        ec.printStackTrace(new PrintStream(System.out));
                    }
                    System.exit(0);
                }
            }
        };

        frame.addWindowListener(exitListener);

        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                con = createConnection();
                refreshItems();
            }
        });
        logOutButton.addMouseListener(new MouseAdapter() {
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

        // TODO: Open Item Edit Menu
        SaleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = SaleTable.rowAtPoint(e.getPoint());
                    int key = (int) SaleTable.getValueAt(row, 0);
                    String stock = JOptionPane.showInputDialog(frame, "Enter new stock", null);
                    int new_stock = 0;
                    try {
                        new_stock = Integer.parseInt(stock);
                    } catch (Exception ex) {
                        error_message("Could not update stock :( Enter a valid value :|");
                        ex.printStackTrace(new PrintStream(System.out));
                    }
                    try {
                        ps = con.prepareStatement(updateStockSale);
                        ps.setInt(1, new_stock);
                        ps.setInt(2, key);
                        ps.executeUpdate();
                        refreshItems();
                        JOptionPane.showMessageDialog(frame, "Successfully updated stock");
                    } catch (Exception ex) {
                        error_message("Could not update stock :( Try again ");
                        ex.printStackTrace(new PrintStream(System.out));
                    }
                    e.consume();
                }
            }
        });

        // TODO: Open Item Edit Menu
        RentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = RentTable.rowAtPoint(e.getPoint());
                    int key = (int) RentTable.getValueAt(row, 0);
                    String stock = JOptionPane.showInputDialog(frame, "Enter new stock", null);
                    int new_stock = 0;
                    try {
                        new_stock = Integer.parseInt(stock);
                    } catch (Exception ex) {
                        error_message("Could not update stock :( Enter a valid value :|");
                        ex.printStackTrace(new PrintStream(System.out));
                    }
                    try {
                        ps = con.prepareStatement(updateRentSale);
                        ps.setInt(1, new_stock);
                        ps.setInt(2, key);
                        ps.executeUpdate();
                        refreshItems();
                        JOptionPane.showMessageDialog(frame, "Successfully updated stock!");
                    } catch (Exception ex) {
                        error_message("Could not update stock :( Try again ");
                        ex.printStackTrace(new PrintStream(System.out));
                    }
                    e.consume();
                }
            }
        });
    }

    //    private DefaultTableModel model8;
    private void createUIComponents() {
        // TODO: place custom component creation code here

        model = new DefaultTableModel(0, 0);
        for (String sa : saleInfo) {
            model.addColumn(sa);
        }
        SaleTable = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model2 = new DefaultTableModel(0, 0);
        for (String r : rentInfo) {
            model2.addColumn(r);
        }

        RentTable = new JTable(model2) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model3 = new DefaultTableModel(0, 0);
        for (String r : AvailCourses) {
            model3.addColumn(r);
        }

        AvailCourTable = new JTable(model3) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model4 = new DefaultTableModel(0, 0);
        for (String r : PastCourses) {
            model4.addColumn(r);
        }

        PastCourTable = new JTable(model4) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model5 = new DefaultTableModel(0, 0);
        for (String r : SaleTrans) {
            model5.addColumn(r);
        }

        SaleTransTable = new JTable(model5) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model6 = new DefaultTableModel(0, 0);
        for (String r : RentTrans) {
            model6.addColumn(r);
        }

        RentTransTable = new JTable(model6) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model7 = new DefaultTableModel(0, 0);
        for (String r : CourseTrans) {
            model7.addColumn(r);
        }

        CourseTransTable = new JTable(model6) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }


    private Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(sqlURL, sqlUsername, sqlPassword);
            return con;
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
        return null;
    }

    protected void refreshItems() {
        model2.setRowCount(0);
        model3.setRowCount(0);
        model.setRowCount(0);
        model4.setRowCount(0);
        model5.setRowCount(0);
        model6.setRowCount(0);
        model7.setRowCount(0);
        displayItems();
    }

    private void displayItems() {
        try {
            ps = con.prepareStatement(querySaleInfo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(3), rs.getDouble(2), rs.getInt(4)
                };
                model.addRow(toAdd);
            }
            ps = con.prepareStatement(queryRentInfo);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(3), rs.getDouble(2), rs.getInt(4)
                };
                model2.addRow(toAdd);
            }

            ps = con.prepareStatement(queryCourAvInfo);
            rs = ps.executeQuery();
            while (rs.next()) {
                ps2 = con.prepareStatement(querygroundloc);
                ps2.setInt(1, rs.getInt(6));
                ResultSet rs2 = ps2.executeQuery();
                String loc = " ";
                while (rs2.next()) {
                    loc = rs2.getString(1);
                }
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4), loc
                };
                model3.addRow(toAdd);
            }
            ps = con.prepareStatement(queryCourPaInfo);
            rs = ps.executeQuery();
            while (rs.next()) {
                ps2 = con.prepareStatement(querygroundloc);
                ps2.setInt(1, rs.getInt(6));
                ResultSet rs2 = ps2.executeQuery();
                String loc = " ";
                while (rs2.next()) {
                    loc = rs2.getString(1);
                }
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4), loc
                };
                model4.addRow(toAdd);
            }
            ps = con.prepareStatement(querySaleTrans);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4),
                        rs.getInt(5), rs.getTimestamp(6)
                };
                model5.addRow(toAdd);
            }
            ps = con.prepareStatement(queryRentTrans);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4),
                        rs.getInt(5), rs.getTimestamp(6)
                };
                model6.addRow(toAdd);
            }
            ps = con.prepareStatement(queryCourTrans);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4),
                        rs.getTimestamp(5)
                };
                model7.addRow(toAdd);
            }


        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
    }

    private static void error_message(String error) {
        JOptionPane.showMessageDialog(null, error, "Error!", JOptionPane.INFORMATION_MESSAGE);
    }


}
