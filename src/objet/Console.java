package objet;

public class Console extends ObjetSalle {
	
	protected int movableFeatureNombreCircuit;
	protected boolean movableFeatureProgrammable;
	protected int movableFeatureNombrePreparation;
	protected String movableFeatureReference;

	public Console() {
		super();
	}

	public int getNombreCircuit() { return movableFeatureNombreCircuit; }
	public void setNombreCircuit(int movableFeatureNombreCircuit) { this.movableFeatureNombreCircuit = movableFeatureNombreCircuit; }
	public boolean isProgrammable() { return movableFeatureProgrammable; }
	public void setProgrammable(boolean movableFeatureProgrammable) { this.movableFeatureProgrammable = movableFeatureProgrammable; }
	public int getNombrePreparation() { return movableFeatureNombrePreparation; }
	public void setNombrePreparation(int movableFeatureNombrePreparation) { this.movableFeatureNombrePreparation = movableFeatureNombrePreparation; }
	public String getReference() { return movableFeatureReference; }
	public void setReference(String movableFeatureReference) { this.movableFeatureReference = movableFeatureReference; }
	
}
