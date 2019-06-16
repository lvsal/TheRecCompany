import java.io.PrintStream;
import java.sql.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditCustInfo extends JDialog {
    private JPanel editPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JFormattedTextField textName;
    private JFormattedTextField textPhone;
    private JFormattedTextField textAddress;
    private JFormattedTextField textCity;
    private JFormattedTextField textProvince;
    private JFrame frame = new JFrame("Edit Customer Info");

    final static String sqlCustInfo = "select c.* from CUSTOMER as c where c.CustomerID = ?";

    final static String sqlCustUpdate = "update CUSTOMER set CustName = ?, CustPhone = ?, CustProv = ?, CustStreet = ?, CustCity = ?" +
            " WHERE CustomerID = ?";

    private Connection con;

    private PreparedStatement ps;

    public EditCustInfo(Connection con, int key, ManagerMenu menu) {
        this.con = con;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);


        frame.setContentPane(editPane);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        try {
            ps = con.prepareStatement(sqlCustInfo);
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textName.setText(rs.getString(2));
                textPhone.setText(rs.getString(3));
                textProvince.setText(rs.getString(4));
                textAddress.setText(rs.getString(5));
                textCity.setText(rs.getString(6));
            }
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }

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
                int option = JOptionPane.showConfirmDialog(null, "Proceed with changes?");
                switch (option) {
                    case 0:
                        String name = textName.getText();
                        String phone = textPhone.getText();
                        String street = textAddress.getText();
                        String city = textCity.getText();
                        String prov = textProvince.getText();
                        try {
                            ps = con.prepareStatement(sqlCustUpdate);
                            ps.setString(1, name);
                            ps.setString(2, phone);
                            ps.setString(3, prov);
                            ps.setString(4, street);
                            ps.setString(5, city);
                            ps.setInt(6, key);
                            ps.executeUpdate();
                        } catch (Exception e2) {
                            e2.printStackTrace(new PrintStream(System.out));
                        }
                        menu.refreshCustomers();
                        frame.dispose();
                        break;
                    case 1:
                        frame.dispose();
                        break;
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
    }

}
