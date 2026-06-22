package model;


/*
 * This class represents a library user.
 * Stores the user's ID, name and contact.
 */
public class Patron {
    private int id;
    private String name;
    private String contact;

    // Constructor
    public Patron(int id, String name, String contact){
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    // Setters and Getters
    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setContact(String contact){
        this.contact = contact;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getContact(){
        return this.contact;
    }

    // Created this method to help debug and display user information
    @Override
    public String toString(){
        return "["+id+"] " + name + " - " + contact;
    }

}
