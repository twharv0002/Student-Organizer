package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.javafx.image.impl.ByteIndexed.Getter;

import javafx.beans.property.SimpleObjectProperty;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;

public class StartViewController implements Initializable{
	
	DataBase database = new DataBase();
	private ObservableList<Assignment> data = FXCollections.observableArrayList();
	ObservableList<Agenda.AppointmentImplLocal> dataSchedule;
	
	private List<Course> courseData = new ArrayList<>();
	private List<Assignment> assignments = new ArrayList<Assignment>();
	private List<Assignment> appointments = new ArrayList<Assignment>();
	private List<TableCheckBox> checkBoxes = new ArrayList<>();
	private Agenda agenda;
	
	@FXML ListView<String> list;
	@FXML TableView <Assignment>table;
	@FXML Label displayedIssueLabel;
	@FXML TextArea descriptionValue;
	@FXML TableColumn<Assignment, String> columnName;
	@FXML TableColumn<Assignment, Date> columnDate;
	@FXML TableColumn<Assignment, String> columnCourse;
	@FXML TableColumn<Assignment, Integer> columnDaysUntil;
	@FXML TableColumn<Assignment, Boolean> columnCompleted;
	@FXML TextField startSearchTextField;
	@FXML Button startSearchButton;
	@FXML Button upcomingButton;
	@FXML Button allButton;
	@FXML Button refreshButton;
	@FXML CheckBox showCompleteRadioButton;
	@FXML AnchorPane startPane;
	@FXML AnchorPane scheduleAnchorPane;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			populateCourseListView();
			populateAssignmentsTableView();
			populateSchedule();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void populateSchedule() throws ClassNotFoundException, SQLException{
		dataSchedule = FXCollections.observableArrayList();
		refreshSchedule();
		agenda = new Agenda();
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
		agenda.appointments().addAll(dataSchedule);
		scheduleAnchorPane.getChildren().add(agenda);
	}

	private void refreshSchedule() throws ClassNotFoundException, SQLException {
		
		dataSchedule.clear();
		appointments.clear();
		getAssignmentsSchedule();
		getEventsSchedule();
	}

	private void getEventsSchedule() throws ClassNotFoundException, SQLException {
		
		ResultSet rs = database.getAppointments();
		
		while(rs.next()){
			dataSchedule.add((ModdedAppointment) new ModdedAppointment(rs.getInt("id")).withStartLocalDateTime(LocalDateTime.parse(rs.getString("starttime")))
					.withEndLocalDateTime(LocalDateTime.parse(rs.getString("endtime")))
					.withDescription(rs.getString("description"))
					.withSummary(rs.getString("summary"))
					.withWholeDay(rs.getBoolean("wholeday"))
					.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(rs.getString("style"))));
		}
	}

	private void getAssignmentsSchedule() throws ClassNotFoundException, SQLException {
		ResultSet rs = database.getAssignments();
		
		while(rs.next()){
			Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
					rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"), rs.getInt("id"));
			appointments.add(assignment);
		}
		
		for (int i = 0; i < appointments.size(); i++) {
			
			dataSchedule.add(new Agenda.AppointmentImplLocal().withStartLocalDateTime(appointments.get(i).getDate().toLocalDate().atTime(12, 00))
					.withEndLocalDateTime(appointments.get(i).getDate().toLocalDate().atTime(13, 00))
					.withDescription(appointments.get(i).getInfo())
					.withSummary(appointments.get(i).getName())
					.withWholeDay(true)
					.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(appointments.get(i).getType())));
		}
	}


	private void populateAssignmentsTableView() throws ClassNotFoundException, SQLException {
		data = FXCollections.observableArrayList();
		ResultSet rs = database.sortAssignmentsByColumn("date", "ASC");
		
		while(rs.next()){
			Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
					rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"));
			if(assignment.getDaysUntil() >= 0)
				assignments.add(assignment);
		}
		//data.addAll(assignments);
		for (int i = 0; i < assignments.size(); i++) {
			if(assignments.get(i).isComplete() == false)
				data.add(assignments.get(i));
		}
		
		columnName.setCellValueFactory(new PropertyValueFactory<Assignment, String>("name"));
		columnDate.setCellValueFactory(new PropertyValueFactory<Assignment, Date>("date"));
		columnCourse.setCellValueFactory(new PropertyValueFactory<Assignment, String>("className"));
		columnDaysUntil.setCellValueFactory(new PropertyValueFactory<Assignment, Integer>("daysUntil"));
		//setDaysUntilColor();
		columnCompleted.setCellValueFactory(new PropertyValueFactory<Assignment, Boolean>("Complete"));

		// Create a checkbox in the completed column
		columnCompleted.setCellFactory(new Callback<TableColumn<Assignment,Boolean>, TableCell<Assignment,Boolean>>() {
			@Override
			public TableCell<Assignment, Boolean> call(TableColumn<Assignment, Boolean> param){
				TableCell<Assignment, Boolean> cell = new TableCell<Assignment, Boolean>(){
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
		
		table.setItems(data);
		table.getColumns().setAll(columnName, columnCourse, columnDate, columnDaysUntil, columnCompleted);
		
	}
	
	EventHandler<ActionEvent> eh = new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
	        if (event.getSource() instanceof CheckBox) {
	            TableCheckBox chk = (TableCheckBox) event.getSource();
	            boolean value = chk.isSelected();
	            List<Assignment> assigns = table.getItems();
	            Assignment ass = assigns.get(chk.getIndex());
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

	private void setDaysUntilColor() {
		columnDaysUntil.setCellFactory(new Callback<TableColumn<Assignment, Integer>, TableCell<Assignment, Integer>>(){
			@Override
			public TableCell<Assignment, Integer> call(TableColumn<Assignment, Integer> param){
				return new TableCell<Assignment, Integer>(){
					@Override
					public void updateItem(Integer item, boolean empty){
						super.updateItem(item, empty);
						//Assignment assignment = (Assignment)getTableRow().getItem();
						if (!isEmpty()) {
							Color color = null;
							int maxColor = 255;
	                        if(item >= 0 && item  <= 7 ){//&& !assignment.isComplete()){
	                        	if(item == 0 ){
	                        		color = Color.rgb(maxColor/1, maxColor - (maxColor/1), 36);
	                        	}
	                        	else{
	                        		color = Color.rgb(maxColor/item, maxColor - (maxColor/item), 36);
	                        	}
	                        	this.setTextFill(color);
	                        	//this.setId("LateCell");    
	                        }
							
							setText(String.valueOf(item));
					
	                    }
					}
				};
			}
		});
	}


	private void populateCourseListView() throws ClassNotFoundException, SQLException {
		ObservableList<String> name = FXCollections.observableArrayList();
		ResultSet rs = database.getCourses();
		
		while(rs.next()){
			Course course = new Course(rs.getString("instructor"), rs.getString("name"), rs.getInt("roomNumber"),
					rs.getInt("absences"), rs.getDouble("finalGrade"), rs.getString("time"));
			courseData.add(course);
		}
		
		for (int i = 0; i < courseData.size(); i++) {
			name.add(courseData.get(i).getName());
		}
		
		list.setItems(name);
	}
	
	private void populateAssignmentsList(ResultSet resultSet) throws SQLException{
		assignments.clear();
		ResultSet rs = resultSet;
		while(rs.next()){
			Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
					rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"));
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
				if(showCompleteRadioButton.isSelected() == false && assignments.get(i).isComplete() == false){
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
		assignments.clear();
		ResultSet rs = null;
		try {
			rs = database.sortAssignmentsByColumn("date", "ASC");
			while(rs.next()){
				Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
						rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"));
				if(assignment.getDaysUntil() >= 0)
					assignments.add(assignment);
			}
			
			refreshTable();
			for (int i = 0; i < assignments.size(); i++) {
				if(assignments.get(i).getDaysUntil() >= 0){
					if(showCompleteRadioButton.isSelected() == true){
						addNewTableItems(i);
					}
					if(showCompleteRadioButton.isSelected() == false && assignments.get(i).isComplete() == false){
						addNewTableItems(i);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addNewTableItems(int i) {
		table.getItems().add(assignments.get(i));
		table.refresh();
	}
	
	@FXML
	void onRefreshButtonClick(ActionEvent event){
		try {
			refreshSchedule();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		agenda.appointments().clear();
		agenda.appointments().addAll(dataSchedule);
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
				if(showCompleteRadioButton.isSelected() == false && assignments.get(i).isComplete() == false){
					addNewTableItems(i);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void refreshTable() {
		table.getItems().clear();
		table.refresh();
	}

	@FXML
	void onRadioButtonClick(ActionEvent event){
		refreshTable();
		if(showCompleteRadioButton.isSelected() == false){
			for (int i = 0; i < assignments.size(); i++) {
				if(assignments.get(i).isComplete() == false){
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
	
	
}
