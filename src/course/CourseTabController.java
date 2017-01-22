package course;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
					setWeightLabels(newValue);
					setGeneralSectionLabels(newValue);
					setCourseSectionLabels(newValue);
					setProgressGridPaneValues(newValue);
				}
			}

			private void setProgressGridPaneValues(Course newValue) {
				Node node = progressGridPane.getChildren().get(0);
				progressGridPane.getChildren().clear();
				progressGridPane.add(node, 0, 0);
				
				ProgressTable progressTable = new ProgressTable(progressGridPane, newValue);
				progressTable.displayProgress();
			}

			private void setCourseSectionLabels(Course newValue) {
				courseNameLabel.setText((String)newValue.getData().getProperty("name"));
				teacherLabel.setText((String)newValue.getData().getProperty("instructor"));
				courseRoomLabel.setText(String.valueOf(newValue.getData().getProperty("roomNumber")));
				courseAbsencesLabel.setText(String.valueOf(newValue.getData().getProperty("absences")));
				courseTimeLabel.setText((String)newValue.getData().getProperty("time"));
			}

			private void setGeneralSectionLabels(Course newValue) {
				nameLabel.setText((String)newValue.getData().getProperty("name"));
				instructorLabel.setText((String)newValue.getData().getProperty("instructor"));
				timeLabel.setText(String.valueOf(newValue.getData().getProperty("time")));
				absencesLabel.setText(String.valueOf(newValue.getData().getProperty("absences")));
				roomLabel.setText((String.valueOf(newValue.getData().getProperty("roomNumber"))));
			}
	
		});
				
	}

	private void setWeightLabels(Course course){
		hwWeightLabel.setText(String.valueOf(course.getWeights().get("homework") * 100));
		quizWeightLabel.setText(String.valueOf(course.getWeights().get("quiz") * 100));
		labWeightLabel.setText(String.valueOf(course.getWeights().get("lab") * 100));
		testWeightLabel.setText(String.valueOf(course.getWeights().get("test") * 100));
		finalWeightLabel.setText(String.valueOf(course.getWeights().get("final") * 100));
		paperWeightLabel.setText(String.valueOf(course.getWeights().get("paper") * 100));
		projectWeightLabel.setText(String.valueOf(course.getWeights().get("project") * 100));
		attendanceWeightLabel.setText(String.valueOf(course.getWeights().get("attendance") * 100));
		discussionWeightLabel.setText(String.valueOf(course.getWeights().get("discussion") * 100));
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
					courseRoomLabel.getText(), courseAbsencesLabel.getText(), courseTimeLabel.getText(), getLabelWeights());
			displayStatusMessage("Course Added");
			courseModel.addCourseWeights(course);
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
	
	private Map<String, Double> getLabelWeights() {
		
		Map<String, Double> weights = new HashMap<String, Double>();
		weights.put("homework", Double.valueOf(hwWeightLabel.getText()) / 100);
		weights.put("quiz", Double.valueOf(quizWeightLabel.getText()) / 100);
		weights.put("lab", Double.valueOf(labWeightLabel.getText()) / 100);
		weights.put("test", Double.valueOf(testWeightLabel.getText()) / 100);
		weights.put("final", Double.valueOf(finalWeightLabel.getText()) / 100);
		weights.put("paper", Double.valueOf(paperWeightLabel.getText()) / 100);
		weights.put("discussion", Double.valueOf(discussionWeightLabel.getText()) / 100);
		weights.put("project", Double.valueOf(projectWeightLabel.getText()) / 100);
		weights.put("attendance", Double.valueOf(attendanceWeightLabel.getText()) / 100);
		weights.put("participation", 0.0);
		return weights;
	}
	
	@FXML
	void onUpdateButtonClick(ActionEvent event){
		if(courseNameLabel.getText().equals("")){
			displayStatusMessage("No Input Given");
		}else{
			Course course = courseListView.getSelectionModel().getSelectedItem();
			String oldName = (String)course.getData().getProperty("name");
			
			course.getData().setProperty("name", courseNameLabel.getText());
			course.getData().setProperty("instructor", teacherLabel.getText());
			course.getData().setProperty("time", courseTimeLabel.getText());
			course.getData().setProperty("absences", Integer.valueOf(courseAbsencesLabel.getText()));
			course.getData().setProperty("roomNumber", Integer.valueOf(courseRoomLabel.getText()));
			course.getData().setProperty("finalGrade", 0.0);
			
			courseListView.refresh();
			courseModel.updateCourse(course);
			courseModel.updateCourseWeights(oldName, (String)course.getData().getProperty("name"), getLabelWeights());
			mainController.update();
			course.setWeights(getLabelWeights());
			displayStatusMessage("Updated");
			
			courseListView.getSelectionModel().clearSelection();
			courseListView.getSelectionModel().select(course);
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
