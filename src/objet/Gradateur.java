package objet;

public class Gradateur extends ObjetSalle {
	
	protected int movableFeatureNombreCircuit;
	protected int movableFeatureIdConsole;
	
	public Gradateur() {
		super();
	}

	public int getNombreCircuit() { return movableFeatureNombreCircuit; }
	public void setNombreCircuit(int movableFeatureNombreCircuit) { this.movableFeatureNombreCircuit = movableFeatureNombreCircuit; }
	public int getConsole() { return movableFeatureIdConsole; }
	public void setConsole(int movableFeatureIdConsole) { this.movableFeatureIdConsole = movableFeatureIdConsole; }
	
}
