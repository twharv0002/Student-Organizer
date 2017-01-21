package course;

import java.util.Map;

public class Course {
	private CourseData data;
	private Map<String, Double> weights;
	private int id;
	
	public Course(CourseData data, Map<String, Double> weights){
		this.weights = weights;
		this.data = data;
	}
	
	public Map<String, Double> getWeights(){
		return weights;
	}
	
	public CourseData getData(){
		return data;
	}
	
	public String toString(){
		return (String)data.getProperty("name");
	}
	
	public int getId(){
		return id;
	}

}
