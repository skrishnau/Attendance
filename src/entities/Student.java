package entities;

public class Student {
	long _id;
	String crn,name;
	
	public Student(String crn, String name) {
		super();
		this.crn = crn;
		this.name = name;
	}

	public Student() {
		// TODO Auto-generated constructor stub
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getCrn() {
		return crn;
	}

	public void setCrn(String crn) {
		this.crn = crn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
