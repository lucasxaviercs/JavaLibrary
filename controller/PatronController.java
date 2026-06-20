package controller;

import model.Patron;
import persistence.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatronController {
    private List<Patron> patrons;
    private int nextId;

    public PatronController(){
        try{
            patrons = FileManager.loadPatrons();
        }catch(IOException e){
            patrons = new ArrayList<>();
        }

        nextId = patrons.stream().mapToInt(Patron::getId).max().orElse(0) + 1;
    }

    public Patron findById(int id){
        for(Patron patron : patrons){
            if(patron.getId() == id){
                return patron;
            }
        }
        return null;
    }

    public void addPatron(String name, String contact) throws IOException{
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("The user's name can not be empty");
        }

        Patron patron = new Patron(nextId++, name.trim(), contact.trim());
        patrons.add(patron);
        FileManager.savePatrons(patrons);
    }

    public void editPatron(int id, String newName, String newContact) throws IOException{
        Patron patron = findById(id);

        if(patron == null){
            throw new IllegalArgumentException("User id " + id + " not found.");
        }

        if(newName == null || newName.trim().isEmpty()){
            throw new IllegalArgumentException("The user's name can not be empty.");
        }

        patron.setName(newName.trim());
        patron.setContact(newContact.trim());
        FileManager.savePatrons(patrons);
    }

    public void deletePatron(int id) throws IOException{
        Patron patron = findById(id);

        if(patron == null){
            throw new IllegalArgumentException("User id " + id + " not found.");
        }

        patrons.remove(patron);
        FileManager.savePatrons(patrons);
    }

    public List<Patron> searchPatrons(String query){
        if(query == null || query.trim().isEmpty()){
            return new ArrayList<>(patrons);
        }

        String q = query.trim().toLowerCase();

        List<Patron> result = new ArrayList<>();
        
        for(Patron patron : patrons){
            if(patron.getName().toLowerCase().contains(q) || String.valueOf(patron.getId()).contains(q)){
                result.add(patron);
            }
        }

        return result;
    }

    public List<Patron> getAllPatrons(){
        return new ArrayList<>(patrons);
    }
}
