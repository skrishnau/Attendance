package com.krishna.workingwithmultipletable;

public class StdPresenceObject {
	
		long _id;
		String crn,name;
		int attdDays;
		
		public StdPresenceObject(String crn, String name) {
			super();
			this.crn = crn;
			this.name = name;
		}

		public StdPresenceObject() {
			// TODO Auto-generated constructor stub
		}
		
		

		public int getAttdDays() {
			return attdDays;
		}

		public void setAttdDays(int attdDays) {
			this.attdDays = attdDays;
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


