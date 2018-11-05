package data;

import objet.Objet;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ArboObjetData {
	
	public static Objet chargerObjet(String chemin) throws IOException {
		Objet objetRetour = null;
		try {
			Document document = new SAXBuilder().build(new File(chemin));
			Element element = document.getRootElement();
			try {
				// on recupere le type de l'objet a charger
				Class<?> classe = Class.forName(element.getName());
				try {
					objetRetour = (Objet)classe.newInstance();
					for(Field tmpField : PrincipaleData.getFields(classe, "fixedFeature")) {
						try {
							// on recupere la valeur de l'attribut xml
							String fieldValue = element.getAttributeValue(tmpField.getName());
							// on modifie l'attribut de l'objet en fonction de son type
							switch(tmpField.getType().getSimpleName()) {
								case "String" :
									tmpField.set(objetRetour, fieldValue);
									break;
								case "int" :
									tmpField.set(objetRetour, Integer.parseInt(fieldValue));
									break;
								case "double" :
									tmpField.set(objetRetour, Double.parseDouble(fieldValue));
									break;
								case "boolean" :
									tmpField.set(objetRetour, Boolean.parseBoolean(fieldValue));
									break;
							}
							
						}
						catch (IllegalArgumentException e) { e.printStackTrace(); }
						catch (IllegalAccessException e) { e.printStackTrace(); }
					}
				}
				catch (InstantiationException e1) { e1.printStackTrace(); }
				catch (IllegalAccessException e1) { e1.printStackTrace(); }
			}
			catch (ClassNotFoundException e) { e.printStackTrace(); }
		}
		catch (JDOMException e) { e.printStackTrace(); }
		return objetRetour;
	}
	
	public static void sauverObjet(Objet objet, String chemin) {
		try {
			Element element = new Element(objet.getClass().getName());
			Attribute tmpAttribut;
			for(Field tmpField : PrincipaleData.getFields(objet, "fixedFeature")) {
				try {
					tmpAttribut = new Attribute(tmpField.getName(), tmpField.get(objet).toString());
					element.setAttribute(tmpAttribut);
				}
				catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
			}
			Document document = new Document(element);
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(document, new FileOutputStream(chemin));
		}
		catch (java.io.IOException e) { e.printStackTrace(); }
	}
	
}