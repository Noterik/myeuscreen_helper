package org.springfield.lou.myeuscreen.publications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.Role;

@PublicationSettings(systemName = "collection", readableName = "Collection", readablePlural = "Collections")
public class Collection extends Publication{
	private List<EUScreenMediaItem> items;
	
	public Collection(){
		super();
		this.populateItems();
		this.populateImage();
	}
	
	public Collection(FsNode node) {
		super(node);
		System.out.println("new Collection()");
		this.populateItems();
		this.populateImage();
		// TODO Auto-generated constructor stub
	}
	
	public Collection(FsNode node, String parent){
		super(node, parent);
		System.out.println("new Collection()");
		this.populateItems();
		this.populateImage();
	}
	
	public static Collection createCollection(IRoleActor actor, String author, String name){
		System.out.println("Collection.createCollection(" + actor + ", " + author + ", " + name + ")");
		Collection col = new Collection(); 
		Date date = new Date();
		col.setAuthor(author);
		col.setName(name);
		col.setCreationDate(date.toString());
		col.setParent(actor.getNode().getPath() + "/publications/1");
		try {
			col.getRights().giveRole(actor, Role.OWNER);
		} catch (AlreadyHasRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("END Collection.createCollection()");
		return col;
	}
	
	@JSONField(field = "items")
	public List<EUScreenMediaItem> getItems(){
		return items;
	}
	
	public boolean contains(String id){
		for(EUScreenMediaItem item : this.items){
			if(item.getNode().getId().equals(id)){
				return true;
			}
		}
		
		return false;
	}
	
	public void addItem(EUScreenMediaItem item){
		System.out.println("Collection.addItem(" + item + ")");
		if(!this.contains(item.getNode().getId()) && this.getPath() != null){
			System.out.println("WE'RE CLEAR, LETS ADD IT!");
			FsNode referringNode = new FsNode();
			referringNode.setId(item.getNode().getId());
			referringNode.setName(item.getNode().getName());
			referringNode.setReferid(item.getNode().getPath());
			
			Fs.insertNode(referringNode, this.getPath());
			
			this.items.add(item);
			this.update();
		}
	}
		
	private void populateItems(){
		try{
			FSList rawItems = FSListManager.get(this.getPath(), false);
			List<FsNode> itemsList = rawItems.getNodes();
			
			this.items = new ArrayList<EUScreenMediaItem>();
			
			for(FsNode node : itemsList){	
				if(node.getName() != "rights")
					items.add(new EUScreenMediaItem(node));
			}
		}catch(NullPointerException npe){
			this.items = new ArrayList<EUScreenMediaItem>();
		}
		
	}
	
	private void populateImage(){
		try{
			this.setImage((String) this.items.get(1).get("screenshot"));
		}catch(IndexOutOfBoundsException e){
			this.setImage(null);
		}
	}

	@Override
	@JSONField(field = "type")
	public String getType() {
		// TODO Auto-generated method stub
		return "collection";
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		System.out.println("UPDATE CALLED");
		System.out.println("OBSERVERS: " + this.getObservers().size());
		super.update();
	}
	
	

}
