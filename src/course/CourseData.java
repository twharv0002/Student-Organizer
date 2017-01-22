package course;

import java.util.Map;

public class CourseData {
	
	private Map<String, Object> properties;

	public CourseData(Map<String, Object> weights) {
		this.properties = weights;
	}
	
	public void setProperty(String key, Object value){
		properties.put(key, value);
	}
	
	public Object getProperty(String property){
		return properties.get(property);
	}
}