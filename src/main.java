import java.sql.*;


public class main {

    public static void main(String[] args) {

        try{
/*
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://remotemysql.com:3306/h7euKF3cs2","h7euKF3cs2","2QL01X7xCG");

            //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from CUSTOMER");

            while(rs.next())
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
*/
            managerMenu menu = new managerMenu();
            //con.close();
        }catch(Exception e){
            System.out.println("EXCEPTION");
            System.out.println(e);}
        }


}

