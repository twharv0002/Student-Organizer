package course;

import java.util.Map;

public class Course {
	private CourseData data;
	private int id;
	
	public Course(CourseData data){
		this.data = data;
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
