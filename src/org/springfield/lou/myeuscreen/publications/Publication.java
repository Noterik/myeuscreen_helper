package org.springfield.lou.myeuscreen.publications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;
import org.springfield.lou.myeuscreen.mapping.ObjectToSmithersGetter;
import org.springfield.lou.myeuscreen.mapping.SmithersToObjectSetter;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.util.ImageUtils;

public abstract class Publication extends MappedObjectWithRights{
	private String id;
	private String author;
	private String name;
	private String creationDate;
	private String image;
	private String layout;
	private String theme;
	
	public Publication(){
		
	}
	
	public Publication(FsNode node, String parent){
		super(node, parent);
		this.setId(node.getId());
		this.setPath(node.getPath());
	}
		
	public Publication(FsNode node){
		super(node);
		this.setId(node.getId());
		this.setPath(node.getPath());
	}
	
	@JSONField(field = "collectable")
	public boolean getCollectable(){
		return getClass().getAnnotation(PublicationSettings.class).collectable();
	}
	
	@JSONField(field = "type")
	public String getType(){
		return getClass().getAnnotation(MappingSettings.class).systemName();
	}
	
	@JSONField(field = "editable")
	public boolean getEditable(){
		return getClass().getAnnotation(PublicationSettings.class).editable();
	}
	
	@ObjectToSmithersGetter(mapTo = "name")
	@JSONField(field = "name")
	public String getName() {
		return name;
	}
	
	@SortableField(field = "name")
	public String getSortableName(){
		return this.getName().toLowerCase();
	}
	
	@SmithersToObjectSetter(mapTo = "name")
	public void setName(String name) {
		this.name = name;
	}
	
	@SortableField(field = "creationDate")
	public Long getSortableCreationDate(){
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		Date d;
		try {
			d = format.parse(this.getCreationDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return new Long(0);
		}
		return d.getTime();
	}

	@ObjectToSmithersGetter(mapTo = "creationDate")
	@JSONField(field = "creationDate")
	public String getCreationDate() {
		return creationDate;
	}

	@SmithersToObjectSetter(mapTo = "creationDate")
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	@SmithersToObjectSetter(mapTo = "author")
	public void setAuthor(String author) {
		this.author = author;
	}

	@ObjectToSmithersGetter(mapTo = "author")
	@JSONField(field="author")
	public String getAuthor(){
		return this.author;
	}
	
	@SmithersToObjectSetter(mapTo = "layout")
	public void setLayout(String layout) {
		this.layout = layout;
	}

	@ObjectToSmithersGetter(mapTo = "layout")
	@JSONField(field="layout")
	public String getLayout(){
		return this.layout;
	}
	
	@SmithersToObjectSetter(mapTo = "theme")
	public void setTheme(String theme) {
		this.theme = theme;
	}

	@ObjectToSmithersGetter(mapTo = "theme")
	@JSONField(field="theme")
	public String getTheme(){
		return this.theme;
	}
	
	@JSONField(field="id")
	public String getId() {
		return id;
	}

	@ObjectToSmithersGetter(mapTo = "image")
	@JSONField(field="image")
	public String getImage() {
		return image;
	}

	@SmithersToObjectSetter(mapTo = "image")
	public void setImage(String image) {
		this.image = ImageUtils.mapURL(image, true);
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void stopSharingWith(IRoleActor actor){
		String uri = actor.getNode().getPath();
		uri += "/publications/1/" + this.getType() + "/" + this.id;
		Fs.deleteNode(uri);
		this.getRights().removeRightsForActor(actor);
	};
	
}