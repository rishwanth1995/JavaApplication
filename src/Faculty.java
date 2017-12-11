import java.util.*;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Faculty {
    
    
    
    private static Connection conn;
    private static PreparedStatement stmt;
    private static Statement createStat;
    
    public static void facultyLogIn() throws SQLException{
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
        String adminQuery =  "SELECT Faculty_ID from faculty f where Faculty_ID = ? AND f.Appointer_User_ID IS NOT NULL";
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
                int faculty_ID = 0; 
                try{
                    conn = ConnectionManager.getConnection();
                    stmt = conn.prepareStatement(adminQuery);
                    stmt.setInt(1, student_id);
                    ResultSet rs = stmt.executeQuery();
                    //stmt = conn.prepareStatement(studentAndAdminCheck);
                    //stmt.setInt(1, student_id);
                    //ResultSet rs1 = stmt.executeQuery();
                    if(rs.next()){
                        faculty_ID= rs.getInt("Faculty_ID");
                        System.out.println("You have successfully logged on as faculty");
                        facultyPage(faculty_ID);
                        break;
                    }else{
                        System.out.println("You are not a faculty, you are redirected to a login page");
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
    
    public static void facultyPage(int id){
        
        System.out.println("Welcome to Faculty Page");
        System.out.println("1.Create a course\n2.Account history\n3.forum_questions\n4.Signout\n5.Exit");
        
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        
        switch(choice){
            case 1:{
                System.out.println("Let's create!!!");
                createCourse(id);
                break;
            }
            case 2:
            {
                System.out.println("Account history as follows");
                accountHistory(id);
                break;
            }
            case 3:
            {
                System.out.println("List of questions ordered by likes");
                forumQuestions(id);
                break;
            }
            case 4:
            {
                System.out.println("Signing_out");
            try {
                LogIn.menu();
            } catch (Exception ex) {
                Logger.getLogger(Faculty.class.getName()).log(Level.SEVERE, null, ex);
            }
                break;
            }
            case 5:
            {
                System.exit(0);
               
            }
            default:
            {
                facultyPage(id);
            }
        }
    }
    
    public static void accountHistory(int id){
        String query = "SELECT c.Course_ID,c.Creation_Date, c.Description, cc.Time_Stamp, c.Name, c.Icon, c.Primary_Topic, c.cost , st.secondary_topic from courses c inner join secondary_topics st on c.Course_ID = st.Course_ID inner join course_creation cc on c.Course_ID = cc.Course_ID where cc.Faculty_ID = ? ;";
       
       
            //Scanner scan = new Scanner(System.in);
            
            
            try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(query);
                stmt.setInt(1,id);
                ResultSet rs = stmt.executeQuery();
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
            System.out.println("Redirecting to home page...");
            
            
                facultyPage(id);
               
            
    }
    
    public static void forumQuestions(int id){
        String query = "SELECT qa.Question,qa.Question_ID as question_no,qa.Student_ID as question_part, COUNT(loq.Student_who_liked) as likes from question_Asked qa inner join likes_for_question loq on qa.Question_ID = loq.Question_ID and qa.Student_ID = loq.Student_ID order by likes";       
        
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
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Enter the question no you want to answer or modify ");
        
        int question_id = scan.nextInt();
        
        System.out.println("Enter the question part you want to modify or answer");
        
        int question_part = scan.nextInt();
        
        System.out.println("Press 1 if you want to change the visibility of the answer to the above question\nPress 2 if you want to answer the above questions");
        
        int choice  = scan.nextInt();
        
        if(choice == 1){
            String updateVisibilty = "update questions_Answer set visibilty = 1 where Question_ID = ? and Student_ID = ?";
            try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(updateVisibilty);
                stmt.setInt(1, question_id);
                stmt.setInt(2, question_part);
                stmt.execute();
                
                
            System.out.println("Press 1 to Go the question\n2.Go to the home page");
            int choice2= scan.nextInt();
            if(choice2 == 1){
                forumQuestions(id);
            }else{
                facultyPage(id);
            }
                
            }catch(SQLException e){
                System.out.println(e);
            }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }else{
            String insertAnswer = "insert into questions_answer values(?,?,?,?,?)";
            
            System.out.println("Enter the answer");
            scan.nextLine();
            String answer = scan.nextLine();
            System.out.println("Enter visibility for your answer");
            
            int visi = scan.nextInt();
            try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(insertAnswer);
                stmt.setInt(1, question_id);
                stmt.setInt(2, question_part);
                stmt.setInt(3, id);
                stmt.setString(4, answer);
                stmt.setInt(5,visi);
                stmt.execute();
                
                System.out.println("Press 1 to Go the question\n2.Go to the home page");
            int choice2= scan.nextInt();
            if(choice2 == 1){
                forumQuestions(id);
            }else{
                facultyPage(id);
            }
                
                
            }catch(SQLException e){
                System.out.println(e);
            }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
        
            
            
            
            
                
     scan.close();
    }
    
    public static void createCourse(int id){
        
        System.out.println("Enter the course name you want to enter");
        Scanner scanner = new Scanner(System.in);
        
        //scanner.nextLine();
        String courseName = scanner.next();
        
        System.out.println("Enter the primary topic for your course");
        
        //scanner.nextLine();
        String primary_topic = scanner.next();
        
        System.out.println("Enter the description for your course");
        
        
        String des = scanner.next();
        
        System.out.println("Enter expected cost for your course");
        
        double cost = scanner.nextDouble();
        
        System.out.println("Enter an icon for your course");
        
        int icon = scanner.nextInt();
        
        Random rand =  new Random();
        int course_id = rand.nextInt(500);
        
        String query1 = "insert into courses values(?,now(),?,?,?,?,?)";
        String query2 = "insert into course_creation values(?,?,now())";
        
        try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(query1);
                stmt.setInt(1, course_id);
                stmt.setString(2,des);
                stmt.setString(3, courseName);
                stmt.setInt(4,icon);
                stmt.setString(5, primary_topic);
                stmt.setDouble(6, cost);
                stmt.execute();
                
                stmt = conn.prepareStatement(query2);
                stmt.setInt(1, id);
                stmt.setInt(2, course_id);
                stmt.execute();
                //System.out.println("Enter the secondary topics of your course now");
                secondaryTopics(course_id);
            }catch(SQLException e){
                System.out.println(e);
            }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        
        scanner.close();
    }
    
    public static void secondaryTopics(int id){
        System.out.println("Enter the secondary topic you want to add");
        Scanner scan = new Scanner(System.in);
       
        String sec = scan.next();
        
        
        String query1 = "INSERT INTO secondary_topics values(?,?);";
        
        try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(query1);
                stmt.setString(1,sec);
                stmt.setInt(2, id);
                stmt.execute();
                System.out.println("Press 1 to Enter the secondary topic again \nPress 2 to add course_material");
                int choice = scan.nextInt();
                if(choice == 1){
                secondaryTopics(id);
                }else{
                    courseMaterial(id);
                }
            }catch(SQLException e){
                System.out.println(e);
            }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        
                scan.close();
    }
    
    public static void courseMaterial(int id){
        Random rand  =  new Random();
        int mat_id =  rand.nextInt(100);
        System.out.println("Enter the name of the course material you want to add");
        Scanner scan = new Scanner(System.in);
        String name = scan.next();
        String query = "INSERT into course_material values(?,?,?,null)";
        
        
        try{
                conn = ConnectionManager.getConnection();
                stmt = conn.prepareStatement(query);
                stmt.setInt(1,id);
                stmt.setInt(2, mat_id);
                stmt.setString(3, name);
                stmt.execute();
                System.out.println("you have successfully created a course");
                System.out.println("Press 1 to Enter the more course Material again \nPress 2 to create more course");
                int choice = scan.nextInt();
                if(choice == 1){
                courseMaterial(id);
                }else{
                    createCourse(id);
                }
            }catch(SQLException e){
                System.out.println(e);
            }finally{
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        
                scan.close();
    }
 }