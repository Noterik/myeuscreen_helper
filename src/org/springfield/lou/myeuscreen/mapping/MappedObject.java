package org.springfield.lou.myeuscreen.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONObservable;

public class MappedObject extends JSONObservable {
	
	public MappedObject(FsNode node){
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(SmithersXMLFieldMapping.class)){
				Annotation annotation = method.getAnnotation(SmithersXMLFieldMapping.class);
				SmithersXMLFieldMapping mapping = (SmithersXMLFieldMapping) annotation;
				String fieldName = mapping.mapTo();
				
				try {
					if(fieldName.equals("@id")){
						method.invoke(this, node.getId());
					}else if(fieldName.equals("@referid")){
						method.invoke(this, node.getReferid());
					}else{
						method.invoke(this, node.getProperty(fieldName));
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
}
