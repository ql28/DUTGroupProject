package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSalle;
import objet.ObjetSupport;
import plan.Plan;
import plan.PlanFeu;
import plan.PlanSalle;

public class PlanData {
	
	/* ATTRIBUTS */
	
	// plans
	private static HashMap<String, Plan> plans = new HashMap<String, Plan>();
	
	// modifications
	private static HashMap<String, ArrayList<Modification>> modifications = new HashMap<String, ArrayList<Modification>>();
	private static HashMap<String, Integer> currentModification = new HashMap<String, Integer>();
	private static HashMap<String, Stack<Objet>> objetsUndo = new HashMap<String, Stack<Objet>>();
	
	// generateur id pour objets
	private static HashMap<String, Integer> compteursId = new HashMap<String, Integer>();

	// souris
	private static String idDernierComponentSelectionner;
	private static int lastPositionDraggedX = 0;
	private static int lastPositionDraggedY = 0;
	private static int lastRelativePositionClickedX = 0;
	private static int lastRelativePositionClickedY = 0;
	
	
	
	
	
	/* METHODES */
	
	// plans
	public static void addObjet(String idPlan, Objet objet) {
		if(plans.get(idPlan) instanceof PlanFeu && objet instanceof ObjetFeu) {
			((PlanFeu)plans.get(idPlan)).addObjetFeu((ObjetFeu)objet);
		}
		else if(plans.get(idPlan) instanceof PlanSalle && objet instanceof ObjetSalle) {
			((PlanSalle)plans.get(idPlan)).addObjetSalle((ObjetSalle)objet);
		}
		else if(objet instanceof ObjetSupport) {
			plans.get(idPlan).addObjetSupport((ObjetSupport)objet);
		}
	}
	public static Plan getPlan(String idPlan) {
		return plans.get(idPlan);
	}
	public static void setPositionObjet(String idPlan, String idObjet, int x, int y) {
		Objet o = plans.get(idPlan).getObjetSupport(idObjet);
		if(o==null) {
			if(plans.get(idPlan) instanceof PlanFeu) {
				o = ((PlanFeu)plans.get(idPlan)).getObjetFeu(idObjet);
			}
			else if(plans.get(idPlan) instanceof PlanSalle) {
				o = ((PlanSalle)plans.get(idPlan)).getObjetSalle(idObjet);
			}
		}
		o.setX(x);
		o.setY(y);
	}
	
	// generateur id pour objets
	public static String getNewId() {
		if(!compteursId.containsKey("")) {
			compteursId.put("", 0);
		}
		int retour = compteursId.get("");
		compteursId.put("", compteursId.get("")+1);
		return ""+retour;
	}
	
	// generateur id pour objets
	public static String getNewId(String idPanel) {
		int retour = compteursId.get(idPanel);
		compteursId.put(idPanel, compteursId.get(idPanel)+1);
		return ""+retour;
	}
	
	// souris
	public static int getLastPositionDraggedX() { return lastPositionDraggedX; }
	public static void setLastPositionDraggedX(int lastPositionDraggedX) { PlanData.lastPositionDraggedX = lastPositionDraggedX; }
	public static int getLastPositionDraggedY() { return lastPositionDraggedY; }
	public static void setLastPositionDraggedY(int lastPositionDraggedY) { PlanData.lastPositionDraggedY = lastPositionDraggedY; }
	public static int getLastRelativePositionClickedX() { return lastRelativePositionClickedX; }
	public static void setLastRelativePositionClickedX(int lastRelativePositionClickedX) { PlanData.lastRelativePositionClickedX = lastRelativePositionClickedX; }
	public static int getLastRelativePositionClickedY() { return lastRelativePositionClickedY; }
	public static void setLastRelativePositionClickedY(int lastRelativePositionClickedY) { PlanData.lastRelativePositionClickedY = lastRelativePositionClickedY; }
	public static void setDernierComponentSelectionner(String id) { idDernierComponentSelectionner = id; }
	public static String getDernierComponentSelectionner() { return idDernierComponentSelectionner; }

	// modifications
	public static HashMap<String, ArrayList<Modification>> getModifications() { return modifications; }
	public static void setModifications(HashMap<String, ArrayList<Modification>> modifications) { PlanData.modifications = modifications; }
	public static void saveModification(String idPanel, String idObjet, int type, int x, int y) {
		modifications.get(idPanel).add(new Modification(idObjet, type, x, y));
		increaseCurrentModification(idPanel);
	}
	public static Modification undoModification(String idPanel) {
		if(getCurrentModification(idPanel)>0) {
			decreaseCurrentModification(idPanel);
			return modifications.get(idPanel).get(getCurrentModification(idPanel));
		}
		return null;
	}
	public static Modification redoModification(String idPanel) {
		if(getCurrentModification(idPanel)<modifications.get(idPanel).size()-1) {
			increaseCurrentModification(idPanel);
			return modifications.get(idPanel).get(getCurrentModification(idPanel));
		}
		return null;
	}
	public static void addPlan(Plan plan) {
		if(!modifications.containsKey(plan.getNom())) {
			modifications.put(plan.getNom(), new ArrayList<Modification>());
			objetsUndo.put(plan.getNom(), new Stack<Objet>());
			currentModification.put(plan.getNom(), -1);
			plans.put(plan.getNom(), plan);
			compteursId.put(plan.getNom(), Plan.getLastId(plan)+1);
		}
		else {
			throw new RuntimeException("deux plans ne peuvent avoir le meme nom");
		}
	}
	public static void removePlan(String titleActivePanel) {
		if(plans.containsKey(titleActivePanel)) {
			plans.remove(titleActivePanel);
			modifications.remove(titleActivePanel);
			currentModification.remove(titleActivePanel);
			objetsUndo.remove(titleActivePanel);
			compteursId.remove(titleActivePanel);
		}
	}
	public static void afficherModifications(String idPanel) {
		for(Modification m : modifications.get(idPanel)) {
			System.out.println(m.id+"/"+m.x+"/"+m.y);
		}
		System.out.println();
	}
	public static void undoObjet(String idPlan, String idObjet) {
		Objet o = plans.get(idPlan).getObjetSupport(idObjet);
		if(o!=null) {
			objetsUndo.get(idPlan).push(o);
			plans.get(idPlan).removeObjetSupport(idObjet);
		}
		else {
			if(plans.get(idPlan) instanceof PlanFeu) {
				objetsUndo.get(idPlan).push(((PlanFeu)plans.get(idPlan)).getObjetFeu(idObjet));
				((PlanFeu)plans.get(idPlan)).removeObjetFeu(idObjet);
			}
			else if(plans.get(idPlan) instanceof PlanSalle) {
				objetsUndo.get(idPlan).push(((PlanSalle)plans.get(idPlan)).getObjetSalle(idObjet));
				((PlanSalle)plans.get(idPlan)).removeObjetSalle(idObjet);
			}
		}
		decreaseCurrentModification(idPlan);
	}
	public static Objet redoObjet(String idPlan) {
		Objet o = objetsUndo.get(idPlan).pop();
		if(o instanceof ObjetSupport) {
			plans.get(idPlan).addObjetSupport((ObjetSupport)o);
		}
		else {
			if(plans.get(idPlan) instanceof PlanFeu && o instanceof ObjetFeu) {
				((PlanFeu)plans.get(idPlan)).addObjetFeu((ObjetFeu)o);
			}
			else if(plans.get(idPlan) instanceof PlanSalle && o instanceof ObjetSalle) {
				((PlanSalle)plans.get(idPlan)).addObjetSalle((ObjetSalle)o);
			}
		}
		return o;
	}
	public static void increaseCurrentModification(String idPanel) { currentModification.put(idPanel, currentModification.get(idPanel)+1); }
	public static void decreaseCurrentModification(String idPanel) { currentModification.put(idPanel, currentModification.get(idPanel)-1); }
	public static int getCurrentModification(String idPanel) { return currentModification.get(idPanel).intValue(); }
	public static HashMap<String, Plan> getPlans() { return plans; }
	
}
