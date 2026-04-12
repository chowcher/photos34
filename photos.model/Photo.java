package photos.model; 

import java.util.*; 
import java.io.Serializable; 
import java.io.File; 


public class Photo {
    private static final long serialVersionUID = 1L; 
    
    private final String filePath; 
    private String caption; 
    private final List<Tag> tags; 
    private final Calendar captureDate; 

    public Photo(String filePath){
        if(filePath == null || filePath.isBlank()){
            throw new IllegalArgumentException("filePath cannot be null or blank"); 
        }

        File file = new File(filePath); 
        if(!file.exists()){
            throw new IllegalArgumentException("file does not exist" + filePath); 
        }


        this.filePath = filePath; 
        caption = ""; 
        tags = new List<Tag>(); 

        // last modified time - captures time of insertion
        long millis = new File(filePath).lastModified(); 
        captureDate = Calendar.getInstance(); 
        capture.setTimeInMillis(millis); 
        captureDate.set(Calendar.MILLISECOND, 0); 
    }


    // ************** GETTERS ************************************
    public String getFilePath(){ return filePath; }

    public String getCaption() {return caption;}

    public Calendar getCaptureDate(){ return captureDate;}
    
    public List<Tag> getTags(){ return Collections.unmodifiableList(tags);}
    
    // ****************************************************************
    
    
    
    public void setCaption(){
        this.caption = (caption == null) ? "" : caption.trim(); 
    }

    
    public boolean addTag(Tag tag, List<String> singleValueTypes){
        if(tag == null){
            throw new IllegalArgumentException("Tag cannot be null"); 
        }
        
        if(tags.contains(tag)){
            return false; 
        }
        
        if(singleValueTypes != null){
            boolean isSingleValue = singleValueTypes.stream()
                .anyMatch(type -> type.equalsIgnoreCase(tag.getName())); 

            if(isSingleValue){
                boolean alreadyHasType = tags.stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase(tag.getName())); 
                if(alreadyHasType){
                    return false; 
                }
            }
        }
        tags.add(tag); 
        return true; 
    }

    public boolean removeTag(Tag tag){
        if(tag == null){
            throw new IllegalArgumentException("Tag cannot be null"); 
        }
        
        if(!tags.contains(tag)){
            return false;
        }
        
        tags.remove(tag);
        return true; 
    }

    public boolean hasTag(String name, String value){
        Tag possible_tag = new Tag(name, value); 
        return tags.contains(possible_tag); 
    }


    public List<Tag> getTagsByName(String name){
        if(name == null || name.isBlank()){
            return Collections.emptyList(); 
        }

        List<Tag> result = new ArrayList<>(); 
        for(Tag tag : tags){
            if(tag.getName().equalsIgnoreCase(name)){
                result.add(tag); 
            }
        }
        
        return result; 

    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true; 
        if(!(o instanceof Photo)) return false; 
        Photo other = (Photo) o; 
        return this.filePath.equals(other.filePath); 
    } 

    @Override
    public int hashCode(){
        return Objects.hash(filePath); 
    }

    @Override
    public String toString(){
        String filename = new File(filePath).getName(); 
        if(caption == null || caption.isBlank()){
            return filename; 
        }
        return filename + "\n(" + caption + ")";
    }

}

