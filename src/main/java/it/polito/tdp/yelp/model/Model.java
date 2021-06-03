package it.polito.tdp.yelp.model;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {

	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private List<Business> vertici;
	private Map<Business, Double> mappaMediaRecensioni;
	
	public Model() {
		this.dao = new YelpDao();
	}
	
	public String creaGrafo(String città, Year anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//VERTICI
		this.vertici = this.dao.getAllBusinessByCityAndYear(città, anno);
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//ARCHI
		this.mappaMediaRecensioni = new HashMap<>();
		for(Business b: this.vertici)
			this.dao.getMediumStars(b.getBusinessId(), anno, mappaMediaRecensioni);
		
		for(Business b1: this.vertici) {
			for(Business b2: this.vertici) {
				if(this.mappaMediaRecensioni.get(b1)<this.mappaMediaRecensioni.get(b2)) {
					Graphs.addEdge(this.grafo, b1, b2, this.mappaMediaRecensioni.get(b2)-this.mappaMediaRecensioni.get(b1));
				}
			}
		}
		
		return "Grafo creato con "+ this.grafo.vertexSet().size() +" vertici e "+ this.grafo.edgeSet().size() +" archi";
	}

	public String trovaCittàMigliore(String città, Year anno) {	
		this.creaGrafo(città, anno);
		
		double mediaMigliore = 0;
		String localeMigliore = null;
		for(Business b: this.vertici) {
			if(this.mappaMediaRecensioni.get(b)>mediaMigliore) {
				mediaMigliore = this.mappaMediaRecensioni.get(b);
				localeMigliore = b.getBusinessName();
			}
		}
		
		return "Locale migliore: "+ localeMigliore +" con media recensioni di "+ mediaMigliore;
	}	
	
	public List<String> getCities() {
		return dao.getAllCities();
	}

}
