package schedule;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.DataBase;
import application.MainController;
import application.ModdedAppointment;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;

public class ScheduleTabController implements Initializable {
	
	@FXML AnchorPane scheduleTabPage;
	@FXML HBox scheduleHbox;
	@FXML AnchorPane scheduleAnchorPane;
	@FXML AnchorPane scheduleAPU;
	@FXML Button refreshButton;
	@FXML Button saveButton;
	@FXML DatePicker displayDatePicker;
	
	private MainController mainController;
	private Agenda agenda;
	private ScheduleModel scheduleModel;
	private DataBase database;
	private List<ModdedAppointment> updatedAppointments = new ArrayList<>();
	private int newAppts;
	private Timeline timeline;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		scheduleModel = new ScheduleModel();
		timeline = new Timeline();
		displayDatePicker.setValue(LocalDate.now());
		database = new DataBase();
		newAppts = 0;
		
		agenda = new Agenda();
		AgendaSkinSwitcher skinSwitcher = new AgendaSkinSwitcher(agenda);
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

		agenda.appointments().addAll(scheduleModel.getAppointments());
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
			
			deleteAlert.setContentText(content);
			if(myAppointment != null){
				deleteAlert.showAndWait();
			}
		    return null;
		});
		
		// On Change listener
		agenda.setAppointmentChangedCallback((Agenda.Appointment myAppointment) -> {
			if(!updatedAppointments.contains(myAppointment)){
				ModdedAppointment appt = (ModdedAppointment) myAppointment;
				updatedAppointments.add(appt);
				startAnimateButton();
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
		update();
	}

	public void update() {
		agenda.appointments().clear();
		agenda.appointments().addAll(scheduleModel.getAppointments());
		stopAnimateButton();
	}
	
	@FXML 
	void onSaveButtonClick(ActionEvent event) throws ClassNotFoundException, SQLException{
		int index = agenda.appointments().size() - newAppts;
		System.out.println(newAppts);
		for (int i = index; i < index + newAppts; i++) {
			database.insertAppointment(agenda.appointments().get(i));
			System.out.println("Adding new Appointment");
		}
		for (int i = 0; i < updatedAppointments.size(); i++) {
			database.updateAppointment(updatedAppointments.get(i));
			System.out.println("Updating Appointments");
		}
		mainController.update();
		stopAnimateButton();
	}

	public void init(MainController mainController) {
		this.mainController = mainController;
	}
	
}
