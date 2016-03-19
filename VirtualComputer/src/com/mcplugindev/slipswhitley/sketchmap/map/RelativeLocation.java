package com.mcplugindev.slipswhitley.sketchmap.map;

public class RelativeLocation {
	
	private int x;
	private int y;
	
	public RelativeLocation (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	public String toString() {
		return x + ":" + y;
	}
	
	public static RelativeLocation fromString(String str) {
		String[] args = str.split(":");
		if(args.length != 2) {
			return null;
		}
		
		int x = 0;
		int y = 0;
		
		try {
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
		}
		catch (Exception ex) {
			return null;
		}
		
		return new RelativeLocation (x, y);
	}


	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	
	
	
	
	

}
