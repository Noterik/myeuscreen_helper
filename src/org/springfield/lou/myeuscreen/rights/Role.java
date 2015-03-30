package org.springfield.lou.myeuscreen.rights;

public enum Role{
	VIEWER ("viewer"),
	COMMENTER ("commenter"),
	EDITOR("editor"),
	OWNER("owner");
	
	private String id;
	
	Role(String id){
		this.id = id;
	}
	
	public String getRoleId(){
		return this.id;
	}
	
	public boolean equals(Role role){
		return role.getRoleId().equals(this.getRoleId());
	}
}
