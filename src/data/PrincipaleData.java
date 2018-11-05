package data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

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

public class PrincipaleData {

	// methode pour charger tout les attributs d'une classe jusqu'a la classe mere Objet
	public static ArrayList<Field> getFields(Objet objet, String typeField) {
		ArrayList<Field> listeAttributs = new ArrayList<Field>();
		Class<?> tmpClass = objet.getClass();
		do {
			for(Field tmpField : tmpClass.getDeclaredFields()) {
				tmpField.setAccessible(true);
				if(tmpField.getName().startsWith(typeField)) {
					listeAttributs.add(tmpField);
				}
			}
			tmpClass = tmpClass.getSuperclass();
		} while(!tmpClass.getName().equals("java.lang.Object"));
		return listeAttributs;
	}

	// methode pour charger tout les attributs d'une classe jusqu'a la classe mere Objet
	public static ArrayList<Field> getFields(Class<?> classe, String typeField) {
		ArrayList<Field> listeAttributs = new ArrayList<Field>();
		Class<?> tmpClass = classe;
		do {
			for(Field tmpField : tmpClass.getDeclaredFields()) {
				tmpField.setAccessible(true);
				if(tmpField.getName().startsWith(typeField)) {
					listeAttributs.add(tmpField);
				}
			}
			tmpClass = tmpClass.getSuperclass();
		} while(!tmpClass.getName().equals("java.lang.Object"));
		return listeAttributs;
	}
	
	public static void sauverXml(Object object, String chemin) {
		if(object instanceof Objet) {
			ArboObjetData.sauverObjet((Objet)object, chemin);
		}
		else if(object instanceof PlanFeu) {
			ArboFeuSalleData.sauverPlanFeu((PlanFeu)object, chemin);
		}
		else if(object instanceof PlanSalle) {
			ArboFeuSalleData.sauverPlanSalle((PlanSalle)object, chemin);
		}
		else {
			throw new IllegalArgumentException("l'argument doit etre une instance de Plan ou Objet");
		}
	}
	
	public static String xmlToString(Object object) {
		String retour = "";
		if(object instanceof Objet) {
			try {
				Element element = new Element(object.getClass().getName());
				Attribute tmpAttribut;
				for(Field tmpField : PrincipaleData.getFields((Objet)object, "fixedFeature")) {
					try {
						tmpAttribut = new Attribute(tmpField.getName(), tmpField.get(object).toString());
						element.setAttribute(tmpAttribut);
					}
					catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
				}
				Document document = new Document(element);
				XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				sortie.output(document, baos);
				retour = baos.toString();
				baos.close();
			}
			catch (java.io.IOException e) { }
		}
		else if(object instanceof PlanFeu) {
			try {
				Element rootElement = new Element(object.getClass().getName());
				// ajout objet feu
				for(ObjetFeu tmpObjetFeu : ((PlanFeu)object).getObjetsFeu()) {
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
				for(ObjetSupport tmpObjetSupport : ((PlanFeu)object).getObjetsSupport()) {
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
					tmpAttribut = new Attribute("nom", ((PlanFeu)object).getNom());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				try {
					tmpAttribut = new Attribute("x", ""+((PlanFeu)object).getX());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				try {
					tmpAttribut = new Attribute("y", ""+((PlanFeu)object).getY());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				try {
					tmpAttribut = new Attribute("planSalle", ((PlanFeu)object).getPlanSalle());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				Document document = new Document(rootElement);
				XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				sortie.output(document, baos);
				
				retour = baos.toString();
				baos.close();
			}
			catch (java.io.IOException e) { e.printStackTrace(); }
		}
		else if(object instanceof PlanSalle) {
			try {
				Element rootElement = new Element(((PlanSalle)object).getClass().getName());

				// ajout objet salle
				for(ObjetSalle tmpObjetSalle : ((PlanSalle)object).getObjetsSalle()) {
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
				for(ObjetSupport tmpObjetSupport : ((PlanSalle)object).getObjetsSupport()) {
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
					tmpAttribut = new Attribute("nom", ((PlanSalle)object).getNom());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				try {
					tmpAttribut = new Attribute("x", ""+((PlanSalle)object).getX());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				try {
					tmpAttribut = new Attribute("y", ""+((PlanSalle)object).getY());
					rootElement.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException e) { e.printStackTrace(); }
				Document document = new Document(rootElement);
				XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				sortie.output(document, baos);
				
				retour = baos.toString();
				baos.close();
			}
			catch (java.io.IOException e) { e.printStackTrace(); }
		}
		return retour;
	}
	
	public static void stringToXml(String xml) {
		
		try {
			// on input l'xml dans le flux
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			
			// on construit l'xml a partir du flux
			Document document = new SAXBuilder().build(bais);
			Element element = document.getRootElement();
			Class<?> classe = Class.forName(element.getName());
			Object instance = classe.newInstance();
			
			//on recupere le chemin ou sauvagarder l'objet
			String chemin = null;
			chemin = instance instanceof ObjetFeu ? "serveur/objet/feu/"+element.getAttributeValue("fixedFeatureNom")+".xml" : chemin;
			chemin = instance instanceof ObjetSalle ? "serveur/objet/salle/"+element.getAttributeValue("fixedFeatureNom")+".xml" : chemin;
			chemin = instance instanceof PlanSalle ? "serveur/plan/salle/"+element.getAttributeValue("nom")+".ps" : chemin;

			if(chemin!=null) {
				FileOutputStream fos = new FileOutputStream(new File(chemin));
				fos.write(xml.getBytes());
				fos.close();
			}
			
			bais.close();
		} 
		catch (JDOMException e1) { e1.printStackTrace(); }
		catch (IOException e1) {e1.printStackTrace(); }
		catch (ClassNotFoundException e) { e.printStackTrace(); }
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
	}
	
	public static String stringToXmlTmp(String xml) {
		
		try {
			// on input l'xml dans le flux
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			
			//on recupere le chemin ou sauvegarder l'objet
			String chemin = null;
				chemin = "serveur/objet/tmp/DescriptionObjetTmp.xml";
				FileOutputStream fos = new FileOutputStream(new File(chemin));
				fos.write(xml.getBytes());
				fos.close();
				bais.close();
			return chemin;
		}
		catch (IOException e1) {e1.printStackTrace(); }
		return null;
	}

}
