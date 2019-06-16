import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;

public class ListInstructor extends JDialog {
    private JPanel instructorPane;
    private JButton buttonCancel;
    private JTable tableInst;
    private JFrame frame = new JFrame("Instructor List");
    private DefaultTableModel model;

    private Connection con;
    private PreparedStatement ps;

    private final static String instructorInfo[] = {"Manager ID", "Name"};

    private final static String queryInst = "Select e.EmpID, e.EmpName from EMPLOYEE as e, USERINFO as u where u.username = e.username AND u.Type = 'Instructor'";

    public ListInstructor(Connection con, JLabel label, int instructorID[]) {
        this.con = con;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        displayCourses();

        frame.setContentPane(instructorPane);
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

        tableInst.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = tableInst.getSelectedRow();
                    int id = (int) tableInst.getValueAt(row, 0);
                    instructorID[0] = id;
                    label.setText((String) tableInst.getValueAt(row, 1));
                    e.consume();
                    frame.dispose();
                }
            }
        });
    }

    private void displayCourses() {
        try {
            ps = con.prepareStatement(queryInst);
            ResultSet rs = ps.executeQuery();
            Object addNone[] = {-1, "None"};
            model.addRow(addNone);
            while (rs.next()) {
                Object toAdd[] = {
                        rs.getInt(1), rs.getString(2)
                };
                model.addRow(toAdd);
            }
        } catch (Exception e) {
            e.printStackTrace(new PrintStream(System.out));
        }
    }


    private void createUIComponents() {

        model = new DefaultTableModel(0, 0);
        for (String i : instructorInfo) {
            model.addColumn(i);
        }
        tableInst = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }


}
