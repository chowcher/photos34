package photos.model; 

import java.util.List;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Album implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    String album_name; 
    ArrayList<Photo> album_photos; 

    /**
     * 
     * @param name sets name of album
     * list is initialized to store photos
     * @throws IllegalArgumentException if album name is not provided
     *
     */

    public Album(String name){
        if(name==null || name.isBlank()) {
            throw new IllegalArgumentException("Album name cannot be blank");
        }
        name = this.name;
        album_photos = new ArrayList<Photo>(); 
    }
    
    public String getName(){
        return name;
    }

    public void setName(String name){
        if (name==null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        this.album_name = name.trim();
    }

    public List<Photo> getPhotos(){
        return album_photos; 
    }

    public int num_photos(){
        return album_photos.size(); 
    }

    public boolean containsPhoto(Photo photo){
        return album_photos.contains(photo);
    }

    public boolean addPhoto(Photo photo){
        if (photo == null) {
            throw new IllegalArgumentException("Photo cannot be null");
        }
        if(containsPhoto(photo)){
            return false; 
        } 
        album_photos.add(photo); 
        return true; 
    }

    public boolean removePhoto(Photo photo){
        if(photo==null) {
            throw new IllegalArgumentException("Photo cannot be null");
        }
        if(containsPhoto(photo)){
            album_photos.remove(photo);
            return true; 
        }
        return false; 
    }    

public Calendar getEarliestDate(){
    if (album_photos.isEmpty()==true) {
        return null;
    }

    Calendar earliestDate = album_photos.get(0).getCaptureDate();
    for (Photo photo: album_photos) {
        if(photo.getCaptureDate().before(earliestDate)) {
            earliestDate = photo.getCaptureDate();
        }
    }
    return earliestDate;
}

public Calendar getLatestDate(){
    if (album_photos.isEmpty()==true) {
        return null;
    }

    Calendar latestDate = album_photos.get(0).getCaptureDate();
    for (Photo photo: album_photos) {
        if(photo.getCaptureDate().after(latestDate)) {
            latestDate = photo.getCaptureDate();
        }
    }
    return latestDate;
}

public String getDateRange() {
    Calendar earliest = getEarliestDate();
    Calendar latest = getLatestDate();
    if(earliest==null) {return "No photos";}

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
    if(sdf.format(earliest.getTime()).equals(sdf.format(latest.getTime()))) {
        return sdf.format(earliest.getTime());
    }
    else{
        return sdf.format(earliest.getTime()) + " - " + sdf.format(latest.getTime());
    }
}

@Override
public boolean equals(Object o) {
    if (this==o) {
        return true;
    }

    if(!(o instanceof Album)) {
        return false;
    }

    Album eq = (Album) o;
    return this.album_name.equalsIgnoreCase(eq.album_name);
}

@Override
public int hashCode() {
    return album_name.toLowerCase().hashCode();
}

@Override
public String toString() {
    return album_name + " (" + num_photos() + " photo" +
        (album_photos.size() == 1 ? "" : "s") + ")";
}

}