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

    public void addPatron(String name, String contact) throws IOException{
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("The user's name can not be empty");
        }

        Patron patron = new Patron(nextId++, name.trim(), contact.trim());
        patrons.add(patron);
        FileManager.savePatrons(patrons);
    }
}
