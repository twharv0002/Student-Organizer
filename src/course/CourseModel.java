package course;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.DataBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CourseModel {

	private ObservableList<Course> dataCourse= FXCollections.observableArrayList();
	private DataBase database;
	
	public CourseModel(){
		database = new DataBase();
		getCoursesFromDB();
	}
	
	public ObservableList<Course> getCourses(){
		refreshCourseList();
		return dataCourse;
	}

	public void refreshCourseList() {
		dataCourse.clear();
		getCoursesFromDB();
	}
	
	public boolean hasCourse(String name){
		for (int i = 0; i < dataCourse.size(); i++) {
			if(dataCourse.get(i).getData().getProperty("name").toString().equals(name))
				return true;
		}
		
		return false;
	}
	
	public Course addNewCourse(String t, String n, String r, String a, String time){
		int room = 0;
		int absences = 0;
		if(!r.equals(""))
			room = Integer.valueOf(r);
		if(!a.equals(""))
			absences = Integer.valueOf(a);
		Map<String, Object> map = new HashMap<>();
		map.put("instructor", t);
		map.put("name", n);
		map.put("roomNumber", room);
		map.put("absences", absences);
		map.put("finalGrade", 0);
		map.put("time", time);
		CourseData data = new CourseData(map);
		Map<String, Double> weights = getWeightsFromDb((String)data.getProperty("name"));
		Course course = new Course(data, weights);
		
		try {
			database.insertCourse(course);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return course;
	}
	
	public void addCourseWeights(List<Double> weights, String name){
		try {
			database.insertWeights(weights, name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getWeights(String course){
		List<String> weights = new ArrayList<>();
		try {
			ResultSet rs = database.getTypeWeightByCourse(course);
			
			while(rs.next()){
				addWeightsFromResultSet(rs, weights);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return weights;
	}
	
	public Map<String, Double> getWeightsFromDb(String course){
		Map<String, Double> weights = new HashMap<>();
		try {
			ResultSet rs = database.getTypeWeightByCourse(course);
			
			while(rs.next()){
				weights.put("homework", rs.getDouble("homework"));
				weights.put("quiz", rs.getDouble("quiz"));
				weights.put("lab", rs.getDouble("lab"));
				weights.put("test", rs.getDouble("test"));
				weights.put("final", rs.getDouble("final"));
				weights.put("paper", rs.getDouble("paper"));
				weights.put("discussion", rs.getDouble("discussion"));
				weights.put("project", rs.getDouble("project"));
				weights.put("attendance", rs.getDouble("attendance"));
				weights.put("participation", rs.getDouble("participation"));
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return weights;
	}

	private void addWeightsFromResultSet(ResultSet rs, List<String> weights) throws SQLException {
		weights.add(String.valueOf(rs.getDouble("homework") * 100));
		weights.add(String.valueOf(rs.getDouble("quiz") * 100));
		weights.add(String.valueOf(rs.getDouble("lab") * 100));
		weights.add(String.valueOf(rs.getDouble("test") * 100));
		weights.add(String.valueOf(rs.getDouble("final") * 100));
		weights.add(String.valueOf(rs.getDouble("paper") * 100));
		weights.add(String.valueOf(rs.getDouble("project") * 100));
		weights.add(String.valueOf(rs.getDouble("attendance") * 100));
		weights.add(String.valueOf(rs.getDouble("discussion") * 100));
	}
	
	public void updateCourse(Course course) {
		try {
			database.updateCourse(course);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public void updateCourseWeights(String oldName, String name, List<Double> weights) {
		try {
			database.updateWeightCourse(oldName, name);
			database.updateWeightByCourse(weights, name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeCourse(String text) {
		try {
			database.deleteCourse(text);
			database.deleteWeight(text);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getWeightLabels(String name){
		List<String> list = new ArrayList<>();
		
		try {
			ResultSet rs = database.getTypeWeightByCourse(name);
			while(rs.next()){
				if(rs.getDouble("Homework") > 0){
					list.add("Homework");
				}
				if(rs.getDouble("Test") > 0){
					list.add("Test");
				}
				if(rs.getDouble("Quiz") > 0){
					list.add("Quiz");
				}
				if(rs.getDouble("Lab") > 0){
					list.add("Lab");
				}
				if(rs.getDouble("Final") > 0){
					list.add("Final");
				}
				if(rs.getDouble("Paper") > 0){
					list.add("Paper");
				}
				if(rs.getDouble("Discussion") > 0){
					list.add("Discussion");
				}
				if(rs.getDouble("Project") > 0){
					list.add("Project");
				}
				if(rs.getDouble("Attendance") > 0){
					list.add("Attendance");
				}
				
				
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	private void getCoursesFromDB(){
		ResultSet rs;
		try {
			rs = database.getCourses();
			while(rs.next()){		
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("instructor", rs.getString("instructor"));
				map.put("name", rs.getString("name"));
				map.put("roomNumber", rs.getInt("roomNumber"));
				map.put("absences", rs.getInt("absences"));
				map.put("finalGrade", rs.getDouble("finalGrade"));
				map.put("time", rs.getString("time"));
				CourseData data = new CourseData(map);
				Map<String, Double> weights = getWeightsFromDb((String)data.getProperty("name"));		
				Course course = new Course(data, weights);
				dataCourse.add(course);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public double getWeight(String name, String type) {
		double value = 0.0;
		try {
			value = database.getTypeWeightByCourse(name).getDouble(type);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
}
