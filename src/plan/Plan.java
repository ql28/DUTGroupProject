package plan;

import java.util.ArrayList;
import java.util.Iterator;

import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSalle;
import objet.ObjetSupport;

public abstract class Plan {
	
	protected String nom;
	protected int x;
	protected int y;
	
	protected ArrayList<ObjetSupport> objetsSupport;

	public Plan() {
		nom = "";
		x = 0;
		y = 0;
		objetsSupport = new ArrayList<ObjetSupport>();
	}

	public void addObjetSupport(ObjetSupport objet) {
		objetsSupport.add(objet);
	}
	
	public ObjetSupport getObjetSupport(String idObjet) {
		Iterator<ObjetSupport> i = objetsSupport.iterator();
		Objet o = null;
		while(i.hasNext()) {
			o = (Objet)i.next();
			if(o.getId().equals(idObjet)) {
				return (ObjetSupport)o;
			}
		}
		return null;
	}

	public void removeObjetSupport(String idObjet) {
		Iterator<ObjetSupport> i = objetsSupport.iterator();
		Objet o = null;
		while(i.hasNext()) {
			o = (Objet)i.next();
			if(o.getId().equals(idObjet)) {
				i.remove();
			}
		}
	}
	
	public static Integer getLastId(Plan plan) {
		Integer lastId = 0;
		Iterator<ObjetSupport> i = plan.getObjetsSupport().iterator();
		Objet o = null;
		while(i.hasNext()) {
			o = (Objet)i.next();
			if(Integer.parseInt(o.getId())>lastId) {
				lastId = Integer.parseInt(o.getId());
			}
		}
		if(plan instanceof PlanFeu) {
			Iterator<ObjetFeu> j = ((PlanFeu)plan).getObjetsFeu().iterator();
			o = null;
			while(j.hasNext()) {
				o = (Objet)j.next();
				if(Integer.parseInt(o.getId())>lastId) {
					lastId = Integer.parseInt(o.getId());
				}
			}
		}
		else if(plan instanceof PlanSalle) {
			Iterator<ObjetSalle> k = ((PlanSalle)plan).getObjetsSalle().iterator();
			o = null;
			while(k.hasNext()) {
				o = (Objet)k.next();
				if(Integer.parseInt(o.getId())>lastId) {
					lastId = Integer.parseInt(o.getId());
				}
			}
		}
		return lastId;
	}

	public ArrayList<ObjetSupport> getObjetsSupport() { return objetsSupport; }	
	public String getNom() { return nom; }
	public void setNom(String nom) { this.nom = nom; }
	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }
	
}
