package org.springfield.lou.myeuscreen.publications;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.myeuscreen.mapping.MappedObject;
import org.springfield.lou.myeuscreen.mapping.NoParentForMappedObjectException;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.IncorrectRightsNodeFormatException;
import org.springfield.lou.myeuscreen.rights.NodeWithRights;
import org.springfield.lou.myeuscreen.rights.Rights;
import org.springfield.lou.myeuscreen.rights.Role;

public abstract class MappedObjectWithRights extends MappedObject implements NodeWithRights {
	
	private Rights rights;
	
	public MappedObjectWithRights(){
		super();
		
		this.rights = new Rights();
	}
	
	public MappedObjectWithRights(FsNode node, String parent){
		super(node, parent);
		
		String cleanedPath = node.getPath().replace("//", "/");
		
		FsNode rightsNode = Fs.getNode(cleanedPath + "/rights/1");
		
		this.rights = null;
		try {
			if(rightsNode != null){
				this.rights = new Rights(rightsNode);
			}else{
				this.rights = new Rights(cleanedPath);
			}
		} catch (IncorrectRightsNodeFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MappedObjectWithRights(FsNode node) {
		super(node);
		String cleanedPath = node.getPath().replace("//", "/");
		
		FsNode rightsNode = Fs.getNode(cleanedPath + "/rights/1");
		
		this.rights = null;
		try {
			if(rightsNode != null){
				this.rights = new Rights(rightsNode);
			}else{
				this.rights = new Rights(cleanedPath);
			}
		} catch (IncorrectRightsNodeFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void giveRole(IRoleActor user, Role role) throws AlreadyHasRoleException {
		// TODO Auto-generated method stub
		rights.giveRole(user, role);
		rights.save();
	}

	@Override
	public Rights getRights() {
		// TODO Auto-generated method stub
		return rights;
	}
	
	public void save() throws NoParentForMappedObjectException{
		super.save();
		//System.out.println("MappedObjectWithRights.save()");
		Rights rights = this.getRights();
		rights.setParent(super.getPath());
		rights.save();
	}

}
