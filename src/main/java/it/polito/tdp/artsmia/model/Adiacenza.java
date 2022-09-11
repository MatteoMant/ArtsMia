package it.polito.tdp.artsmia.model;

public class Adiacenza {
	private ArtObject a1;
	private ArtObject a2;
	private int peso;
	
	public Adiacenza(ArtObject a1, ArtObject a2, int peso) {
		super();
		this.a1 = a1;
		this.a2 = a2;
		this.peso = peso;
	}

	public ArtObject getA1() {
		return a1;
	}

	public void setA1(ArtObject a1) {
		this.a1 = a1;
	}

	public ArtObject getA2() {
		return a2;
	}

	public void setA2(ArtObject a2) {
		this.a2 = a2;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

}
