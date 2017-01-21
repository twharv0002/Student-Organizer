package assignment;

import java.io.IOException;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddAssignmentPopUp {
	
	public AddAssignmentPopUp(){
		display();
	}
	
	public void display(){
		final Stage dialog = new Stage();
		Scene scene;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/views/insertAssignment.fxml"));
		dialog.initModality(Modality.APPLICATION_MODAL);
		try {
			AnchorPane root = (AnchorPane)loader.load();
			scene = new Scene(root);
			dialog.setScene(scene);
			dialog.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
