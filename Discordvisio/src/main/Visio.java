package main;

import java.util.Date;

public class Visio {
	
	private long date;
	private int duration; // secondes
	private String course;
	private String chap;
	private String link;
	private boolean active = false;
	

	
	public Visio(long date, int duration, String course, String chap, String link) {
		super();
		this.date = date;
		this.duration = duration;
		this.course = course;
		this.chap = chap;
		this.link = link;
	}

	public int getDuration() {
		return duration;
	}

	public String getCourse() {
		return course;
	}

	public String getChap() {
		return chap;
	}

	public String getLink() {
		return link;
	}

	public long getStartTimeStamp() {
		return date/1000;
	}
	
	public long getEndTimeStamp() {
		return (date/1000)+duration;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	@Override
	public String toString() {
		return  new Date(date).toString() +" " +  duration+"seconds  "+  course+" "+ chap+" 	\n"+  link;
	}
}