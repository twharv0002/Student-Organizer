package application;

import assignment.Assignment;
import assignment.AssignmentTabController;
import course.CourseTabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import schedule.ScheduleTabController;

public class MainController {

	@FXML private HomeTabController homeTabController;
	@FXML private AssignmentTabController assignmentTabController;
	@FXML private ScheduleTabController scheduleTabController;
	@FXML private CourseTabController courseTabController;

	@FXML private void initialize() {
		homeTabController.init(this);
		assignmentTabController.init(this);
		scheduleTabController.init(this);
		courseTabController.init(this);
	}
	
	public void update(){
		System.out.println("Relaying data");
		homeTabController.update();
		scheduleTabController.update();
		assignmentTabController.update();
	}
	
}
