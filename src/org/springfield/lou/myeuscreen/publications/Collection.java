package org.springfield.lou.myeuscreen.publications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;
import org.springfield.lou.myeuscreen.mapping.NoParentForMappedObjectException;
import org.springfield.lou.myeuscreen.mapping.ObjectChildrenToSmithersGetter;
import org.springfield.lou.myeuscreen.mapping.ObjectToSmithersGetter;
import org.springfield.lou.myeuscreen.mapping.SmithersToObjectChildrenSetter;
import org.springfield.lou.myeuscreen.mapping.SmithersToObjectSetter;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.Role;

@MappingSettings(systemName = "collection")
@PublicationSettings(readableName = "Collection", readablePlural = "Collections", editable = true, collectable = false)
public class Collection extends Publication {
	private List<CollectionItem> items;
	private String description;

	public Collection() {
		super();
		this.populateItems();
		this.populateImage();
	}

	public Collection(FsNode node) {
		super(node);
		this.populateItems();
		this.populateImage();
		// TODO Auto-generated constructor stub
	}

	public Collection(FsNode node, String parent) {
		super(node, parent);
		this.populateItems();
		this.populateImage();
	}

	public static Collection createCollection(IRoleActor actor, String author,
			String name, String description) {
		System.out.println("Collection.createCollection(" + actor + ", "
				+ author + ", " + name + ", " + description + ")");
		Collection col = new Collection();
		Date date = new Date();
		col.setAuthor(author);
		col.setName(name);
		col.setDescription(description);
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

	@ObjectToSmithersGetter(mapTo = "description")
	@JSONField(field = "description")
	public String getDescription() {
		return description;
	}

	@SmithersToObjectSetter(mapTo = "description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JSONField(field = "items")
	@ObjectChildrenToSmithersGetter(ordered = true)
	public List<CollectionItem> getItems() {
		return items;
	}
	
	@SmithersToObjectChildrenSetter(mapTo = "collectionitem", type=CollectionItem.class, ordered=true)
	public void setItems(List<CollectionItem> items){
		this.items = items;
	}

	public boolean contains(String id) {
		for (CollectionItem item : this.items) {
			try{
				if (item.getEUScreenItem().getNode().getId().equals(id)) {
					return true;
				}
			}
			catch(NullPointerException npe){
				return false;
			}
		}

		return false;
	}

	public void addItem(EUScreenMediaItem item) {
		System.out.println("Collection.addItem(" + item + ")");
		if (!this.contains(item.getNode().getId()) && this.getPath() != null) {
			CollectionItem colItem = CollectionItem.createCollectionItem(this,
					item.getNode().getId(), item);
			this.items.add(colItem);
			this.update();
		}
	}
	
	public void changeItemPos(CollectionItem item, int newPos){
		this.items.remove(item);
		this.items.add(newPos, item);	
		try {
			this.save();
		} catch (NoParentForMappedObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateItems() {
		System.out.println("POPULATE ITEMS!");
		this.items = new ArrayList<CollectionItem>();
		try {
			FSList rawItems = FSListManager.get(this.getPath() + "/collectionitem", false);
			List<FsNode> itemsList = rawItems.getNodes();

			this.items = new ArrayList<CollectionItem>();

			for (FsNode node : itemsList) {
				if (node.getName() != "rights")
					items.add(new CollectionItem(node));
			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

	}

	private void populateImage() {
		try {
			this.setImage((String) this.items.get(1).getEUScreenItem()
					.get("screenshot"));
		} catch (IndexOutOfBoundsException e) {
			this.setImage(null);
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		super.update();
	}

}
