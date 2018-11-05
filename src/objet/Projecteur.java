package objet;

public class Projecteur extends ObjetFeu {
	
	protected int fixedFeaturePuissance;
	protected int fixedFeaturePoids;
	protected int fixedFeatureLongueurLigneElec;
	
	protected int movableFeatureHauteur;
	protected int movableFeatureAngleFaisceau;
	protected String movableFeatureGelatine;
	protected int movableFeatureDistanceObjetEclairer;
	protected int movableFeatureIdBarre;
	
	public Projecteur() {
		super();
		fixedFeaturePuissance = 0;
		fixedFeaturePoids = 0;
		fixedFeatureLongueurLigneElec = 0;
		movableFeatureHauteur = 0;
		movableFeatureAngleFaisceau = 0;
		movableFeatureGelatine = "";
		movableFeatureDistanceObjetEclairer = 0;
		movableFeatureIdBarre = 0;
	}

	public int getPuissance() { return fixedFeaturePuissance; }
	public void setPuissance(int fixedFeaturePuissance) { this.fixedFeaturePuissance = fixedFeaturePuissance; }
	public int getPoids() { return fixedFeaturePoids; }
	public void setPoids(int fixedFeaturePoids) { this.fixedFeaturePoids = fixedFeaturePoids; }
	public int getLongueurLigneElec() { return fixedFeatureLongueurLigneElec; }
	public void setLongueurLigneElec(int fixedFeatureLongueurLigneElec) { this.fixedFeatureLongueurLigneElec = fixedFeatureLongueurLigneElec; }
	public int getHauteur() { return movableFeatureHauteur; }
	public void setHauteur(int movableFeatureHauteur) { this.movableFeatureHauteur = movableFeatureHauteur; }
	public int getOrientation() { return movableFeatureOrientation; }
	public void setOrientation(int movableFeatureOrientation) { this.movableFeatureOrientation = movableFeatureOrientation; }
	public int getAngleFaisceau() { return movableFeatureAngleFaisceau; }
	public void setAngleFaisceau(int movableFeatureAngleFaisceau) { this.movableFeatureAngleFaisceau = movableFeatureAngleFaisceau; }
	public String getGelatine() { return movableFeatureGelatine; }
	public void setGelatine(String movableFeatureGelatine) { this.movableFeatureGelatine = movableFeatureGelatine; }
	public int getDistanceObjetEclairer() { return movableFeatureDistanceObjetEclairer; }
	public void setDistanceObjetEclairer(int movableFeatureDistanceObjetEclairer) { this.movableFeatureDistanceObjetEclairer = movableFeatureDistanceObjetEclairer; }
	public int getBarre() { return movableFeatureIdBarre; }
	public void setBarre(int movableFeatureIdBarre) { this.movableFeatureIdBarre = movableFeatureIdBarre; }
	
}
