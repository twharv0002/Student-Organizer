package application;
	
import java.sql.Date;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	private Stage primaryStage;
	private Scene scene;
	private AnchorPane root;
	private DataBase database;
	private Assignment assignment;
	private Course course;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Student Organizer");
		// TESTING BELOW
		String input = "2016/09/28";
		input = input.replace('/', '-');
		Date sqlDate = Date.valueOf(input);
		assignment = new Assignment("HW1", sqlDate, "Ch 1 - 9", 0, true, "Advanced Programming", "Homework");
		course = new Course("Hitz", "Advanced Programming", 11, 0, 100, "MW 2:40 - 3:55");
		database = new DataBase();
//		try {
//			database.getConnection();
			//database.insertCourse(course);
//			try {
//				database.insertAssignment(assignment);
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//database.insertWeight(0.3, "test");
			//database.updateGrade("HW3", 70);
			//database.deleteCourse("Advanced Programming");
			//System.out.println(assignment.getDate().getTime() - System.currentTimeMillis());
			//System.out.println(assignment.getDate());
			//System.out.println(assignment.getDaysUntil());
//			
//			ResultSet rs = database.getCourses();
//			while(rs.next()){
//				System.out.println(rs.getString("name") + " " + rs.getString("instructor") + " " + rs.getString("time"));
//			}
//			rs = database.getGrades();
////			while(rs.next()){
////				System.out.println(rs.getString("grade"));
////			}
//			rs = database.sortAssignmentsBy("class", "ASC");
//			while(rs.next()){
//				System.out.println(rs.getString("name") + " " + rs.getString("class"));
//			}
//		} catch (ClassNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
		//}
		// END OF TESTING
		loadFXMLView();		
	}
	
	private void loadFXMLView()
	{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/Start View.fxml"));
		
		try{
			
			root = (AnchorPane)loader.load();
	
			scene = new Scene(root);
			
			primaryStage.setScene(scene);
			
			scene.getStylesheets().add("application/StudentOrganizer.css");
					
			primaryStage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
