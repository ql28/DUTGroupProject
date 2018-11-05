package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import view.DialogueAdministrationView;
import view.DialogueConnexionAdministrationView;

public class DialogueConnexionAdministrationCtrl implements ActionListener {
	
	private static DialogueConnexionAdministrationCtrl dialogueAdministrationCtrl = new DialogueConnexionAdministrationCtrl();

	private static Connection cn;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueConnexionAdministrationView.getInstance().getAnnuler()) {
			DialogueConnexionAdministrationView.getInstance().setVisible(false);
		}
		else if(src==DialogueConnexionAdministrationView.getInstance().getConnexion()) {
			//Connexion  a la BDD
			String url = "jdbc:mysql://localhost/adec56";
			String login = DialogueConnexionAdministrationView.getInstance().getIdentifiant().getText();
			String passwd = new String(DialogueConnexionAdministrationView.getInstance().getMotDePasse().getPassword());
			Statement st = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("Connexion en cours...");
				cn = DriverManager.getConnection(url,login,passwd);
			    st = cn.createStatement();
	    		String sql = "show grants for '"+login+"'@'%'";
	    		ResultSet result = st.executeQuery(sql);
	    		String utilisateur = null;
    			while(result.next()){
    				String r = (result.getString(1));
    				String[] parts = r.split("GRANT ");
    				utilisateur = parts[1];
    				parts = utilisateur.split(" ON");
    				utilisateur = parts[0];
				}
    			if(utilisateur.equals("SELECT, INSERT, UPDATE, DELETE")){
    			    System.out.println("Connexion r�ussie");
    				DialogueConnexionAdministrationView.getInstance().getIdentifiant().setText("");
    				DialogueConnexionAdministrationView.getInstance().getMotDePasse().setText("");
    				DialogueConnexionAdministrationView.getInstance().setVisible(false);
    				
    				//initialisation de la liste ds plans de la bdd
					sql = "SELECT * FROM Plan_de_salle";
					result = st.executeQuery(sql);
				    while(DialogueAdministrationView.getInstance().getTableModel().getRowCount() != 0){
				    	DialogueAdministrationView.getInstance().getTableModel().removeRow(0);
				    }
					while(result.next()){
					    String[] test = {result.getObject(1).toString()};						    
					    DialogueAdministrationView.getInstance().getTableModel().addRow(test);
					}
    				DialogueAdministrationView.getInstance().setVisible(true);
				}
    			else{
    			    System.out.println("Connexion refus�e");
					cn.close();
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
    			    System.out.println("Connexion ferm�e");
					cn.close();
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
			}
		}
	}

	public static DialogueConnexionAdministrationCtrl getInstance() { return dialogueAdministrationCtrl; }
	public static Connection getConnexion(){ return cn; }
}
