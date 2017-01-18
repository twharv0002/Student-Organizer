package assignment;

public class Assignment {

	private int id;
	private AssignmentSpec spec;
	
	public Assignment(int id, AssignmentSpec spec){
		this.id = id;
		this.spec = spec;
	}
	
	public Assignment(AssignmentSpec spec){
		this.spec = spec;
	}
	
	public AssignmentSpec getSpec(){
		return spec;
	}

	public int getId() {
		return id;
	}
	
	
	
}
