package org.springfield.lou.myeuscreen.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONObservable;

public abstract class MappedObject extends JSONObservable {

	private String id = null;
	private String nodeName;
	private String parentURI = null;
	private String path = null;
	private Integer order = null;
	private FsNode node = null;
	
	public MappedObject(){
		parseSmithersName();
	}
	
	public MappedObject(Map<String, Object> properties){
		
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
			}else if(method.isAnnotationPresent(SmithersToObjectChildrenSetter.class)){
				
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
	
	public FsNode getNode(){
		return this.node;
	}
	
	public MappedObject(FsNode node){
		parseSmithersName();
		
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(SmithersToObjectSetter.class)){
				SmithersToObjectSetter mapping = method.getAnnotation(SmithersToObjectSetter.class);
				String fieldName = mapping.mapTo();
				
				try {
					if(fieldName.equals("@id")){
						method.invoke(this, node.getId());
					}else if(fieldName.equals("@referid")){
						method.invoke(this, node.getReferid());
					}else if(!fieldName.isEmpty()){
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
			}else if(method.isAnnotationPresent(SmithersToObjectChildrenSetter.class)){
				SmithersToObjectChildrenSetter setter = method.getAnnotation(SmithersToObjectChildrenSetter.class);
				Class<? extends MappedObject> type = setter.type();
				try {
					Constructor<? extends MappedObject> constructor = type.getConstructor(FsNode.class);
					MappingSettings mappingSettings = type.getAnnotation(MappingSettings.class);
					String systemName = mappingSettings.systemName();
					List<FsNode> childrenNodes = Fs.getNodes(node.getPath() + "/" + systemName, 1);
					ArrayList<MappedObject> children = new ArrayList<MappedObject>();
					for(FsNode childNode : childrenNodes){
						MappedObject child = constructor.newInstance(childNode);
						children.add(child);
					}
					method.invoke(this, children);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
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
		this.node = node;
		this.path = node.getPath();
	}
	
	public MappedObject(FsNode node, FsNode parent){
		this(node);
		this.parentURI = parent.getPath();
	}
	
	public MappedObject(FsNode node, String parent){
		this(node);
		this.parentURI = parent;
	}
	
	@JSONField(field = "id")
	public String getId(){
		return this.id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	@JSONField(field = "path")
	public String getPath(){
		if(this.node != null){
			return node.getPath();
		}
		return null;
	}
	
	public void setNode(FsNode node){
		this.node = node;
	}
	
	public void save() throws NoParentForMappedObjectException{
		//System.out.println("MappedObject.save()");
		//System.out.println("MappedObject.save(): LETS SAVE THIS NODE: " + this.getNodeName() + "/" + this.getId());
		//System.out.println("MappedObject.save(): PARENT URI = " + parentURI);
		if(parentURI != null && (Fs.getNode(parentURI) != null || Fs.getNodes(parentURI, 1).size() > 0)){
			
			if(this.getNode() == null){
				//System.out.println("MappedObject.save(): LET'S CREATE A NEW NODE!");
				FsNode newNode = new FsNode();
				newNode.setName(this.nodeName);
				
				if(this.getId() == null){
					//System.out.println("MappedObject.save(): LET'S CREATE A NEW IDENTIFIER!");
					this.setId(UUID.randomUUID().toString());
				}
				
				//System.out.println("MappedObject.save(): THE NEW IDENTIFIER = " + this.getId());
				newNode.setId(this.getId());
				if(Fs.insertNode(newNode, parentURI)){
					//System.out.println("MappedObject.save(): NEW NODE INSERTED!");
					this.setNode(Fs.getNode(parentURI + "/" + this.nodeName + "/" + this.getId()));
				}
			}
			
			//System.out.println("MappedObject.save(): THE NODE ALREADY EXISTS!");
			List<FsNode> referNodes = new ArrayList<FsNode>();
			List<MappedObject> children = new ArrayList<MappedObject>();
			
			if(this.getNode() != null){
				for(Method method : this.getClass().getMethods()){
					Annotation[] annotations = method.getDeclaredAnnotations();
					for(Annotation annotation : annotations){
						if(annotation.annotationType().equals(ObjectToSmithersGetter.class)){
							ObjectToSmithersGetter getter = (ObjectToSmithersGetter) annotation;
							String smithersPropName = getter.mapTo();
							if(smithersPropName != null && !smithersPropName.isEmpty()){
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
									//System.out.println("MappedObject.save(): LET'S UPDATE PROPERTY = " + smithersPropName + " TO = " + resultsStr + ", FOR NODE = " + this.getPath());
									Fs.setProperty(this.getPath(), smithersPropName, resultsStr);
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
						}else if(annotation.annotationType().equals(SmithersReference.class)){
							if(method.getReturnType().equals(FsNode.class)){
								try {
									FsNode referredNode = (FsNode) method.invoke(this);
									referNodes.add(referredNode);
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch(NullPointerException e){
									e.printStackTrace();
								}
							}
						}else if(annotation.annotationType().equals(ObjectChildrenToSmithersGetter.class)){
							try {
								Object results = method.invoke(this);
								ObjectChildrenToSmithersGetter getter = (ObjectChildrenToSmithersGetter) annotation;
								boolean ordered = getter.ordered();
								if(List.class.isAssignableFrom(results.getClass())){
									int i = 0;
									List<Object> castedResults = (List<Object>) results;
									for(Object result : castedResults){
										if(MappedObject.class.isAssignableFrom(result.getClass())){
											MappedObject resultObj = (MappedObject) result;
											if(ordered)
												resultObj.setOrder(i);
											children.add(resultObj);
										}
										i++;
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
			}
			
			for(Iterator<FsNode> referenceIterator = referNodes.iterator(); referenceIterator.hasNext();){
				FsNode referedNode = referenceIterator.next();
				String referIdPath = this.getPath() + "/" + referedNode.getName() + "/" + referedNode.getId();
				if(Fs.getNode(referedNode.getPath()) != null && Fs.getNode(referIdPath) == null){
					FsNode newReferingNode = new FsNode();
					newReferingNode.setName(referedNode.getName());
					newReferingNode.setId(referedNode.getId());
					newReferingNode.setReferid(referedNode.getPath());
					Fs.insertNode(newReferingNode, this.getPath());
				}
			}
			
			for(Iterator<MappedObject> childrenIterator = children.iterator(); childrenIterator.hasNext();){
				MappedObject child = childrenIterator.next();
				child.save(this.getPath());
			}
		}else{
			throw new NoParentForMappedObjectException("Please provide a correct parent for this object, parent given: " + this.parentURI);
		}
			
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
			node.setId(this.getId());
			node.setReferid(this.path);
			Fs.insertNode(node, referencingParentNode.getPath());
		}else{
			throw new ReferencedNodeNotExistsException("The node your trying to create a reference for doesn't exist yet!");
		}
	}
	
	public void setOrder(Integer order){
		this.order = order;
	}
	
	public Integer order(){
		return this.order;
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

}
