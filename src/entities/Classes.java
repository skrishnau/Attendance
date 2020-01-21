package entities;

public class Classes {
	long _id;
	int batch;
	String program,course;
	
	
	public Classes(int batch, String program, String course) {
		super();
		this.program = program;
		this.course = course;
		this.batch = batch;
	}
	

	public Classes(long _id, int batch, String program, String course) {
		super();
		this._id = _id;
		this.program = program;
		this.course = course;
		this.batch = batch;
	}


	public long get_id() {
		return _id;
	}
	public void set_id(long ids) {
		this._id = ids;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public int getBatch() {
		return batch;
	}
	public void setBatch(int batch) {
		this.batch = batch;
	}
	
	
}
