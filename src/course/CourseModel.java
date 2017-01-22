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

	private ObservableList<Course> courses= FXCollections.observableArrayList();
	private DataBase database;
	
	public CourseModel(){
		database = new DataBase();
		getCoursesFromDB();
	}
	
	public ObservableList<Course> getCourses(){
		refreshCourseList();
		return courses;
	}

	public void refreshCourseList() {
		courses.clear();
		getCoursesFromDB();
	}
	
	public boolean hasCourse(String name){
		for (int i = 0; i < courses.size(); i++) {
			if(courses.get(i).getData().getProperty("name").toString().equals(name))
				return true;
		}
		
		return false;
	}
	
	public void addNewCourse(Course course){
		try {
			database.insertCourse(course);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addCourseWeights(Course course){
		try {
			database.insertWeights(course);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	public void updateCourse(Course course) {
		try {
			database.updateCourse(course);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public void updateCourseWeights(String oldName, String name, Map<String, Double> weights) {
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
	
	public List<String> getWeightLabels(Course course){
		List<String> list = new ArrayList<>();
		for(Map.Entry<String, Double> entry : course.getWeights().entrySet()){
			if(entry.getValue() > 0)
				list.add(entry.getKey());
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
				courses.add(course);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
