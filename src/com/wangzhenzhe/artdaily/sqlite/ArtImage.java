package com.wangzhenzhe.artdaily.sqlite;

import android.graphics.Bitmap;

public class ArtImage {
	
	
	public String name;
	public Artist artist;
	public String info;
	public Bitmap bmp;

	public ArtImage(String name, Artist artist, String info, Bitmap bmp) {
		this.name = name;
		this.artist = artist;
		this.info = info;
		this.bmp = bmp;
	}
	
	

}
