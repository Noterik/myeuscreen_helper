package org.springfield.lou.myeuscreen.rights;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.homer.LazyMarge;
import org.springfield.lou.homer.MargeObserver;

public class Rights implements MargeObserver{
	private String parentPath;
	private FsNode node;
	private Map<Role, ArrayList<IRoleActor>> roles;
	
	
	public Rights(FsNode parentNode, IRoleActor user) throws AlreadyHasRoleException{
		System.out.println("Rights()");
		roles = new HashMap<Role, ArrayList<IRoleActor>>();
		node = new FsNode();
		this.parentPath = parentNode.getPath();
		node.setName("rights");
		node.setId("1");
		this.giveRole(user, Role.OWNER);
	}
	
	public Rights(FsNode rightsNode) throws IncorrectRightsNodeFormatException{
		System.out.println("Rights(" + rightsNode.getPath() + ")");
		roles = new HashMap<Role, ArrayList<IRoleActor>>();
		this.parseNode(rightsNode);
		
		parentPath = "";
		
		String[] pathSplits = rightsNode.getPath().split("/");
		for(int i = 0; i < (pathSplits.length - 2); i++){
			parentPath += pathSplits[i];
		}
		
		LazyMarge.addObserver(rightsNode.getPath(), this);
	}
	
	public void giveRole(IRoleActor user, Role role) throws AlreadyHasRoleException{
		if(roles.get(role) != null){
			for(IRoleActor curActor : roles.get(role)){
				if(curActor.getNode().getPath().equals(user.getNode().getPath())){
					throw new AlreadyHasRoleException("Actor " + curActor.getNode().getPath() + " already has role " + role.getRoleId());
				}
			}
		}else{
			roles.put(role, new ArrayList<IRoleActor>());
		}
		
		this.roles.get(role).add(user);
		
		this.save();
	}
	
	
	public String toString(){
		return this.roles.toString();
	}
	
	public void save(){
		String path = node.getPath();
		String creationDate = new Date().toString();
		if(path != null){
			creationDate = node.getProperty("creationDate");
			Fs.deleteNode(path);
		}
		
		
		node.setProperty("creationDate", creationDate);
		node.setProperty("lastUpdate", new Date().toString());
		
		Fs.insertNode(node, parentPath);
		
		for(Role role : roles.keySet()){
			ArrayList<IRoleActor> actorsForRole = roles.get(role);
			if(actorsForRole.size() > 0){
				FsNode roleNode = new FsNode();
				roleNode.setName("role");
				roleNode.setId(role.getRoleId());
				
				Fs.insertNode(roleNode, parentPath + "/rights/1");
				
				for(IRoleActor actor : actorsForRole){
					FsNode actorNode = new FsNode();
					actorNode.setName(actor.getNode().getName());
					actorNode.setId(UUID.randomUUID().toString());
					actorNode.setReferid(actor.getNode().getPath());
					
					Fs.insertNode(actorNode, parentPath + "/rights/1/role/" + role.getRoleId());
				}
			}
		}
	}
	
	private void refresh(){
		try {
			this.parseNode(Fs.getNode(node.getPath()));
		} catch (IncorrectRightsNodeFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseNode(FsNode rightsNode) throws IncorrectRightsNodeFormatException{
		if(rightsNode == null || !rightsNode.getName().equals("rights")){
			throw new IncorrectRightsNodeFormatException("The node name is not <rights>, please check if you're passing the correct node!");
		}else{
			FSList rolesFsList = FSListManager.get(rightsNode.getPath() + "/role", false);
			List<FsNode> rolesList = rolesFsList.getNodes();
			for(FsNode node : rolesList){
				String roleId = node.getId();
				FSList actorFsList = FSListManager.get(node.getPath(), false);
				List<FsNode> actorsList = actorFsList.getNodes();
				
				Role role = null;
				
				for(Role currentRole : Role.values()){
					if(currentRole.getRoleId().equals(roleId)){
						role = currentRole;
						break;
					}
				}
				
				for(FsNode actorNode : actorsList){
					IRoleActor actor = new RoleActor(actorNode);
					if(this.roles.get(role) == null){
						this.roles.put(role, new ArrayList<IRoleActor>());
					}
					this.roles.get(role).add(actor);
				}
			}
		}
	}

	@Override
	public void remoteSignal(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		refresh();
	}
}
