import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.*;

public class EditGrounds {
    private JFormattedTextField textLocation;
    private JButton buttonCancel;
    private JButton buttonOK;
    private JButton buttonDel;
    private JPanel grndPane;
    private JFrame frame = new JFrame("Grounds");

    private final static String queryAdd = "Insert into GROUNDS (Location) values (?)";

    private final static String queryUpdate = "Update GROUNDS set Location = ? where Ground = ?";

    private final static String queryDelete = "Delete from GROUNDS where Ground = ?";

    private final static String queryCheck = "SELECT CourseID from COURSES where Ground = ? AND Available = true";

    private Connection con;
    private PreparedStatement ps;

    public EditGrounds(Connection con, ManagerMenu menu) {
        this.con = con;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        buttonDel.setVisible(false);

        frame.setContentPane(grndPane);
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

        buttonCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                frame.dispose();
            }
        });
        buttonOK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String location = textLocation.getText();
                try {
                    ps = con.prepareStatement(queryAdd);
                    ps.setString(1, location);
                    ps.executeUpdate();
                    menu.refreshGrounds();
                    frame.dispose();
                } catch (Exception e6) {
                    JOptionPane.showMessageDialog(null, "Adding new grounds failed.");
                    e6.printStackTrace(new PrintStream(System.out));
                }
            }
        });

    }

    public EditGrounds(Connection con, int key, ManagerMenu menu) {
        this.con = con;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);


        frame.setContentPane(grndPane);
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

        buttonCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                frame.dispose();
            }
        });
        buttonOK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String location = textLocation.getText();
                try {
                    ps = con.prepareStatement(queryUpdate);
                    ps.setString(1, location);
                    ps.setInt(2, key);
                    ps.executeUpdate();
                    menu.refreshGrounds();
                    frame.dispose();
                } catch (Exception e6) {
                    JOptionPane.showMessageDialog(null, "Editing location failed.");
                    e6.printStackTrace(new PrintStream(System.out));
                }
            }
        });
        buttonDel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                try {
                    if (courseUsed(key, con)) {
                        throw new Exception();
                    }
                    ps = con.prepareStatement(queryDelete);
                    ps.setInt(1, key);
                    ps.executeUpdate();
                    menu.refreshGrounds();
                    frame.dispose();
                } catch (Exception e6) {
                    JOptionPane.showMessageDialog(null, "Deleting ground failed. \n" +
                            "Grounds may be currently used for a course.");
                    e6.printStackTrace(new PrintStream(System.out));
                }
            }
        });
    }

    private boolean courseUsed(int key, Connection con) throws Exception {
        ps = con.prepareStatement(queryCheck);
        ps.setInt(1, key);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            if (rs.getInt(1) > 0) return true;
        return false;
    }

}
