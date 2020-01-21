package files.read.write;

public class CRN {

	int batch;
	int roll;

	public int getBatch() {
		return batch;
	}

	public int getRoll() {
		return roll;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public void setRoll(int roll) {
		this.roll = roll;
	}

	public CRN(int batch, int roll) {
		this.batch = batch;
		this.roll = roll;
	}
}
