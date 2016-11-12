package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AssignmentViewController implements Initializable{
	
	@FXML Button newAssignmentButton;
	@FXML Button assignmentSearchButton;
	@FXML Button updateButton;
	@FXML Button deleteButton;
	@FXML TextField assignmentSearchTextField;
	@FXML TextField nameTextField;
	@FXML TextField gradeTextField;
	@FXML DatePicker dateDatePicker;
	@FXML TextArea detailsTextArea;
	@FXML ComboBox<String> courseComboBox;
	@FXML ComboBox<String> dateComboBox;
	@FXML ComboBox<String> typeComboBox;
	@FXML ComboBox<String> detailsTypeComboBox;
	@FXML ComboBox<String> gradeComboBox;
	@FXML ComboBox<String> classComboBox;
	@FXML CheckBox completedCheckBox;
	@FXML CheckBox detailsCompletedCheckBox;
	@FXML ListView<Assignment> assignmentListView;
	private List<Assignment> assignments = new ArrayList<>();
	private List<Assignment> searchAssignments = new ArrayList<>();
	ObservableList<Assignment> name;
	private DataBase database;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		database = new DataBase();
		String[] types = {"", "Homework", "Quiz", "Lab","Test","Final","Paper","Discussion", "Project"};
		
		try {
			typeComboBox.getItems().addAll(types);
			typeComboBox.getSelectionModel().select(0);
		    detailsTypeComboBox.getItems().addAll(types);
		    detailsTypeComboBox.getSelectionModel().select(0);
		    dateComboBox.getItems().addAll("", "Upcoming", "Past", "Next 7 Days", "Next 30 Days");
		    dateComboBox.getSelectionModel().select(0);
		    gradeComboBox.getItems().addAll("", "A", "B", "C", "F");
		    gradeComboBox.getSelectionModel().select(0);
		    
			ResultSet rs = database.getCourses();
			courseComboBox.getItems().add("");
			while(rs.next()){
				courseComboBox.getItems().add(rs.getString("name"));
				classComboBox.getItems().add(rs.getString("name"));
			}
			
			courseComboBox.getSelectionModel().select(0);
			
			populateListView();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		assignmentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Assignment>() {
			@Override
			public void changed(ObservableValue<? extends Assignment> observable, Assignment oldValue,
					Assignment newValue) {
				if(newValue != null){
					detailsTextArea.setText(newValue.getInfo());
					nameTextField.setText(newValue.getName());
					classComboBox.setValue(newValue.getClassName());
					dateDatePicker.setValue(newValue.getDate().toLocalDate());
					gradeTextField.setText(String.valueOf(newValue.getGrade()));
					detailsCompletedCheckBox.setSelected(newValue.isComplete());
					detailsTypeComboBox.setValue(newValue.getType());
				}
				else{
					detailsTextArea.setText("");
					nameTextField.setText("");
					classComboBox.setValue("");
					dateDatePicker.setValue(null);;
					gradeTextField.setText("");
					detailsCompletedCheckBox.setSelected(false);
					detailsTypeComboBox.setValue("");
				}
			}
		});
		
	}
	
	private void populateListView() throws ClassNotFoundException, SQLException{
		name = FXCollections.observableArrayList();
		getAssignments();
		name.addAll(assignments);
		
		assignmentListView.setCellFactory(new Callback<ListView<Assignment>, ListCell<Assignment>>() {
			
			@Override
			public ListCell<Assignment> call(ListView<Assignment> param) {
				ListCell<Assignment> assignment = new<Assignment> ListCell(){
					@Override
					protected void updateItem(Object item, boolean empty){
						super.updateItem(item, empty);
						Assignment ass = (Assignment)item;
						if(ass != null){
							
							GridPane gridPane = new GridPane();
							gridPane.setPadding(new Insets(10, 0, 0, 10));
							Label name = new Label(ass.getName());
							name.setStyle("");
							gridPane.add(name, 0, 0);
							gridPane.setHgrow(name, Priority.ALWAYS);
							Label grade = new Label(String.valueOf(ass.getGrade()));
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

	private void getAssignments() throws ClassNotFoundException, SQLException {
		ResultSet rs = database.getAssignments();
		assignments.clear();
		while(rs.next()){
			Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
					rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"), rs.getInt("id"));
				assignments.add(assignment);
		}
	}
	
	@FXML
	void onAssignmentSearchButtonClick(ActionEvent event){
		
		try {
			getAssignments();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Assignments size: " + assignments.size());
		searchAssignments.clear();
		searchAssignments.addAll(assignments);
		assignmentListView.getItems().clear();
		
//		private void filterSearch(){
//			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
//				Assignment ass = iterator.next();
//				if(!assignmentSearchTextField.getText().equals("") && !assignmentSearchTextField.getText().equals(ass.getName())){
//					iterator.remove();
//				}
//				if(!courseComboBox.getValue().equals("") && !courseComboBox.getValue().equals(ass.getClassName())){
//					iterator.remove();
//				}
//				if(!typeComboBox.getValue().equals("") && !typeComboBox.getValue().equals(ass.getType())){
//					iterator.remove();
//				}
//				if(!dateComboBox.getValue().equals("") && !dateComboBox.getValue().equals(ass.getName())){
//					iterator.remove();
//				}
//			}
//		}
	
		if(!assignmentSearchTextField.getText().equals("")){
			System.out.println("Checking name");
			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
				Assignment ass = iterator.next();
				if(!ass.getName().equals(assignmentSearchTextField.getText())){
					System.out.println("removed");
					iterator.remove();
				}
			}
		}
		else{
			System.out.println("No name given");
		}
		
		if(!courseComboBox.getValue().equals("")){
			System.out.println("working");
			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
				Assignment ass = iterator.next();
				if(!ass.getClassName().equals(courseComboBox.getValue())){
					iterator.remove();
					System.out.println("removed ass");
				}
			}
		}
		
		if(!typeComboBox.getValue().equals("")){
			System.out.println("working");
			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
				Assignment ass = iterator.next();
				if(!ass.getType().equals(typeComboBox.getValue())){
					iterator.remove();
					System.out.println("removed ass");
				}
			}
		}
		
		if(!dateComboBox.getValue().equals("")){
			System.out.println("working");
			String choice = dateComboBox.getValue();
			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
				Assignment ass = iterator.next();
				if(choice.equals("Upcoming")){
					if(!(ass.getDaysUntil() >= 0))
						iterator.remove();
				}
				if(choice.equals("Past")){
					if(!(ass.getDaysUntil() < 0)){
						iterator.remove();
					}
				}
				if(choice.equals("Next 7 Days")){
					if(!(ass.getDaysUntil() <= 7 && ass.getDaysUntil() >= 0))
						iterator.remove();
				}
				if(choice.equals("Next 30 Days")){
					if(!(ass.getDaysUntil() <= 30 && ass.getDaysUntil() >= 0))
						iterator.remove();
				}
			}
		}
		
		if(!gradeComboBox.getValue().equals("")){
			String choice = gradeComboBox.getValue();
			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
				Assignment ass = iterator.next();
				if(choice.equals("A")){
					if(!(ass.getGrade() >= 90))
						iterator.remove();
				}
				if(choice.equals("B")){
					if(!(ass.getGrade() >= 80 && ass.getGrade() < 90)){
						iterator.remove();
					}
				}
				if(choice.equals("C")){
					if(!(ass.getGrade() >= 70 && ass.getGrade() < 80))
						iterator.remove();
				}
				if(choice.equals("F")){
					if(!(ass.getGrade() < 70))
						iterator.remove();
				}
			}
		}
		
		if(completedCheckBox.isSelected() == true){
			for(Iterator<Assignment> iterator = searchAssignments.iterator(); iterator.hasNext();){
				Assignment ass = iterator.next();
				if(ass.isComplete() == true){
					iterator.remove();
				}
			}
		}
		
		System.out.println("Done looking");
		System.out.println(searchAssignments.size());
		assignmentListView.getItems().addAll(searchAssignments);
		assignmentListView.refresh();
	}
	
	@FXML
	void onUpdateButtonClick(ActionEvent event){
		Assignment assignment = assignmentListView.getSelectionModel().getSelectedItem();
		assignment.setClassName(classComboBox.getSelectionModel().getSelectedItem());
		assignment.setName(nameTextField.getText());
		assignment.setComplete(detailsCompletedCheckBox.isSelected());
		assignment.setInfo(detailsTextArea.getText());
		assignment.setGrade(Double.valueOf(gradeTextField.getText()));
		assignment.setType(detailsTypeComboBox.getSelectionModel().getSelectedItem());
		assignment.setDate(dateDatePicker.getValue());
		
		assignmentListView.refresh();
		
		try {
			database.updateAssignment(assignment);
			System.out.println("Assignment Updated");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	void onDeleteButtonClick(ActionEvent event){
		Alert deleteAlert = new Alert(AlertType.CONFIRMATION, "Confirm Delete?");
		Alert chooseAlert = new Alert(AlertType.WARNING, "Choose an assignment to delete");
		chooseAlert.setHeaderText("No Assignment Selected");
		Assignment ass = assignmentListView.getSelectionModel().getSelectedItem();
		if(ass != null){
			Optional<ButtonType> resultDelete = deleteAlert.showAndWait();
			if (resultDelete.isPresent() && resultDelete.get() == ButtonType.OK) {
				try {
					database.deleteAssignment(ass.getId());
					updateAssignmentList();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			 }
		}
		else{
			chooseAlert.showAndWait();
		}
	}
	
	@FXML
	void onNewAssignmentButtonClick(ActionEvent event){
		displayNewAssignmentPopUp();
	}
	
	private void updateAssignmentList() throws ClassNotFoundException, SQLException{
		getAssignments();
		assignmentListView.getItems().remove(assignmentListView.getSelectionModel().getSelectedIndex());
		assignmentListView.refresh();
	}
	
	private void displayNewAssignmentPopUp(){
		  final Stage dialog = new Stage();
		  Scene scene;
		  FXMLLoader loader = new FXMLLoader();
		  loader.setLocation(Main.class.getResource("/practiceFxml.fxml"));
		  dialog.initModality(Modality.APPLICATION_MODAL);
		  try {
			AnchorPane root = (AnchorPane)loader.load();
			scene = new Scene(root);
			dialog.setScene(scene);
			dialog.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
	}

}
