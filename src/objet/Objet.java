package objet;

import javax.swing.ImageIcon;

import view.PlanView;
import data.PlanData;
import data.ReferenceImage;

public class Objet {

	protected String fixedFeatureNom;
	
	protected String movableFeatureId;
	protected int movableFeatureX;
	protected int movableFeatureY;
	protected int movableFeatureOrientation;
	
	public Objet() {
		fixedFeatureNom = "";
		if(PlanView.getInstance().getTabPan().getSelectedIndex()>-1) {
			movableFeatureId = PlanData.getNewId(PlanView.getInstance().getTitleActivePanel());
		}
		else {
			movableFeatureId = PlanData.getNewId();
		}
		movableFeatureX = 0;
		movableFeatureY = 0;
		movableFeatureOrientation = 0;
	}
	
	public ImageIcon getImageIcon() { return ReferenceImage.getImageIcon(fixedFeatureNom, movableFeatureOrientation); }
	
	public String getNom() { return fixedFeatureNom; }
	public void setNom(String fixedFeatureNom) { this.fixedFeatureNom = fixedFeatureNom; }
	public int getX() { return movableFeatureX; }
	public void setX(int movableFeatureX) { this.movableFeatureX = movableFeatureX; }
	public int getY() { return movableFeatureY; }
	public void setY(int movableFeatureY) { this.movableFeatureY = movableFeatureY; }
	public String getId() { return movableFeatureId; }
	public int getOrientation() { return movableFeatureOrientation; }
	public void setOrientation(int movableFeatureOrientation) { this.movableFeatureOrientation = movableFeatureOrientation; }
	
}
