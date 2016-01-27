package org.springfield.lou.myeuscreen.publications;

import java.net.URLEncoder;
import java.util.Date;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;
import org.springfield.lou.myeuscreen.mapping.NoParentForMappedObjectException;
import org.springfield.lou.myeuscreen.mapping.ObjectToSmithersGetter;
import org.springfield.lou.myeuscreen.mapping.SmithersToObjectSetter;
import org.springfield.lou.myeuscreen.publications.EUScreenMediaItem;
import org.springfield.lou.myeuscreen.publications.Publication;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.Role;

@MappingSettings(systemName = "videoposter")
@PublicationSettings(readableName = "Videoposter", readablePlural = "Video Posters", editable = true, collectable = false)
public class VideoPoster extends Publication {
		
	private String html = "";
	
	public VideoPoster() {
		super();
	}
	
	public VideoPoster(FsNode node) {
		super(node);
		// TODO Auto-generated constructor stub
	}
		
	public VideoPoster(FsNode node, String parent){
		super(node, parent);
	}

	public static VideoPoster getVideoPostersByPath(String path){
		FsNode node = Fs.getNode(path);
		return new VideoPoster(node);
	}
	
	@ObjectToSmithersGetter(mapTo = "html")
	@JSONField(field = "html")
	public String getHTML() {
		return html;
	}
	
	@SmithersToObjectSetter(mapTo = "html")
	public void setHTML(String html) {
		this.html = html;
	}
	
	public static VideoPoster createVideoPoster(IRoleActor actor, String id, String title, String author, String html, String layout, String theme, String icon){
		VideoPoster poster = new VideoPoster();
		poster.setId(id);
		poster.setName(title);
		poster.setCreationDate(new Date().toString());
		poster.setAuthor(author);
		poster.setHTML(html);
		poster.setLayout(layout);
		poster.setTheme(theme);
		poster.setImage(icon);
		
		try {
			poster.getRights().giveRole(actor, Role.OWNER);
		} catch (AlreadyHasRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return poster;
	}
	
	public static VideoPoster createVideoPoster(IRoleActor actor, String title, String author, String html, String layout, String theme, String icon){
		VideoPoster poster = new VideoPoster();
		poster.setName(title);
		poster.setCreationDate(new Date().toString());
		poster.setAuthor(author);
		poster.setHTML(html);
		poster.setLayout(layout);
		poster.setTheme(theme);

		poster.setImage(icon);
		
		try {
			poster.getRights().giveRole(actor, Role.OWNER);
		} catch (AlreadyHasRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return poster;
	}


	@Override
	public void save() throws NoParentForMappedObjectException {
		// TODO Auto-generated method stub
		super.save();
			
	}
}
