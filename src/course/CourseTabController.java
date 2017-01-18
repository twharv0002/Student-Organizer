package course;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.FadeLabel;
import application.MainController;
import application.ProgressTable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

public class CourseTabController implements Initializable {
	
	@FXML ListView<Course> courseListView;
	@FXML Label nameLabel;
	@FXML Label instructorLabel;
	@FXML Label roomLabel;
	@FXML Label timeLabel;
	@FXML Label absencesLabel;
	@FXML Button clearButton;
	@FXML Button addButton;
	@FXML Button refreshButton;
	@FXML GridPane progressGridPane;
	@FXML TextField courseNameLabel;
	@FXML TextField courseTimeLabel;
	@FXML TextField teacherLabel;
	@FXML TextField courseAbsencesLabel;
	@FXML TextField courseRoomLabel;
	@FXML TextField hwWeightLabel;
	@FXML TextField quizWeightLabel;
	@FXML TextField labWeightLabel;
	@FXML TextField testWeightLabel;
	@FXML TextField finalWeightLabel;
	@FXML TextField paperWeightLabel;
	@FXML TextField discussionWeightLabel;
	@FXML TextField attendanceWeightLabel;
	@FXML TextField projectWeightLabel;
	@FXML FadeLabel updatedLabel;
	
	private CourseModel courseModel;
	private MainController mainController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		courseModel = new CourseModel();
		courseListView.setItems(courseModel.getCourses());
		
		courseListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {			
			@Override
			public void changed(ObservableValue<? extends Course> observable, Course oldValue,
					Course newValue) {
				if(newValue != null){
					setWeightLabels(newValue.getName());
					setGeneralSectionLabels(newValue);
					setCourseSectionLabels(newValue);
					setProgressGridPaneValues(newValue);
				}
			}

			private void setProgressGridPaneValues(Course newValue) {
				Node node = progressGridPane.getChildren().get(0);
				progressGridPane.getChildren().clear();
				progressGridPane.add(node, 0, 0);
				
				ProgressTable progressTable = new ProgressTable(progressGridPane, newValue.getName());
				progressTable.displayProgress();
			}

			private void setCourseSectionLabels(Course newValue) {
				courseNameLabel.setText(newValue.getName());
				teacherLabel.setText(newValue.getInstructor());
				courseRoomLabel.setText(String.valueOf(newValue.getRoomNumber()));
				courseAbsencesLabel.setText(String.valueOf(newValue.getAbsences()));
				courseTimeLabel.setText(newValue.getClassTime());
			}

			private void setGeneralSectionLabels(Course newValue) {
				nameLabel.setText(newValue.getName());
				instructorLabel.setText(newValue.getInstructor());
				timeLabel.setText(newValue.getClassTime());
				absencesLabel.setText(String.valueOf(newValue.getAbsences()));
				roomLabel.setText(String.valueOf(newValue.getRoomNumber()));
			}
	
		});
				
	}

	private void setWeightLabels(String course){
		List<String> weights = courseModel.getWeights(course);
		
		hwWeightLabel.setText(weights.get(0));
		quizWeightLabel.setText(weights.get(1));
		labWeightLabel.setText(weights.get(2));
		testWeightLabel.setText(weights.get(3));
		finalWeightLabel.setText(weights.get(4));
		paperWeightLabel.setText(weights.get(5));
		projectWeightLabel.setText(weights.get(6));
		attendanceWeightLabel.setText(weights.get(7));
		discussionWeightLabel.setText(weights.get(8));
	}

	@FXML
	void onClearButtonClick(ActionEvent event){
		  clearFields();
	}
	
	private void clearFields() {
		  courseNameLabel.setText("");
		  courseTimeLabel.setText("");
		  teacherLabel.setText("");
		  courseAbsencesLabel.setText("");
		  courseRoomLabel.setText("");
		  hwWeightLabel.setText("0");
		  quizWeightLabel.setText("0");
		  labWeightLabel.setText("0");
		  testWeightLabel.setText("0");
		  finalWeightLabel.setText("0");
		  paperWeightLabel.setText("0");
		  discussionWeightLabel.setText("0");
		  attendanceWeightLabel.setText("0");
		  projectWeightLabel.setText("0");
	}
	
	@FXML
	void onAddButtonClick(ActionEvent event){
		if(courseNameLabel.getText().equals("")){
			displayStatusMessage("No Input Given");
		}
		else if(courseModel.hasCourse(courseNameLabel.getText())){
			displayStatusMessage("Course already exists");
		}
		else{
			Course course = courseModel.addNewCourse(teacherLabel.getText(), courseNameLabel.getText(), 
					courseRoomLabel.getText(), courseAbsencesLabel.getText(), courseTimeLabel.getText());
			displayStatusMessage("Course Added");
			courseModel.addCourseWeights(getLabelWeights(), course.getName());
			mainController.update();
			courseListView.getItems().add(course);
			courseListView.refresh();
			courseListView.getSelectionModel().clearAndSelect(courseListView.getItems().size() - 1);
			}		
	}

	private void displayStatusMessage(String status) {
		updatedLabel.setText(status);
		updatedLabel.animate();
	}

	private List<Double> getLabelWeights() {
		List<Double> weights = new ArrayList<>();
		weights.add(Double.valueOf(hwWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(quizWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(labWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(testWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(finalWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(paperWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(discussionWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(projectWeightLabel.getText()) / 100);
		weights.add(Double.valueOf(attendanceWeightLabel.getText()) / 100);
		weights.add(0.0);
		return weights;
	}
	
	@FXML
	void onUpdateButtonClick(ActionEvent event){
		if(courseNameLabel.getText().equals("")){
			displayStatusMessage("No Input Given");
		}else{
			Course course = courseListView.getSelectionModel().getSelectedItem();
			String oldName = course.getName();
			
			course.setName(courseNameLabel.getText());
			course.setInstructor(teacherLabel.getText());
			course.setClassTime(courseTimeLabel.getText());
			course.setAbsences(Integer.valueOf(courseAbsencesLabel.getText()));
			course.setRoomNumber(Integer.valueOf(courseRoomLabel.getText()));
			course.setFinalGrade(0.0);
			
			courseListView.refresh();
			
			courseModel.updateCourse(course);
			mainController.update();
			courseModel.updateCourseWeights(oldName, course.getName(), getLabelWeights());
			
			displayStatusMessage("Updated");
			
			courseListView.getSelectionModel().clearSelection();
			courseListView.getSelectionModel().select(course);
			System.out.println(course.getName() + " Updated");
		}
	  
	}
	
	@FXML
	void onDeleteButtonClick(ActionEvent event){
		if(courseNameLabel.getText().equals("")){
			displayStatusMessage("No Input Given");
		}
		else{
			if(courseModel.hasCourse(courseNameLabel.getText())){
				
				Alert deleteAlert = new Alert(AlertType.CONFIRMATION, "Confirm Delete?");
				Optional<ButtonType> resultDelete = deleteAlert.showAndWait();
				if (resultDelete.isPresent() && resultDelete.get() == ButtonType.OK) {
					courseModel.removeCourse(courseNameLabel.getText());
					mainController.update();
					displayStatusMessage("Course Deleted");
					clearFields();
					courseListView.getItems().remove(courseListView.getSelectionModel().getSelectedIndex());
					courseListView.refresh();
				}
			}
			else{
				displayStatusMessage("Course Not Found");
			}
		}
	}

	public void init(MainController mainController) {
		this.mainController = mainController;
	}
	
}
