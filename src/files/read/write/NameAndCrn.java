package files.read.write;

public class NameAndCrn {
	CRN crn;
	String name;

	public CRN getCrn() {
		return crn;
	}

	public void setCrn(CRN crn) {
		this.crn = crn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NameAndCrn(CRN crn, String name) {
		super();
		this.crn = crn;
		this.name = name;
	}

	public NameAndCrn() {

	}
}
