package org.springfield.lou.myeuscreen.rights;

public interface NodeWithRights {
	public void giveRole(IRoleActor user, Role role) throws AlreadyHasRoleException;
	public Rights getRights();
}
