package org.springfield.lou.myeuscreen.publications;

import org.springfield.fs.FsNode;
import org.springfield.lou.myeuscreen.mapping.MappedObject;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;
import org.springfield.lou.myeuscreen.mapping.SmithersReference;

@MappingSettings(systemName = "collectionitem")
public class CollectionItem extends MappedObject{ 
	private EUScreenMediaItem item;
	
	public CollectionItem(){}
	
	public CollectionItem(FsNode node, String parent){
		super(node, parent);
	}
	
	public CollectionItem(FsNode node){
		super(node);
	}
	
	public EUScreenMediaItem getEUScreenItem(){
		return item;
	}

	@Override
	public String getNodeName() {
		// TODO Auto-generated method stub
		return "collectionitem";
	}
	
	public void setItem(EUScreenMediaItem item){
		this.item = item;
	}
	
	@SmithersReference
	public FsNode getEUScreenItemReference(){
		return item.getNode();
	}
	
	public static CollectionItem createCollectionItem(Collection parent, String id, EUScreenMediaItem item){
		CollectionItem colItem = new CollectionItem();
		colItem.setId(id);
		colItem.setItem(item);
		colItem.setParent(parent.getPath());
		return colItem;
	}
	
}
