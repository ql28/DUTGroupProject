package data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSalle;
import objet.ObjetSupport;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import plan.PlanFeu;
import plan.PlanSalle;
import view.PlanView;

public class ArboFeuSalleData {

	public static PlanFeu chargerPlanFeu(String chemin) throws IOException {
		PlanFeu planFeuRetour = new PlanFeu();
		Document document = null;
		try {
			document = new SAXBuilder().build(new File(chemin));
		} catch (JDOMException e1) {
			e1.printStackTrace();
		}
		Element rootElement = document.getRootElement();
		Objet tmpObjet = null;
		for(Element tmpElement : rootElement.getChildren()) {
			Class<?> classe = null;
			try {
				// on recupere le type de l'objet a charger
				classe = Class.forName(tmpElement.getName());
			}
			catch (ClassNotFoundException e) { e.printStackTrace(); }
			tmpObjet = ArboObjetData.chargerObjet("local/objet/feu/"+tmpElement.getAttributeValue("fixedFeatureNom")+".xml");
			if(tmpObjet==null) {
				tmpObjet = ArboObjetData.chargerObjet("serveur/objet/feu/"+tmpElement.getAttributeValue("fixedFeatureNom")+".xml");
			}
			if(tmpObjet!=null) {
				for(Field tmpField : PrincipaleData.getFields(classe, "movableFeature")) {
					try {
						// on recupere la valeur de l'attribut xml
						String fieldValue = tmpElement.getAttributeValue(tmpField.getName());
						// on modifie l'attribut de l'objet en fonction de son type
						switch(tmpField.getType().getSimpleName()) {
						case "String" :
							tmpField.set(tmpObjet, fieldValue);
							break;
						case "int" :
							tmpField.set(tmpObjet, Integer.parseInt(fieldValue));
							break;
						case "double" :
							tmpField.set(tmpObjet, Double.parseDouble(fieldValue));
							break;
						case "boolean" :
							tmpField.set(tmpObjet, Boolean.parseBoolean(fieldValue));
							break;
						}
					}
					catch (IllegalArgumentException e) { e.printStackTrace(); }
					catch (IllegalAccessException e) { e.printStackTrace(); }
				}
			}
			else {
				PlanView.displayToast("Des objets n'ont pas pu etre charge dans le plan", PlanView.MESSAGE_ERROR);
			}

			// lecture nom et dimensions plan et salle associe
			planFeuRetour.setNom(rootElement.getAttributeValue("nom"));
			planFeuRetour.setX(Integer.parseInt(rootElement.getAttributeValue("x")));
			planFeuRetour.setY(Integer.parseInt(rootElement.getAttributeValue("y")));
			planFeuRetour.setPlanSalle(rootElement.getAttributeValue("planSalle"));

			if(tmpObjet instanceof ObjetSupport) {
				planFeuRetour.addObjetSupport((ObjetSupport)tmpObjet);
			}
			else if(tmpObjet instanceof ObjetFeu) {
				planFeuRetour.addObjetFeu((ObjetFeu)tmpObjet);
			}
		}
		return planFeuRetour;
	}

	public static PlanSalle chargerPlanSalle(String chemin) throws IOException {
		if(chemin==null || chemin.equals("")) {
			throw new IOException();
		}
		PlanSalle planSalleRetour = new PlanSalle();
		try {
			Document document = new SAXBuilder().build(new File(chemin));
			Element rootElement = document.getRootElement();
			Objet tmpObjet = null;
			for(Element tmpElement : rootElement.getChildren()) {
				System.out.println("ok");
				try {
					// on recupere le type de l'objet a charger
					Class<?> classe = Class.forName(tmpElement.getName());
					try {
						try {
							tmpObjet = ArboObjetData.chargerObjet("local/objet/salle/"+tmpElement.getAttributeValue("fixedFeatureNom")+".xml");
						}
						catch(IOException e) {}
						if(tmpObjet==null) {
							tmpObjet = ArboObjetData.chargerObjet("serveur/objet/salle/"+tmpElement.getAttributeValue("fixedFeatureNom")+".xml");
						}
						if(tmpObjet!=null) {
							
							for(Field tmpField : PrincipaleData.getFields(classe, "movableFeature")) {
								try {
									// on recupere la valeur de l'attribut xml
									String fieldValue = tmpElement.getAttributeValue(tmpField.getName());
									// on modifie l'attribut de l'objet en fonction de son type
									switch(tmpField.getType().getSimpleName()) {
										case "String" :
											tmpField.set(tmpObjet, fieldValue);
											break;
										case "int" :
											tmpField.set(tmpObjet, Integer.parseInt(fieldValue));
											break;
										case "double" :
											tmpField.set(tmpObjet, Double.parseDouble(fieldValue));
											break;
										case "boolean" :
											tmpField.set(tmpObjet, Boolean.parseBoolean(fieldValue));
											break;
									}
								}
								catch (IllegalArgumentException e) { e.printStackTrace(); }
								catch (IllegalAccessException e) { e.printStackTrace(); }
							}
						}
						else {
							PlanView.displayToast("Des objets n'ont pas pu etre charge dans le plan", PlanView.MESSAGE_ERROR);
						}
						
						// lecture nom et dimensions plan
						planSalleRetour.setNom(rootElement.getAttributeValue("nom"));
						planSalleRetour.setX(Integer.parseInt(rootElement.getAttributeValue("x")));
						planSalleRetour.setY(Integer.parseInt(rootElement.getAttributeValue("y")));
						if(tmpObjet instanceof ObjetSupport) {
							planSalleRetour.addObjetSupport((ObjetSupport)tmpObjet);
						}
						else if(tmpObjet instanceof ObjetSalle) {
							planSalleRetour.addObjetSalle((ObjetSalle)tmpObjet);
						}
					}
					catch(IOException e) {}
				}
				catch (ClassNotFoundException e) { e.printStackTrace(); }
			}
		}
		catch (JDOMException e) { e.printStackTrace(); }
		return planSalleRetour;
	}
	
	//public static String toXml(Object planSalle) {

	public static void sauverPlanSalle(PlanSalle planSalle, String chemin) {
		try {
			Element rootElement = new Element(planSalle.getClass().getName());

			// ajout objet salle
			for(ObjetSalle tmpObjetSalle : planSalle.getObjetsSalle()) {
				Element subElement = new Element(tmpObjetSalle.getClass().getName());
				Attribute tmpAttribut;
				
				try {
					tmpAttribut = new Attribute("fixedFeatureNom", tmpObjetSalle.getNom());
					subElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				
				for(Field tmpField : PrincipaleData.getFields(tmpObjetSalle, "movableFeature")) {
					try {
						tmpAttribut = new Attribute(tmpField.getName(), tmpField.get(tmpObjetSalle).toString());
						subElement.setAttribute(tmpAttribut);
					}
					catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
				}
				rootElement.addContent(subElement);
			}
			//ajout objet support
			for(ObjetSupport tmpObjetSupport : planSalle.getObjetsSupport()) {
				Element subElement = new Element(tmpObjetSupport.getClass().getName());
				Attribute tmpAttribut;
				
				try {
					tmpAttribut = new Attribute("fixedFeatureNom", tmpObjetSupport.getNom());
					subElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				
				for(Field tmpField : PrincipaleData.getFields(tmpObjetSupport, "movableFeature")) {
					try {
						tmpAttribut = new Attribute(tmpField.getName(), tmpField.get(tmpObjetSupport).toString());
						subElement.setAttribute(tmpAttribut);
					}
					catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
				}
				rootElement.addContent(subElement);
			}
			// ajout nom et dimensions plan et salle associe
			Attribute tmpAttribut;
			try {
				tmpAttribut = new Attribute("nom", planSalle.getNom());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			try {
				tmpAttribut = new Attribute("x", ""+planSalle.getX());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			try {
				tmpAttribut = new Attribute("y", ""+planSalle.getY());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			Document document = new Document(rootElement);
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			if(chemin.startsWith("serveur/plan/salle")) {
				chemin = "local/plan/salle" ;
				PlanView.displayToast("Impossible de sauvegarder un plan du serveur, le plan a ete sauver en local", PlanView.MESSAGE_ERROR);
			}
			sortie.output(document, new FileOutputStream(chemin+"/"+planSalle.getNom()+".ps"));
		}
		catch (java.io.IOException e) { e.printStackTrace(); }
	}

	
	public static void sauverPlanFeu(PlanFeu planFeu, String chemin) {
		try {
			Element rootElement = new Element(planFeu.getClass().getName());

			// ajout objet feu
			for(ObjetFeu tmpObjetFeu : planFeu.getObjetsFeu()) {
				Element subElement = new Element(tmpObjetFeu.getClass().getName());
				Attribute tmpAttribut;
				
				try {
					tmpAttribut = new Attribute("fixedFeatureNom", tmpObjetFeu.getNom());
					subElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				
				for(Field tmpField : PrincipaleData.getFields(tmpObjetFeu, "movableFeature")) {
					try {
						tmpAttribut = new Attribute(tmpField.getName(), tmpField.get(tmpObjetFeu).toString());
						subElement.setAttribute(tmpAttribut);
					}
					catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
				}
				rootElement.addContent(subElement);
			}
			//ajout objet support
			for(ObjetSupport tmpObjetSupport : planFeu.getObjetsSupport()) {
				Element subElement = new Element(tmpObjetSupport.getClass().getName());
				Attribute tmpAttribut;
				
				try {
					tmpAttribut = new Attribute("fixedFeatureNom", tmpObjetSupport.getNom());
					subElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				
				for(Field tmpField : PrincipaleData.getFields(tmpObjetSupport, "movableFeature")) {
					try {
						tmpAttribut = new Attribute(tmpField.getName(), tmpField.get(tmpObjetSupport).toString());
						subElement.setAttribute(tmpAttribut);
					}
					catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
				}
				rootElement.addContent(subElement);
			}
			// ajout nom et dimensions plan et salle associe
			Attribute tmpAttribut;
			try {
				tmpAttribut = new Attribute("nom", planFeu.getNom());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			try {
				tmpAttribut = new Attribute("x", ""+planFeu.getX());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			try {
				tmpAttribut = new Attribute("y", ""+planFeu.getY());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			try {
				tmpAttribut = new Attribute("planSalle", planFeu.getPlanSalle());
				rootElement.setAttribute(tmpAttribut);
			}
			catch (IllegalArgumentException e) { e.printStackTrace(); }
			Document document = new Document(rootElement);
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(document, new FileOutputStream(chemin+"/"+planFeu.getNom()+".pf"));
		}
		catch (java.io.IOException e) { e.printStackTrace(); }
	}

}
