package com.rlum.nikeplus;

public class runItem {
	private String runID;
	private String workoutType;
	

	private String startValue ;
	private String distanceValue ;
	private String durationValue ;
	private String calorieValue ;
	
	public String getWorkoutType() {
		return workoutType;
	}
	public void setWorkoutType(String workoutType) {
		this.workoutType = workoutType;
	}
	public String getRunID() {
		return runID;
	}
	public void setRunID(String string) {
		this.runID = string;
	}
	
	public String getStartValue() {
		return startValue;
	}
	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public String getDistanceValue() {
		return distanceValue;
	}
	public void setDistanceValue(String distanceValue) {
		this.distanceValue = distanceValue;
	}
	public String getDurationValue() {
		return durationValue;
	}
	public void setDurationValue(String durationValue) {
		this.durationValue = durationValue;
	}
	public String getCalorieValue() {
		return calorieValue;
	}
	public void setCalorieValue(String calorieValue) {
		this.calorieValue = calorieValue;
	}
	@Override
	public String toString(){
		return "id="+getRunID() + ":type=" + getWorkoutType() + ":start=" + getStartValue() + ":dist=" + getDistanceValue() + ":dur=" +getDurationValue() + ":cal=" + getCalorieValue();
		
	}
	
	
	
}
