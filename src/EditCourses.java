import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditCourses extends JDialog {
    private JPanel coursePane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textName;
    private JButton btnInst;
    private JTextField textFee;
    private JButton btnDelete;
    private JComboBox availableBox;
    private JLabel labelInst;

    private JFrame frame = new JFrame("Course Menu");

    private int instructorID[] = {-1};

    private final static String queryAdd = "Insert into COURSES (CourseName, Fee, Available, InstructorID) values (?, ?, ?, ?)";
    private final static String queryAdd2 = "Insert into COURSES (CourseName, Fee, Available) values (?, ?, ?)";

    private final static String queryEdit = "Update COURSES set CourseName = ?, Fee = ?, Available = ?, InstructorID = ?";
    private final static String queryEdit2 = "Update COURSES set CourseName = ?, Fee = ?, Available = ?, InstructorID = NULL";

    private final static String queryDelete = "Delete from COURSES where CourseID = ?";

    private final static String queryFKC0 = "SET FOREIGN_KEY_CHECKS = 0";
    private final static String queryFKC1 = "SET FOREIGN_KEY_CHECKS = 1";


    private final static String queryLoad = "SELECT * from COURSES where CourseID = ?";

    private final static String queryInstructor = "Select EmpName from EMPLOYEE where EmpID = ?";


    private Connection con;
    private PreparedStatement ps;

    public EditCourses(Connection con, ManagerMenu menu) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        btnDelete.setVisible(false);
        frame.setContentPane(coursePane);
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

                String name = textName.getText();
                double fee = Double.parseDouble(textFee.getText());
                boolean available = false;
                if(((String) availableBox.getSelectedItem()).equals("Yes")) available = true;
                int option = JOptionPane.showConfirmDialog(null, "Add new course?");
                switch (option) {
                    case 0:
                        try {
                            if (instructorID[0] > -1) {
                                ps = con.prepareStatement(queryAdd);
                            } else {
                                ps = con.prepareStatement(queryAdd2);
                            }

                            ps.setString(1, name);
                            ps.setDouble(2, fee);
                            ps.setBoolean(3, available);

                            if (instructorID[0] > -1) {
                                ps.setInt(4, instructorID[0]);
                            }

                            PreparedStatement fkc = con.prepareStatement(queryFKC0);
                            fkc.executeUpdate();
                            ps.executeUpdate();

                            fkc = con.prepareStatement(queryFKC1);
                            fkc.executeUpdate();
                            menu.refreshCourses();
                            frame.dispose();

                        } catch (Exception e2) {
                            JOptionPane.showMessageDialog(null, "Adding new course has failed.");
                            e2.printStackTrace(new java.io.PrintStream(System.out));
                        }
                        break;
                    case 1:
                        frame.dispose();
                        break;
                }
            }
        });

        btnInst.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                ListInstructor list = new ListInstructor(con, labelInst, instructorID);
            }
        });
    }

    public EditCourses(Connection con, int key, ManagerMenu menu) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
        try {
            ps = con.prepareStatement(queryLoad);
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();
            ps = con.prepareStatement(queryInstructor);
            while (rs.next()) {
                textName.setText(rs.getString(2));
                textFee.setText(rs.getString(3));
                if (rs.getBoolean(5)) {
                    availableBox.setSelectedIndex(0);
                } else {
                    availableBox.setSelectedIndex(1);
                }
                ps.setInt(1, rs.getInt(4));
                rs = ps.executeQuery();
                while (rs.next()) labelInst.setText(rs.getString(1));
            }

        } catch (Exception e3) {
            e3.printStackTrace(new java.io.PrintStream(System.out));
        }



        frame.setContentPane(coursePane);
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

                String name = textName.getText();
                double fee = Double.parseDouble(textFee.getText());
                boolean available = false;
                if(((String) availableBox.getSelectedItem()).equals("Yes")) available = true;
                int option = JOptionPane.showConfirmDialog(null, "Proceed with changes?");
                switch (option) {
                    case 0:
                        try {
                            if (instructorID[0] > -1) {
                                ps = con.prepareStatement(queryEdit);
                            } else {
                                ps = con.prepareStatement(queryEdit2);
                            }

                            ps.setString(1, name);
                            ps.setDouble(2, fee);
                            ps.setBoolean(3, available);

                            if (instructorID[0] > -1) {
                                ps.setInt(4, instructorID[0]);
                            }

                            PreparedStatement fkc = con.prepareStatement(queryFKC0);
                            fkc.executeUpdate();
                            ps.executeUpdate();

                            fkc = con.prepareStatement(queryFKC1);
                            fkc.executeUpdate();
                            menu.refreshCourses();
                            frame.dispose();

                        } catch (Exception e2) {
                            JOptionPane.showMessageDialog(null, "Editing course has failed.");
                            e2.printStackTrace(new java.io.PrintStream(System.out));
                        }
                        break;
                    case 1:
                        frame.dispose();
                        break;
                }
            }
        });

        btnDelete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int option = JOptionPane.showConfirmDialog(null, "Delete course?");
                switch (option) {
                    case 0:
                        try {
                            ps = con.prepareStatement(queryDelete);
                            ps.setInt(1, key);
                            ps.executeUpdate();
                        } catch (Exception e5) {
                            JOptionPane.showMessageDialog(null, "Deleting course has failed.");
                            e5.printStackTrace(new java.io.PrintStream(System.out));
                        }
                        menu.refreshCourses();
                        frame.dispose();
                        break;
                }
            }
        });

        btnInst.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                ListInstructor list = new ListInstructor(con, labelInst, instructorID);
            }
        });
    }


}
