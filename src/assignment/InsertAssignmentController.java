package assignment;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import application.DataBase;
import application.FadeLabel;
import application.Main;
import application.MainController;
import course.Course;
import course.CourseModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InsertAssignmentController implements Initializable {
	
	@FXML TextField nameLabel;
	@FXML ComboBox<Type> typeCombo;
	@FXML DatePicker datePicker;
	@FXML TextArea descriptionTextArea;
	@FXML Button addButton;
	@FXML FadeLabel assignmentCompleteLabel;
	@FXML ComboBox<String> classComboBox;
	
	private CourseModel courseModel;
	private DataBase db;
	
	public MainController mainController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db = new DataBase();
		courseModel = new CourseModel();
		
		ObservableList<Course> courses = courseModel.getCourses();
		int i = 0;
		while(i < courses.size() ){
			Course course = courses.get(i);
			classComboBox.getItems().add(course.getName());
			i++;
		}
		typeCombo.getItems().addAll(Type.values());
	}
	
	@FXML
	void onAddButtonClick(ActionEvent event){
		
		
		if(classComboBox.getItems().isEmpty()){
			Alert warningAlert = new Alert(AlertType.WARNING);
			warningAlert.setContentText("No courses found. To add an assignment, first create a new Course");
			warningAlert.show();
		}
		
		else if(nameLabel.getText().equals("") || datePicker.getValue() == null ||
				classComboBox.getSelectionModel().getSelectedItem() == null || 
				typeCombo.getSelectionModel().getSelectedItem() == null)
		{
			assignmentCompleteLabel.setText("* Fields Required");
			assignmentCompleteLabel.animate();
		}
		else{
			AssignmentSpec spec = new AssignmentSpec(nameLabel.getText(), convertDate(datePicker.getValue()), 
					descriptionTextArea.getText(), 0, false, classComboBox.getSelectionModel().getSelectedItem().toString(), 
					typeCombo.getSelectionModel().getSelectedItem());
			Assignment assignment = new Assignment(spec);
			try {
				db.insertAssignment(assignment);
				assignmentCompleteLabel.setText("Assignment Inserted");
				mainController.update();
				System.out.println("Assignment Inserted");
				assignmentCompleteLabel.animate();
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public Date convertDate(LocalDate date){
		Date sqlDate = Date.valueOf(date);
		return sqlDate;
	}
}
