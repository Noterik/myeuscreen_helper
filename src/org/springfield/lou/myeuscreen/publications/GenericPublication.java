package org.springfield.lou.myeuscreen.publications;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;

public class GenericPublication extends Publication {

	private static String systemName = "";
	private static String readableName = "All";
	private Publication actualPublication;
	
	public GenericPublication(FsNode node) {
		super(node);
		System.out.println("new GenericPublication()");
		this.parseNode(node);
	}

	public static String getSystemName(){
		return systemName;
	}
	
	public static String getReadableName(){
		return readableName;
	}
	
	private void parseNode(FsNode node){
		for(PublicationType type : PublicationType.values()){
			Class<? extends Publication> typeClass = type.getTypeClass();
			try {
				String systemName = (String) typeClass.getMethod("getSystemName").invoke(this);
				if(systemName.equals(node.getName())){
					Constructor<? extends Publication> constructor = typeClass.getConstructor(FsNode.class);
					this.actualPublication = constructor.newInstance(node);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String toString(){
		return this.actualPublication.toString();
	}

	@Override
	@JSONField(field = "type")
	public String getType() {
		// TODO Auto-generated method stub
		return this.actualPublication.getType();
	}
}
