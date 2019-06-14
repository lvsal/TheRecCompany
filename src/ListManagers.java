import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;

public class ListManagers extends JDialog {
    private JPanel listMngrPane;
    private JButton buttonCancel;
    private JTable mngrTable;
    private JFrame frame = new JFrame("Manager List");
    private DefaultTableModel model;

    private final static String mngrInfo[] = {"Manager ID", "Name"};

    private final static String queryMngr = "Select e.EmpID, e.EmpName FROM EMPLOYEE AS e, USERINFO as u WHERE u.Type = 'Manager' AND e.username = u.username";

    private Connection con;
    private PreparedStatement ps;

    public ListManagers(Connection con, JLabel label, int managerID[]) {
        this.con = con;
        $$$setupUI$$$();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        displayManagers();

        frame.setContentPane(listMngrPane);
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
        mngrTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = mngrTable.getSelectedRow();
                    int id = (int) mngrTable.getValueAt(row, 0);
                    managerID[0] = id;
                    label.setText((String) mngrTable.getValueAt(row, 1));
                    e.consume();
                    frame.dispose();
                }
            }
        });
    }


    private void createUIComponents() {
        model = new DefaultTableModel(0, 0);
        for (String m : mngrInfo) {
            model.addColumn(m);
        }
        mngrTable = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void displayManagers() {
        try {
            ps = con.prepareStatement(queryMngr);
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

}
