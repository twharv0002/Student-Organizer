package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	private Stage primaryStage;
	private Scene scene;
	private AnchorPane root;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Student Organizer");
		loadFXMLView();
	}
	
	private void loadFXMLView()
	{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/views/Start View.fxml"));
		try{
			
			root = (AnchorPane)loader.load();
	
			scene = new Scene(root);
			
			primaryStage.setScene(scene);
			
			scene.getStylesheets().add("/styles/StudentOrganizer.css");

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
