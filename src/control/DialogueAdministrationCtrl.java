package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.DialogueAdministrationView;
import data.ArboObjetData;
import data.PrincipaleData;

public class DialogueAdministrationCtrl implements ActionListener, ListSelectionListener {
	
	private static DialogueAdministrationCtrl dialogueAdministrationCtrl = new DialogueAdministrationCtrl();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		Statement st = null;
		if(src==DialogueAdministrationView.getInstance().getQuitter()) {
			DialogueAdministrationView.getInstance().setVisible(false);
			
			//suppression du fichier temporaire utilisé pour afficher la description de l'item selectionné dans la liste
			File folder = new File("serveur/objet/tmp/");
			File fList[] = folder.listFiles();
			
			for (int i = 0; i < fList.length; i++) {
			    File pes = fList[i];
			    if (pes.getName().contains("Tmp.xml")) {
			    	fList[i].delete();
			    }
			}
			DialogueAdministrationView.getInstance().getChoixType().setSelectedIndex(0);
			DialogueAdministrationView.getInstance().getTriValidation().setSelectedIndex(0);
			DialogueAdministrationView.getInstance().getPanelVue().removeAll();
			
		}
		else if(src==DialogueAdministrationView.getInstance().getChoixType() || src==DialogueAdministrationView.getInstance().getTriValidation()) {
		    while(DialogueAdministrationView.getInstance().getTableModel().getRowCount() != 0){
		    	DialogueAdministrationView.getInstance().getTableModel().removeRow(0);
		    }
			if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Plans de salle"){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "SELECT * FROM Plan_de_salle";
					ResultSet result = st.executeQuery(sql);
					
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Tout"){
						while(result.next()){
						    System.out.println("Plan de salle Tout !");
						    String[] test = {result.getObject(1).toString()};						    
						    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Validés"){
						while(result.next()){
							// recupere le champ autorise, si = 1 alors l'administration a autorise le plan
							if((boolean)result.getObject(3) == true){
							    System.out.println("Plan de salle Validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
					    		//PrincipaleData.stringToXml(result.getObject(2).toString());
							}
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Non-validés"){
						while(result.next()){
							// recupere sle champ autorise, si = 1 alors l'administration a autorise le plan
							if((boolean)result.getObject(3) == false){
							    System.out.println("Plan de salle Non-validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
					    		//PrincipaleData.stringToXml(result.getObject(2).toString());
							}
						}
					}
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}
			}
			else if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Objets"){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "SELECT * FROM Objet";
					ResultSet result = st.executeQuery(sql);
					
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Tout"){
						while(result.next()){
						    System.out.println("Objets Tout !");
						    String[] test = {result.getObject(1).toString()};						    
						    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Validés"){
						while(result.next()){
							// recupere le champ autorise, si = 1 alors l'administration a autoris� le plan
							if((boolean)result.getObject(4) == true){
							    System.out.println("Objets Validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
							}
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Non-validés"){
						while(result.next()){
							//recupere le champ autorise, si = 1 alors l'administration a autorise le plan
							if((boolean)result.getObject(4) == false){
							    System.out.println("Objets Non-validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
							}
						}
					}
				}
				catch(SQLException e1){e1.printStackTrace();}
				catch(ClassNotFoundException e1){e1.printStackTrace();}
			}
		}
		else if(src==DialogueAdministrationView.getInstance().getButtonOk()) {
			String selectedTable = null;
			if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Plans de salle"){
				selectedTable = "Plan_de_salle";
			}
			else if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Objets"){
				selectedTable = "Objet";
			}
			if(DialogueAdministrationView.getInstance().getActionAEffectuer().getSelectedItem().toString() == "Valider"){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "UPDATE "+ selectedTable +" SET valide = true WHERE NOM = '"+DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0)+"'";
					st.executeUpdate(sql);
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}
			}
			if(DialogueAdministrationView.getInstance().getActionAEffectuer().getSelectedItem().toString() == "Invalider"){
				System.out.println("Modifier " + DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0));
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "UPDATE "+ selectedTable +" SET valide = false WHERE NOM = '"+DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0)+"'";
					st.executeUpdate(sql);
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}
			}
			if(DialogueAdministrationView.getInstance().getActionAEffectuer().getSelectedItem().toString() == "Supprimer"){
				System.out.println("Supprimer " + DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0));
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "DELETE FROM "+ selectedTable +" WHERE NOM = '"+DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0)+"'";
					st.executeUpdate(sql);
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}
			}

			//Mise a jour de la liste, non-optimis� !
			while(DialogueAdministrationView.getInstance().getTableModel().getRowCount() != 0){
			    	DialogueAdministrationView.getInstance().getTableModel().removeRow(0);
		    }
			if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Plans de salle"){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "SELECT * FROM Plan_de_salle";
					ResultSet result = st.executeQuery(sql);
					
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Tout"){
						while(result.next()){
						    System.out.println("Plan de salle Tout !");
						    String[] test = {result.getObject(1).toString()};						    
						    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Validés"){
						while(result.next()){
							// recuperele champ autorise, si = 1 alors l'administration a autoris� le plan
							if((boolean)result.getObject(3) == true){
							    System.out.println("Plan de salle Validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
					    		//PrincipaleData.stringToXml(result.getObject(2).toString());
							}
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Non-validés"){
						while(result.next()){
							//recupere le champ autorise, si = 1 alors l'administration a autoris� le plan
							if((boolean)result.getObject(3) == false){
							    System.out.println("Plan de salle Non-validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
							}
						}
					}
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}
			}
			else if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Objets"){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "SELECT * FROM Objet";
					ResultSet result = st.executeQuery(sql);
					
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Tout"){
						while(result.next()){
						    System.out.println("Objets Tout !");
						    String[] test = {result.getObject(1).toString()};						    
						    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Validés"){
						while(result.next()){
							//r�cup�re le champ autorise, si = 1 alors l'administration a autoris� le plan
							if((boolean)result.getObject(4) == true){
							    System.out.println("Objets Validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
							}
						}
					}
					if(DialogueAdministrationView.getInstance().getTriValidation().getSelectedItem().toString() == "Non-validés"){
						while(result.next()){
							//r�cup�re le champ autorise, si = 1 alors l'administration a autoris� le plan
							if((boolean)result.getObject(4) == false){
							    System.out.println("Objets Non-validés !");
							    String[] test = {result.getObject(1).toString()};						    
							    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
							}
						}
					}
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(DialogueAdministrationView.getInstance().getTable().getSelectedRow() != -1){
			System.out.println(DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0));
			DialogueAdministrationView.getInstance().getButtonOk().setEnabled(true);
			String table = null;
			if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Objets"){
				table = "Objet";
			}
			if(DialogueAdministrationView.getInstance().getChoixType().getSelectedItem().toString() == "Plans de salle"){
				table = "Plan_de_salle";
			}
			if(table.equals("Objet")){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Statement st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "SELECT * FROM "+table+" WHERE NOM = '"+DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0)+"'"; ;
					ResultSet result = st.executeQuery(sql);
					while(result.next()){
			    		String chemin = PrincipaleData.stringToXmlTmp(result.getObject(2).toString());
						try {
							DialogueAdministrationView.getInstance().getPanelVue().removeAll();
							DialogueAdministrationView.getInstance().displayFeature(ArboObjetData.chargerObjet(chemin));
							DialogueAdministrationView.getInstance().repaint();
							DialogueAdministrationView.getInstance().revalidate();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
		    	catch(SQLException e1){
		    		e1.printStackTrace();
		    	}
		    	catch(ClassNotFoundException e1){
		    		e1.printStackTrace();
		    	}
			}
			else if(table.equals("Plan_de_salle")){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Statement st = DialogueConnexionAdministrationCtrl.getConnexion().createStatement();
					String sql = "SELECT * FROM "+table+" WHERE NOM = '"+DialogueAdministrationView.getInstance().getTable().getValueAt(DialogueAdministrationView.getInstance().getTable().getSelectedRow(), 0)+"'"; ;
					ResultSet result = st.executeQuery(sql);
					while(result.next()){
			    		String chemin = PrincipaleData.stringToXmlTmp(result.getObject(2).toString());
			    		// description du plan a faire
					}
				}
		    	catch(SQLException e1){
		    		e1.printStackTrace();
		    	}
		    	catch(ClassNotFoundException e1){
		    		e1.printStackTrace();
		    	}
			}
		}
		else{
        DialogueAdministrationView.getInstance().getButtonOk().setEnabled(false);
		}
	}
	public static DialogueAdministrationCtrl getInstance() { return dialogueAdministrationCtrl; }
}
