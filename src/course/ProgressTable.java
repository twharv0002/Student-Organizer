package course;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import assignment.Assignment;
import assignment.AssignmentModel;
import assignment.Type;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ProgressTable {

	private GridPane progressGridPane;
	private CourseModel courseModel;
	private AssignmentModel assignmentModel;
	private List<String> weightTypes;
	private Course course;
	private DecimalFormat decimalFormat;
	private double totalProgress;
	
	public ProgressTable(GridPane progressGridPane, Course course){
		courseModel = new CourseModel();
		assignmentModel = new AssignmentModel();
		this.progressGridPane = progressGridPane;
		this.course = course;
		weightTypes = courseModel.getWeightLabels(course);
		decimalFormat = new DecimalFormat("#.##");
		decimalFormat.setRoundingMode(RoundingMode.CEILING);
		totalProgress = 0;
	}
	
	public void displayProgress(){
		addHeaderLabelsToGrid();
		addDynamicLabelsToGrid();
		addTotalLabelToGrid();
		addTotalProgressLabelToGrid();
	}

	private void addHeaderLabelsToGrid() {
		addHeaderLabelToGrid("Type", 0);
		addHeaderLabelToGrid("Weight", 1);
		addHeaderLabelToGrid("Progress", 2);
	}

	private void addDynamicLabelsToGrid() {
		for (int i = 0; i < weightTypes.size(); i++) {
			createTypeLabels(weightTypes, i);
			addWeightLabelToGrid(String.valueOf(course.getWeights().get(weightTypes.get(i))), i);
			addProgressLabelToGrid(calculateProgress(weightTypes.get(i), course.getWeights().get(weightTypes.get(i))), i);
			totalProgress += calculateProgress(weightTypes.get(i), course.getWeights().get(weightTypes.get(i)));
		}
	}

	private void addTotalLabelToGrid(){
		Label totalLabel = new Label("Total");
		progressGridPane.add(totalLabel, 0, 2 + weightTypes.size());
		GridPane.setHalignment(totalLabel, HPos.CENTER);
	}

	private void addProgressLabelToGrid(double progress, int i) {
		Label progressLabel = new Label(decimalFormat.format(progress));
		progressGridPane.add(progressLabel, 2, i + 1);
		GridPane.setHalignment(progressLabel, HPos.CENTER);
	}

	private void addTotalProgressLabelToGrid() {
		Label totalProgressLabel = new Label(decimalFormat.format(totalProgress));
		progressGridPane.add(totalProgressLabel, 2, 2 + weightTypes.size());
		GridPane.setHalignment(totalProgressLabel, HPos.CENTER);
	}
	
	private void addWeightLabelToGrid(String weight, int pos){
		Label weightLabel = new Label(weight);
		progressGridPane.add(weightLabel, 1, pos + 1);
		GridPane.setHalignment(weightLabel, HPos.CENTER);
	}
	
	private void addHeaderLabelToGrid(String name, int pos){
		Label label = new Label(name);
		label.setStyle("-fx-font-weight: bold");
		progressGridPane.add(label, pos, 0);
		GridPane.setHalignment(label, HPos.CENTER);
	}

	private void createTypeLabels(List<String> weightTypes, int i) {
		Label label = new Label(weightTypes.get(i));
		progressGridPane.add(label, 0, i + 1);
		GridPane.setHalignment(label, HPos.CENTER);
	}

	public double calculateProgress(String type, double typeWeight){ 
		int num = 0;
		double sum = 0;
		double progress = 0;
		List<Assignment> assignmentsFromCourse = assignmentModel.getAssignmentsFromCourse((String)course.getData().getProperty("name")); 
		
		for (int j = 0; j < assignmentsFromCourse.size(); j++) {
			if(assignmentsFromCourse.get(j).getSpec().getType().equals(Type.valueOf(type.toUpperCase()))){
				sum += assignmentsFromCourse.get(j).getSpec().getGrade();
				num++;
			}
		}
		if(num != 0){
			progress = (typeWeight) * (sum/num);
		}
		return progress;
	}
	
}
