package control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import objet.Objet;
import objet.ObjetSupport;
import plan.Plan;
import plan.PlanFeu;
import plan.PlanSalle;
import view.DialogueDescriptionObjetView;
import view.PlanView;
import view.PrincipaleView;
import data.ArboFeuSalleData;
import data.Modification;
import data.PlanData;
import data.PrincipaleData;

public class PlanCtrl implements MouseWheelListener, KeyListener, ActionListener, ChangeListener, MouseListener, DropTargetListener, MouseMotionListener {

	private static PlanCtrl planCtrl = new PlanCtrl();

	public void mousePressed(MouseEvent e) {
		
		Object src = e.getSource();
		
		if(src==PlanView.getInstance().getActivePanel()) {
			if(PlanView.getInstance().getTabPan().getSelectedIndex()>-1) {
				PlanData.setDernierComponentSelectionner(null);
				PlanView.getInstance().clearLabelSelection();
			}
		}
		else {
			if(e.getButton()==MouseEvent.BUTTON3) {
				JMenuItem menuItemDescription = new JMenuItem("description");
				menuItemDescription.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Plan p = PlanData.getPlan(PlanView.getInstance().getTitleActivePanel());
						Objet o = null;
						if(p instanceof PlanFeu) {
							o = ((PlanFeu)p).getObjetFeu(PlanData.getDernierComponentSelectionner());
						}
						else if(p instanceof PlanSalle) {
							o = ((PlanSalle)p).getObjetSalle(PlanData.getDernierComponentSelectionner());
						}
						if(o==null) {
							p.getObjetSupport(PlanData.getDernierComponentSelectionner());
						}

						DialogueDescriptionObjetView.getInstance().displayFeature(o);
					}
				});
				JMenuItem menuItemSupprimer = new JMenuItem("supprimer");
				menuItemSupprimer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for(Component tmpComponent : PlanView.getInstance().getActivePlan().getComponents()) {
							if(tmpComponent.getName().equals(PlanData.getDernierComponentSelectionner())) {
								PlanView.getInstance().getActivePlan().remove(tmpComponent);
								PlanView.getInstance().repaint();
								if(PlanData.getPlan(PlanView.getInstance().getTitleActivePanel()) instanceof PlanFeu) {
									((PlanFeu)PlanData.getPlan(PlanView.getInstance().getTitleActivePanel())).removeObjetFeu(tmpComponent.getName());
								}
								else if(PlanData.getPlan(PlanView.getInstance().getTitleActivePanel()) instanceof PlanSalle) {
									((PlanSalle)PlanData.getPlan(PlanView.getInstance().getTitleActivePanel())).removeObjetSalle(tmpComponent.getName());
								}
								PlanData.getPlan(PlanView.getInstance().getTitleActivePanel()).removeObjetSupport(tmpComponent.getName());
							}
						}
					}
				});
				JPopupMenu popupMenu = new JPopupMenu();
				popupMenu.add(menuItemDescription);
				popupMenu.add(menuItemSupprimer);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
	
			// mise a jour boutons sauvegarde undo redo
			if(src==PlanView.getInstance().getTabPan()) {
				if(PlanView.getInstance().getTabPan().getSelectedComponent()!=null) {
					if(PlanView.getInstance().isSaveCurrentPlan()) {
						PlanView.getInstance().getSaveButton().setEnabled(false);
						PrincipaleView.getInstance().getEnregistrerMI().setEnabled(false);
					}
					else if(!PlanView.getInstance().isSaveCurrentPlan()){
						PlanView.getInstance().getSaveButton().setEnabled(true);
						PrincipaleView.getInstance().getEnregistrerMI().setEnabled(true);
					}
					
					String idCurrentPanel = (PlanView.getInstance().getTitleActivePanel());
					PlanView.getInstance().getCancelButton().setEnabled(
						!(PlanData.getCurrentModification(idCurrentPanel)<0)
					);
					PlanView.getInstance().getRestoreButton().setEnabled(
						!(PlanData.getCurrentModification(idCurrentPanel)>PlanData.getModifications().get(idCurrentPanel).size()-2)
					);
				}
			}
			else {
				if(src instanceof JLabel) {
					PlanData.setLastRelativePositionClickedX(e.getX());
					PlanData.setLastRelativePositionClickedY(e.getY());
					PlanView.getInstance().clearLabelSelection();
					// sauvagarde du JLabel selectionner
					PlanData.setDernierComponentSelectionner(((JComponent)e.getSource()).getName());
					// selection du nouvel objet
					PlanView.getInstance().getComponent(PlanData.getDernierComponentSelectionner()).setBorder(BorderFactory.createLineBorder(Color.RED));
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.getSource() instanceof JLabel) {
			
			Point anciennePosition = ((JComponent)e.getSource()).getLocation();
			Point deplacementTotal = e.getPoint();
			Point nouvellePosition = new Point(
				anciennePosition.x+deplacementTotal.x-PlanData.getLastRelativePositionClickedX(),
				anciennePosition.y+deplacementTotal.y-PlanData.getLastRelativePositionClickedY());
			if(nouvellePosition.x<0) { nouvellePosition.x = 0; }
			if(nouvellePosition.y<0) { nouvellePosition.y = 0; }
			
			Dimension d = (((Container)(JComponent)e.getSource()).getParent()).getPreferredSize();
			if(nouvellePosition.x>d.width-((JLabel)e.getSource()).getWidth()) { nouvellePosition.x = d.width-((JLabel)e.getSource()).getWidth(); }
			if(nouvellePosition.y>d.height-((JLabel)e.getSource()).getHeight()) { nouvellePosition.y = d.height-((JLabel)e.getSource()).getHeight(); }
			// modification de l'affichage
			((JComponent)e.getSource()).setLocation(nouvellePosition);
			
			// modification label position curseur
			Point p = ((JLabel)e.getSource()).getLocation();
			int px = (p.x+e.getPoint().x);
			int py =(p.y+e.getPoint().y);
			if(px<0) { px = 0; }
			if(py<0) { py = 0; }
			if(px>d.width) { px = d.width; }
			if(py>d.height) { py = d.height; }
			PlanView.getInstance().getLabelPositionCurseur().setText("x:"+px+" , y:"+py);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object src = e.getSource();

		if(e.getSource() instanceof JLabel &&
		PlanData.getDernierComponentSelectionner()!=null &&
		PlanView.getInstance().getComponent(PlanData.getDernierComponentSelectionner())!=null &&
		PlanView.getInstance().getComponent(PlanData.getDernierComponentSelectionner()) instanceof JLabel) {
			String nomPanel = PlanView.getInstance().getTitleActivePanel();
			if(src!=PlanView.getInstance().getTabPan()) {
				Point deplacementTotal = e.getPoint();
				Point anciennePosition = PlanView.getInstance().getComponent(PlanData.getDernierComponentSelectionner()).getLocation();
				Point nouvellePosition = new Point(
					anciennePosition.x+deplacementTotal.x-PlanData.getLastRelativePositionClickedX(),
					anciennePosition.y+deplacementTotal.y-PlanData.getLastRelativePositionClickedY()
				);
				if(nouvellePosition.x<0) { nouvellePosition.x = 0; }
				if(nouvellePosition.y<0) { nouvellePosition.y = 0; }
				Dimension d = (((Container)(JComponent)e.getSource()).getParent()).getPreferredSize();
				if(nouvellePosition.x>d.width-((JLabel)e.getSource()).getWidth()) { nouvellePosition.x = d.width-((JLabel)e.getSource()).getWidth(); }
				if(nouvellePosition.y>d.height-((JLabel)e.getSource()).getHeight()) { nouvellePosition.y = d.height-((JLabel)e.getSource()).getHeight(); }

				// sauvegarde de la position du drop JLabel
				PlanView.getInstance().setLabelObjet(PlanView.getInstance().getComponent(PlanData.getDernierComponentSelectionner()), nouvellePosition);
				String nomPanelModifie = PlanView.getInstance().getTabPan().getTitleAt(PlanView.getInstance().getTabPan().getSelectedIndex());
				String nomLabelModifie = PlanData.getDernierComponentSelectionner();
				PlanView.getInstance().eraseRedoModification();

				PlanData.saveModification(nomPanelModifie, nomLabelModifie, Modification.DEPLACEMENT, nouvellePosition.x, nouvellePosition.y);

				PlanView.getInstance().setTitleToSavedOfCurrentPlan();

				PlanView.getInstance().getCancelButton().setEnabled(!(PlanData.getCurrentModification(nomPanel)<0));
				PlanView.getInstance().getRestoreButton().setEnabled(false);

				// modification des datas
				PlanData.setPositionObjet(nomPanel, nomLabelModifie, nouvellePosition.x, nouvellePosition.y);
			}
		}

	}

	@Override
	public void dragOver(DropTargetDragEvent e) {
		// sauvegarde de la derniere position du drag JTree
		Point point = e.getLocation();
		PlanData.setLastPositionDraggedX(point.x);
		PlanData.setLastPositionDraggedY(point.y);
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src==PlanView.getInstance().getSaveButton()) {
			String nomPanel = PlanView.getInstance().getTitleActivePanel();
			if(PlanData.getPlan(nomPanel) instanceof PlanFeu) {
				PrincipaleData.sauverXml(PlanData.getPlan(nomPanel), "local/plan/feu");
			}
			else if(PlanData.getPlan(nomPanel) instanceof PlanSalle) {
				PrincipaleData.sauverXml((PlanSalle)PlanData.getPlan(nomPanel), "local/plan/salle");
			}
			PrincipaleView.reloadJTrees();
			PlanView.displayToast("Enregistrement reussi", PlanView.MESSAGE_INFORMATION);
			PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
		}
		else if(src==PlanView.getInstance().getOpenButton()) {
			JFileChooser chooser = new JFileChooser();//cr�ation dun nouveau filechosser
			chooser.setCurrentDirectory(new File("local/plan"));
			chooser.setFileFilter(new FileNameExtensionFilter("Fichier plan", "pf","ps"));
			chooser.setApproveButtonText("Ouvrir un plan"); //intitul� du bouton
			if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {	
				PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);
				PrincipaleView.getInstance().getEnregistrerMI().setEnabled(true);
				PlanView.getInstance().getSaveButton().setEnabled(true);
				PrincipaleView.getInstance().getExporterEnPdfMI().setEnabled(true);
				PrincipaleView.getInstance().getEnregistrerSousMI().setEnabled(true);
				String ext = chooser.getSelectedFile().getAbsolutePath().substring(chooser.getSelectedFile().getAbsolutePath().lastIndexOf("."));

				if (ext.equals(".pf")) {
					PlanFeu pf = null;
					try {
						pf = ArboFeuSalleData.chargerPlanFeu(chooser.getSelectedFile().getAbsolutePath());
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
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
				else if(ext.equals(".ps")){
					PlanSalle ps = null;
					try {
						ps = ArboFeuSalleData.chargerPlanSalle(chooser.getSelectedFile().getAbsolutePath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
			}
		}
		else if(src==PlanView.getInstance().getCancelButton()) {
			if(PlanView.getInstance().getTabPan().getSelectedIndex()>-1) {
				String nomCurrentPanel = PlanView.getInstance().getTitleActivePanel();
				Modification m = PlanData.undoModification(nomCurrentPanel);
				if(m!=null) {
					for(Component tmpComponent : PlanView.getInstance().getActivePlan().getComponents()) {
						if(tmpComponent.getName().equals(m.id)) {
							// si creation de l'objet
							if(m.type==Modification.CREATION) {
								PlanView.getInstance().getActivePlan().remove(tmpComponent);
								PlanView.getInstance().repaint();
								PlanData.undoObjet(nomCurrentPanel, tmpComponent.getName());
							}
							// si deplacement de l'objet
							else if(m.type==Modification.DEPLACEMENT) {
								PlanView.getInstance().setLabelObjet((JLabel)tmpComponent, new Point(m.x, m.y));
								PlanData.setPositionObjet(nomCurrentPanel, tmpComponent.getName(), m.x, m.y);
							}
							// si suppresion de l'objet
							else if(m.type==Modification.SUPPRESSION) {
								Objet objet = PlanData.redoObjet(nomCurrentPanel);

								JLabel labelImageObjet = new JLabel();
								labelImageObjet.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
								labelImageObjet.setName(objet.getId());
								ImageIcon nouvelleIcone = objet.getImageIcon();
								labelImageObjet.setIcon(nouvelleIcone);

								// source de transfert de donnees
								labelImageObjet.addMouseListener(PlanCtrl.getInstance());
								labelImageObjet.addMouseMotionListener(PlanCtrl.getInstance());

								PlanView.getInstance().getActivePlan().add(labelImageObjet);
								// modification ordre prio en Z
								if(!(objet instanceof ObjetSupport)) { PlanView.getInstance().getActivePlan().setComponentZOrder(labelImageObjet, 0); }

								labelImageObjet.setBounds(m.x, m.y, labelImageObjet.getWidth(), labelImageObjet.getHeight());
								labelImageObjet.setSize(nouvelleIcone.getIconWidth(), nouvelleIcone.getIconHeight());
								objet.setX(m.x);
								objet.setY(m.y);

								PlanView.getInstance().repaint();
								
							}
						}
					}
				}
				PlanView.getInstance().setTitleToSavedOfCurrentPlan();
				
				// mise a jour du bouton redo et undo
				String idCurrentPanel = (PlanView.getInstance().getTitleActivePanel());
				if(PlanData.getCurrentModification(idCurrentPanel)<0) {
					PlanView.getInstance().getCancelButton().setEnabled(false);
				}
				else {
					PlanView.getInstance().getCancelButton().setEnabled(true);
				}
				if(!(PlanData.getCurrentModification(idCurrentPanel)>PlanData.getModifications().get(idCurrentPanel).size()-2)) {
					PlanView.getInstance().getRestoreButton().setEnabled(true);
				}
			}
		}
		else if(src==PlanView.getInstance().getRestoreButton()) {
			if(PlanView.getInstance().getTabPan().getSelectedIndex()>-1) {
				String nomCurrentPanel = PlanView.getInstance().getTitleActivePanel();
				Modification m = PlanData.redoModification(nomCurrentPanel);
				if(m!=null) {
					// si modif du type creation alors ajout de l'objet
					if(m.type==Modification.CREATION) {
						Objet objet = PlanData.redoObjet(nomCurrentPanel);

						JLabel labelImageObjet = new JLabel();
						labelImageObjet.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
						labelImageObjet.setName(objet.getId());
						ImageIcon nouvelleIcone = objet.getImageIcon();
						labelImageObjet.setIcon(nouvelleIcone);

						// source de transfert de donnees
						labelImageObjet.addMouseListener(PlanCtrl.getInstance());
						labelImageObjet.addMouseMotionListener(PlanCtrl.getInstance());

						PlanView.getInstance().getActivePlan().add(labelImageObjet);
						// modification ordre prio en Z
						if(!(objet instanceof ObjetSupport)) { PlanView.getInstance().getActivePlan().setComponentZOrder(labelImageObjet, 0); }

						labelImageObjet.setBounds(m.x, m.y, labelImageObjet.getWidth(), labelImageObjet.getHeight());
						labelImageObjet.setSize(nouvelleIcone.getIconWidth(), nouvelleIcone.getIconHeight());
						objet.setX(m.x);
						objet.setY(m.y);

						PlanView.getInstance().repaint();

					}
					// sinon deplacement de l'objet
					else if(m.type==Modification.DEPLACEMENT){
						for(Component tmpComponent : PlanView.getInstance().getActivePlan().getComponents()) {
							if(tmpComponent.getName().equals(m.id)) {
								PlanView.getInstance().setLabelObjet((JLabel)tmpComponent, new Point(m.x, m.y));
								PlanData.setPositionObjet(nomCurrentPanel, tmpComponent.getName(), m.x, m.y);
							}
						}
					}
					// si suppresion de l'objet
					else if(m.type==Modification.SUPPRESSION) {
						for(Component tmpComponent : PlanView.getInstance().getActivePlan().getComponents()) {
							if(tmpComponent.getName().equals(m.id)) {
								PlanView.getInstance().getActivePlan().remove(tmpComponent);
								PlanView.getInstance().repaint();
								PlanData.undoObjet(nomCurrentPanel, tmpComponent.getName());
							}
						}
					}
					PlanView.getInstance().setTitleToSavedOfCurrentPlan();
					
					// mise a jour du bouton redo et undo
					if(PlanData.getCurrentModification(nomCurrentPanel)>PlanData.getModifications().get(nomCurrentPanel).size()-2) {
						PlanView.getInstance().getRestoreButton().setEnabled(false);
					}
					else {
						PlanView.getInstance().getRestoreButton().setEnabled(true);
					}
					if(!(PlanData.getCurrentModification(nomCurrentPanel)<0)) {
						PlanView.getInstance().getCancelButton().setEnabled(true);
					}
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		if(src==PlanView.getInstance().getTabPan()){
			if(PlanView.getInstance().getTabPan().getSelectedComponent()==null) {
				PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(false);
				PrincipaleView.getInstance().getExporterEnPdfMI().setEnabled(false);
				PrincipaleView.getInstance().getEnregistrerSousMI().setEnabled(false);
				
			}
			if(PlanView.getInstance().getTabPan().getSelectedComponent()!=null) {
				PlanView.getInstance().clearLabelSelection();
				JPanel panSelected = (JPanel)PlanView.getInstance().getTabPan().getSelectedComponent();
				PlanView.getInstance().setActivePlan(panSelected);
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		String nomPanel = PlanView.getInstance().getTitleActivePanel();
		
		JLabel label = PlanView.getInstance().getComponent(PlanData.getDernierComponentSelectionner());
		
		if(label!=null) {
			Objet objet = PlanData.getPlan(nomPanel).getObjetSupport(PlanData.getDernierComponentSelectionner());
			if(objet==null) {
				if(PlanData.getPlan(nomPanel) instanceof PlanFeu) {
					objet = ((PlanFeu)PlanData.getPlan(nomPanel)).getObjetFeu(PlanData.getDernierComponentSelectionner());
				}
				else if(PlanData.getPlan(nomPanel) instanceof PlanSalle) {
					objet = ((PlanSalle)PlanData.getPlan(nomPanel)).getObjetSalle(PlanData.getDernierComponentSelectionner());
				}
			}
			
			objet.setOrientation(objet.getOrientation()+(int)e.getPreciseWheelRotation()*5);
			ImageIcon nouvelleImage = objet.getImageIcon();
			Point nouvellePosition = new Point(
				label.getX()-(nouvelleImage.getIconWidth()-label.getWidth())/2,
				label.getY()-(nouvelleImage.getIconHeight()-label.getHeight())/2
			);
			if(nouvellePosition.x<0) { nouvellePosition.x = 0; }
			if(nouvellePosition.y<0) { nouvellePosition.y = 0; }
			Dimension d = (((Container)(JComponent)e.getSource()).getParent()).getPreferredSize();
			if(nouvellePosition.x>d.width-label.getWidth()) { nouvellePosition.x = d.width-label.getWidth(); }
			if(nouvellePosition.y>d.height-label.getHeight()) { nouvellePosition.y = d.height-label.getHeight(); }
			label.setLocation(nouvellePosition.x, nouvellePosition.y);
			label.setSize(nouvelleImage.getIconWidth(), nouvelleImage.getIconHeight());
			objet.setX(nouvellePosition.x);
			objet.setY(nouvellePosition.y);
			label.setIcon(nouvelleImage);
			PlanView.getInstance().setTitleToSavedOfCurrentPlan();

		}
	}
	
	@Override
	public void drop(DropTargetDropEvent e) {}
	@Override
	public void dropActionChanged(DropTargetDragEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {
		if(e.getSource() instanceof JPanel) {
			PlanView.getInstance().getLabelPositionCurseur().setText("x:"+e.getPoint().x+" , y:"+e.getPoint().y);
		}
		else if(e.getSource() instanceof JLabel) {
			Point p = ((JLabel)e.getSource()).getLocation();
			PlanView.getInstance().getLabelPositionCurseur().setText(
				"x:"+(p.x+e.getPoint().x)
				+" , y:"+
				(p.y+e.getPoint().y)
			);
		}
	}
	@Override
	public void dragEnter(DropTargetDragEvent e) {}
	@Override
	public void dragExit(DropTargetEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}

	public static PlanCtrl getInstance() { return planCtrl; }

}
