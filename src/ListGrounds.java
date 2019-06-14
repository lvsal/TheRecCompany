import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.sql.*;

public class ListGrounds {

    private JPanel groundPane;
    private JTable tableGround;
    private JButton buttonCancel;
    private JFrame frame = new JFrame("GroundList");
    private DefaultTableModel model;

    private final static String queryGrounds = "Select * from GROUNDS";
    private final static String groundInfo[] = {"Ground No.", "Location"};

    private Connection con;
    private PreparedStatement ps;

    public ListGrounds(Connection con, JLabel label, int groundID[]) {
        this.con = con;
        $$$setupUI$$$();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);

        displayGrounds();

        frame.setContentPane(groundPane);
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

        tableGround.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() == 2) {
                    int row = tableGround.getSelectedRow();
                    int id = (int) tableGround.getValueAt(row, 0);
                    groundID[0] = id;
                    label.setText((String) tableGround.getValueAt(row, 1));
                    e.consume();
                    frame.dispose();
                }
            }
        });
    }

    private void displayGrounds() {
        try {
            ps = con.prepareStatement(queryGrounds);
            ResultSet rs = ps.executeQuery();
            Object addNone[] = {-1, "TBD"};
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
        for (String g : groundInfo) {
            model.addColumn(g);
        }

        tableGround = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

}
