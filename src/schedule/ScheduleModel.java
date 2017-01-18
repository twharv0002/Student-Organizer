package schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import application.DataBase;
import application.ModdedAppointment;
import assignment.Assignment;
import assignment.AssignmentModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfxtras.scene.control.agenda.Agenda;

public class ScheduleModel {

	private ObservableList<Agenda.AppointmentImplLocal> dataSchedule;
	private AssignmentModel assignmentModel;
	private DataBase database;
	
	public ScheduleModel(){
		dataSchedule = FXCollections.observableArrayList();
		assignmentModel = new AssignmentModel();
		database = new DataBase();
	}
	
	public ObservableList<Agenda.AppointmentImplLocal> getAppointments(){
		dataSchedule.clear();
		try {
			getAssignmentsFromDB();
			getAppointmentsFromDB();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataSchedule;
	}
	
	private void getAssignmentsFromDB() throws ClassNotFoundException, SQLException{
		List<Assignment> assignments = assignmentModel.getAssignmentMasterList();
		for (int i = 0; i < assignments.size(); i++) {
			
			dataSchedule.add(new Agenda.AppointmentImplLocal().withStartLocalDateTime(assignments.get(i).getSpec().getDate().toLocalDate().atTime(12, 00))
					.withEndLocalDateTime(assignments.get(i).getSpec().getDate().toLocalDate().atTime(13, 00))
					.withDescription(assignments.get(i).getSpec().getInfo())
					.withSummary(assignments.get(i).getSpec().getName())
					.withWholeDay(true)
					.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass(assignments.get(i).getSpec().getType().toString())));
		}
		
	}
	
	private void getAppointmentsFromDB() throws ClassNotFoundException, SQLException{
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
	
}
