





public class Album{
    String album_name; 
    List<Photo> album_photos; 

    public Album(String name){
        name = this.name; 
        album_photos = new List<Photo>; 
    }

    public void addPhoto(Photo photo){

    }
    
    
    
    
    
    
    
    
    public String getName(){
        return name;
    }

    public List<Photo> getPhotos{
        return album_photos; 
    }

    public int num_photos(){
        return album_photos.size(); 
    }

    public boolean containsPhoto(Photo photo){
        album_photos.contains(photo); 
    }

    public boolean addPhoto(Photo photo){
        if(!containsPhoto(photo)){
            return false; 
        } 
        album_photos.add(photo); 
        return true; 
    }

    public boolean removePhoto(Photo photo){
        if(containsPhoto(photo)){
            album_photos.remove(photo);
            return true; 
        }
        return false; 
    }

    public 
    



}