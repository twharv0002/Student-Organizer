package assignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.DataBase;

public class AssignmentModel {

	private List<Assignment> assignmentMasterList;
	private DataBase database;
	
	public AssignmentModel(){
		database = new DataBase();
		assignmentMasterList = new ArrayList<>();
		getAssignmentsFromDB();
	}
	
	private void getAssignmentsFromDB(){
		try {
			ResultSet rs = database.sortAssignmentsByColumn("date", "ASC");
			
			while(rs.next()){
				
				AssignmentSpec spec = new AssignmentSpec(rs.getString("name"), rs.getDate("date"), rs.getString("info"),
						rs.getDouble("grade"), rs.getBoolean("completed"), rs.getString("class"), Type.valueOf(rs.getString("type").toUpperCase()));
				Assignment assignment = new Assignment(rs.getInt("id"), spec);
				assignmentMasterList.add(assignment);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Assignment> search(AssignmentSpec searchSpec, DateOption dateOption,
			String gradeOption){
		
		List<Assignment> searchAssignments = new ArrayList<>();
		
		for(Iterator<Assignment> iterator = assignmentMasterList.iterator(); iterator.hasNext();){
			Assignment assignment = iterator.next();
			if(assignment.getSpec().matches(searchSpec, dateOption, gradeOption)){
				searchAssignments.add(assignment);
			}
		}
		
		refresh();
		return searchAssignments;
	}
	
	public void refresh(){
		assignmentMasterList.clear();
		getAssignmentsFromDB();
	}
	
	public List<Assignment> getAssignmentMasterList(){
		refresh();
		return assignmentMasterList;
	}
	
	public List<Assignment> getUpcomingAssignments(){
		List<Assignment> upcomingList = new ArrayList<>();
		for (int i = 0; i < assignmentMasterList.size(); i++) {
			if(assignmentMasterList.get(i).getSpec().getDaysUntil() >= 0){
				upcomingList.add(assignmentMasterList.get(i));
			}
		}
		return upcomingList;
	}
	
	public List<Assignment> getAssignmentsFromCourse(String course){
		List<Assignment> assignmentsFromCourse = new ArrayList<>();
		for (int i = 0; i < assignmentMasterList.size(); i++) {
			if(assignmentMasterList.get(i).getSpec().getClassName().equals(course)){
				assignmentsFromCourse.add(assignmentMasterList.get(i));
			}
		}
		return assignmentsFromCourse;
	}

	public void updateAssignment(Assignment assignment) {
		try {
			database.updateAssignment(assignment);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	} 
	
	public void deleteAssignment(int id){
		try {
			database.deleteAssignment(id);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
