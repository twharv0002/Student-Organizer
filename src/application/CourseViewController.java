package application;

import java.math.RoundingMode;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CourseViewController implements Initializable {
	
	@FXML ListView<Course> courseListView;
	@FXML Label nameLabel;
	@FXML Label instructorLabel;
	@FXML Label roomLabel;
	@FXML Label timeLabel;
	@FXML Label absencesLabel;
	@FXML Button clearButton;
	@FXML Button addButton;
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
	
	private DataBase database;
	private ObservableList<Course> name;
	private List<Assignment> assignments = new ArrayList<>();
	private List<Course> courseData = new ArrayList<>();
	
	@FXML
	void onClearButtonClick(ActionEvent event){
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
			System.out.println("Future error message");
		}
		else{
			// CHECK IF COURSE NAME ALREADY EXISTS
				try {
					if(database.hasCourse(courseNameLabel.getText())){
						System.out.println("Course already exists");
					}
					else{
						System.out.println("Creating new course");
					// VALIDATE FIELDS
						int room = 0;
						int absences = 0;
						if(!courseRoomLabel.getText().equals(""))
							room = Integer.valueOf(courseRoomLabel.getText());
						if(!courseAbsencesLabel.getText().equals(""))
							absences = Integer.valueOf(courseAbsencesLabel.getText());
							
						Course course = new Course(teacherLabel.getText(), courseNameLabel.getText(), 
								room, absences, 0, courseTimeLabel.getText());
						
						
						database.insertCourse(course);
						List<Double> weights = getLabelWeights();
						
						database.insertWeights(weights, courseNameLabel.getText());
							
						// UPDATE THE LISTVIEW TO INCLUDE THE NEW COURSE
						courseListView.getItems().add(course);
						courseListView.refresh();
							
						}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
					
			}
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
		Course course = courseListView.getSelectionModel().getSelectedItem();
		String oldName = course.getName();
		course.setName(courseNameLabel.getText());
		course.setInstructor(teacherLabel.getText());
		course.setClassTime(courseTimeLabel.getText());
		course.setAbsences(Integer.valueOf(courseAbsencesLabel.getText()));
		course.setRoomNumber(Integer.valueOf(courseRoomLabel.getText()));
		course.setFinalGrade(0.0);
		
		List<Double> weights = getLabelWeights();
		
		courseListView.refresh();
		
		try {
			database.updateCourse(course);
			database.updateWeightCourse(oldName, course.getName());
			
			database.updateWeightByCourse(weights, course.getName());
			System.out.println(course.getName() + " Updated");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}
	
	@FXML
	void onDeleteButtonClick(ActionEvent event){
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		database = new DataBase();
		
		try {
			populateCourseListView();
			populateAssignmentList();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		courseListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {			
			@Override
			public void changed(ObservableValue<? extends Course> observable, Course oldValue,
					Course newValue) {
				if(newValue != null){
					
					System.out.println(newValue.getId());
					setWeightLabels(newValue.getName());
					
					nameLabel.setText(newValue.getName());
					instructorLabel.setText(newValue.getInstructor());
					timeLabel.setText(newValue.getClassTime());
					absencesLabel.setText(String.valueOf(newValue.getAbsences()));
					roomLabel.setText(String.valueOf(newValue.getRoomNumber()));
					
					courseNameLabel.setText(newValue.getName());
					teacherLabel.setText(newValue.getInstructor());
					courseRoomLabel.setText(String.valueOf(newValue.getRoomNumber()));
					courseAbsencesLabel.setText(String.valueOf(newValue.getAbsences()));
					courseTimeLabel.setText(newValue.getClassTime());
					
					Node node = progressGridPane.getChildren().get(0);
					progressGridPane.getChildren().clear();
					progressGridPane.add(node, 0, 0);
					try {
						populateAssignmentList();
						showProgressDisplay(newValue.getName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
			}

			private void showProgressDisplay(String name) throws ClassNotFoundException, SQLException {
				List<Assignment> localAss = new ArrayList<>();
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				
				for (int i = 0; i < assignments.size(); i++) {
					if(assignments.get(i).getClassName().equals(name)){
						localAss.add(assignments.get(i));
					}
				}
				
				createProgressGrid();
				List<String> weightTypes = getAllWeightTypes(name);
				
				double value = 0;
				for (int i = 0; i < weightTypes.size(); i++) {
					createTypeLabels(weightTypes, i);
					
					double typeWeight = getWeight(name, weightTypes.get(i));				
					double progress = calculateProgress(localAss, weightTypes, i, typeWeight);
					
					Label newLabel = new Label(String.valueOf(typeWeight));
					Label newLabel2 = new Label(df.format(progress));
					progressGridPane.add(newLabel, 1, i + 1);
					progressGridPane.add(newLabel2, 2, i + 1);
					GridPane.setHalignment(newLabel, HPos.CENTER);
					GridPane.setHalignment(newLabel2, HPos.CENTER);
					
					value += progress;
				}
				Label totalLabel = new Label("Total");
				Label progressLabel = new Label(df.format(value));
				progressGridPane.add(totalLabel, 0, 2 + weightTypes.size());
				progressGridPane.add(progressLabel, 2, 2 + weightTypes.size());
				GridPane.setHalignment(totalLabel, HPos.CENTER);
				GridPane.setHalignment(progressLabel, HPos.CENTER);
			}

			private double calculateProgress(List<Assignment> localAss, List<String> weightTypes, int i,
					double typeWeight) {
				int num = 0;
				double sum = 0;
				double progress = 0;
				for (int j = 0; j < localAss.size(); j++) {
					if(localAss.get(j).getType().equals(weightTypes.get(i))){
						sum += localAss.get(j).getGrade();
						num++;
					}
				}
				if(num != 0){
					progress = (typeWeight) * (sum/num);
					System.out.println(progress + " = " + typeWeight + " * " + sum + " / " + num);
				}
				return progress;
			}

			private void createTypeLabels(List<String> weightTypes, int i) {
				Label label = new Label(weightTypes.get(i));
				progressGridPane.add(label, 0, i + 1);
				GridPane.setHalignment(label, HPos.CENTER);
			}

			private void createProgressGrid() {
				Label typeLabel = new Label("Type");
				typeLabel.setStyle("-fx-font-weight: bold");
				Label weightLabel = new Label("Weight");
				weightLabel.setStyle("-fx-font-weight: bold");
				Label progLabel = new Label("Progress");
				progLabel.setStyle("-fx-font-weight: bold");
				
				progressGridPane.add(typeLabel, 0, 0);
				progressGridPane.add(weightLabel, 1, 0);
				progressGridPane.add(progLabel, 2, 0);
				
				GridPane.setHalignment(typeLabel, HPos.CENTER);
				GridPane.setHalignment(weightLabel, HPos.CENTER);
				GridPane.setHalignment(progLabel, HPos.CENTER);
			}
		});
		
		
	}
	
	private void setWeightLabels(String course){
		ResultSet rs;
		try {
			rs = database.getTypeWeightByCourse(course);
			
			while(rs.next()){
				hwWeightLabel.setText(String.valueOf(rs.getDouble("homework") * 100));
				quizWeightLabel.setText(String.valueOf(rs.getDouble("quiz") * 100));
				labWeightLabel.setText(String.valueOf(rs.getDouble("lab") * 100));
				testWeightLabel.setText(String.valueOf(rs.getDouble("test") * 100));
				finalWeightLabel.setText(String.valueOf(rs.getDouble("final") * 100));
				paperWeightLabel.setText(String.valueOf(rs.getDouble("paper") * 100));
				projectWeightLabel.setText(String.valueOf(rs.getDouble("project") * 100));
				attendanceWeightLabel.setText(String.valueOf(rs.getDouble("attendance") * 100));
				discussionWeightLabel.setText(String.valueOf(rs.getDouble("discussion") * 100));
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private double getWeight(String course, String type){
		double value = 0.0;
		try {
			value = database.getTypeWeightByCourse(course).getDouble(type);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	private List<String> getAllWeightTypes(String name) throws ClassNotFoundException, SQLException{
		List<String> list = new ArrayList<>();
		
		ResultSet rs = database.getTypeWeightByCourse(name);
		while(rs.next()){
			if(rs.getDouble("Homework") > 0){
				list.add("Homework");
			}
			if(rs.getDouble("Test") > 0){
				list.add("Test");
			}
			if(rs.getDouble("Quiz") > 0){
				list.add("Quiz");
			}
			if(rs.getDouble("Lab") > 0){
				list.add("Lab");
			}
			if(rs.getDouble("Final") > 0){
				list.add("Final");
			}
			if(rs.getDouble("Paper") > 0){
				list.add("Paper");
			}
			if(rs.getDouble("Discussion") > 0){
				list.add("Discussion");
			}
			if(rs.getDouble("Project") > 0){
				list.add("Project");
			}
			if(rs.getDouble("Attendance") > 0){
				list.add("Attendance");
			}
		}
		
		return list;
	}
	
	private void populateAssignmentList() throws ClassNotFoundException, SQLException{
		assignments.clear();
		ResultSet rs = database.getAssignments();
		while(rs.next()){
			Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
					rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"), rs.getInt("id"));
				assignments.add(assignment);
		}
	}
	
	private void populateCourseListView() throws ClassNotFoundException, SQLException {
		name = FXCollections.observableArrayList();
		ResultSet rs = database.getCourses();
		
		while(rs.next()){
			Course course = new Course(rs.getString("instructor"), rs.getString("name"), rs.getInt("roomNumber"),
					rs.getInt("absences"), rs.getDouble("finalGrade"), rs.getString("time"), rs.getInt("id"));
			courseData.add(course);
		}
		
		for (int i = 0; i < courseData.size(); i++) {
			name.add(courseData.get(i));
		}
		
		
		courseListView.setItems(name);
	}
	
}
