package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	private ArtsmiaDAO dao;
	private Graph<Artist, DefaultWeightedEdge> grafo;
	private Map<Integer, Artist> idMap;
	
	private List<Artist> best;
	private Integer peso;
	
	
	public Model() {
		super();
		this.dao = new ArtsmiaDAO();
	}

	public List<String> getAllRoles(){
		return this.dao.getAllRoles();
	}
	
	public void creaGrafo(String role) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		this.dao.getVertici(role, idMap);
		
		//Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungo gli archi
		for(Adiacenza a: this.dao.getAdiacenze(role, idMap)) {
			if(this.grafo.containsVertex(a.getA1()) && this.grafo.containsVertex(a.getA2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		 System.out.println("Grafo creato!");
		 System.out.println("#VERTICI: "+ this.grafo.vertexSet().size());
		 System.out.println("#ARCHI: "+ this.grafo.edgeSet().size());
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public boolean grafoCreato() {
		if(this.grafo == null)
			return false;
		else
			return true;
	}
	
	public List<Adiacenza> getArtistiConnessi(){
		List<Adiacenza> connessi = new ArrayList<>();
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			Adiacenza a = new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), (int) this.grafo.getEdgeWeight(e));
		    connessi.add(a);
		}
		Collections.sort(connessi, new Comparator<Adiacenza>() {

			@Override
			public int compare(Adiacenza o1, Adiacenza o2) {
				
				return (-1)*o1.getPeso().compareTo(o2.getPeso());
			}
			
		});
		return connessi;
	}
	
	public List<Artist> trovaPercorso(Integer id){
		this.best = new ArrayList<>();
		
		List<Artist> parziale = new ArrayList<>();
		
		Artist partenza = idMap.get(id);
		
		parziale.add(partenza);
		
		this.peso = -1;
		
		cerca(parziale, peso);
		
		return this.best;
	}

	private void cerca(List<Artist> parziale, Integer peso) {

		if(parziale.size() > this.best.size()) {
			this.best = new ArrayList<>(parziale);
		}
		
		Artist ultimo = parziale.get(parziale.size()-1);
		for(Artist a: Graphs.neighborListOf(this.grafo, ultimo)) {
			DefaultWeightedEdge e = this.grafo.getEdge(ultimo, a);
			if(peso == -1 && !parziale.contains(a)) {
					parziale.add(a);
					cerca(parziale, (int) this.grafo.getEdgeWeight(e));
					parziale.remove(parziale.size()-1);
			}else {
				if(this.grafo.getEdgeWeight(e) == peso && !parziale.contains(a)) {
					parziale.add(a);
					cerca(parziale, peso);
					parziale.remove(parziale.size()-1);
				}
			}
		}
		
	}

	public boolean grafoContiene(Integer id) {
		if(this.grafo.containsVertex(idMap.get(id)))
			return true;
		return false;
	}
}
