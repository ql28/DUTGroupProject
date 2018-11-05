package control;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSalle;
import objet.ObjetSupport;
import plan.PlanFeu;
import plan.PlanSalle;
import data.ArboObjetData;
import data.PlanData;
import view.ArboObjetView;
import view.DialogueDescriptionObjetView;
import view.DialogueNouvelObjetView;
import view.PlanView;
import view.PrincipaleView;

public class ArboObjetCtrl implements KeyListener, MouseListener, MouseMotionListener {

	private static ArboObjetCtrl arboObjetCtrl = new ArboObjetCtrl();

	@Override
	public void keyReleased(KeyEvent e) {
		// modification du noeud racine de la JTree en fonction de la saisie de recherche
		PrincipaleView.reloadJTrees();

		// affichage du mot clef "Rechercher" quand il n'ya rien dans la saisie � la maniere de google
		if(ArboObjetView.getInstance().getZoneRechercher().getText().equals("")) {
			ArboObjetView.getInstance().getZoneRechercher().setFont(new Font("Arial", Font.ITALIC, 12));
			ArboObjetView.getInstance().getZoneRechercher().setText("Rechercher");
			ArboObjetView.getInstance().getZoneRechercher().setCaretPosition(0);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		final Object src = e.getSource();
		
		if(e.getButton()==MouseEvent.BUTTON3) {
			if(src instanceof JTree) {
				
				int row = ((JTree)src).getClosestRowForLocation(e.getX(), e.getY());
				((JTree)src).setSelectionRow(row);

				JMenuItem menuItemOuvrir = new JMenuItem("ouvrir");
				menuItemOuvrir.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
		
						if(PlanView.getInstance().getTabPan().getSelectedIndex()>-1) {
							try {
								String nomFichierXml = ((JTree)src).getPathForRow(((JTree)src).getSelectionRows()[0]).toString();
								nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
								
								Objet objet = ArboObjetData.chargerObjet(((JTree)src).getName()+nomFichierXml);
								
								objet.setX(0);
								objet.setY(0);
				
								String nomPanel = PlanView.getInstance().getTitleActivePanel();
				
								if((objet instanceof ObjetFeu && PlanData.getPlan(nomPanel) instanceof PlanFeu) ||
								(objet instanceof ObjetSalle && PlanData.getPlan(nomPanel) instanceof PlanSalle)) {
									PlanData.addObjet(nomPanel, objet);
									PlanView.getInstance().addObjet(nomPanel, objet);
									PlanData.setDernierComponentSelectionner(objet.getId());
								}
								else if(objet instanceof ObjetSupport) {
									PlanData.addObjet(nomPanel, objet);
									PlanView.getInstance().addObjet(nomPanel, objet);
									PlanData.setDernierComponentSelectionner(objet.getId());
								}
								else {
									PlanView.displayToast(
										"Impossible d'inserer un "+(objet instanceof ObjetFeu ? "Objet Feu" : "Objet Salle")+
										" dans un "+(PlanData.getPlan(nomPanel) instanceof PlanFeu ? "Plan Feu" : "Plan Salle")
										, PlanView.MESSAGE_ERROR
									);
								}
							} catch (IOException e1) {}
						}
					}
				});
				
				JMenuItem menuItemDescription = new JMenuItem("description");
				menuItemDescription.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String nomFichierXml = ((JTree)src).getPathForRow(((JTree)src).getSelectionRows()[0]).toString();
						nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
						try {
							Objet o = ArboObjetData.chargerObjet(((JTree)src).getName()+nomFichierXml);
							DialogueDescriptionObjetView.getInstance().displayFeature(o);
						}
						catch (IOException e1) { e1.printStackTrace(); }
					}
				});

				JMenuItem menuItemSupprimer = new JMenuItem("supprimer");
				menuItemSupprimer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int option = JOptionPane.showConfirmDialog(null, "Voulez vous supprimer definitivement l'objet ?", "Suppression d'un objet", JOptionPane.YES_NO_OPTION);
						if(option==JOptionPane.YES_OPTION) {
							String nomFichierXml = ((JTree)src).getPathForRow(((JTree)src).getSelectionRows()[0]).toString();
							nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
							File f = new File(((JTree)src).getName()+nomFichierXml);
							if(f.isFile()) {
								f.delete();
							}
							f = new File("res/img_objets/"+nomFichierXml.substring(0, nomFichierXml.lastIndexOf(".xml"))+".png");
							if(f.isFile()) {
								f.delete();
							}
							PrincipaleView.reloadJTrees();
						}
					}
				});
				
				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.add(menuItemOuvrir);
				popupMenu.add(menuItemDescription);
				popupMenu.add(menuItemSupprimer);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		if(e.getSource().equals(ArboObjetView.getInstance().getZoneRechercher())) {
			// si la zone de saisie contient le texte "Rechercher" alors on place le curseur au d�but du texte � la maniere de google
			if(ArboObjetView.getInstance().getZoneRechercher().getText().equals("Rechercher")) {
				ArboObjetView.getInstance().getZoneRechercher().setCaretPosition(0);
			}
		}
		if(e.getSource() instanceof JTree) {
			TransferHandlerJTreeObjets.getInstance().setDernierJTreeCliquer((JTree)e.getSource());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// desaffichage du mot clef "Rechercher" quand le clavier a frappees la maniere de google
		if(ArboObjetView.getInstance().getZoneRechercher().getText().equals("Rechercher") &&
		ArboObjetView.getInstance().getZoneRechercher().getFont().equals(new Font("Arial", Font.ITALIC, 12))) {
			ArboObjetView.getInstance().getZoneRechercher().setText("");
			ArboObjetView.getInstance().getZoneRechercher().setFont(new Font("Arial", Font.BOLD, 15));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object src = e.getSource();
		if(src==ArboObjetView.getInstance().getBoutonNouveau()){
			DialogueNouvelObjetView.getInstance().setVisible(true);
		}
		else if(e.getClickCount()==2 && PlanData.getPlans().size()>0) {
			JTree tree = ((JTree)e.getSource());
			if(tree.getSelectionRows().length>0) {
				String nomFichierXml = tree.getPathForRow(tree.getSelectionRows()[0]).toString();
				nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
				Objet objet;
				try {
					objet = ArboObjetData.chargerObjet(tree.getName()+nomFichierXml);

					objet.setX(0);
					objet.setY(0);
	
					String nomPanel = PlanView.getInstance().getTitleActivePanel();
					
					if((objet instanceof ObjetFeu && PlanData.getPlan(nomPanel) instanceof PlanFeu) ||
					(objet instanceof ObjetSalle && PlanData.getPlan(nomPanel) instanceof PlanSalle)) {
						PlanData.addObjet(nomPanel, objet);
						PlanView.getInstance().addObjet(nomPanel, objet);
						PlanData.setDernierComponentSelectionner(objet.getId());
					}
					else if(objet instanceof ObjetSupport) {
						PlanData.addObjet(nomPanel, objet);
						PlanView.getInstance().addObjet(nomPanel, objet);
						PlanData.setDernierComponentSelectionner(objet.getId());
					}
					else {
						PlanView.displayToast(
							"Impossible d'inserer un "+(objet instanceof ObjetFeu ? "Objet Feu" : "Objet Salle")+
							" dans un "+(PlanData.getPlan(nomPanel) instanceof PlanFeu ? "Plan Feu" : "Plan Salle")
							, PlanView.MESSAGE_ERROR
						);
					}
				} catch (IOException e1) {}
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static ArboObjetCtrl getInstance() { return arboObjetCtrl; }

}
