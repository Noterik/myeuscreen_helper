package org.springfield.lou.myeuscreen.publications;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;

@PublicationSettings(systemName = "", readableName = "All", readablePlural = "All")
public class GenericPublication extends Publication {

	private Publication actualPublication;
	
	public GenericPublication(FsNode node, String parent) throws NoSettingsForPublicationDeclaredException{
		super(node, parent);
		this.parseNode(node);
	}
	
	public GenericPublication(FsNode node) throws NoSettingsForPublicationDeclaredException {
		super(node);
		System.out.println("new GenericPublication()");
		this.parseNode(node);
	}
	
	private void parseNode(FsNode node) throws NoSettingsForPublicationDeclaredException{
		for(PublicationType type : PublicationType.values()){
			Class<? extends Publication> typeClass = type.getTypeClass();
			PublicationSettings settings = typeClass.getAnnotation(PublicationSettings.class);
			if(settings != null){
				String systemName = settings.systemName();
				if(systemName.equals(node.getName())){
					Constructor<? extends Publication> constructor;
					try {
						constructor = typeClass.getConstructor(FsNode.class);
						this.actualPublication = constructor.newInstance(node);
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
			}else{
				throw new NoSettingsForPublicationDeclaredException("No settings declared! Please create a @PublicationSettings for " + this.getClass().getName());
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
