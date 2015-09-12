package com.lire.image.entity;

public class Image {
	private double score;
	private String imagepath;
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	
	public Image(double score, String imagepath) {
		this.score = score;
		String[] imagespath = imagepath.split("\\\\");
		this.imagepath = imagespath[imagespath.length-1];
	}
	
	
}
