package org.springfield.lou.myeuscreen.publications;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class EUScreenMediaItem extends JSONObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8711495459997210363L;
	private FsNode node;
	
	public EUScreenMediaItem(FsNode node){
		this.node = node;
		
		if(node.getReferid() != null){
			this.node = Fs.getNode(node.getReferid());
		}
		
		parseFields();
	}
	
	public FsNode getNode(){
		return this.node;
	}
	
	private void parseFields(){
		HashMap<String, String> fields = FieldMappings.getMappings();
		for(String key : fields.keySet()){
			if(node.getProperty(fields.get(key)) != null){
				this.put(key, node.getProperty(fields.get(key)));
			}
		}
	}
	
}
