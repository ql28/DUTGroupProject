package objet;

public class Gradin extends ObjetSalle {
	
	protected int movableFeatureNombrePlace;
	
	public Gradin() {
		super();
		movableFeatureNombrePlace = 0;
	}

	public int getNombrePlace() { return movableFeatureNombrePlace; }
	public void setNombrePlace(int movableFeatureNombrePlace) { this.movableFeatureNombrePlace = movableFeatureNombrePlace; }

}
