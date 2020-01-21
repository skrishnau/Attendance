package entities;

/**
 * 
 * @param 
 * 		long _id; int presence ;
 *
 */
public class EachDay {
	int presence=0;
	long _id;
	
	
	
	public EachDay() {
		super();
	}

	public EachDay(int presence, long _id) {
		super();
		this.presence = presence;
		this._id = _id;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public int getPresence() {
		return presence;
	}

	public void setPresence(int presence) {
		this.presence = presence;
	}
	
}
