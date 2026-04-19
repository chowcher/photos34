package model;

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
        initializeDefaults();
    }

    private void initializeDefaults() {
        if(!userExists("admin")) {
            users.add(new User("admin"));
        }

        if(!userExists("stock")) {
            User stockUser = new User("stock");
            Album stockAlbum = new Album("stock");

            String[] stockFileNames = {
                "Unknown.jpeg",
                "Unknown-1.jpeg",
                "Unknown-2.jpeg",
                "Unknown-3.jpeg",
                "Unknown-4.jpeg",
            };

            // Stock photos are bundled as classpath resources under /model/data/.
            // Photo requires a real filesystem path, so we extract each resource
            // into a persistent "data/" directory next to the running app on first launch.
            java.io.File dataDir = new java.io.File("data");
            dataDir.mkdirs();

            for (String name : stockFileNames) {
                java.io.File dest = new java.io.File(dataDir, name);
                // Only copy if not already extracted
                if (!dest.exists()) {
                    try (java.io.InputStream in =
                            UserManager.class.getResourceAsStream("/model/data/" + name)) {
                        if (in != null) {
                            java.nio.file.Files.copy(in, dest.toPath());
                        }
                    } catch (java.io.IOException e) { /* skip this photo */ }
                }
                try {
                    stockAlbum.addPhoto(new Photo(dest.getAbsolutePath()));
                } catch (IllegalArgumentException e) { }
            }

            stockUser.addAlbum(stockAlbum);
            users.add(stockUser);
        }
    }

    public static UserManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserManager not initialized.");}
        return instance;
    }

    public static void setInstance(UserManager manager) {
        instance = manager;
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
        if (username == null || username.isBlank()) return null;
        String norm = username.trim().toLowerCase();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(norm)) {
                return u;
            }
        }
        return null;
    }

    public boolean userExists(String username){
        return getUser(username)!=null;
    }
}