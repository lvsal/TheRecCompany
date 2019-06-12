import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.LinkedList;


public class ManagerMenu {
    private JTabbedPane tabbedMenus;
    private JPanel managerPanel;
    private JPanel itemPanel;
    private JTable customerTable;
    private JButton btnRefresh;
    private JButton btnLogout;


    private JTabbedPane tabbedPane1;
    private JTable saleTable;
    private JTable rentTable;
    private JButton buttonAdd;
    private JTable empTable;
    private JButton btnAddEmp;
    private JTable courseTable;
    private JButton btnCourse;
    private JScrollPane scrollPanel;
    private JTable appointmentTable;
    private JButton signOutButton;
    private JLabel nameLabel;
    //private JButton requestButton;
    private JFrame frame = new JFrame("Manager Menu");
    private DefaultTableModel model;
    private DefaultTableModel model2;
    private DefaultTableModel model3;
    private DefaultTableModel model4;
    private DefaultTableModel model5;

    private final static String customerInfo[] = {"Customer ID", "Name", "Phone", "Address", "City", "Province"};

    private final static String saleInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String rentInfo[] = {"ItemID", "Name", "Price", "Stock"};
    private final static String empInfo[] = {"Employee ID", "Name", "Phone", "Address", "City", "Province", "Manager", "Username", "Type"};
    private final static String courseInfo[] = {"Course ID", "Course Name", "Fee", "Instructor Name", "Available"};


    private final static String sqlURL = "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2?useSSL=false";
    private final static String sqlUsername = "h7euKF3cs2";
    private final static String sqlPassword = "2QL01X7xCG";

    private final static String queryCust = "Select * from CUSTOMER";

    private final static String querySaleInfo = "Select * from FOR_SALE";
    private final static String queryRentInfo = "Select * from FOR_RENT";

    private final static String queryEmpInfo = "Select * from EMPLOYEE";
    private final static String queryType = "Select Type from USERINFO where username = ?";
    private final static String queryManager = "Select EmpName from EMPLOYEE where EmpID = ?";



    private PreparedStatement ps;


    private Connection con;
    private ManagerMenu menu;

    public ManagerMenu(Connection connection) {

        menu = this;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

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
                refreshEmployees();
                refreshCourses();
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
                    Login login = new Login(con);
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
        btnAddEmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                EditEmployee newEmp = new EditEmployee(con, menu);
            }
        });
        empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = empTable.rowAtPoint(e.getPoint());
                    int key = (int) empTable.getValueAt(row, 0);
                    if (!((String) empTable.getValueAt(row, 8)).equals("Manager")) {
                        String username = (String) empTable.getValueAt(row, 7);
                        EditEmployee emp = new EditEmployee(con, key, menu, username);
                    }
                    e.consume();
                }
            }
        });
        btnCourse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                EditCourses course = new EditCourses(con, menu);
            }
        });

        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = courseTable.rowAtPoint(e.getPoint());
                    int key = (int) courseTable.getValueAt(row, 0);
                    EditCourses courses2 = new EditCourses(con, key, menu);
                    e.consume();
                }
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

        model4 = new DefaultTableModel(0,0);
        for (String e : empInfo) {
            model4.addColumn(e);
        }

        empTable = new JTable(model4) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        model5 = new DefaultTableModel(0, 0);
        for (String c : courseInfo) {
            model5.addColumn(c);
        }

        courseTable = new JTable(model5) {
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

    private void displayEmployee() {
        String type = "";
        String manager = "";
        PreparedStatement ps2;
        PreparedStatement ps3;
        ResultSet rs2;
        ResultSet rs3;
        try {
            ps = con.prepareStatement(queryEmpInfo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ps2 = con.prepareStatement(queryType);
                ps2.setString(1, rs.getString(8));
                rs2 = ps2.executeQuery();
                while(rs2.next()) type = rs2.getString(1);

                ps3 = con.prepareStatement(queryManager);
                ps3.setInt(1, rs.getInt(7));
                rs3 = ps3.executeQuery();
                while(rs3.next()) manager = rs3.getString(1);

                Object toAdd[] = {
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(5), rs.getString(6), rs.getString(4), manager, rs.getString(8),
                        type
                };
                model4.addRow(toAdd);
            }
        } catch (Exception e) {
            e.printStackTrace(new java.io.PrintStream(System.out));
        }
    }

    protected void refreshEmployees() {
        model4.setRowCount(0);
        displayEmployee();
    }

    private final static String queryCourses = "Select * from COURSES";
    private final static String queryInstructor = "Select EmpName FROM EMPLOYEE where EmpID = ?";
    private void displayCourses() {
        ResultSet rs;
        ResultSet rs2;
        String instructor = "";
        String available = "";
        try {
            ps = con.prepareStatement(queryCourses);
            rs = ps.executeQuery();
            while (rs.next()) {
                ps = con.prepareStatement(queryInstructor);
                ps.setInt(1, rs.getInt(4));
                rs2 = ps.executeQuery();
                while (rs2.next()) instructor = rs2.getString(1);
                if (rs.getBoolean(5)) {
                    available = "Yes";
                } else {
                    available = "No";
                }
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(2), rs.getDouble(3), instructor, available
                };
                model5.addRow(toAdd);
            }

        } catch (Exception e) {
            e.printStackTrace(new java.io.PrintStream(System.out));
        }
    }

    protected void refreshCourses() {
        model5.setRowCount(0);
        displayCourses();
    }


}
