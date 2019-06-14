import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;

public class EditEmployee extends JDialog {
    private JPanel empPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JFormattedTextField textName;
    private JFormattedTextField textPhone;
    private JFormattedTextField textStreet;
    private JPasswordField textPassword;
    private JPasswordField textConfirm;
    private JFormattedTextField textCity;
    private JFormattedTextField textProvince;
    private JFormattedTextField textUsername;
    private JComboBox typeBox;
    private JButton btnDelete;
    private JButton btnMngr;
    private JLabel labelMngr;
    private JPanel panelUsername;
    private JPanel panelPassword;
    private JPanel panelConfirm;
    private JFrame frame = new JFrame("Employee Account");

    private int managerID[] = {-1};


    // SQL Queries
    private final static String queryEmpCreate = "Insert into EMPLOYEE (EmpName, EmpPhone, EmpProv, EmpStreet, EmpCity, username, ManagerID)" +
            "values (?, ?, ?, ?, ?, ?, ?)";
    private final static String queryEmpCreate2 = "Insert into EMPLOYEE (EmpName, EmpPhone, EmpProv, EmpStreet, EmpCity, username)" +
            "values (?, ?, ?, ?, ?, ?)";
    private final static String queryEdit = "Update EMPLOYEE set EmpName = ?, EmpPhone = ?, EmpProv = ?, EmpStreet = ?, EmpCity = ?, ManagerID = ? " +
            "where EmpID = ?";

    private final static String queryEdit2 = "Update EMPLOYEE set EmpName = ?, EmpPhone = ?, EmpProv = ?, EmpStreet = ?, EmpCity = ?, ManagerID = NULL" +
            " where EmpID = ?";
    private final static String queryEditInfo = "Select EmpName, EmpPhone, EmpStreet, EmpCity, EmpProv, ManagerID from EMPLOYEE where EmpID = ?";
    private final static String queryManager = "Select EmpName from EMPLOYEE where EmpID = ?";
    private final static String queryFKC0 = "SET FOREIGN_KEY_CHECKS = 0";
    private final static String queryFKC1 = "SET FOREIGN_KEY_CHECKS = 1";
    private final static String queryUserCreate = "Insert into USERINFO (username, password, type) values (?, ?, ?)";
    private final static String queryUserExists = "Select username from USERINFO where username = ?";
    private final static String queryDelEmp = "Delete from EMPLOYEE where EmpID = ?";
    private final static String queryDelUser = "Delete from USERINFO where username = ?";
    private final static String queryType = "Select Type from USERINFO where username = ?";
    private final static String queryUpdateType = "Update USERINFO set Type = ? Where username = ?";

    private final static String queryUpdateCourses = "Update COURSES set InstructorID = NULL where InstructorID = ?";


    private Connection con;
    private PreparedStatement ps;

    public EditEmployee(Connection con, ManagerMenu menu) {
        this.con = con;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        btnDelete.setVisible(false);

        frame.setContentPane(empPane);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure that you want to close this window?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    frame.dispose();
                }
            }
        };

        frame.addWindowListener(exitListener);
        buttonOK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String name = textName.getText();
                String phone = textPhone.getText();
                String street = textStreet.getText();
                String city = textCity.getText();
                String province = textProvince.getText();
                String username = textUsername.getText().toLowerCase();
                String password = String.valueOf(textPassword.getPassword());
                String confirm = String.valueOf(textConfirm.getPassword());
                String type = typeBox.getSelectedItem().toString();
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(null, "Password must match.");
                } else {
                    int option = JOptionPane.showConfirmDialog(null, "Create new employee?");
                    switch (option) {
                        case 0:
                            try {
                                String userCheck = null;
                                ps = con.prepareStatement(queryUserExists);
                                ps.setString(1, username);
                                ResultSet user = ps.executeQuery();
                                while (user.next()) userCheck = user.getString(1);
                                if (userCheck == null) {
                                    if (managerID[0] > -1) {
                                        ps = con.prepareStatement(queryEmpCreate);
                                    } else {
                                        ps = con.prepareStatement(queryEmpCreate2);
                                    }

                                    PreparedStatement ps2 = con.prepareStatement(queryUserCreate);


                                    PreparedStatement ps3 = con.prepareStatement(queryFKC0);

                                    ps.setString(1, name);
                                    ps.setString(2, phone);
                                    ps.setString(3, province);
                                    ps.setString(4, street);
                                    ps.setString(5, city);
                                    ps.setString(6, username);
                                    if (managerID[0] > -1) {
                                        ps.setInt(7, managerID[0]);
                                    }


                                    ps2.setString(1, username);
                                    ps2.setString(2, password);
                                    ps2.setString(3, type);

                                    ps3.executeUpdate();
                                    ps2.executeUpdate();
                                    ps.executeUpdate();

                                    ps3 = con.prepareStatement(queryFKC1);
                                    ps3.executeUpdate();


                                    menu.refreshEmployees();
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Username already exists.");
                                }


                            } catch (Exception e3) {
                                e3.printStackTrace(new PrintStream(System.out));
                            }
                            break;
                    }

                }

            }
        });

        buttonCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                frame.dispose();
            }
        });
        btnMngr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                ListManagers list = new ListManagers(con, labelMngr, managerID);
            }
        });
    }


    public EditEmployee(Connection con, int key, ManagerMenu menu, String username) {
        this.con = con;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
        String type = "";
        try {
            ps = con.prepareStatement(queryType);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) type = rs.getString(1);
            if (type.equals("Employee")) {
                typeBox.setSelectedIndex(0);
            } else if (type.equals("Manager")) {
                typeBox.setSelectedIndex(1);
            } else {
                typeBox.setSelectedIndex(2);
            }
            ps = con.prepareStatement(queryEditInfo);
            ps.setInt(1, key);
            rs = ps.executeQuery();
            int manID = -1;
            while (rs.next()) {
                textName.setText(rs.getString(1));
                textPhone.setText(rs.getString(2));
                textStreet.setText(rs.getString(3));
                textCity.setText(rs.getString(4));
                textProvince.setText(rs.getString(5));
                manID = rs.getInt(6);
            }
            ps = con.prepareStatement(queryManager);
            ps.setInt(1, manID);
            rs = ps.executeQuery();
            while (rs.next()) labelMngr.setText(rs.getString(1));
        } catch (Exception e5) {
            e5.printStackTrace(new PrintStream(System.out));
        }

        if (type.equals("Manager")) btnDelete.setVisible(false);
        panelConfirm.setVisible(false);
        panelPassword.setVisible(false);
        panelUsername.setVisible(false);


        frame.setContentPane(empPane);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure that you want to close the window?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0) {
                    frame.dispose();
                }
            }
        };

        frame.addWindowListener(exitListener);
        buttonOK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String name = textName.getText();
                String phone = textPhone.getText();
                String street = textStreet.getText();
                String city = textCity.getText();
                String province = textProvince.getText();
                String type = (String) typeBox.getSelectedItem();
                int option = JOptionPane.showConfirmDialog(null, "Proceed with changes?");
                switch (option) {
                    case 0:
                        try {
                            if (managerID[0] > -1) {
                                ps = con.prepareStatement(queryEdit);
                            } else {
                                ps = con.prepareStatement(queryEdit2);
                            }


                            PreparedStatement ps3 = con.prepareStatement(queryFKC0);

                            ps.setString(1, name);
                            ps.setString(2, phone);
                            ps.setString(3, province);
                            ps.setString(4, street);
                            ps.setString(5, city);
                            if (managerID[0] > -1) {
                                ps.setInt(6, managerID[0]);
                                ps.setInt(7, key);
                            } else {
                                ps.setInt(6, key);
                            }

                            PreparedStatement ps2 = con.prepareStatement(queryUpdateType);
                            ps2.setString(1, type);
                            ps2.setString(2, username);
                            ps2.executeUpdate();

                            ps3.executeUpdate();
                            ps.executeUpdate();

                            ps3 = con.prepareStatement(queryFKC1);
                            ps3.executeUpdate();

                            if (!type.equals("Instructor")) {
                                removeInstructor(key, menu);
                            }

                            menu.refreshEmployees();
                            frame.dispose();
                        } catch (Exception e3) {
                            JOptionPane.showMessageDialog(null, "Editing employee has failed.");
                            e3.printStackTrace(new PrintStream(System.out));
                        }
                        break;


                }

            }
        });
        btnDelete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the employee?");
                switch (option) {
                    case 0:
                        try {
                            PreparedStatement ps2 = con.prepareStatement(queryDelEmp);
                            PreparedStatement ps3 = con.prepareStatement(queryDelUser);
                            ps2.setInt(1, key);
                            ps3.setString(1, username);
                            removeInstructor(key, menu);
                            ps2.executeUpdate();
                            ps3.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Employee has been successfully deleted.");
                            menu.refreshEmployees();
                            frame.dispose();
                        } catch (Exception e4) {
                            JOptionPane.showMessageDialog(null, "Deleting employee failed.");
                            e4.printStackTrace(new PrintStream(System.out));
                        }
                }
            }
        });
        buttonCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                frame.dispose();
            }
        });
        btnMngr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                ListManagers list = new ListManagers(con, labelMngr, managerID);
            }
        });
    }

    private void removeInstructor(int key, ManagerMenu menu) throws Exception {

        PreparedStatement ps = con.prepareStatement(queryUpdateCourses);
        ps.setInt(1, key);
        ps.executeUpdate();
        menu.refreshCourses();
    }

}
