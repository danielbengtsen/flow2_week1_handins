package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import dto.PersonsDTO;
import utils.EMF_Creator;
import facades.PersonFacade;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    
    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    
    private static final PersonFacade FACADE =  PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Success!\"}";
    }
    
    @Path("filldb")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String fillDB() {
        FACADE.fillDB();
        return "{\"msg\":\"Success!\"}";
    }
    
    @Path("getperson/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPerson(@PathParam("id") int id) {
        PersonDTO pDTO = FACADE.getPerson(id);
        return GSON.toJson(pDTO);
    }
    
    @Path("getallpersons")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersons() {
        PersonsDTO persons = FACADE.getAllPersons();
        return GSON.toJson(persons);
    }
    
    @Path("addperson")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String addPerson() {
        PersonDTO pDTO = FACADE.addPerson("Hopla", "Dopla", "86957384");
        return GSON.toJson(pDTO);
    }
    
    @Path("editperson/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String editPerson(@PathParam("id") int id) {
        PersonDTO pDTO = FACADE.editPerson(FACADE.getPerson(id));
        return GSON.toJson(pDTO);
    }
    
    @Path("deleteperson/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") int id) {
        PersonDTO pDTO = FACADE.deletePerson(id);
        return GSON.toJson(pDTO);
    }
}
