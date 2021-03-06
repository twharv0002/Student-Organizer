package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import assignment.Assignment;
import course.Course;
import jfxtras.scene.control.agenda.Agenda.Appointment;

public class DataBase {
	
	private static Connection con;
	private static boolean hasData = false;
	
	// Establish connection with the database
	public void getConnection() throws SQLException, ClassNotFoundException {

		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:StudentOrganizer.db");
		initialize();
	}
	
	public void close(){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Checks if table exists, creates otherwise
	// Takes name of table, and the sql query to create a table
	private void createTable(String tableName, String createTableQuery) throws SQLException{
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';");
		if(!res.next()){
			System.out.println("Building the " + tableName + " table");
			Statement state2 = con.createStatement();
			state2.execute(createTableQuery);
		}
	}
	
	// Check if database and associated tables exists, create them otherwise
	private void initialize() throws SQLException {
		if(!hasData){
			hasData = true;
		
			createTable("assignments", "CREATE TABLE assignments(id integer,"
					+ "name varchar(60)," + "date datetime,"
					+ "grade double(0.0),"+ "completed bool(0)," 
					+ "info varchar(1000)," + "class varchar(60)," + "type varchar(60),"+ "primary key(id));");
			
			createTable("courses", "CREATE TABLE courses(id integer,"
					+ "name varchar(60)," + "instructor varchar(60),"
					+ "time varchar(60)," + "absences integer,"
					+ "roomNumber integer," + "finalGrade double,"
					+ "primary key(id));");
			
			createTable("weights", "CREATE TABLE weights(id integer,"
					+ "name varchar(60)," + "homework double,"
					+ "quiz double," + "lab double,"
					+ "test double," + "final double,"
					+ "paper double," + "discussion double,"
					+ "project double," + "attendance double,"
					+ "participation double," + "primary key(id));");
			
			createTable("appointments", "CREATE TABLE appointments(id integer,"
					+ "summary varchar(60)," + "description varchar(200),"
					+ "starttime varchar(60)," + "endtime varchar(60),"
					+ "style varchar(60)," + "wholeday bool(0),"
					+ "primary key(id));");
		}
	}
	// Method to check if course is in the database
	public boolean hasCourse(String course) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		ResultSet rs = getCourse(course);
		if(rs.next()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public ResultSet getCourse(String course) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet rs = state.executeQuery("SELECT * FROM courses WHERE name='" + course + "'");
		return rs;
	}
	
	// Insert and get methods for assignments
	public void insertAssignment(Assignment assignment) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement prep = con.prepareStatement("INSERT INTO assignments values (?,?,?,?,?,?,?,?);");
		prep.setString(2, assignment.getSpec().getName());
		prep.setDate(3, assignment.getSpec().getDate());
		prep.setDouble(4, assignment.getSpec().getGrade());
		prep.setBoolean(5, assignment.getSpec().isComplete());
		prep.setString(6, assignment.getSpec().getInfo());
		prep.setString(7, assignment.getSpec().getClassName());
		prep.setString(8, assignment.getSpec().getType().toString());
		prep.execute();
		
	}
	
	public ResultSet getAssignments() throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT * FROM assignments");
		return res;
	}
	
	public ResultSet getTypeWeightByCourse(String course) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT * FROM weights WHERE name='" + course + "'");
		return res;
	}
	
	// Insert and get methods for courses
	public void insertCourse(Course course) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement prep = con.prepareStatement("INSERT INTO courses values (?,?,?,?,?,?,?);");
		prep.setString(2, (String)course.getData().getProperty("name"));
		prep.setString(3, (String)course.getData().getProperty("instructor"));
		prep.setString(4, (String)course.getData().getProperty("time"));
		prep.setInt(5, (int)course.getData().getProperty("absences"));
		prep.setInt(6, (int)course.getData().getProperty("roomNumber"));
		prep.setDouble(7, (double)course.getData().getProperty("finalGrade"));
		prep.execute();
	}
	
	public ResultSet getCourses() throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT * FROM courses");
		return res;
	}
	// Insert and get methods for weights - Inserts weight into the appropriate type column
	public void insertWeights(List<Double> weights, String course) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement statement = con.prepareStatement("INSERT INTO weights values(?,?,?,?,?,?,?,?,?,?,?,?);");
		statement.setString(2, course);
		statement.setDouble(3, weights.get(0));
		statement.setDouble(4, weights.get(1));
		statement.setDouble(5, weights.get(2));
		statement.setDouble(6, weights.get(3));
		statement.setDouble(7, weights.get(4));
		statement.setDouble(8, weights.get(5));
		statement.setDouble(9, weights.get(6));
		statement.setDouble(10, weights.get(7));
		statement.setDouble(11, weights.get(8));
		statement.setDouble(12, weights.get(9));
		statement.executeUpdate();
	}
	
	// Insert and get methods for weights - Inserts weight into the appropriate type column
		public void insertWeights(Course course) throws SQLException, ClassNotFoundException{
			if(con == null){
				getConnection();
			}
			
			PreparedStatement statement = con.prepareStatement("INSERT INTO weights values(?,?,?,?,?,?,?,?,?,?,?,?);");
			statement.setString(2, (String)course.getData().getProperty("name"));
			statement.setDouble(3, (double)course.getWeights().get("homework"));
			statement.setDouble(4, (double)course.getWeights().get("quiz"));
			statement.setDouble(5, (double)course.getWeights().get("lab"));
			statement.setDouble(6, (double)course.getWeights().get("test"));
			statement.setDouble(7, (double)course.getWeights().get("final"));
			statement.setDouble(8, (double)course.getWeights().get("paper"));
			statement.setDouble(9, (double)course.getWeights().get("discussion"));
			statement.setDouble(10, (double)course.getWeights().get("project"));
			statement.setDouble(11, (double)course.getWeights().get("attendance"));
			statement.setDouble(12, (double)course.getWeights().get("participation"));
			statement.executeUpdate();
		}
	
	public ResultSet getWeights() throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT * FROM weights");
		return res;
	}
	
	public void insertAppointment(Appointment appt) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}

		PreparedStatement prep = con.prepareStatement("INSERT INTO appointments values (?,?,?,?,?,?,?);");
		prep.setString(2, appt.getSummary());
		prep.setString(3, appt.getDescription());
		prep.setString(4, appt.getStartLocalDateTime().toString() );
		prep.setString(5, (appt.getEndLocalDateTime().equals(null)) 
				? appt.getStartLocalDateTime().toString() : appt.getEndLocalDateTime().toString());
		prep.setString(6, appt.getAppointmentGroup().getStyleClass());
		prep.setBoolean(7, appt.isWholeDay());
		prep.execute();
	}
	
	public ResultSet getAppointments() throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT * FROM appointments");
		return res;
	}
	
	// Sorting methods
	public ResultSet sortAssignmentsBySearch(String query) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		String[] columns = {"name", "class", "grade", "completed", "date", "type"};
		Statement state = con.createStatement();
		ResultSet rs = null;
		for (int i = 0; i < columns.length; i++) {
			String sql = "SELECT * FROM assignments WHERE " + columns[i] + "='" + query + "' COLLATE NOCASE;";
			rs = state.executeQuery(sql);
			System.out.println(sql);
			if(rs.next()){
				System.out.println("Found");
				rs = state.executeQuery(sql);
				break;
			}
		}
		return rs;
	}
	
	
	public ResultSet sortAssignmentsByColumn(String sortValue, String sortBy) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "SELECT * FROM assignments ORDER BY " + sortValue + " " + sortBy;
		ResultSet rs = state.executeQuery(sql);
		
		return rs;
	}
	public ResultSet sortAssignmentsByColumnAndDate(String sortValue, String sortBy) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "SELECT * FROM assignments WHERE completed='0' ORDER BY " + sortValue + " " + sortBy;
		ResultSet rs = state.executeQuery(sql);
		
		return rs;
	}
	//---------------
	// Update methods
	//---------------
	
	public void updateAssignment(Assignment assignment) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement prep = con.prepareStatement("UPDATE assignments SET name=?, date=?, grade=?,"
				+ " completed=?, info=?, class=?, type=? WHERE id=?;");
		prep.setString(1, assignment.getSpec().getName());
		prep.setDate(2, assignment.getSpec().getDate());
		prep.setDouble(3, assignment.getSpec().getGrade());
		prep.setBoolean(4, assignment.getSpec().isComplete());
		prep.setString(5, assignment.getSpec().getInfo());
		prep.setString(6, assignment.getSpec().getClassName());
		prep.setString(7, assignment.getSpec().getType().toString());
		prep.setInt(8, assignment.getId());
		prep.execute();
 	}
	
	public void updateAppointment(ModdedAppointment appointment) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement prep = con.prepareStatement("UPDATE appointments SET summary=?, description=?, starttime=?,"
				+ " endtime=?, style=?, wholeday=? WHERE id=?;");
		prep.setString(1, appointment.getSummary());
		prep.setString(2, appointment.getDescription());
		prep.setString(3, appointment.getStartLocalDateTime().toString());
		prep.setString(4, (appointment.getEndLocalDateTime().equals(null))
				? appointment.getStartLocalDateTime().toString() : appointment.getEndLocalDateTime().toString());
		prep.setString(5, appointment.getAppointmentGroup().getStyleClass());
		prep.setBoolean(6, appointment.isWholeDay());
		prep.setInt(7, appointment.getId());
		prep.execute();
	}
	
	public void updateCourse(Course course) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement prep = con.prepareStatement("UPDATE courses SET name=?, instructor=?, time=?,"
				+ " absences=?, roomNumber=?, finalGrade=? WHERE id=?;");
		prep.setString(1, (String)course.getData().getProperty("name"));
		prep.setString(2, (String)course.getData().getProperty("instructor"));
		prep.setString(3, (String)course.getData().getProperty("time"));
		prep.setInt(4, (int)course.getData().getProperty("absences"));
		prep.setInt(5, (int)course.getData().getProperty("roomNumber"));
		prep.setDouble(6, (double)course.getData().getProperty("finalGrade"));
		prep.setInt(7, course.getId());
		prep.execute();
	}
	
	public void updateWeightCourse(String oldName, String newName) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "UPDATE weights SET name = '" + newName + "' WHERE name=" + "'" + oldName + "'";
		state.executeUpdate(sql);
	}
	
	public void updateWeightByCourse(Map<String, Double> weights, String course) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		PreparedStatement prep = con.prepareStatement("UPDATE weights SET homework=?, quiz=?,"
				+ " lab=?, test=?, final=?, paper=?, discussion=?, project=?, attendance=? WHERE name=?;");
		prep.setDouble(1, weights.get("homework"));
		prep.setDouble(2, weights.get("quiz"));
		prep.setDouble(3, weights.get("lab"));
		prep.setDouble(4, weights.get("test"));
		prep.setDouble(5, weights.get("final"));
		prep.setDouble(6, weights.get("paper"));
		prep.setDouble(7, weights.get("discussion"));
		prep.setDouble(8, weights.get("project"));
		prep.setDouble(9, weights.get("attendance"));
		prep.setString(10, course);
		prep.execute();
		System.out.println("Executed");
	}
	
	public void updateGrade(String name, double grade) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "UPDATE assignments SET grade = " + grade + " WHERE name=" + "'" + name + "'";
		state.executeUpdate(sql);
	}
	
	public void updateCompleted(String name, boolean value) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		int num = 0;
		if(value == true){
			num = 1;
		}
		
		Statement state = con.createStatement();
		String sql = "UPDATE assignments SET completed = '" + num + "' WHERE name=" + "'" + name + "'";
		state.executeUpdate(sql);
	}
	
	// Delete methods
	public void deleteCourse(String name) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "DELETE FROM courses WHERE name='" + name + "'";
		state.executeUpdate(sql);
		System.out.println(name + " was deleted from courses");
	}
	
	public void deleteWeight(String name) throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "DELETE FROM weights WHERE name='" + name + "'";
		state.executeUpdate(sql);
		System.out.println(name + " was deleted from weights");
	}
	
	public void deleteAssignment(int id) throws ClassNotFoundException, SQLException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		String sql = "DELETE FROM assignments WHERE id='" + id + "'";
		state.executeUpdate(sql);
		System.out.println("Deleted " + id);
	}
	
	// Get all grades
	public ResultSet getGrades() throws SQLException, ClassNotFoundException{
		if(con == null){
			getConnection();
		}
		
		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT grade FROM assignments");
		
		return res;
	}
	
	

}
