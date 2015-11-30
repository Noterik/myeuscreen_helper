package org.springfield.lou.myeuscreen.publications;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.simple.JSONObject;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.IJSONObserver;
import org.springfield.lou.myeuscreen.mapping.MappingSettings;
import org.springfield.lou.myeuscreen.mapping.NoParentForMappedObjectException;
import org.springfield.lou.myeuscreen.mapping.ReferencedNodeNotExistsException;
import org.springfield.lou.myeuscreen.pagination.PaginatedArrayList;
import org.springfield.lou.myeuscreen.publications.Publication;
import org.springfield.lou.myeuscreen.publications.PublicationType;
import org.springfield.lou.myeuscreen.rights.IRoleActor;

public class PublicationsBox extends Observable implements IJSONObserver{
	private HasPublicationsBox owner;
	private String path;
	private FsNode node;
	private HashMap<PublicationType, PaginatedArrayList<Publication>> publications;
	
	public PublicationsBox(FsNode node, HasPublicationsBox owner){
		this.node = node;
		this.path = node.getPath();
		this.owner = owner;
		populate();
	}
	
	public PublicationsBox(String path, HasPublicationsBox owner){
		if(Fs.getNode(path) != null){
			this.node = Fs.getNode(path);
		}
		this.path = path;
		this.owner = owner;
		populate();
	}
	
	private void populate(){
		System.out.println("PublicationsBox.populate()");
		this.publications = new HashMap<PublicationType, PaginatedArrayList<Publication>>();
		
		PublicationType[] publicationTypes = PublicationType.values();
		PaginatedArrayList<Publication> allPublications = new PaginatedArrayList<Publication>();
		
		for(PublicationType type : publicationTypes){	
			if(type != PublicationType.ALL){
				try {
					Class<? extends Publication> typeClass = type.getTypeClass();
					MappingSettings systemMapping = typeClass.getAnnotation(MappingSettings.class);
					
					String systemName = null;
					if(systemMapping != null){
						systemName = systemMapping.systemName();
					}
					
					if(systemName == null){
						systemName = typeClass.getName().toLowerCase();
					}
					
					String uri = this.path + "/" + systemName;
					
					PaginatedArrayList<Publication> paginatedPublications = new PaginatedArrayList<Publication>();
					
					List<FsNode> publicationsList = Fs.getNodes(uri, 2);
										
					this.publications.put(type, paginatedPublications);
					//FSList publications = FSListManager.get(uri, false);
					
					if(publicationsList != null && publicationsList.size() > 0){
						for(FsNode pNode : publicationsList){
							Constructor<? extends Publication> constructor = typeClass.getConstructor(FsNode.class, String.class);
							String actualPNodePath = pNode.getPath().replace("//", "/");
							FsNode actualPNode = Fs.getNode(actualPNodePath);
							if(actualPNode.getReferid() != null){
								actualPNode = Fs.getNode(actualPNode.getReferid());
							}
							Publication newP = constructor.newInstance(actualPNode, this.path);
							System.out.println("NEW PUBLICATION: " + newP);
							newP.addObserver(this);
							paginatedPublications.add(newP);
							allPublications.add(newP);
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		this.publications.put(PublicationType.ALL, allPublications);
	}
	
	public HashMap<PublicationType, PaginatedArrayList<Publication>> getPublications(){
		return this.publications;
	}
	
	public Publication getPublicationByPath(String path){
		for(PublicationType pubType : this.publications.keySet()){
			List<Publication> pubsForType = this.publications.get(pubType);
			for(Publication p : pubsForType){
				if(p.getPath().equals(path)){
					return p;
				}
			}
		}
		return null;
	}
	
	public void addPublication(Publication p) throws NoParentForMappedObjectException{
		PublicationType pType = PublicationType.getTypeForPublication(p);
		
		for(PublicationType type : this.publications.keySet()){
			PaginatedArrayList<Publication> list = this.publications.get(type);
			
			for(Publication cp : list){

				if(cp.getId().equals(p.getId())){
					deletePublication(cp);
					break;
				}
			}
		}
		
		this.publications.get(pType).add(p);
		this.publications.get(PublicationType.ALL).add(p);
		p.save(this.node);
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addPublication(Publication p, boolean refer) throws ReferencedNodeNotExistsException, NoParentForMappedObjectException{
		if(refer){
			String pTypeStr = p.getType();
			PublicationType pType = PublicationType.getByName(pTypeStr);
			if(this.publications.get(pType) == null){
				this.publications.put(pType, new PaginatedArrayList<Publication>());
			}
			this.publications.get(pType).add(p);
			this.publications.get(PublicationType.ALL).add(p);
			p.createReference(this.node);
			this.setChanged();
			this.notifyObservers();
		}else{
			this.addPublication(p);
		}
		
	}
	
	public void deletePublication(Publication p){
		PaginatedArrayList<Publication> listToRemoveFrom = null;
		Publication pubToRemove = null;

		for(PublicationType type : this.publications.keySet()){
			PaginatedArrayList<Publication> list = this.publications.get(type);
			for(Publication cp : list){
				if(cp.getPath().equals(p.getPath())){
					pubToRemove = cp;
					listToRemoveFrom = list;
				}
			}
		}
		
		if(listToRemoveFrom != null && pubToRemove != null){
			String uriToRemove = this.path + "/" + pubToRemove.getType() + "/" + pubToRemove.getId();
			Fs.deleteNode(uriToRemove);
			
			PaginatedArrayList<Publication> allList = this.publications.get(PublicationType.ALL);
		
			String pTypeStr = p.getType();
			PublicationType pType = PublicationType.getByName(pTypeStr);
			PaginatedArrayList<Publication> pList = this.publications.get(pType);

			allList.remove(pubToRemove);
			pList.remove(pubToRemove);
			listToRemoveFrom.remove(pubToRemove);
		
			this.publications.remove(p);
			this.setChanged();
			this.notifyObservers();
			
			if(owner instanceof IRoleActor){
				IRoleActor ownerActor = (IRoleActor) owner;
				p.getRights().removeRightsForActor(ownerActor);
			}
		}
	}

	@Override
	public void update(JSONObject arg0) {
		// TODO Auto-generated method stub
		this.setChanged();
		this.notifyObservers();
	}
}
