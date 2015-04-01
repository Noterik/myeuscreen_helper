package org.springfield.lou.myeuscreen.publications;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.myeuscreen.mapping.MappedObject;
import org.springfield.lou.myeuscreen.rights.AlreadyHasRoleException;
import org.springfield.lou.myeuscreen.rights.IRoleActor;
import org.springfield.lou.myeuscreen.rights.IncorrectRightsNodeFormatException;
import org.springfield.lou.myeuscreen.rights.NodeWithRights;
import org.springfield.lou.myeuscreen.rights.Rights;
import org.springfield.lou.myeuscreen.rights.Role;

public abstract class MappedObjectWithRights extends MappedObject implements NodeWithRights {
	
	private Rights rights;

	public MappedObjectWithRights(FsNode node) {
		super(node);
		String cleanedPath = node.getPath().replace("//", "/");
		
		FsNode rightsNode = Fs.getNode(cleanedPath + "/rights/1");
		
		this.rights = null;
		try {
			this.rights = new Rights(rightsNode);
		} catch (IncorrectRightsNodeFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void giveRole(IRoleActor user, Role role) throws AlreadyHasRoleException {
		// TODO Auto-generated method stub
		rights.giveRole(user, role);
	}

	@Override
	public Rights getRights() {
		// TODO Auto-generated method stub
		return rights;
	}

}
