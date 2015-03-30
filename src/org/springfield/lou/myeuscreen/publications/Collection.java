package org.springfield.lou.myeuscreen.publications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.Rights;
import org.springfield.lou.myeuscreen.rights.Role;
import org.springfield.lou.json.JSONField;

public class Collection extends Publication{
	private static String systemName = "collection";
	private static String readableName = "Collections";
	private List<EUScreenMediaItem> items;
	
	public Collection(FsNode node) {
		super(node);
		this.populateItems();
		this.populateImage();
		// TODO Auto-generated constructor stub
	}
	
	//Overwritten static function
	public static String getSystemName() {
		return systemName;
	}
	
	//Overwritten static function
	public static String getReadableName() {
		return readableName;
	}
	
	public static Collection createCollection(IRoleActor actor, String author, String name){
		System.out.println("Collection::createCollection()");
		FsNode collectionNode = new FsNode();
		Date date = new Date();
		collectionNode.setId(UUID.randomUUID().toString());
		collectionNode.setName("collection");
		collectionNode.setProperty("author", author);
		collectionNode.setProperty("name", name);
		collectionNode.setProperty("creationDate", date.toString());
			
		String path = actor.getNode().getPath()  + "/publications/1";
		Fs.insertNode(collectionNode, path);
		
		FsNode insertedNode = Fs.getNode(path + "/collection/" + collectionNode.getId());
		
		try {
			new Rights(insertedNode, actor);
		} catch (AlreadyHasRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Collection col = new Collection(insertedNode);
		
		return col;
	}
	
	@JSONField(field = "items")
	public List<EUScreenMediaItem> getItems(){
		return items;
	}
		
	private void populateItems(){
		FSList rawItems = FSListManager.get(this.getPath());
		List<FsNode> itemsList = rawItems.getNodes();
		
		this.items = new ArrayList<EUScreenMediaItem>();
		
		for(FsNode node : itemsList){	
			items.add(new EUScreenMediaItem(node));
		}
		
	}
	
	private void populateImage(){
		if(items.size() > 0){
			this.setImage((String) this.items.get(0).get("screenshot"));
		}
	}

	@Override
	@JSONField(field = "type")
	public String getType() {
		// TODO Auto-generated method stub
		return "collection";
	}

}
