

import com.sun.codemodel.internal.JOp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;
import java.sql.Timestamp;
import java.util.Date;

public class CustomerMenu {
    private JPanel customerPanel;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JTable SaleTable;
    private JTable RentTable;
    private JTable CoursesTable;
    private JButton refreshButton;
    private JButton logOutButton;
    private JTabbedPane tabbedPane3;
    private JTable SoldTransTable;
    private JTable RentTransTable;
    private JTable CoursesTransTable;
    private JFrame frame = new JFrame("Customer Menu");
    private DefaultTableModel model2;
    private DefaultTableModel model3;
    private DefaultTableModel model5;
    private DefaultTableModel model6;
    private DefaultTableModel model7;
    private DefaultTableModel model8;
    private String username;

    private final static String saleInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String rentInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String courseInfo[] = {"CourseID", "CourseName", "Fee", "Ground"};
    private final static String TransRentInfo[] = {"ItemID", "TransID", "Price", "Quantity", "TransDate", "Duration"};
    private final static String TransSaleInfo[] = {"ItemID", "TransID", "Price", "Quantity", "TransDate"};
    private final static String TransCoursesInfo[] = {"CourseID", "TransID", "Price", "TransDate"};

    private final static String queryTransSold = "Select * from TRANSACTION_SALE where CustomerID=?";
    private final static String queryTransRent = "Select * from TRANSACTION_RENT where CustomerID=?";
    private final static String queryTransCourse = "Select * from TRANSACTION_COURSES where CustomerID=?";

    private final static String sqlURL = "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2";
    private final static String sqlUsername = "h7euKF3cs2";
    private final static String sqlPassword = "2QL01X7xCG";

    private final static String querySaleInfo = "Select * from FOR_SALE";
    private final static String queryRentInfo = "Select * from FOR_RENT";
    private final static String queryCoursesInfo = "Select * from COURSES where Available=1";
    private final static String queryCustId = "Select c.CustomerID from CUSTOMER as c where c.username=?";

    private final static String updateRentStock = "Update FOR_RENT set Stock=? where ItemID=?";
    private final static String updateSaleStock = "Update FOR_SALE set Stock=? where ItemID=?";

    //    private final static String querySalePrice = "Select * from FOR_SALE where ItemID=?";
//    private final static String queryRentPrice = "Select * from FOR_RENT where ItemID=?";
    private final static String querygroundloc = "Select Location from GROUNDS where Ground=?";
//    private final static String disablefk = "SET FOREIGN_KEY_CHECKS = 0";
//    private final static String enablefk = "SET FOREIGN_KEY_CHECKS = 1";
    private final static String saleTransInsert = "insert into TRANSACTION_SALE (ItemID, CustomerID, Price, Quantity, TransDate) values (?, ?, ?, ?, ?)";
    private final static String rentTransInsert = "insert into TRANSACTION_RENT (ItemID, CustomerID, Price, Quantity, TransDate, Duration) values (?, ?, ?, ?, ?, ?)";
    private final static String courTransInsert = "insert into TRANSACTION_COURSES (CourseID, CustomerID, Price, TransDate) values (?, ?, ?, ?)";
    private final static String checkifenrolled = "select COUNT(*) from TRANSACTION_COURSES where CustomerID=? AND CourseID=?";

    //    private final static String updateRentTable = "insert into RENTS (ItemID, CustomerID, TransID, Quantity, Duration) values (?, ?, ?, ?, ?)";
//    private final static String queryRentTrans = "Select * from TRANSACTION_RENT where CustomerID=?";
    private PreparedStatement ps;
    private PreparedStatement ps2;
    private Statement st;

    private Connection con;
    private CustomerMenu menu;


    public CustomerMenu(String user, Connection c) {

        con = c;
        menu = this;
        username = user;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        displayItems();
        frame.setContentPane(customerPanel);
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

        CoursesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = CoursesTable.rowAtPoint(e.getPoint());
                    int cid = (int) CoursesTable.getValueAt(row, 0);
//                    int course = (int) CoursesTable.getValueAt(row, 0);
                    double price = (double) CoursesTable.getValueAt(row, 2);
                    int custard = -1;
                    try {
                        ps = con.prepareStatement(queryCustId);
                        ps.setString(1, username);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            custard = rs.getInt(1);
                        }
                        ps = con.prepareStatement(checkifenrolled);
                        ps.setInt(1, custard);
                        ps.setInt(2, cid);
                        rs = ps.executeQuery();
                        boolean exist = false;
                        int check = 0;
                        while (rs.next()){
                            check = rs.getInt(1);
                        }
                        if(check != 0){
                            exist = true;
                            JOptionPane.showMessageDialog(frame, "Already enrolled in this course.");
                        }
                        if(!exist) {
                            ps = con.prepareStatement(courTransInsert);
                            ps.setInt(1, cid);
                            ps.setInt(2, custard);
                            ps.setDouble(3, price);
                            Date date = new Date();
                            long time = date.getTime();
                            Timestamp ts = new Timestamp(time);
                            ps.setTimestamp(4, ts);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Thank you for enrolling!");
                            refreshItems();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    e.consume();
                }
            }
        });

        SaleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = SaleTable.rowAtPoint(e.getPoint());
                    int key = (int) SaleTable.getValueAt(row, 0);
                    int stock = (int) SaleTable.getValueAt(row, 3);
                    double price = (double) SaleTable.getValueAt(row, 2);
                    int quantity = 0;
                    while (quantity > stock || quantity == 0) {
                        String quantity_box = JOptionPane.showInputDialog(frame, "Please enter the quantity. \n The maximum amount you can buy is " + stock, null);
                        if (quantity_box.isEmpty()) {
                            break;
                        }
                        try {
                            quantity = Integer.parseInt(quantity_box);
                        } catch (Exception c) {
                            quantity = 0;
                            c.printStackTrace();
                        }
                        if (quantity > stock || quantity == 0) {
                            quantity = 0;
                            error_message("Please enter a valid quantity");
                        }
                        frame.addWindowListener(exitListener);
                    }
                    int custard = -1;
                    if (quantity != 0) {
                        try {
                            ps = con.prepareStatement(queryCustId);
                            ps.setString(1, username);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                custard = rs.getInt(1);
                            }
                            ps = con.prepareStatement(updateSaleStock);
                            ps.setInt(1, stock - quantity);
                            ps.setInt(2, key);
                            ps.executeUpdate();
                            ps = con.prepareStatement(saleTransInsert);
                            ps.setInt(1, key);
                            ps.setInt(2, custard);
                            ps.setDouble(3, quantity * price);
                            ps.setInt(4, quantity);
                            Date date = new Date();
                            long time = date.getTime();
                            Timestamp ts = new Timestamp(time);
                            ps.setTimestamp(5, ts);
                            ps.executeUpdate();
                            refreshItems();
                            JOptionPane.showMessageDialog(frame, "Successfully Purchased!");
                        } catch (Exception ex) {
                            ex.printStackTrace(new PrintStream(System.out));
                        }
                    }

                    e.consume();
                }
            }
        });

        RentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = RentTable.rowAtPoint(e.getPoint());
                    int key = (int) RentTable.getValueAt(row, 0);
                    int stock = (int) RentTable.getValueAt(row, 3);
                    double price = (double) RentTable.getValueAt(row, 2);
                    int duration = 0;
                    int quantity = 0;
                    while (duration > 250 || duration == 0 || quantity > stock || quantity == 0) {
                        String duration_box = JOptionPane.showInputDialog(frame, "How long would you like to rent this item? \n " +
                                "Please enter your duration in days. The maximum amount of time you can rent is 250 days", null);
                        if (duration_box.isEmpty()) {
                            break;
                        }
                        try {
                            duration = Integer.parseInt(duration_box);
                        } catch (Exception c) {
                            duration = 0;
                            c.printStackTrace(new PrintStream(System.out));
                        }
                        if (duration > 250) {
                            duration = 0;
                            error_message("Please enter a valid duration between 1 and 250");
                        }
                        String quantity_box = JOptionPane.showInputDialog(frame, "Please enter the quantity. \n The maximum amount you can rent is " + stock, null);
                        try {
                            quantity = Integer.parseInt(quantity_box);
                        } catch (Exception c) {
                            quantity = 0;
                            c.printStackTrace(new PrintStream(System.out));
                        }
                        if (quantity > stock) {
                            quantity = 0;
                            error_message("Please enter a valid quantity");
                        }
                    }
                    int custard = -1;
                    if (quantity > 0 && duration > 0) {
                        try {
                            ps = con.prepareStatement(queryCustId);
                            ps.setString(1, username);
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                custard = rs.getInt(1);
                            }
                            ps = con.prepareStatement(updateRentStock);
                            ps.setInt(1, stock - quantity);
                            ps.setInt(2, key);
                            ps.executeUpdate();
                            ps = con.prepareStatement(rentTransInsert);
                            ps.setInt(1, key);
                            ps.setInt(2, custard);
                            ps.setDouble(3, quantity * price);
                            ps.setInt(4, quantity);
                            Date date = new Date();
                            long time = date.getTime();
                            Timestamp ts = new Timestamp(time);
                            ps.setTimestamp(5, ts);
                            ps.setInt(6, duration);
                            ps.executeUpdate();
                            refreshItems();
                            JOptionPane.showMessageDialog(frame, "Successfully Rented!");
                        } catch (Exception ex) {
                            ex.printStackTrace(new PrintStream(System.out));
                        }
                    }
                    e.consume();
                }
            }
        });

    }

    public void createUIComponents() {
        model2 = new DefaultTableModel(0, 0);
        for (String sa : saleInfo) {
            model2.addColumn(sa);
        }
        SaleTable = new JTable(model2) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model3 = new DefaultTableModel(0, 0);
        for (String r : rentInfo) {
            model3.addColumn(r);
        }

        RentTable = new JTable(model3) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model5 = new DefaultTableModel(0, 0);
        for (String cour : courseInfo) {
            model5.addColumn(cour);
        }

        CoursesTable = new JTable(model5) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model6 = new DefaultTableModel(0, 0);
        for (String trans : TransRentInfo) {
            model6.addColumn(trans);
        }

        RentTransTable = new JTable(model6) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        model7 = new DefaultTableModel(0, 0);
        for (String trans : TransSaleInfo) {
            model7.addColumn(trans);
        }

        SoldTransTable = new JTable(model7) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model8 = new DefaultTableModel(0, 0);
        for (String trans : TransCoursesInfo) {
            model8.addColumn(trans);
        }

        CoursesTransTable = new JTable(model8) {
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
        model5.setRowCount(0);
        model6.setRowCount(0);
        model7.setRowCount(0);
        model8.setRowCount(0);
        displayItems();
    }

    private void displayItems() {
        try {
            int custard = -1;
            ps = con.prepareStatement(queryCustId);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                custard = rs.getInt(1);
            }
            ps = con.prepareStatement(querySaleInfo);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(3), rs.getDouble(2), rs.getInt(4)
                };
                model2.addRow(toAdd);
            }
            ps = con.prepareStatement(queryRentInfo);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(3), rs.getDouble(2), rs.getInt(4)
                };
                model3.addRow(toAdd);
            }

            ps = con.prepareStatement(queryCoursesInfo);
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
                        rs.getInt(1), rs.getString(2), rs.getDouble(3), loc
                };
                model5.addRow(toAdd);
            }


            ps = con.prepareStatement(queryTransSold);
            ps.setInt(1, custard);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getInt(3), rs.getDouble(4), rs.getInt(5), rs.getTimestamp(6)
                };
                model7.addRow(toAdd);
            }

            ps = con.prepareStatement(queryTransRent);
            ps.setInt(1, custard);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getInt(3), rs.getDouble(4), rs.getInt(5), rs.getTimestamp(6),
                        rs.getInt(7)
                };
                model6.addRow(toAdd);
            }

            ps = con.prepareStatement(queryTransCourse);
            ps.setInt(1, custard);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getInt(3), rs.getDouble(4), rs.getTimestamp(5)
                };
                model8.addRow(toAdd);
            }
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
    }

    private static void error_message(String error) {
        JOptionPane.showMessageDialog(null, error, "Error!", JOptionPane.INFORMATION_MESSAGE);
    }


}
