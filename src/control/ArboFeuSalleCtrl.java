package control;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import plan.PlanFeu;
import plan.PlanSalle;
import view.ArboFeuSalleView;
import view.DialogueNouveauPlanView;
import view.PlanView;
import view.PrincipaleView;
import data.ArboFeuSalleData;
import data.PlanData;

public class ArboFeuSalleCtrl implements ActionListener, KeyListener, MouseListener {
	
	private static ArboFeuSalleCtrl arboFeuSalleCtrl = new ArboFeuSalleCtrl();
	
	@Override
	public void keyReleased(KeyEvent e) {
		// modification du noeud racine de la JTree en fonction de la saisie de recherche
		
		PrincipaleView.reloadJTrees();
		
		// affichage du mot clef "Rechercher" quand il n'ya rien dans la saisie � la maniere de google
		if(ArboFeuSalleView.getInstance().getZoneRechercher().getText().equals("")) {
			ArboFeuSalleView.getInstance().getZoneRechercher().setFont(new Font("Arial", Font.ITALIC, 12));
			ArboFeuSalleView.getInstance().getZoneRechercher().setText("Rechercher");
			ArboFeuSalleView.getInstance().getZoneRechercher().setCaretPosition(0);
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
						String nomFichierXml = ((JTree)src).getPathForRow(((JTree)src).getSelectionRows()[0]).toString();
						nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
						System.out.println(((JTree)src).getName()+nomFichierXml);
						if(nomFichierXml.endsWith(".pf")) {
							try {
								PlanFeu pf = ArboFeuSalleData.chargerPlanFeu(((JTree)src).getName()+nomFichierXml);
								if(!PlanData.getModifications().keySet().contains(pf.getNom())) {
									PlanView.chargerPlanFeu(pf);
									PlanData.addPlan(pf);
									PlanView.getInstance().clearLabelSelection();
									PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
									PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);

								}
								else {
									PlanView.displayToast("Le plan \""+pf.getNom()+"\" est deja en cours d'edition", PlanView.MESSAGE_ERROR);
								}
							}
							catch(IOException e2) {}
						}
						else if(nomFichierXml.endsWith(".ps")) {
							try {
								PlanSalle ps = ArboFeuSalleData.chargerPlanSalle(((JTree)src).getName()+nomFichierXml);
								if(!PlanData.getModifications().keySet().contains(ps.getNom())) {
									PlanView.chargerPlanSalle(ps);
									PlanData.addPlan(ps);
									PlanView.getInstance().clearLabelSelection();
									PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
									PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);
								}
								else {
									PlanView.displayToast("Le plan \""+ps.getNom()+"\" est deja en cours d'edition", PlanView.MESSAGE_ERROR);
								}
							}
							catch(IOException e3) {}
						}

					}
				});

				JMenuItem menuItemSupprimer = new JMenuItem("supprimer");
				menuItemSupprimer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int option = JOptionPane.showConfirmDialog(null, "Voulez vous supprimer definitivement le plan ?", "Suppression d'un plan", JOptionPane.YES_NO_OPTION);
						if(option==JOptionPane.YES_OPTION) {
							String nomFichierXml = ((JTree)src).getPathForRow(((JTree)src).getSelectionRows()[0]).toString();
							nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
							File f = new File(((JTree)src).getName()+nomFichierXml);
							if(f.isFile()) {
								f.delete();
							}
							PrincipaleView.reloadJTrees();
						}
					}
				});
				
				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.add(menuItemOuvrir);
				popupMenu.add(menuItemSupprimer);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		// si la zone de saisie contient le texte "Rechercher" alors on place le curseur au d�but du texte
		if(ArboFeuSalleView.getInstance().getZoneRechercher().getText().equals("Rechercher")) {
			ArboFeuSalleView.getInstance().getZoneRechercher().setCaretPosition(0);
		}
		if(e.getSource() instanceof JTree) {
			TransferHandlerJTreePlans.getInstance().setDernierJTreeCliquer((JTree)e.getSource());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// si la zone de saisie contient le texte "Rechercher" alors on place le curseur au d�but du texte
		if(ArboFeuSalleView.getInstance().getZoneRechercher().getText().equals("Rechercher") && 
		ArboFeuSalleView.getInstance().getZoneRechercher().getFont().equals(new Font("Arial", Font.ITALIC, 12))) {
			ArboFeuSalleView.getInstance().getZoneRechercher().setText("");
			ArboFeuSalleView.getInstance().getZoneRechercher().setFont(new Font("Arial", Font.BOLD, 15));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==ArboFeuSalleView.getInstance().getBoutonNouveau()) {
			DefaultTreeModel model = (DefaultTreeModel)DialogueNouveauPlanView.getInstance().getArboSalle().getModel();
			model.setRoot(PrincipaleView.loadTree("local/plan/salle", ""));
			DialogueNouveauPlanView.getInstance().setVisible(true);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()==2) {
			if(e.getSource() instanceof JTree) {
				JTree tree = ((JTree)e.getSource());
				if(tree.getSelectionRows().length>0) {
					String nomFichierXml = tree.getPathForRow(tree.getSelectionRows()[0]).toString();
					nomFichierXml = nomFichierXml.substring(3, nomFichierXml.lastIndexOf("]"));
					if(nomFichierXml.endsWith(".pf")) {
						try {
							PlanFeu pf = ArboFeuSalleData.chargerPlanFeu(tree.getName()+nomFichierXml);
							if(!PlanData.getModifications().keySet().contains(pf.getNom())) {
								PlanView.chargerPlanFeu(pf);
								PlanData.addPlan(pf);
								PlanView.getInstance().clearLabelSelection();
								PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
								PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);
								PrincipaleView.getInstance().getExporterEnPdfMI().setEnabled(true);
								PrincipaleView.getInstance().getEnregistrerSousMI().setEnabled(true);
							}
							else {
								PlanView.displayToast("Le plan \""+pf.getNom()+"\" est deja en cours d'edition", PlanView.MESSAGE_ERROR);
							}
						}
						catch(IOException e1) {}
					}
					else if(nomFichierXml.endsWith(".ps")) {
						try {
							PlanSalle ps = ArboFeuSalleData.chargerPlanSalle(tree.getName()+nomFichierXml);
							if(!PlanData.getModifications().keySet().contains(ps.getNom())) {
								PlanView.chargerPlanSalle(ps);
								PlanData.addPlan(ps);
								PlanView.getInstance().clearLabelSelection();
								PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
								PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);
								PrincipaleView.getInstance().getExporterEnPdfMI().setEnabled(true);
								PrincipaleView.getInstance().getEnregistrerSousMI().setEnabled(true);
							}
							else {
								PlanView.displayToast("Le plan \""+ps.getNom()+"\" est deja en cours d'edition", PlanView.MESSAGE_ERROR);
							}
						}
						catch(IOException e2) {}
					}
				}
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
	public void keyTyped(KeyEvent e) {}

	public static ArboFeuSalleCtrl getInstance() { return arboFeuSalleCtrl; }
}