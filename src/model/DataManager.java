package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataManager{
    public static final String DATA_FILE = "data/users.dat";
    
    private DataManager() {} //Don't isntantiate

    private static void ensureDataDirectory() {
        File dir = new File("data");
        if (!dir.exists()) { dir.mkdirs(); }
    }
    
    public static void saveUsers(UserManager manager) {
        ensureDataDirectory();

        try(ObjectOutputStream x = new ObjectOutputStream(
            new FileOutputStream(DATA_FILE))) {
                x.writeObject(manager);
            } catch (IOException e) {}
    }

    public static UserManager loadUsers() {
        UserManager manager;
        try (ObjectInputStream x = new ObjectInputStream (new FileInputStream(DATA_FILE))) {
            manager = (UserManager) x.readObject();
        } catch (FileNotFoundException e) { manager=new UserManager();
            manager = new UserManager();
        } catch (IOException | ClassNotFoundException e) {
            manager= new UserManager();
        }

        UserManager.setInstance(manager);
        return manager;
    }

    
}