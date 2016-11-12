package application;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.effect.Glow;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import jfxtras.internal.scene.control.skin.agenda.AgendaMonthSkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentImplLocal;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;

public class ScheduleViewController implements Initializable {
	
	@FXML AnchorPane scheduleTabPage;
	@FXML HBox scheduleHbox;
	@FXML AnchorPane scheduleAnchorPane;
	@FXML AnchorPane scheduleAPU;
	@FXML Button refreshButton;
	@FXML Button saveButton;
	@FXML DatePicker displayDatePicker;
	
	private Agenda agenda;
	private AgendaSkinSwitcher skinSwitcher;
	private DataBase database;
	private List<Assignment> assignments = new ArrayList<>();
	private List<ModdedAppointment> appointments = new ArrayList<>();
	private List<ModdedAppointment> updatedAppointments = new ArrayList<>();
	private int newAppts;
	private ObservableList<Agenda.AppointmentImplLocal> data;
	private Timeline timeline;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		timeline = new Timeline();
		displayDatePicker.setValue(LocalDate.now());
		data = FXCollections.observableArrayList();
		database = new DataBase();
		newAppts = 0;
		
		try {
			getDatabaseAssignments();
			addAppointmentsToData();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		agenda = new Agenda();
		skinSwitcher = new AgendaSkinSwitcher(agenda);
		AnchorPane.setLeftAnchor(skinSwitcher, 20.0);
		AnchorPane.setTopAnchor(skinSwitcher, 20.0);
		agenda.setPrefHeight(scheduleAnchorPane.getPrefHeight());
		agenda.setPrefWidth(scheduleAnchorPane.getPrefWidth());
		agenda.setMaxHeight(Agenda.USE_COMPUTED_SIZE);
		agenda.setMaxWidth(Agenda.USE_COMPUTED_SIZE);
		AnchorPane.setBottomAnchor(agenda, 0.0);
		AnchorPane.setTopAnchor(agenda, 0.0);
		AnchorPane.setRightAnchor(agenda, 5.0);
		AnchorPane.setLeftAnchor(agenda, 5.0);

		agenda.appointments().addAll(data);
		scheduleAPU.getChildren().add(skinSwitcher);
		scheduleAnchorPane.getChildren().add(agenda);
		
		// Create new Appointments by click and drag
		agenda.newAppointmentCallbackProperty().set( (localDateTimeRange) -> {
			newAppts++;
			startAnimateButton();
	          return new ModdedAppointment(0)
	                  .withStartLocalDateTime(localDateTimeRange.getStartLocalDateTime())
	                  .withEndLocalDateTime(localDateTimeRange.getEndLocalDateTime())
	                  .withDescription("No Description")
	                  .withSummary("No Summary")
	                  .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group7"));
	      });
		
		// On double click action
		agenda.setActionCallback((Agenda.Appointment myAppointment) -> {
			String description = myAppointment.getDescription();
			String summary = myAppointment.getSummary();
			String content = "Summary: " + summary + "\nDescription: " 
			+ description;
			
			Alert deleteAlert = new Alert(AlertType.INFORMATION);
			deleteAlert.setTitle("");
			deleteAlert.setGraphic(null);
			deleteAlert.setHeaderText("Event Information");
			if(!myAppointment.isWholeDay()){
				content += "\nTime: " + myAppointment.getStartLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_TIME) + " - " + myAppointment.getEndLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
			}
			// This section explains how to change the color of an appointment
//			System.out.println(myAppointment.getAppointmentGroup().getStyleClass());
//			myAppointment.setAppointmentGroup(agenda.appointmentGroups().get(0));
//			agenda.refresh();
			deleteAlert.setContentText(content);
			if(myAppointment != null){
				deleteAlert.showAndWait();
			}
		    return null;
		});
		
		// On Change listener
		agenda.setAppointmentChangedCallback((Agenda.Appointment myAppointment) -> {
			if(!updatedAppointments.contains(myAppointment)){
				updatedAppointments.add((ModdedAppointment) myAppointment);
				System.out.println("ID: " + updatedAppointments.get(0).getId());
				System.out.println(myAppointment.getSummary());
				System.out.println();
				startAnimateButton();
				System.out.println(updatedAppointments.size());
				System.out.println(appointments.size());
			}
			return null;
		});
		
		displayDatePicker.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
		    {
		        if (newPropertyValue)
		        {
		            System.out.println("Textfield on focus");
		        }
		        else
		        {
		        	LocalDate ld = displayDatePicker.getValue();
		        	LocalTime lt = LocalTime.now();
		            agenda.setDisplayedLocalDateTime(LocalDateTime.of(ld, lt));
		        }
		    }
		});
		
	}

	private void startAnimateButton() {
		saveButton.setStyle("-fx-background-color: darkcyan");
		saveButton.setTextFill(Color.WHITE);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		final KeyValue kv = new KeyValue(saveButton.opacityProperty(), 0.5, Interpolator.EASE_BOTH);
		final KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
		timeline.getKeyFrames().add(kf);
		timeline.play();
	}
	
	private void stopAnimateButton() {
		timeline.stop();
		saveButton.setOpacity(1);
		saveButton.setStyle("-fx-background-color: gray");
		saveButton.setTextFill(Color.BLACK);
		updatedAppointments.clear();
		newAppts = 0;
	}

	@FXML
	void onRefreshButtonClick(ActionEvent event) throws ClassNotFoundException, SQLException{
		data.clear();
		assignments.clear();
		appointments.clear();
		
		getDatabaseAssignments();
		addAppointmentsToData();
		
		agenda.appointments().clear();
		agenda.appointments().addAll(data);
		stopAnimateButton();
		System.out.println(newAppts);
		System.out.println(agenda.appointments().size());
	}
	
	@FXML 
	void onSaveButtonClick(ActionEvent event) throws ClassNotFoundException, SQLException{
		int index = agenda.appointments().size() - newAppts;
		System.out.println(newAppts);
		for (int i = index; i < index + newAppts ; i++) {
			database.insertAppointment(agenda.appointments().get(i));
			System.out.println("Adding new Appointment");
		}
		for (int i = 0; i < updatedAppointments.size(); i++) {
			database.updateAppointment(updatedAppointments.get(i));
			System.out.println("Updating Appointments");
		}
		stopAnimateButton();
	}
	
	private void getDatabaseAssignments() throws ClassNotFoundException, SQLException {
		ResultSet rs = database.getAssignments();
		
		while(rs.next()){
			Assignment assignment = new Assignment(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
					rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), rs.getString("type"), rs.getInt("id"));
				assignments.add(assignment);
		}
	}
	
	private void getDatabaseAppointments() throws SQLException, ClassNotFoundException{
		ResultSet rs = database.getAppointments();
		
		while(rs.next()){
			appointments.add((ModdedAppointment) new ModdedAppointment(rs.getInt("id")).withStartLocalDateTime(LocalDateTime.parse(rs.getString("starttime")))
					.withEndLocalDateTime(LocalDateTime.parse(rs.getString("endtime")))
					.withDescription(rs.getString("description"))
					.withSummary(rs.getString("summary"))
					.withWholeDay(rs.getBoolean("wholeday"))
					.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(rs.getString("style"))));
		}
	}
	// Test for GitHub on the small-features branch
	private void addAppointmentsToData() throws ClassNotFoundException, SQLException {
		for (int i = 0; i < assignments.size(); i++) {
			
			data.add(new Agenda.AppointmentImplLocal().withStartLocalDateTime(assignments.get(i).getDate().toLocalDate().atTime(12, 00))
					.withEndLocalDateTime(assignments.get(i).getDate().toLocalDate().atTime(13, 00))
					.withDescription((assignments.get(i).getInfo().equals("") ? "No Description" : assignments.get(i).getInfo()))
					.withSummary(assignments.get(i).getName())
					.withWholeDay(true)
					.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(assignments.get(i).getType())));
		}
		getDatabaseAppointments();
		data.addAll(appointments);
		
	}
	
}
