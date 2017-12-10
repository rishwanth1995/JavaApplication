/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rishw
 */
public class Quiz {
    public static void startQuiz(int id, int course_id, int material_id){
        System.out.println("Press 1 to start the quiz\nPress 2 to Go Back");
        String query = "SELECT q.S.NO.,q.Text,q.Correct as correct_option from Question q where q.Course_Id = ? and q.Material_ID = ?";
        String queryAnswer = "SELECT a.answer_sno,a.answers from answers a where a.S.No. = ? and a.Course_ID = ?  and a.Material_ID = ?";
        try{
            conn = ConnectionManager.getConnection();
        }
        
    }
}
