package model; 

import java.io.Serializable;
import java.util.*;

public class User implements Serializable{ 
    
    private static final long serialVersionUID = 1L;

    private final String username; 
    private final List<Album> albums; 
    private final List<String> tagTypes;
    private final List<String> singleValueTagTypes;

    /**
     * initializes new user object 
     * @param username sets name of user
     * initializes list for single and multi tag types
     * adds location and person tag
     * initializes new list for albums
     */

    public User(String username){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Username cannot be null or blank");
       }
       
       this.username = username.trim().toLowerCase(); 
       this.albums = new ArrayList<>();
       this.tagTypes = new ArrayList<>();
       this.singleValueTagTypes = new ArrayList<>();

       tagTypes.add("location");
       tagTypes.add("person");
       singleValueTagTypes.add("location");
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

    public boolean removeAlbumByName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        return albums.removeIf(a -> a.getName().equalsIgnoreCase(name));
    }

    public Album getAlbumByName(String name) {
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

        Album album = getAlbumByName(albumName);
        if (album==null) {
            return false;
        }

        album.setName(newName);
        return true;
    }

    public List<String> getTagTypes() {
        return tagTypes;
    }

    public List<String> getSingleValueTagTypes() {
        return singleValueTagTypes;
    }


    public boolean addTagType(String typeName, boolean single) {
        if(typeName==null || typeName.isBlank()) {
            throw new IllegalArgumentException("Tag type cannot be blank.");
        }
        String typeNormalized = typeName.trim().toLowerCase();

        for (String type:tagTypes) {
            if (type.equalsIgnoreCase(typeNormalized)) {
                return false;
            }
        }

        tagTypes.add(typeNormalized);
        if (single) {
            singleValueTagTypes.add(typeNormalized);
        }
        return true;
    }

    public boolean removeTagType(String typeName) {
        if(typeName == null || typeName.isBlank()) {
            return false;
        }


        boolean removed = tagTypes.removeIf(t -> t.equalsIgnoreCase(typeName));
        singleValueTagTypes.removeIf(t -> t.equalsIgnoreCase(typeName));
        return removed;
    }

    public Set<Photo> getAllPhotos() {
        Set<Photo> photoList = new HashSet<>();
        for(Album album:albums) {
            photoList.addAll(album.getPhotos());
        }
        return photoList;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) {
            return true;
        }

        if (! (o instanceof User)) {
            return false;
        }

        User eq = (User) o;
        return this.username.equalsIgnoreCase(eq.username);
    }

    @Override
    public int hashCode() {
        return username.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return username;
    }

}

