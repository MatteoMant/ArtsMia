package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private Map<Integer, ArtObject> idMap;
	private ArtsmiaDAO dao;

	public Model() {
		// this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		// se facciamo la 'new del grafo' nel costruttore del modello il grafo viene
		// creato solo una volta, ovvero quando creaimo il modello
		dao = new ArtsmiaDAO(); // questa volta ha senso istanziarlo nel Model() perchè ce ne serve solo 1
		idMap = new HashMap<Integer, ArtObject>();
	}

	public void creaGrafo() {
		// in questo modo il grafo viene distrutto e creato da zero. In questo modo
		// siamo sicuri che il grafo sia pulito
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// adesso che abbiamo creato il grafo dobbiamo aggiungere i VERTICI
		// List<ArtObject> vertici = dao.listObjects();
		// piuttosto che ottenere una lista di vertici possiamo modificare il metodo
		// passando la nostra Identity Map
		// che inizialmente sarà vuota per farcela riempire

		dao.listObjects(idMap); // qui riempio la mappa

		Graphs.addAllVertices(this.grafo, idMap.values());

		// adesso dobbiamo aggiungere gli ARCHI
		
		// APPROCCIO 1: possiamo fare un doppio ciclo for sui vertici del grafo per
		// recuperare tutte le possibili
		// coppie di vertici e per ogni coppia vado a chiedere al db se tali vertici
		// sono collegati o meno (in caso affermativo creo un arco che li collega) 
		// Nota: questo approccio è il più semplice in assoluto ma funziona solo se il numero di vertici è
		// piccolo; al crescere del numero di vertici dobbiamo cambiare strategia
		
		/*
		 for (ArtObject a1 : this.grafo.vertexSet()) { 
		  	for (ArtObject a2 : this.grafo.vertexSet()) { 
		  		if (!a1.equals(a2) && !this.grafo.containsEdge(a1, a2)) { 
		  			int peso = dao.getPeso(a1, a2); // prima di collegare questa coppia di vertici dobbiamo chiedere al database se sono collegati 
		  			if (peso > 0) { // aggiungo l'arco 
		  			Graphs.addEdgeWithVertices(this.grafo, a1, a2, peso); 
		  			} 
		  		} 
		  	} 
		 }
		 */
		
		
		// APPROCCIO 2 : chiediamo al database di fare tutto il lavoro. L'idea è di partire da un oggetto e di 
		// chiedere al db di darci tutti gli oggetti ad esso collegati
		for (Adiacenza a : this.dao.getAdiacenze(idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}

		System.out.println("Grafo creato!");
		System.out.println("Numero di vertici : " + this.grafo.vertexSet().size());
		System.out.println("Numero di archi : " + this.grafo.edgeSet().size());

	}

	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

	public ArtObject getObject(int objectId) {
		return idMap.get(objectId);
	}
	
	
	/**
	 * con questo metodo a partire da un vertice vogliamo trovare la dimensione della componente connessa a cui
	 * questo vertice appartiene; per fare ciò possiamo visitare il grafo a partire da questo vertice perchè 
	 * così facendo visitiamo tutti i vertici che possiamo raggiungere da quel vertice
	 * @param vertice
	 * @return
	 */
	public int getComponenteConnessa(ArtObject vertice) {
		Set<ArtObject> visitati = new HashSet<>();    // Set di vertici visitati
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> it = 
				new DepthFirstIterator<ArtObject, DefaultWeightedEdge>(this.grafo, vertice);
		
		// visitiamo il grafo e recuperiamo i vertici visitati , mettendoli dentro ad un set
		while (it.hasNext()) {
			visitati.add(it.next());
		}
	
		return visitati.size();
		
		// ALTERNATIVA 2
		// ConnectivityInspector c = new ConnectivityInspector(this.grafo);
		// return c.connectedSetOf(vertice).size();
	}
	
}
