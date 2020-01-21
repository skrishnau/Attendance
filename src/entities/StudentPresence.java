package entities;

public class StudentPresence {
	String crn,name;
	String presence;
	long _id;
	boolean selected;
	
	
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		if(selected==true){
			this.presence="P";
		}else{this.presence="A";}
		this.selected = selected;
	}
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public StudentPresence(String crn, String name, String presence) {
		super();
		this.crn = crn;
		this.name = name;
		this.presence = presence;
	}
	public StudentPresence() {
		// TODO Auto-generated constructor stub
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
	public String getPresence() {
		if(this.presence==""){
			return ("A");
		}else{
			return presence;
		}
	}
	public void setPresence(String presence) {
		this.presence = presence;
	}
	
	
}
