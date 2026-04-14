package photos.model;

import java.io.Serializable;
import java.util.*; 

public class Tag implements Serializable{
    
    private static final long serialVersionUID = 1L; 
    
    private final String name; 
    private final String value; 

    /**
     * new tag is created with given name and value 
     * all strings normalized to lowercase to ensure consistent cross application comparison
     * 
     * @param name is the tag type eg. "location" or "person"
     * @param value this is tag contents eg. "United States" or "Harry"
     * 
     * 
     */


    public Tag(String name, String value){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Tag name cannot be null or blank");
       }
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("Tag value cannot be null or blank");
        }
       
       this.name = name.trim().toLowerCase(); 
       this.value = value.trim(); 
    }

    public String getName(){
        return name; 
    }

    public String getValue(){
        return value; 
    }

    @Override 
    public boolean equals(Object o){
        if(this == o) return true; 
        if(!(o instanceof Tag)) return false; 
        Tag other = (Tag) o; 
        return this.name.equalsIgnoreCase(other.name) &&
               this.value.equalsIgnoreCase(other.value); 
    }

    @Override
    public int hashCode(){
        return Objects.hash(name.toLowerCase(), value.toLowerCase()); 
    }

    @Override
    public String toString(){
        return name + "=" + value; 
    }


    


}