package org.springfield.lou.myeuscreen.publications;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

import org.json.simple.JSONObject;

public class PublicationComparator implements Comparator<Publication> {

	private String field;
	private SortDirection direction;
	
	public enum SortField{
		CREATIONDATE   	("creationDate", "DATE"),
		NAME			("name", "NAME");
		
		private String fieldName;
		private String readable;
		
	    SortField(String name, String readable) {
	    	fieldName = name;
	    	this.readable = readable;
	    }

		public String getFieldName() {
			return fieldName;
		}
		
		public String getReadable() {
			return readable;
		}
		
		public static SortField getByFieldName(String fieldName){
			for(SortField field : SortField.values()){
				if(field.getFieldName().toLowerCase().equals(fieldName.toLowerCase())){
					return field;
				}
			}
			return null;
		}
		
		public JSONObject toJSON(){
			JSONObject json = new JSONObject();
			json.put("fieldName", this.fieldName);
			json.put("readable", readable);
			
			return json;
		}
	}
	
	public enum SortDirection {
		UP,
		DOWN;
		
		public static SortDirection getByName(String name){
			for(SortDirection direction : SortDirection.values()){
				if(direction.name().toLowerCase().equals(name.toLowerCase())){
					return direction;
				}
			}
			return null;
		}
	}
	
	
	public PublicationComparator(SortField field, SortDirection direction){
		this.field = field.getFieldName(); 
		this.direction = direction;
	}
	
	@Override
	public int compare(Publication pub0, Publication pub1) {
		// TODO Auto-generated method stub
		for(Method method : Publication.class.getDeclaredMethods()){
			if(method.isAnnotationPresent(SortableField.class)){
				Annotation annotation = method.getAnnotation(SortableField.class);
				SortableField sortableFieldAnnotation = (SortableField) annotation;
				String fieldName = sortableFieldAnnotation.field();
				
				if(fieldName.equals(this.field)){
					try {
						
						Object val1 = method.invoke(pub0);
						Object val2 = method.invoke(pub1);
						
						if(val1 instanceof Comparable && val2 instanceof Comparable){
							Comparable<Object> comparableVal1 = (Comparable<Object>) val1;
							Comparable<Object> comparableVal2 = (Comparable<Object>) val2;
							
							if(this.direction.equals(SortDirection.DOWN)){
								return comparableVal1.compareTo(comparableVal2);
							}else{
								return comparableVal2.compareTo(comparableVal1);
							}
						}

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
	
		}
		
		return 0;
	}

}
