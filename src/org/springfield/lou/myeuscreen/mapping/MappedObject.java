package org.springfield.lou.myeuscreen.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONObservable;

public abstract class MappedObject extends JSONObservable {
	
	private String id;
	private String nodeName;
	private String parentURI = null;
	private String path = null;
	
	public MappedObject(){
		id = null;
		parseSmithersName();
	}
	
	public MappedObject(Map<String, Object> properties){
		id = null;
		
		parseSmithersName();
		
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(SmithersToObjectSetter.class)){
				Annotation annotation = method.getAnnotation(SmithersToObjectSetter.class);
				SmithersToObjectSetter mapping = (SmithersToObjectSetter) annotation;
				String fieldName = mapping.mapTo();
				
				if(properties.containsKey(fieldName)){
					try {
						method.invoke(this, properties.get(fieldName));
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
	
	public MappedObject(Map<String, Object> properties, FsNode parent){
		this(properties);
		this.parentURI = parent.getPath();
	}
	
	public MappedObject(Map<String, Object> properties, String parent){
		this(properties);
		this.parentURI = parent;
	}
	
	public MappedObject(FsNode node){
		
		parseSmithersName();
		
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(SmithersToObjectSetter.class)){
				Annotation annotation = method.getAnnotation(SmithersToObjectSetter.class);
				SmithersToObjectSetter mapping = (SmithersToObjectSetter) annotation;
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
		
		this.path = node.getPath();
	}
	
	public MappedObject(FsNode node, FsNode parent){
		this(node);
		this.parentURI = parent.getPath();
		this.path = node.getPath();
	}
	
	public MappedObject(FsNode node, String parent){
		this(node);
		this.parentURI = parent;
		this.path = node.getPath();
	}
	
	@SmithersToObjectSetter(mapTo = "@id")
	public void setId(String id){
		this.id = id;
	}
	
	@JSONField(field = "id")
	public String getId(){
		return this.id;
	}
	
	public void save() throws NoParentForMappedObjectException{
		System.out.println("myeuscreen: MappedObject.save()");
		if(parentURI != null && Fs.getNode(parentURI) != null){
			FsNode node = new FsNode();
			node.setName(this.nodeName);
			
			if(this.getId() == null){
				this.setId(UUID.randomUUID().toString());
			}
			
			node.setId(this.getId());
			
			for(Method method : this.getClass().getMethods()){
				Annotation[] annotations = method.getDeclaredAnnotations();
				for(Annotation annotation : annotations){
					if(annotation.annotationType().equals(ObjectToSmithersGetter.class)){
						ObjectToSmithersGetter getter = (ObjectToSmithersGetter) annotation;
						String smithersPropName = getter.mapTo();
						try {
							Object results = method.invoke(this);
							String resultsStr = "";
							if(results != null){
								try{
									resultsStr = (String) results;
								}catch(ClassCastException cce){
									resultsStr = results.toString();
								}
							}
							node.setProperty(smithersPropName, resultsStr);
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
			
			this.path = this.parentURI + "/" + this.nodeName + "/" + this.getId();
			System.out.println("PATH: " + this.path);
			if(Fs.getNode(path) != null){
				System.out.println("myeuscreen: it already exists, delete the existing node");
				Fs.deleteNode(path);
			}
			
			System.out.println("XML: " + node.asXML());
			
			Fs.insertNode(node, this.parentURI);
			this.setPath(path);
		}else{
			throw new NoParentForMappedObjectException("Please provide a correct parent for this object, parent given: " + this.parentURI);
		}
			
		System.out.println("myeuscreen: END MappedObject.save()");
	}
		
	public void save(String parentNode) throws NoParentForMappedObjectException{		
		this.parentURI = parentNode;
		this.save();
	}
	
	public void save(FsNode parentNode) throws NoParentForMappedObjectException{
		this.parentURI = parentNode.getPath();
		this.save();
	}
	
	public void createReference(FsNode referencingParentNode) throws ReferencedNodeNotExistsException{
		if(this.path != null){
			FsNode node = new FsNode();
			node.setName(this.nodeName);
			node.setId(this.id);
			node.setReferid(this.path);
			Fs.insertNode(node, referencingParentNode.getPath());
		}else{
			throw new ReferencedNodeNotExistsException("The node your trying to create a reference for doesn't exist yet!");
		}
	}
	
	public String getNodeName(){
		return this.nodeName;
	}
	
	private void parseSmithersName(){
		SmithersName smithersName = this.getClass().getAnnotation(SmithersName.class);
		if(smithersName != null){
			nodeName = smithersName.name();
		}else{
			nodeName = this.getClass().getSimpleName().toLowerCase();
		}
	}
	
	public void setParent(FsNode parent){
		this.parentURI = parent.getPath();
	}
	
	public void setParent(String parent){
		this.parentURI = parent;
	}

	@JSONField(field="path")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path.contains("//")){
			path = path.replace("//", "/");
		}
		this.path = path;
	}
}
