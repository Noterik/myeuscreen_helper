package org.springfield.lou.myeuscreen.publications;

import org.springfield.fs.FsNode;

public class Teaser extends Publication {
	
	private static String systemName = "teaser";
	private static String readableName = "Teasers";
	
	public Teaser(FsNode node) {
		super(node);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Overwritten static function
	public static String getSystemName() {
		return systemName;
	}
	
	//Overwritten static function
	public static String getReadableName() {
		return readableName;
	}

}
