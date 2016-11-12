package application;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class InsertAssignmentController implements Initializable {
	
	@FXML TextField nameLabel;
	@FXML ComboBox<String> typeCombo;
	@FXML DatePicker datePicker;
	@FXML TextArea descriptionTextArea;
	@FXML Button addButton;
	@FXML Label assignmentCompleteLabel;
	@FXML ComboBox<String> classComboBox;
	
	private DataBase db;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db = new DataBase();
		ResultSet rs = null;
		try {
			rs = db.getCourses();
			while(rs.next()){
				Course course = new Course(rs.getString("instructor"), rs.getString("name"), rs.getInt("roomNumber"),
						rs.getInt("absences"), rs.getDouble("finalGrade"), rs.getString("time"));
				classComboBox.getItems().add(course.getName());
			}
			typeCombo.getItems().addAll("Homework", "Quiz", "Lab","Test","Final","Paper","Discussion", "Project");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	void onAddButtonClick(ActionEvent event){
		
		Assignment assignment = new Assignment(nameLabel.getText(), convertDate(datePicker.getValue()), 
				descriptionTextArea.getText(), 0, false, classComboBox.getSelectionModel().getSelectedItem().toString(), 
				typeCombo.getSelectionModel().getSelectedItem().toString());
		try {
			db.insertAssignment(assignment);
			System.out.println("Assignment Inserted");
			assignmentCompleteLabel.setText("Assignment Inserted");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private Date convertDate(LocalDate date){
		Date sqlDate = Date.valueOf(date);
		return sqlDate;
	}
	
}
