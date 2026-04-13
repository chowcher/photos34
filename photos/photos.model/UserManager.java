package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserManager implements Serializable {
    private static final long serialVersionUID=1L;
    private final List<User> users;
    private static UserManager instance;

    public UserManager(){
        this.users=new ArrayList<>();
        
        if(!userExists("admin")) {
            users.add(new User("admin"));
        }

        if(!userExists("stock")) {
            User stockUser = new User("stock");
            Album stockAlbum = new Album("stock");

            String[] stockFiles = { //TODO: Add stock photos
                "data/stock1.jpg",
                "data/stock2.jpg",
                "data/stock3.jpg",
                "data/stock4.jpg",
                "data/stock5.jpg",
            };

            for (String path : stockFiles){
                try {
                    stockAlbum.addPhoto(new Photo(path));
                } catch (IllegalArgumentException e) { }
            }

            stockUser.addAlbum(stockAlbum);
            users.add(stockUser);
        }
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public User addUser(String username){
        if(username==null||username.isBlank()) {return null;}
        String norm = username.trim().toLowerCase();

        if(norm.equals("admin")||norm.equals("stock")
        ||userExists(norm)){
            return null;
        }

        User newUser = new User(norm);
        users.add(newUser);
        return newUser;
    }

    public boolean removeUser(String username) { 
        if (username==null||username.isBlank()){
            return false;
        }
        String norm = username.trim().toLowerCase();

        if(norm.equals("admin")||norm.equals("stock")) {
            return false;
        }

        return users.removeIf(u->u.getUsername().equalsIgnoreCase(norm));
    }

    public User getUser(String username) {
        //to add
        User temp = new User("temp");
        return temp;
    }

    public boolean userExists(String username){
        return getUser(username)!=null;
    }
}