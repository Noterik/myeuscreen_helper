package org.springfield.lou.myeuscreen.publications;

public enum PublicationType {
	ALL (GenericPublication.class),
	BOOKMARK (Bookmark.class),
	COLLECTION (Collection.class),
	VIDEOPOSTER (VideoPoster.class);
	
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
	
	public PublicationSettings getSettings(){
		return type.getAnnotation(PublicationSettings.class);
	}

	public static PublicationType getTypeForPublication(Publication p) {
		for(PublicationType type : PublicationType.values()){
			if(p.getClass().isAssignableFrom(type.getTypeClass())){
				return type;
			}
		}
		return null;
	}
	
}
