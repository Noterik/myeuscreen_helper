package org.springfield.lou.myeuscreen.publications;

import java.util.List;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.myeuscreen.mapping.MappedObject;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;
import org.springfield.lou.myeuscreen.mapping.SmithersReference;

@MappingSettings(systemName = "collectionitem")
public class CollectionItem extends MappedObject{ 
	private EUScreenMediaItem item;
	private int order;
	
	public CollectionItem(){}
	
	public CollectionItem(FsNode node, String parent){
		super(node, parent);
		try{
			this.order = Integer.parseInt(node.getProperty("myeuscreen_child_order"));
		}catch(NumberFormatException nfe){
			nfe.printStackTrace();
			this.order = 9999;
		}
		this.item = getReferedVideo(node);
	}
	
	public CollectionItem(FsNode node){
		super(node);
		try{
			this.order = Integer.parseInt(node.getProperty("myeuscreen_child_order"));
		}catch(NumberFormatException nfe){
			nfe.printStackTrace();
			this.order = 9999;
		}
		this.item = getReferedVideo(node);
	}
	
	private EUScreenMediaItem getReferedVideo(FsNode node){
		List<FsNode> referedNodes = Fs.getNodes(node.getPath() + "/video", 1);
		if(referedNodes.size() > 0){
			FsNode referNode = Fs.getNode(referedNodes.get(0).getReferid());
			return new EUScreenMediaItem(referNode);
		}
		return null;
	}
	
	@JSONField(field = "item")
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
	
	@JSONField(field = "order")
	public int getOrder(){
		return this.order;
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
