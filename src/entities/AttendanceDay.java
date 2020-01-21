package entities;

import java.util.Date;

public class AttendanceDay {
	long _id;
	long clsId;
	Date days;
	
	public AttendanceDay(long clsId, Date days) {
		super();
		this.clsId = clsId;
		this.days = days;
	}
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public long getClsId() {
		return clsId;
	}
	public void setClsId(long clsId) {
		this.clsId = clsId;
	}
	public Date getDays() {
		return days;
	}
	public void setDays(Date days) {
		this.days = days;
	}
	public String getDaysS(){
		String daysS=days.toString();
		return daysS;
	}
	
}
