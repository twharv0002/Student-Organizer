package application;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import assignment.Assignment;
import assignment.AssignmentModel;
import assignment.AssignmentSpec;
import assignment.Type;
import course.Course;
import course.CourseModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import schedule.ScheduleModel;

public class HomeTabController implements Initializable{
	
	private DataBase database;
	private ObservableList<AssignmentSpec> data = FXCollections.observableArrayList();
	private List<Assignment> assignments = new ArrayList<Assignment>();
	private List<TableCheckBox> checkBoxes = new ArrayList<>();
	private Agenda agenda;
	private ScheduleModel sh;
	private CourseModel ch;
	private AssignmentModel ah;
	
	private MainController mainController;

	@FXML ListView<Course> list;
	@FXML TableView<AssignmentSpec> table;
	@FXML Label displayedIssueLabel;
	@FXML TextArea descriptionValue;
	@FXML TableColumn<AssignmentSpec, String> columnName;
	@FXML TableColumn<AssignmentSpec, Date> columnDate;
	@FXML TableColumn<AssignmentSpec, String> columnCourse;
	@FXML TableColumn<AssignmentSpec, Integer> columnDaysUntil;
	@FXML TableColumn<AssignmentSpec, Boolean> columnCompleted;
	@FXML TextField startSearchTextField;
	@FXML Button startSearchButton;
	@FXML Button upcomingButton;
	@FXML Button allButton;
	@FXML Button refreshButton;
	@FXML CheckBox showCompleteRadioButton;
	@FXML AnchorPane homeTab;
	@FXML AnchorPane scheduleAnchorPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ah = new AssignmentModel();
		sh = new ScheduleModel();
		ch = new CourseModel();
		agenda = new Agenda();
		database = new DataBase();
		data = FXCollections.observableArrayList();
		
		
		try {
			list.setItems(ch.getCourses());
			initializeColumnCellFactories();
			populateAssignmentsTableView();
			initializeAgenda();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {

		    @Override
		    public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
		        
		        System.out.println("Selected item: " + newValue);
		    }
		});
	}
	
	private void initializeAgenda() throws ClassNotFoundException, SQLException{
		agenda.setAllowDragging(false);
		agenda.setAllowResize(false);
		agenda.setPrefHeight(scheduleAnchorPane.getPrefHeight());
		agenda.setPrefWidth(scheduleAnchorPane.getPrefWidth());
		agenda.setMaxHeight(Agenda.USE_COMPUTED_SIZE);
		agenda.setMaxWidth(Agenda.USE_COMPUTED_SIZE);
		AnchorPane.setBottomAnchor(agenda, 0.0);
		AnchorPane.setTopAnchor(agenda, 0.0);
		AnchorPane.setRightAnchor(agenda, 5.0);
		AnchorPane.setLeftAnchor(agenda, 5.0);
		agenda.appointments().addAll(sh.getAppointments());
		scheduleAnchorPane.getChildren().add(agenda);
	}

	private void populateAssignmentsTableView() throws ClassNotFoundException, SQLException {
		assignments = ah.getUpcomingAssignments();
		List<AssignmentSpec> specs = new ArrayList<>();
		for (Assignment assignment : assignments) {
			specs.add(assignment.getSpec());
		}
		data.addAll(specs);
		table.setItems(data);
		table.getColumns().setAll(columnName, columnCourse, columnDate, columnDaysUntil, columnCompleted);
		
	}

	private void initializeColumnCellFactories() {
		columnName.setCellValueFactory(new PropertyValueFactory<AssignmentSpec, String>("name"));
		columnDate.setCellValueFactory(new PropertyValueFactory<AssignmentSpec, Date>("date"));
		columnCourse.setCellValueFactory(new PropertyValueFactory<AssignmentSpec, String>("className"));
		columnDaysUntil.setCellValueFactory(new PropertyValueFactory<AssignmentSpec, Integer>("daysUntil"));
		columnCompleted.setCellValueFactory(new PropertyValueFactory<AssignmentSpec, Boolean>("Complete"));

		// Create a checkbox in the completed column
		columnCompleted.setCellFactory(new Callback<TableColumn<AssignmentSpec, Boolean>, TableCell<AssignmentSpec, Boolean>>() {
			@Override
			public TableCell<AssignmentSpec, Boolean> call(TableColumn<AssignmentSpec, Boolean> param){
				TableCell<AssignmentSpec, Boolean> cell = new TableCell<AssignmentSpec, Boolean>(){
					@Override
					protected void updateItem(Boolean item, boolean empty){
						if(item != null){
							super.updateItem(item, empty);
							TableCheckBox checkbox = new TableCheckBox(getTableRow().getIndex());
							checkbox.setSelected(item);
							setGraphic(checkbox);
							checkbox.setOnAction(eh);
							checkBoxes.add(checkbox);
						}
					}
				};
				return cell;
			}
		});
	}
	
	EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
	        if (event.getSource() instanceof CheckBox) {
	            TableCheckBox chk = (TableCheckBox) event.getSource();
	            boolean value = chk.isSelected();
	            AssignmentSpec ass = table.getItems().get(chk.getIndex());
	            try {
					database.updateCompleted(ass.getName(), value);
					ass.setComplete(value);
					if(value == true){
						table.getItems().remove(ass);
						table.getItems().remove(chk);
						table.refresh();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            System.out.println("Updated " + ass.getName() + " to " + value );
	        }
	    }
	};
	
	private void populateAssignmentsList(ResultSet resultSet) throws SQLException{
		assignments.clear();
		while(resultSet.next()){
			AssignmentSpec spec = new AssignmentSpec(resultSet.getString("name"), resultSet.getDate("date"), resultSet.getString("info"),
					resultSet.getDouble("grade"), resultSet.getBoolean("completed"), resultSet.getString("class"), Type.valueOf(resultSet.getString("type").toUpperCase()));
			Assignment assignment = new Assignment(resultSet.getInt("id"), spec);
			assignments.add(assignment);
		}
	}
	
	@FXML 
	void searchButtonOnClick(ActionEvent event){
		String query = startSearchTextField.getText().toLowerCase();
		try {
			populateAssignmentsList(database.sortAssignmentsBySearch(query));
			refreshTable();
			for (int i = 0; i < assignments.size(); i++) {
				if(showCompleteRadioButton.isSelected() == true){
					addNewTableItems(i);
				}
				if(showCompleteRadioButton.isSelected() == false && assignments.get(i).getSpec().isComplete() == false){
					addNewTableItems(i);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println();
			e.printStackTrace();
		}
		
		
	}
	
	@FXML
	void onUpcomingButtonClick(ActionEvent event){
		updateTable();
	}

	public void updateTable() {
		assignments.clear();
		assignments.addAll(ah.getUpcomingAssignments());
		refreshTable();
		for (int i = 0; i < assignments.size(); i++) {
			if(assignments.get(i).getSpec().getDaysUntil() >= 0){
				if(showCompleteRadioButton.isSelected() == true){
					addNewTableItems(i);
				}
				if(showCompleteRadioButton.isSelected() == false && assignments.get(i).getSpec().isComplete() == false){
					addNewTableItems(i);
				}
			}
		}
	}

	private void addNewTableItems(int i) {
		table.getItems().add(assignments.get(i).getSpec());
		table.refresh();
	}
	
	@FXML
	void onRefreshButtonClick(ActionEvent event){
		update();
	}

	public void update() {
		assignments.clear();
		ah.refresh();
		assignments.addAll(ah.getAssignmentMasterList());
		list.getItems().clear();
		list.setItems(ch.getCourses());
		agenda.appointments().clear();
		agenda.appointments().addAll(sh.getAppointments());
		updateTable();
	}
	
	@FXML
	void onAllButtonClick(ActionEvent event){
		try {
			populateAssignmentsList(database.sortAssignmentsByColumn("date", "ASC"));
			refreshTable();
			for (int i = 0; i < assignments.size(); i++) {
				if(showCompleteRadioButton.isSelected() == true){
					addNewTableItems(i);
				}
				if(showCompleteRadioButton.isSelected() == false && assignments.get(i).getSpec().isComplete() == false){
					addNewTableItems(i);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void refreshTable() {
		table.getItems().clear();
		table.refresh();
	}

	@FXML
	void onRadioButtonClick(ActionEvent event){
		refreshTable();
		if(showCompleteRadioButton.isSelected() == false){
			for (int i = 0; i < assignments.size(); i++) {
				if(assignments.get(i).getSpec().isComplete() == false){
					addNewTableItems(i);
				}
			}
		}
		else{
			for (int i = 0; i < assignments.size(); i++) {
				addNewTableItems(i);
			}
		}
	}

	public void init(MainController mainController) {
		this.mainController = mainController;
		
	}
	
	
}
