package it.polito.tdp.artsmia;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ArtsmiaController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnArtistiConnessi;

    @FXML
    private Button btnCalcolaPercorso;

    @FXML
    private ComboBox<String> boxRuolo;

    @FXML
    private TextField txtArtista;

    @FXML
    private TextArea txtResult;

    @FXML
    void doArtistiConnessi(ActionEvent event) {
    
    	txtResult.clear();
    	List<Adiacenza> connessi = new ArrayList<>(this.model.getArtistiConnessi());
    	txtResult.appendText("ARTISTI CONNESSI: \n");
    	for(Adiacenza a: connessi) {
    		txtResult.appendText( a.getA1()+ " --- " + a.getA2() + " #Esposizioni comuni: " + a.getPeso() + "\n");
    	}
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Integer id = 0;
    	try {
    		id = Integer.parseInt(txtArtista.getText());
    	}catch(NumberFormatException e) {
    		txtResult.appendText("ERRORE: Inserire un numero intero valido come identificatore dell'artista!\n");
    	    return;
    	}
    	if(!this.model.grafoContiene(id)) {
    		txtResult.appendText("ERRORE: Artista non presente nel grafo. Inserire un identificativo valido tra quelli presenti nel grafo!");
    	    return;
    	}
        
    	List<Artist> percorso = new ArrayList<>(this.model.trovaPercorso(id));
    	txtResult.appendText("PERCORSO PIU LUNGO TROVATO con # "+ percorso.size() + " esposizioni condivise:\n");
    	for(Artist a: percorso) {
    		txtResult.appendText(a +"\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String role = boxRuolo.getValue();
    	if(role == null) {
    		txtResult.appendText("ERRORE: Selezionare prima un ruolo dal menu a tendina!\n");
    	    return;
    	}
    	this.model.creaGrafo(role);
    	btnArtistiConnessi.setDisable(false);
    	btnCalcolaPercorso.setDisable(false);

    	txtResult.appendText("Grafo creato!\n");
    	txtResult.appendText("#VERTICI: "+ this.model.nVertici()+"\n");
    	txtResult.appendText("#ARCHI: "+ this.model.nArchi()+"\n");
    }

    
    @FXML
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnArtistiConnessi != null : "fx:id=\"btnArtistiConnessi\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnCalcolaPercorso != null : "fx:id=\"btnCalcolaPercorso\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert boxRuolo != null : "fx:id=\"boxRuolo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtArtista != null : "fx:id=\"txtArtista\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Artsmia.fxml'.";

    }
    

    public void setModel(Model model) {
    	this.model = model;
    	boxRuolo.getItems().addAll(this.model.getAllRoles());
    	btnArtistiConnessi.setDisable(true);
    	btnCalcolaPercorso.setDisable(true);
    }

}
