package files.read.write;

public class NameAndCrnAsString {
	private String name;
	private String crn;
	public NameAndCrnAsString(String crn, String name) {
		super();
		this.name = name;
		this.crn = crn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCrn() {
		return crn;
	}
	public void setCrn(String crn) {
		this.crn = crn;
	}
	
}
