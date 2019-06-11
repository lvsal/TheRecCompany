import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditItem {
    private JFormattedTextField textItemName;
    private JFormattedTextField textPrice;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JFormattedTextField textStock;
    private JButton buttonDel;
    private JPanel itemPane;
    private JPanel typePanel;
    private JComboBox typeBox;
    private JFrame frame = new JFrame("Item Information");


    private final static String queryLoadRent = "Select * from FOR_RENT where ItemID = ?";
    private final static String queryLoadSale = "Select * from FOR_SALE where ItemID = ?";

    private final static String rentUpdate = "Update FOR_RENT set ItemName = ?, Stock = ?, Price = ? Where ItemID = ?";
    private final static String saleUpdate = "Update FOR_SALE set ItemName = ?, Stock = ?, Price = ? Where ItemID = ?";

    private final static String rentDelete = "Delete from FOR_RENT where ItemID = ?";
    private final static String saleDelete = "Delete from FOR_SALE where ItemID = ?";

    private final static String newRent = "Insert into FOR_RENT (ItemName, Stock, Price) values (?, ?, ?)";
    private final static String newSale = "Insert into FOR_SALE (ItemName, Stock, Price) values (?, ?, ?)";


    private Connection con;

    private PreparedStatement ps = null;



    public EditItem(Connection con, int key, ManagerMenu menu, String type) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        if (type.equals("rent") || type.equals("sale")) {
            typePanel.setVisible(false);
        } else {
            buttonDel.setVisible(false);
        }
        frame.setSize(width / 2, height / 2);


        frame.setContentPane(itemPane);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        try {
            if (type.equals("rent")) {
                ps = con.prepareStatement(queryLoadRent);
            } else if (type.equals("sale")) {
                ps = con.prepareStatement(queryLoadSale);
            }
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textItemName.setText(rs.getString(3));
                textPrice.setText(rs.getString(2));
                textStock.setText(rs.getString(4));
            }
        } catch (Exception e) {
            e.printStackTrace(new java.io.PrintStream(System.out));
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
                        String name = textItemName.getText();
                        double price = Double.parseDouble(textPrice.getText());
                        int stock = Integer.parseInt(textStock.getText());
                        try {
                            if (type.equals("rent")) {
                                ps = con.prepareStatement(rentUpdate);
                                ps.setString(1 , name);
                                ps.setInt(2, stock);
                                ps.setDouble(3, price);
                                ps.setInt(4, key);
                            } else {
                                ps = con.prepareStatement(saleUpdate);
                                ps.setString(1, name);
                                ps.setInt(2, stock);
                                ps.setDouble(3, price);
                                ps.setInt(4, key);
                            }
                            ps.executeUpdate();
                        } catch (Exception e2) {
                            e2.printStackTrace(new java.io.PrintStream(System.out));
                        }
                        menu.refreshItems();
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
        buttonDel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the item?");
                switch (option) {
                    case 0:
                        try {
                            if (type.equals("rent")) {
                                ps = con.prepareStatement(rentDelete);
                                ps.setInt(1, key);

                            } else {
                                ps = con.prepareStatement(saleDelete);
                                ps.setInt(1, key);
                            }
                            ps.executeUpdate();
                            menu.refreshItems();
                            frame.dispose();
                        } catch (Exception e3) {
                            e3.printStackTrace(new java.io.PrintStream(System.out));
                        }
                        break;
                }
            }
        });
    }

    public EditItem(Connection con, ManagerMenu menu) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        buttonDel.setVisible(false);

        frame.setSize(width / 2, height / 2);


        frame.setContentPane(itemPane);
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
                int option = JOptionPane.showConfirmDialog(null, "Proceed with changes?");
                switch (option) {
                    case 0:
                        String name = textItemName.getText();
                        double price = Double.parseDouble(textPrice.getText());
                        int stock = Integer.parseInt(textStock.getText());
                        String type = typeBox.getSelectedItem().toString();
                        try {
                            if (type.equals("Rent")) {
                                ps = con.prepareStatement(newRent);
                            } else if (type.equals("Sale")) {
                                ps = con.prepareStatement(newSale);
                            }

                            ps.setString(1 , name);
                            ps.setInt(2, stock);
                            ps.setDouble(3, price);
                            ps.executeUpdate();
                        } catch (Exception e2) {
                            e2.printStackTrace(new java.io.PrintStream(System.out));
                        }
                        menu.refreshItems();
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
