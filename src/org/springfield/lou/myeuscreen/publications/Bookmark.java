package org.springfield.lou.myeuscreen.publications;

import java.util.Date;
import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.mapping.NoParentForMappedObjectException;
import org.springfield.lou.myeuscreen.publications.EUScreenMediaItem;
import org.springfield.lou.myeuscreen.publications.Publication;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.Role;

@PublicationSettings(
		systemName = "bookmark", 
		readableName = "Bookmark", 
		readablePlural = "Bookmarks", 
		editable = false
)
public class Bookmark extends Publication {
	
	private EUScreenMediaItem item;
	
	public Bookmark() {
		super();
	}
	
	public Bookmark(FsNode node) {
		super(node);
		populate();
		// TODO Auto-generated constructor stub
	}
		
	public Bookmark(FsNode node, String parent){
		super(node, parent);
		populate();
	}

	public static Bookmark getBookmarkByPath(String path){
		FsNode node = Fs.getNode(path);
		return new Bookmark(node);
	}
	
	public static Bookmark createBookmark(IRoleActor actor, EUScreenMediaItem item){
		FsNode node = new FsNode();
		node.setName("bookmark");
		Bookmark bookmark = new Bookmark();
		bookmark.setCreationDate(new Date().toString());
		bookmark.setAuthor("system");
		bookmark.setItem(item);
		
		try {
			bookmark.getRights().giveRole(actor, Role.OWNER);
		} catch (AlreadyHasRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bookmark;
	}
	
	public void setItem(EUScreenMediaItem item){
		this.item = item;
	}
	
	public EUScreenMediaItem getItem(){
		return this.item;
	}
	
	@JSONField(field = "image")
	public String getImage(){
		return (String) this.item.get("screenshot");
	}
	
	@JSONField(field = "name")
	public String getName(){
		return (String) this.item.get("title");
	}
	
	@Override
	public void save() throws NoParentForMappedObjectException {
		// TODO Auto-generated method stub
		super.save();
				
		String path = super.getPath();
		if(path != null){
			String childPath = path + "/" + item.getNode().getName() + "/" + item.getNode().getId();
			
			
			if(Fs.getNode(childPath) == null){
				FsNode child = new FsNode();
				child.setName(item.getNode().getName());
				child.setId(item.getNode().getId());
				
				if(item.getNode().getReferid() != null){
					child.setReferid(item.getNode().getReferid());
				}else if(item.getNode().getPath() != null){
					child.setReferid(item.getNode().getPath());
				}
								
				Fs.insertNode(child, path);
			}
		}
	}

	private void populate(){
		String path = super.getPath();
		
		if(path != null){
			List<FsNode> children = Fs.getNodes(path, 2); 
			
			for(FsNode child : children){
				if(!child.getName().equals("rights")){
					item = new EUScreenMediaItem(child);
					this.setImage(item.getNode().getProperty("screenshot"));
				}
			}
		}
	}

}
