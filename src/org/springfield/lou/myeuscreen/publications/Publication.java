package org.springfield.lou.myeuscreen.publications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.IncorrectRightsNodeFormatException;
import org.springfield.lou.myeuscreen.rights.NodeWithRights;
import org.springfield.lou.myeuscreen.rights.Rights;
import org.springfield.lou.myeuscreen.rights.Role;
import org.springfield.lou.myeuscreen.mapping.MappedObject;
import org.springfield.lou.myeuscreen.mapping.SmithersXMLFieldMapping;
import org.springfield.lou.myeuscreen.util.ImageUtils;
import org.springfield.lou.json.JSONField;

public abstract class Publication extends MappedObjectWithRights{
	private String id;
	private String author;
	private String name;
	private String creationDate;
	private String image;
	private String path;
		
	public Publication(FsNode node){
		super(node);
		this.setId(node.getId());
		this.setPath(node.getPath());
	}
	
	@JSONField(field = "type")
	public abstract String getType();
	
	@SortableField(field = "name")
	@JSONField(field = "name")
	public String getName() {
		return name;
	}

	@SmithersXMLFieldMapping(mapTo = "name")
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

	@JSONField(field = "creationDate")
	public String getCreationDate() {
		return creationDate;
	}

	@SmithersXMLFieldMapping(mapTo = "creationDate")
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	@SmithersXMLFieldMapping(mapTo = "author")
	public void setAuthor(String author) {
		this.author = author;
	}

	@JSONField(field="author")
	public String getAuthor(){
		return this.author;
	}
	
	@JSONField(field="id")
	public String getId() {
		return id;
	}

	@JSONField(field="image")
	public String getImage() {
		return image;
	}

	@SmithersXMLFieldMapping(mapTo = "image")
	public void setImage(String image) {
		this.image = ImageUtils.mapURL(image, true);
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static String getSystemName(){
		return "";
	}
	
	public static String getReadableName(){
		return "All";
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

	@Override
	public void giveRole(IRoleActor user, Role role) {
		// TODO Auto-generated method stub
		
	}	
	
}