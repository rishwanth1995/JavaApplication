/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rishw
 */
import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Admin {
    
    
    
    private static Connection conn;
    private static PreparedStatement stmt;
    private static Statement createStat;
    
    public static void adminLogIn() throws SQLException{
        int t = 3;
        Scanner scanner = null;
        while (t > 0){
        
        scanner = new Scanner(System.in);
        System.out.println("Enter the email address");
        String email = scanner.next();
        String password = null;
        String passwordQuery = null;
        String loginQuery = "SELECT Password from login where Email = ?";
        String studentQuery = "SELECT Student_ID from login where Email = ?";
        String adminQuery =  "SELECT Admin_ID from admin where Admin_ID = ?";
        //String studentAndAdminCheck = "SELECT a.Admin_ID from Student s inner join admin a on s.Student_ID = a.admin_ID where s.Student_ID = ?";
        int student_id = 0;
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(loginQuery);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            stmt = conn.prepareStatement(studentQuery);
            stmt.setString(1, email);
            ResultSet rs1 = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Enter the password");
                password = scanner.next();
                passwordQuery = rs.getString("Password");
                
            }else{
                System.out.println("Email_ID not found" );
            }
            
            if(rs1.next()){
                //System.out.println("hello");
                student_id = rs1.getInt("Student_ID");
            }
        }catch(SQLException e){
            System.out.println(e);
        }finally{
            conn.close();
        }
        
        try{
            boolean status = Password.check(password, passwordQuery);
            
            if(status){
                int admin_ID = 0; 
                try{
                    conn = ConnectionManager.getConnection();
                    stmt = conn.prepareStatement(adminQuery);
                    stmt.setInt(1, student_id);
                    ResultSet rs = stmt.executeQuery();
                    //stmt = conn.prepareStatement(studentAndAdminCheck);
                    //stmt.setInt(1, student_id);
                    //ResultSet rs1 = stmt.executeQuery();
                    if(rs.next()){
                        admin_ID= rs.getInt("Admin_ID");
                        System.out.println("You have successfully logged on as admin");
                        adminPage(admin_ID);
                        break;
                    }else{
                        System.out.println("You are not a admin, you are redirected to a login page");
                        LogIn.menu();
                        break;
                    }
                }catch(SQLException e){
                    System.out.println(e);
                }finally{
                    conn.close();
                }
            }else{
                System.out.println("You have entered the wrong password");
                t--;
            }
            
        }catch(Exception e){
            System.out.println("Empty or wrong password, Please try again");
        }
            if(t < 0){
                System.out.println("Chances exceeded");
                System.exit(0);
            }
        
        }
        
        scanner.close();
        
    }
    
    public static void adminPage(int id){
        
        System.out.println("Welcome to Admin Page");
        System.out.println("Press 1 to verify the faculty\nPress 2 to verify other admins\nPress 3 to add new admin\nPress 4 to print the list of students\nPress 5 Apply for enhanced position\nPress 6 for university turnover\nPress 7 Go Back\nPress 8\nSign Out");
        
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        
        switch(choice){
            case 1:{
                System.out.println("Here the list of student who have applied for faculty position");
                verifyFaculty(id);
                break;
            }
            case 2:
            {
                System.out.println("Here is the list of fellow admin who have applied for enhanced position");
                verifyAdmin(id);
                break;
            }
            case 3:
            {
                System.out.println("Add new admin");
                newAdmin(id);
                break;
            }
            case 4:
            {
                System.out.println("List of students and their contact");
                studentList(id);
            }
            case 5:
            {   try {
                applyEnhancedPosition(id);
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
                break;
            }
            case 7:
            {
                System.out.println("Redirecting to the home page");
            try {
                Students.studentLogIn();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            case 8:{
            try {
                LogIn.menu();
            } catch (Exception ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            case 6:
            {
            try {
                getUniversityTurnover(id);
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
                break;
            }
            default:
            {
                System.exit(0);
            }
        }
        
        
           scanner.close();
        
        
        
        
    }
    
    public static void verifyFaculty(int id){
            String facultyQuery = "SELECT CONCAT(s.First_Name,\" \",s.Last_Name) as name, s.Email as Faculty_Email, f.Faculty_ID, f.Work_Website, f.Affliation from Student s inner join Faculty f on s.Student_ID = f.Faculty_ID where f.Appointer_User_ID is null and f.Faculty_ID <> ? group by Faculty_ID order by name ;";
            
            
            Scanner scanner= new Scanner(System.in);
            
            
            String updateFaculty = "update faculty set Appointer_User_ID = ? ,Time_of_Verification = now() where Faculty_ID = ?";
            
            
            
            try{
                //System.out.println(id);
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(facultyQuery);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                DBTablePrinter.printResultSet(rs);
                
                System.out.println("Enter the faculty id you have to approve");
                int faculty_ID = scanner.nextInt();
                stmt = conn.prepareStatement(updateFaculty);
                stmt.setInt(1, id);
                stmt.setInt(2,faculty_ID);
                stmt.execute();
                
            }catch(SQLException e){
                System.out.println(e);
            }finally{
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Faculty successfully approved Press 1 to Go back Press 2 to add new Faculty");
            int choice = scanner.nextInt();
            if(choice == 1){
                adminPage(id);
               
            }else{
                verifyFaculty(id);
                
            }
            
            scanner.close();
        }
    
    public static void verifyAdmin(int id){
        String query = "SELECT CONCAT(s.First_Name,\" \",s.Last_Name) as name, s.Email as Admin_Email, a.Enhanced_Position, a.Admin_ID from Student s inner join admin a on s.Student_ID = a.Admin_ID where a.Admin_ID <> ? and a.Authorizer_ID IS NULL group by Admin_ID order by name ;";
        
        Scanner scan = new Scanner(System.in);
        
        String updateQuery = "UPDATE ADMIN SET Authorizer_ID = ? , TimeStamp = now() where Admin_ID = ?";
        
            
            
            try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(query);
                stmt.setInt(1,id);
                ResultSet rs = stmt.executeQuery();
                DBTablePrinter.printResultSet(rs);
                
                System.out.println("Enter the admin Id you want to approve");
                int id1 = scan.nextInt();
                stmt = conn.prepareStatement(updateQuery);
                stmt.setInt(1, id);
                stmt.setInt(2, id1);
                stmt.execute();
                
            }catch(SQLException e){
                System.out.println(e);
            }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            System.out.println("Admin successfully approved Press 1 to Go back Press 2 to approve admin");
            int choice = scan.nextInt();
            if(choice == 1){
                adminPage(id);
               
            }else{
                verifyAdmin(id);
                
            }
        scan.close();
    }
    
    public static void newAdmin(int id){
        System.out.println("Here is the list of all the students and their id, enter the id of the student you want to promote as admin");
        String query = "SELECT CONCAT(s.First_Name,\" \",s.Last_Name) as name, s.Email, Student_ID FROM student s";
        Scanner scanner = new Scanner(System.in);
        
        String insertQuery = "INSERT INTO admin values(?,null,null,null)";
        try{
            conn = ConnectionManager.getConnection();
            createStat = conn.createStatement();
            ResultSet rs = createStat.executeQuery(query);
            DBTablePrinter.printResultSet(rs);
            int id1 = scanner.nextInt();
            stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, id1);
            stmt.execute();
        }catch(SQLException e){
            System.out.println(e);
        }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("New Admin added Press 1 to Go back Press 2 to add new Admin");
        
            int choice = scanner.nextInt();
            
            if(choice == 1){
                adminPage(id);
               
            }else{
                newAdmin(id);
                
            }
        scanner.close();
    }
    
    public static void applyEnhancedPosition(int id) throws SQLException{
        System.out.println("Here is the list of position for admins, type the position you want");
        System.out.println("1.Supervisor\n2.Director\n3.Dean\n4.Co-ordinator");
        Scanner scanner= new Scanner(System.in);
        String position = scanner.next();
        String query = "UPDATE ADMIN SET Enhanced_Position = ? where Admin_ID = ?";
        try{
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1,position);
            stmt.setInt(2, id);
            stmt.execute();
        }catch(SQLException e){
            System.out.println(e);
        }finally{
            conn.close();
        }
        System.out.println("You have successfully applied you are redirected now");
        adminPage(id);
        scanner.close();
    }
    
    public static void studentList(int id){
        System.out.println("Here is the list of contact of all the student who have a course saved in their subject of interest but they are not enrolled in it");
        
        String query = " SELECT s.Student_ID, CONCAT(s.First_Name,\" \",s.Last_Name) as name,c.Contact, co.name as course_name from ((student s inner join contact c on s.Student_ID = C.Student_ID) inner join (select soi.Student_ID, soi.Course_ID from subject_of_interest soi  where soi.Course_ID not in (select sc.Course_ID from student_Courses sc))q on c.Student_ID = q.Student_ID) inner join courses co on q.Course_ID = co.Course_ID GROUP BY s.Student_ID;";
        
        
        try{
            conn = ConnectionManager.getConnection();
            createStat = conn.createStatement();
            ResultSet rs = createStat.executeQuery(query);
            DBTablePrinter.printResultSet(rs);
        }catch(SQLException e){
            System.out.println(e);
        }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 1 to Go back");
        int choice = scanner.nextInt();
        if(choice == 1){
            adminPage(id);
            }
        scanner.close();
    }
    
    public static void getUniversityTurnover(int id) throws SQLException{
        
        System.out.println("This is the university turnover for each course");
        
        String query = "SELECT student_courses.Course_ID AS Course_ID, courses.Name AS Course_Name, count(student_courses.Student_ID) AS Number_of_Enrollments, SUM(student_courses.Amount_per_course) AS Course_Fees, CONCAT(\"$\",ROUND(0.3*SUM(student_courses.Amount_per_course),2)) AS Income_per_Course_at_30_percent_margin " +
"FROM student_courses INNER JOIN courses ON student_courses.Course_ID = courses.Course_ID " +
"GROUP BY student_courses.Course_ID " +
"ORDER BY SUM(student_courses.Amount_per_course) DESC;";
        
        try{
            conn = ConnectionManager.getConnection();
            createStat = conn.createStatement();
            ResultSet rs = createStat.executeQuery(query);
            
            DBTablePrinter.printResultSet(rs);
            
            
        }catch(SQLException e){
            System.out.println(e);
        }finally{
            conn.close();
        }
        
        System.out.println("Press 1 to Go Back\nPress 2 to Exit");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if(choice == 1){
            adminPage(id);
        }else{
            System.exit(0);
        }
        scanner.close();
    }
    
}

