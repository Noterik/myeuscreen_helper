package org.springfield.lou.myeuscreen.publications;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;

@MappingSettings(systemName = "")
@PublicationSettings(readableName = "All", readablePlural = "All", editable = false, collectable = false)
public class GenericPublication extends Publication {

	private Publication actualPublication;
	
	public GenericPublication(FsNode node, String parent) throws NoSettingsForPublicationDeclaredException{
		super(node, parent);
		this.parseNode(node);
	}
	
	public GenericPublication(FsNode node) throws NoSettingsForPublicationDeclaredException {
		super(node);
		this.parseNode(node);
	}
	
	private void parseNode(FsNode node) throws NoSettingsForPublicationDeclaredException{
		for(PublicationType type : PublicationType.values()){
			Class<? extends Publication> typeClass = type.getTypeClass();
			PublicationSettings settings = typeClass.getAnnotation(PublicationSettings.class);
			MappingSettings mappingSettings = typeClass.getAnnotation(MappingSettings.class);
			if(settings != null){
				String systemName = mappingSettings.systemName();
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
		return this.actualPublication.getType();
	}
	
	@Override
	@JSONField(field = "editable")
	public boolean getEditable(){
		return this.actualPublication.getEditable();
	}
}
