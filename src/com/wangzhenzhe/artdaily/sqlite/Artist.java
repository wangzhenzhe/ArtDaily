package com.wangzhenzhe.artdaily.sqlite;

public class Artist {
	
	public int _id;
	public String name;
	public int age;
	public String info;
	
	
	public Artist() {
		// TODO Auto-generated constructor stub
	}
	
	public Artist(String name, int age, String info)
	{
		this.name = name;
		this.age = age;
		this.info = info;
	}

}
