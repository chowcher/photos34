package photos; 

import java.util.*; 
import java.io.Serializable;

public class User implements Serializable{ 
    
    private static final long serialVersionUID = 1L;

    private final String username; 
    private final List<Album> albums; 
    private final List<String> multiTagTypes;
    private final List<String> singleTagTypes;

    public User(String username){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Username cannot be null or blank");
       }
       
       this.username = username.trim().toLowerCase(); 
       this.albums = new ArrayList<>();
       this.multiTagTypes = new ArrayList<>();
       this.singleTagTypes = new ArrayList<>();

       singleTagTypes.add("location");
       multiTagTypes.add("person");
    }

    public String getUsername() {
        return username;
    }

    public List<Album> getAlbums()  {
        return albums;
    }

    public boolean addAlbum(Album album) {
        if (album==null) {
            throw new IllegalArgumentException("Album cannot be null");
        }

        for (Album current : albums) {
            if (current.getName().equalsIgnoreCase(album.getName())) {
                return false;
            }
        }

        albums.add(album);
        return true;
    }

    public boolean removeAlbum(Album album) {
        if(album==null) {
            throw new IllegalArgumentException("Album cannot be null");
        }
        return albums.remove(album);
    }

    public boolean removeAlbum(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        return albums.removeIf(a -> a.getName().equalsIgnoreCase(name));
    }

    public Album getAlbum(String name) {
        if(name == null || name.isBlank()) {
            return null;
        }

        for (Album album: albums) {
            if(album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }

        return null;
    }

    public boolean renameAlbum(String albumName, String newName) {
        if(albumName == null || albumName.isBlank()) {
            throw new IllegalArgumentException("Album name cannot be null");
        }
       if(newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("New name cannot be null");
        }
        
        for (Album album: albums) {
            if (album.getName().equalsIgnoreCase(newName)
                && !album.getName().equalsIgnoreCase(albumName))
                {
                    return false;
                }
        }

        Album album = getAlbum(albumName);
        if (album==null) {
            return false;
        }

        return albums.setName(newName);
        return true;
    }

/*
    add getMultiTagTypes(), getSingleTagTypes(),
    add tagType(), removeTagType(), getPhotos(),
    equals(), a hashcode func, 
*/

}

