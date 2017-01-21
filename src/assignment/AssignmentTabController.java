package assignment;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.FadeLabel;
import application.MainController;
import course.Course;
import course.CourseModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AssignmentTabController implements Initializable{
	
	@FXML Button newAssignmentButton;
	@FXML Button updateButton;
	@FXML Button deleteButton;
	@FXML TextField assignmentSearchTextField;
	@FXML TextField nameTextField;
	@FXML TextField gradeTextField;
	@FXML DatePicker dateDatePicker;
	@FXML TextArea detailsTextArea;
	@FXML ComboBox<String> courseComboBox;
	@FXML ComboBox<DateOption> dateComboBox;
	@FXML ComboBox<Type> typeComboBox;
	@FXML ComboBox<Type> detailsTypeComboBox;
	@FXML ComboBox<String> gradeComboBox;
	@FXML ComboBox<String> classComboBox;
	@FXML CheckBox completedCheckBox;
	@FXML CheckBox detailsCompletedCheckBox;
	@FXML ListView<Assignment> assignmentListView;
	@FXML FadeLabel updatedLabel;
	
	private CourseModel courseModel;
	private AssignmentModel assignmentModel;
	public MainController mainController;
	@FXML private InsertAssignmentController insertAssignmentController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		courseModel = new CourseModel();
		assignmentModel = new AssignmentModel();
		
		assignmentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		initComboBoxes();
		populateListView();
		
		assignmentSearchTextField.textProperty().addListener((event) -> {
			search();
		});
		
		courseComboBox.setOnAction((event) -> {
			search();
		});
		
		typeComboBox.setOnAction((event) -> {
			search();
		});
		
		dateComboBox.setOnAction((event) -> {
			search();
		});
		
		gradeComboBox.setOnAction((event) -> {
			search();
		});
		
		completedCheckBox.setOnAction((event) -> {
			search();
		});
		
		assignmentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Assignment>() {
			@Override
			public void changed(ObservableValue<? extends Assignment> observable, Assignment oldValue,
					Assignment newValue) {
				if(newValue != null){
					setDetailsForAssignment(newValue);
				}
				else{
					clearDetailsForAssignment();
				}
			}

			private void clearDetailsForAssignment() {
				detailsTextArea.setText("");
				nameTextField.setText("");
				classComboBox.setValue("");
				dateDatePicker.setValue(null);;
				gradeTextField.setText("");
				detailsCompletedCheckBox.setSelected(false);
				detailsTypeComboBox.setValue(Type.BLANK);
			}

			private void setDetailsForAssignment(Assignment newValue) {
				detailsTextArea.setText(newValue.getSpec().getInfo());
				nameTextField.setText(newValue.getSpec().getName());
				classComboBox.setValue(newValue.getSpec().getClassName());
				dateDatePicker.setValue(newValue.getSpec().getDate().toLocalDate());
				gradeTextField.setText(String.valueOf(newValue.getSpec().getGrade()));
				detailsCompletedCheckBox.setSelected(newValue.getSpec().isComplete());
				detailsTypeComboBox.setValue(newValue.getSpec().getType());
			}
		});
		
	}

	private void initComboBoxes() {
		setTypeComboBox();
		setDateComboBox();
		setGradeComboBox();	
		setCourseComboBox();
	}

	private void setTypeComboBox() {
		setTypeComboBox(detailsTypeComboBox);
		setTypeComboBox(typeComboBox);
	}

	private void setGradeComboBox() {
		gradeComboBox.getItems().addAll("", "A", "B", "C", "F");
		gradeComboBox.getSelectionModel().select(0);
	}

	private void setDateComboBox() {
		dateComboBox.getItems().addAll(DateOption.values());
		dateComboBox.getSelectionModel().select(0);
	}
	
	private void setTypeComboBox(ComboBox<Type> box){
		box.getItems().addAll(Type.values());
		box.getSelectionModel().select(0);
	}

	private void setCourseComboBox() {
		List<Course> courses = courseModel.getCourses();
		
		courseComboBox.getItems().clear();
		classComboBox.getItems().clear();
		
		courseComboBox.getItems().add("");
		for (int i = 0; i < courses.size(); i++) {
			courseComboBox.getItems().add(courses.get(i).getName());
			classComboBox.getItems().add(courses.get(i).getName());
		}
		courseComboBox.getSelectionModel().select(0);
	}
	
	private void populateListView(){
		ObservableList<Assignment> name = FXCollections.observableArrayList();
		name.addAll(assignmentModel.getAssignmentMasterList());
		
		assignmentListView.setCellFactory(new Callback<ListView<Assignment>, ListCell<Assignment>>() {
			
			@Override
			public ListCell<Assignment> call(ListView<Assignment> param) {
				ListCell<Assignment> assignment = new ListCell<Assignment>(){
					@Override
					protected void updateItem(Assignment item, boolean empty){
						super.updateItem(item, empty);
						if(item != null){
							
							GridPane gridPane = new GridPane();
							gridPane.setPadding(new Insets(10, 0, 0, 10));
							Label name = new Label(item.getSpec().getName());
							name.setStyle("");
							gridPane.add(name, 0, 0);
							GridPane.setHgrow(name, Priority.ALWAYS);
							Label grade = new Label(String.valueOf(item.getSpec().getGrade()));
							gridPane.add(grade, 1, 0);
							setGraphic(gridPane);
						}
						else{
							setGraphic(null);
						}
					}
				};
				return assignment;
			}
		});
		
		assignmentListView.setItems(name);
	}

	public void search() {
		String name = assignmentSearchTextField.getText();
		String className = courseComboBox.getValue();
		Type type = typeComboBox.getValue();
		DateOption dateOption = dateComboBox.getValue();
		String gradeOption = gradeComboBox.getValue();
		boolean completed = completedCheckBox.isSelected();
		
		AssignmentSpec spec = new AssignmentSpec(name, new Date(0), "", 0.0, completed, className, type);
		List<Assignment> searchAssignments = assignmentModel.search(spec, dateOption, gradeOption);
		
		assignmentListView.getItems().clear();
		assignmentListView.getItems().addAll(searchAssignments);
		assignmentListView.refresh();
	}
	
	@FXML
	void onUpdateButtonClick(ActionEvent event){
		Assignment assignment = updateSelectedItem();
		assignmentModel.updateAssignment(assignment);
		assignmentListView.refresh();
		mainController.update();
		updatedLabel.animate();
	}

	private Assignment updateSelectedItem() {
		Assignment assignment = assignmentListView.getSelectionModel().getSelectedItem();
		assignment.getSpec().setClassName(classComboBox.getSelectionModel().getSelectedItem());
		assignment.getSpec().setName(nameTextField.getText());
		assignment.getSpec().setComplete(detailsCompletedCheckBox.isSelected());
		assignment.getSpec().setInfo(detailsTextArea.getText());
		assignment.getSpec().setGrade(Double.valueOf(gradeTextField.getText()));
		assignment.getSpec().setType(detailsTypeComboBox.getSelectionModel().getSelectedItem());
		assignment.getSpec().setDate(dateDatePicker.getValue());
		return assignment;
	}
	
	@FXML
	void onDeleteButtonClick(ActionEvent event){
		if(assignmentListView.getSelectionModel().getSelectedItems().isEmpty()){
			Alert chooseAlert = new Alert(AlertType.WARNING, "Choose an assignment to delete");
			chooseAlert.setHeaderText("No Assignment Selected");
			chooseAlert.showAndWait();
		}
		else{
			try{
			String names = getSelectedItemsNames();
			
			List<Assignment> selectedItems = confirmDeleteSelectedItems(names);
			
			if(selectedItems != null)
				removeAssignments(selectedItems);
			
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private List<Assignment> confirmDeleteSelectedItems(String names) {
		Alert deleteAlert = new Alert(AlertType.CONFIRMATION, "Selected Items:" + names);
		deleteAlert.setHeaderText("Confirm Delete");
		Optional<ButtonType> resultDelete = deleteAlert.showAndWait();
		
		List<Assignment> selectedItems = assignmentListView.getSelectionModel().getSelectedItems();
		
		if (resultDelete.isPresent() && resultDelete.get() == ButtonType.OK){
			for (int i = 0; i < assignmentListView.getSelectionModel().getSelectedItems().size(); i++) {		
				Assignment ass = selectedItems.get(i);		
				assignmentModel.deleteAssignment(ass.getId());
				mainController.update();
			}
		}
		else
			selectedItems = null;
		return selectedItems;
	}

	private String getSelectedItemsNames() {
		String names = "\n";
		int selectedItemsCount = assignmentListView.getSelectionModel().getSelectedItems().size();
		for (int i = 0; i < selectedItemsCount; i++) {
			names += assignmentListView.getSelectionModel().getSelectedItems().get(i).getSpec().getName() + "\n";
		}
		return names;
	}
	
	private void removeAssignments(List<Assignment> selectedAssignments) throws ClassNotFoundException, SQLException{
		assignmentListView.getItems().removeAll(selectedAssignments);
		assignmentListView.refresh();
	}

	@FXML
	void onNewAssignmentButtonClick(ActionEvent event) throws IOException{
		final Stage dialog = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/views/insertAssignment.fxml"));
		AnchorPane frame = fxmlLoader.load();
		Scene scene = new Scene(frame);
		InsertAssignmentController controller = (InsertAssignmentController) fxmlLoader.getController();
		controller.mainController = mainController;
		dialog.setScene(scene);
		dialog.show();
	}
	
	public void update(){
		setCourseComboBox();
		assignmentListView.getItems().clear();
		assignmentListView.getItems().addAll(assignmentModel.getAssignmentMasterList());
		assignmentListView.refresh();
	}

	public void init(MainController mainController) {
		this.mainController = mainController;
		
	}

}
