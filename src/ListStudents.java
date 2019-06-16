import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.PrintStream;

public class ListStudents extends JDialog{
    private JPanel studentPanel;
    private JTable Studentable1;
    private JButton buttonClose;
    private JFrame frame = new JFrame(" List of students");
    private DefaultTableModel model;

    private Connection con;
    private PreparedStatement ps;
    private PreparedStatement ps2;
    private int sensei_id;
    private int course_id;

    private final static String InstStudent[] = {"CustomerID", "Customer Name", "Phone"};
    private final static String queryStudents = "Select CustomerID, CustName, CustPhone from CUSTOMER where CustomerID=?";
    private final static String queryTransAvailCour = "Select CustomerID from TRANSACTION_COURSES where CourseID=?";


    public ListStudents(Connection c, int instid, int course){
        con = c;
        sensei_id = instid;
        course_id = course;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        frame.setSize(width / 2, height / 2);
        displayStudents();
        frame.setContentPane(studentPanel);
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
        buttonClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                frame.dispose();
            }
        });

    }

    private void displayStudents(){
        try {
            ps = con.prepareStatement(queryTransAvailCour);
            ps.setInt(1, course_id);
            ResultSet results = ps.executeQuery();
            while(results.next()){
                ps2 = con.prepareStatement(queryStudents);
                ps2.setInt(1, results.getInt(1));
                ResultSet customer = ps2.executeQuery();
                while(customer.next()) {
                    Object toAdd[] = {
                            customer.getInt(1), customer.getString(2), customer.getString(3)
                    };
                    model.addRow(toAdd);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace(new PrintStream(System.out));
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        model = new DefaultTableModel(0, 0);
        for (String s : InstStudent) {
            model.addColumn(s);
        }
        Studentable1 = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
