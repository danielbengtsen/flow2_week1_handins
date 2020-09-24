
package dto;

import entities.Person;

public class PersonDTO {
    
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    //private Person person;

    public PersonDTO(Person p) {
        this.id = p.getId();
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
        this.phone = p.getPhone();
    }
    
    public PersonDTO(String fn, String ln, String phone) {
        this.firstName = fn;
        this.lastName = ln;
        this.phone = phone;
    }
    
    public PersonDTO() {
        
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        //this.person.setFirstName(firstName);
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        //this.person.setLastName(lastName);
    }

    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        //this.person.setPhone(phone);
    }
    
    
    
    
    
}
