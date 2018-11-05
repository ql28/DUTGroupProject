package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.tree.DefaultMutableTreeNode;

import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSupport;
import plan.PlanSalle;
import view.DialogueEnvoyerView;
import data.ArboFeuSalleData;
import data.ArboObjetData;
import data.PrincipaleData;

public class DialogueEnvoyerCtrl implements ActionListener {
	
	private static DialogueEnvoyerCtrl dialogueEnvoyerCtrl = new DialogueEnvoyerCtrl();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueEnvoyerView.getInstance().getAnnuler()) {
			DialogueEnvoyerView.getInstance().setVisible(false);
		}
		else if(src==DialogueEnvoyerView.getInstance().getTypeEnvoi()) {
			if(DialogueEnvoyerView.getInstance().getTypeEnvoi().getSelectedItem().toString() == "Plan de salle"){
				DialogueEnvoyerView.getInstance().changeArbo("Plan de salle");
			}
			
			if(DialogueEnvoyerView.getInstance().getTypeEnvoi().getSelectedItem().toString() == "Objet de feu"){
				DialogueEnvoyerView.getInstance().changeArbo("Objet de feu");
			}
			
			if(DialogueEnvoyerView.getInstance().getTypeEnvoi().getSelectedItem().toString() == "Objet de salle"){
				DialogueEnvoyerView.getInstance().changeArbo("Objet de salle");
			}
		}
		else if(src==DialogueEnvoyerView.getInstance().getEnvoyer()) {
			
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode) DialogueEnvoyerView.getInstance().getArbo().getLastSelectedPathComponent();
		    if (node == null){
		        System.out.println("selection nulle"); 
		        return;
		    }
		    Object nodeInfo = node.getUserObject();

		    if (node.isLeaf()) {
		        String file = (String) nodeInfo;
		        System.out.println("selection du fichier : " +file);
		        
		        //si un plan de salle est s�lectionn�		        
				if(DialogueEnvoyerView.getInstance().getTypeEnvoi().getSelectedItem().toString() == "Plan de salle"){
					PlanSalle planSalle = null;
					try {
						planSalle = (PlanSalle)ArboFeuSalleData.chargerPlanSalle("local/plan/salle/"+file);
					} catch (IOException e2) {
						e2.printStackTrace();
					}			        
					//Envoi � la BBD
			    	String url = "jdbc:mysql://localhost/adec56";
			    	String login = "root";
			    	String passwd = "azerty";
			    	Connection cn = null;
			    	Statement st = null;
		    	    PreparedStatement ps = null;
			    	
			    	try{
			    		Class.forName("com.mysql.jdbc.Driver");
			    		cn = DriverManager.getConnection(url,login,passwd);
			    		st = cn.createStatement();
			    	    String REQUEST = "INSERT INTO plan_de_salle (nom, data, valide) VALUES (?, ?, ?)";
			    	    try {
			    	      cn.setAutoCommit(false);
			    	      ps = cn.prepareStatement(REQUEST);
			    	      ps.setString(1, planSalle.getNom());
			    	      ps.setString(2, PrincipaleData.xmlToString(planSalle));
			    	      ps.setInt(3, 0);
			    	      ps.executeUpdate();
			    	      cn.commit();
			    	      DialogueEnvoyerView.getInstance().setVisible(false);
			    	    } finally {
			    	      ps.close();
			    	    }
			    	    String sql = "SELECT * FROM plan_de_salle";
			    		ResultSet result = st.executeQuery(sql);
			    	    //On r�cup�re les MetaData
			    	    ResultSetMetaData resultMeta = result.getMetaData();
			    	    
			    	    System.out.println("\n****************************************************************************************************");
			    	    for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			    	      System.out.print("\t" + resultMeta.getColumnName(i).toUpperCase() + "\t *");
			    	    }
			    	    System.out.println("\n****************************************************************************************************");
			    	    while(result.next()){
			    	      for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			    	        System.out.print("\t" + result.getObject(i).toString() + "\t |");
			    	      }
			    	      System.out.println("\n--------------------------------------------------------------------------------------------------");
			    	    }
			    	}
			    	catch(SQLException e1){
			    		e1.printStackTrace();
			    	}
			    	catch(ClassNotFoundException e1){
			    		e1.printStackTrace();
			    	}
			    	finally{
			    		try{
			    			cn.close();
			    			st.close();
			    		}
			    		catch(SQLException e1){
			    			e1.printStackTrace();
			    		}
			    	}		        
		        }
				if(DialogueEnvoyerView.getInstance().getTypeEnvoi().getSelectedItem().toString() == "Objet de feu"){
					Objet objet = null;
					
					try {
						objet = (Objet)ArboObjetData.chargerObjet("local/objet/feu/"+file);
					} catch (IOException e3) {
						e3.printStackTrace();
					}
					if (objet instanceof ObjetFeu){
						try {
							objet = (ObjetFeu)ArboObjetData.chargerObjet("local/objet/feu/"+file);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					else if(objet instanceof ObjetSupport){
						try {
							objet = (ObjetSupport)ArboObjetData.chargerObjet("local/objet/feu/"+file);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					
			        //Envoi � la BBD
			    	String url = "jdbc:mysql://localhost/adec56";
			    	String login = "root";
			    	String passwd = "azerty";
			    	Connection cn = null;
			    	Statement st = null;
			    	FileInputStream fis = null;
		    	    PreparedStatement ps = null;
			    	
			    	try{
			    		Class.forName("com.mysql.jdbc.Driver");
			    		cn = DriverManager.getConnection(url,login,passwd);
			    		st = cn.createStatement();
			    	    String REQUEST = "INSERT INTO objet (nom, data, image, valide) VALUES (?, ?, ?, ?)";
			    	    
			    	    try {
			    	      cn.setAutoCommit(false);
			    	      File file1 = new File("res/img_objets/" +objet.getNom()+".png");
			    	      try {
							fis = new FileInputStream(file1);
			    	      } catch (FileNotFoundException e1) {
							e1.printStackTrace();
			    	      }
			    	      ps = cn.prepareStatement(REQUEST);
			    	      ps.setString(1, objet.getNom());
			    	      ps.setString(2, PrincipaleData.xmlToString(objet));
			    	      ps.setBinaryStream(3, fis, (int) file1.length());
			    	      ps.setInt(4, 0);
			    	      ps.executeUpdate();
			    	      cn.commit();
			    	      DialogueEnvoyerView.getInstance().setVisible(false);
			    	    } finally {
			    	      ps.close();
			    	      try {
							fis.close();
			    	      } catch (IOException e1) {
							e1.printStackTrace();
			    	      }
			    	    }

			    	    String sql = "SELECT * FROM Objet";
			    		ResultSet result = st.executeQuery(sql);
			    	    //On r�cup�re les MetaData
			    	    ResultSetMetaData resultMeta = result.getMetaData();
			    	    
			    	    System.out.println("\n****************************************************************************************************");
			    	    for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			    	      System.out.print("\t" + resultMeta.getColumnName(i).toUpperCase() + "\t *");
			    	    }
			    	    System.out.println("\n****************************************************************************************************");
			    	    while(result.next()){
			    	      for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			    	        System.out.print("\t" + result.getObject(i).toString() + "\t |");
			    	      }
			    	      System.out.println("\n--------------------------------------------------------------------------------------------------");
			    	    }
			    	}
			    	catch(SQLException e1){
			    		e1.printStackTrace();
			    	}
			    	catch(ClassNotFoundException e1){
			    		e1.printStackTrace();
			    	}
			    	finally{
			    		try{
			    			cn.close();
			    			st.close();
			    		}
			    		catch(SQLException e1){
			    			e1.printStackTrace();
			    		}
			    	} 
		        }
		        if(DialogueEnvoyerView.getInstance().getTypeEnvoi().getSelectedItem().toString() == "Objet de salle"){
					Objet objet = null;
					try {
						objet = (Objet)ArboObjetData.chargerObjet("local/objet/feu/"+file);
					} catch (IOException e3) {
						e3.printStackTrace();
					}
					if (objet instanceof ObjetFeu){
						try {
							objet = (ObjetFeu)ArboObjetData.chargerObjet("local/objet/feu/"+file);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					else if(objet instanceof ObjetSupport){
						try {
							objet = (ObjetSupport)ArboObjetData.chargerObjet("local/objet/feu/"+file);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}
					
					//Envoi � la BBD
			    	String url = "jdbc:mysql://localhost/adec56";
			    	String login = "root";
			    	String passwd = "azerty";
			    	Connection cn = null;
			    	Statement st = null;
			    	FileInputStream fis = null;
		    	    PreparedStatement ps = null;
			    	
			    	try{
			    		Class.forName("com.mysql.jdbc.Driver");
			    		cn = DriverManager.getConnection(url,login,passwd);
			    		st = cn.createStatement();
			    	    String REQUEST = "INSERT INTO objet (nom, data, image, valide) VALUES (?, ?, ?, ?)";
			    	    
			    	    try {
			    	      cn.setAutoCommit(false);
			    	      File file1 = new File("res/img_objets/" +objet.getNom()+".png");
			    	      try {
							fis = new FileInputStream(file1);
			    	      } catch (FileNotFoundException e1) {
							e1.printStackTrace();
			    	      }
			    	      ps = cn.prepareStatement(REQUEST);
			    	      ps.setString(1, objet.getNom());
			    	      ps.setString(2, PrincipaleData.xmlToString(objet));
			    	      ps.setBinaryStream(3, fis, (int) file1.length());
			    	      ps.setInt(4, 0);
			    	      ps.executeUpdate();
			    	      cn.commit();
			    	      DialogueEnvoyerView.getInstance().setVisible(false);
			    	    } finally {
			    	      ps.close();
			    	      try {
							fis.close();
			    	      } catch (IOException e1) {
							e1.printStackTrace();
			    	      }
			    	    }
			    	    String sql = "SELECT * FROM Objet";
			    		ResultSet result = st.executeQuery(sql);
			    	    //On r�cup�re les MetaData
			    	    ResultSetMetaData resultMeta = result.getMetaData();
			    	    
			    	    System.out.println("\n****************************************************************************************************");
			    	    for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			    	      System.out.print("\t" + resultMeta.getColumnName(i).toUpperCase() + "\t *");
			    	    }
			    	    System.out.println("\n****************************************************************************************************");
			    	    while(result.next()){
			    	      for(int i = 1; i <= resultMeta.getColumnCount(); i++){
			    	        System.out.print("\t" + result.getObject(i).toString() + "\t |");
			    	      }
			    	      System.out.println("\n--------------------------------------------------------------------------------------------------");
			    	    }
			    	}
			    	catch(SQLException e1){
			    		e1.printStackTrace();
			    	}
			    	catch(ClassNotFoundException e1){
			    		e1.printStackTrace();
			    	}
			    	finally{
			    		try{
			    			cn.close();
			    			st.close();
			    		}
			    		catch(SQLException e1){
			    			e1.printStackTrace();
			    		}
			    	}	
		        }
		    }
		}
	}

	public static DialogueEnvoyerCtrl getInstance() { return dialogueEnvoyerCtrl; }

}
