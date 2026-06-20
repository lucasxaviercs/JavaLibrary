package JavaLibrary.model;

public class Patron {
    private int id;
    private String name;
    private String contact;

    public Patron(int id, String name, String contact){
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

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

    @Override
    public String toString(){
        return "["+id+"] " + name + " - " + contact;
    }

}
