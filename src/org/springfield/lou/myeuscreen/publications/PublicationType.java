package org.springfield.lou.myeuscreen.publications;

public enum PublicationType {
	ALL (GenericPublication.class),
	COLLECTION (Collection.class),
	TEASER (Teaser.class);
	
	private final Class<? extends Publication> type;
	PublicationType(Class<? extends Publication> type){
		this.type = type;
	}
	
	public Class<? extends Publication> getTypeClass(){
		return type;
	}
	
	public static PublicationType getByName(String name){
		for(PublicationType type : PublicationType.values()){
			if(type.name().toLowerCase().equals(name.toLowerCase())){
				return type;
			}
		}
		
		return null;
	}
}
