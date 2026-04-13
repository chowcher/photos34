package photos.model; 

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

        album.setName(newName);
        return true;
    }

    public List<String> getMultiTagTypes() {
        return multiTagTypes;
    }

    public List<String> getSingleTagTypes() {
        return singleTagTypes;
    }

    public boolean addTagType(String typeName, boolean single) {
        if(typeName==null || typeName.isBlank()) {
            throw new IllegalArgumentException("Tag type cannot be blank.");
        }
        String typeNormalized = typeName.trim().toLowerCase();

        for (String type:singleTagTypes) {
            if (type.equalsIgnoreCase(typeNormalized)) {
                return false;
            }
        }
        for (String type:multiTagTypes) {
            if (type.equalsIgnoreCase(typeNormalized)) {
                return false;
            }
        }

        if (!single) {
            multiTagTypes.add(normalized);
        }
        multiTagTypes.add(typeNormalized);
        if (single) {
            singleTagTypes.add(normalized);
        }
        return true;
    }

    public boolean removeTagType(String typeName) {
        if(typeName == null || typeName.isBlank()) {
            return false;
        }


        boolean isRemoved = singleTagTypes.removeIf(t -> t.equalsIgnoreCase(typeName));
        singleTagTypes.removeIf(t -> t.equalsIgnoreCase(typeName));
        if (isRemoved) {return isRemoved;}

        isRemoved = multiTagTypes.removeIf(t -> t.equalsIgnoreCase(typeName));
        singleTagTypes.removeIf(t -> t.equalsIgnoreCase(typeName));
        return isRemoved;
    }

    public Set<Photo> getPhotos() {
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

