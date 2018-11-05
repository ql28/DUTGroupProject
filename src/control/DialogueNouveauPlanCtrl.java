package control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.swing.JPanel;

import plan.PlanFeu;
import plan.PlanSalle;
import view.DialogueNouveauPlanView;
import view.PlanView;
import view.PrincipaleView;
import data.ArboFeuSalleData;
import data.PlanData;

public class DialogueNouveauPlanCtrl implements ActionListener, KeyListener {

	private static DialogueNouveauPlanCtrl dialogueNouveauPlanCtrl = new DialogueNouveauPlanCtrl();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueNouveauPlanView.getInstance().getChoixSalle()) {
			DialogueNouveauPlanView.getInstance().getPanPrincipalFeu().setVisible(false);
			DialogueNouveauPlanView.getInstance().getPanPrincipalSalle().setVisible(true);
			DialogueNouveauPlanView.getInstance().repaint();
			DialogueNouveauPlanView.getInstance().revalidate();
		}
		else if(src==DialogueNouveauPlanView.getInstance().getChoixFeu()) {	
			DialogueNouveauPlanView.getInstance().getPanPrincipalSalle().setVisible(false);
			DialogueNouveauPlanView.getInstance().getPanPrincipalFeu().setVisible(true);
			DialogueNouveauPlanView.getInstance().repaint();
			DialogueNouveauPlanView.getInstance().revalidate();
		}
		else if(src==DialogueNouveauPlanView.getInstance().getChoixFeuVierge()) {
			DialogueNouveauPlanView.getInstance().getScrollArboSalle().setVisible(false);
			DialogueNouveauPlanView.getInstance().getPanelDimensionsFeu().setVisible(true);
			DialogueNouveauPlanView.getInstance().repaint();
			DialogueNouveauPlanView.getInstance().revalidate();
		}
		if(src==DialogueNouveauPlanView.getInstance().getChoixFeuBaseSurSalle()) {
			DialogueNouveauPlanView.getInstance().getPanelDimensionsFeu().setVisible(false);
			DialogueNouveauPlanView.getInstance().getScrollArboSalle().setVisible(true);
			DialogueNouveauPlanView.getInstance().repaint();
			DialogueNouveauPlanView.getInstance().revalidate();
		}
		if(src==DialogueNouveauPlanView.getInstance().getFermer()) {
			DialogueNouveauPlanView.getInstance().setVisible(false);
			DialogueNouveauPlanView.getInstance().repaint();
			DialogueNouveauPlanView.getInstance().revalidate();
		}
		else if(src==DialogueNouveauPlanView.getInstance().getCreer()) {
			PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);
			PrincipaleView.getInstance().getEnregistrerMI().setEnabled(true);
			PlanView.getInstance().getSaveButton().setEnabled(true);
			PrincipaleView.getInstance().getEnregistrerSousMI().setEnabled(true);
			PrincipaleView.getInstance().getExporterEnPdfMI().setEnabled(true);

			if(!DialogueNouveauPlanView.getInstance().getNom().getText().equals("")) {
				if(DialogueNouveauPlanView.getInstance().getChoixSalle().isSelected()==true) {
					if((int)(DialogueNouveauPlanView.getInstance().getPuissanceMax().getValue())>-1) {
						if((int)(DialogueNouveauPlanView.getInstance().getDimensionXSalle().getValue())>-1) {
							if((int)(DialogueNouveauPlanView.getInstance().getDimensionYSalle().getValue())>-1) {
								PlanSalle p1 = new PlanSalle();
								p1.setNom(DialogueNouveauPlanView.getInstance().getNom().getText());
								p1.setX((int)(DialogueNouveauPlanView.getInstance().getDimensionXSalle().getValue()));
								p1.setY((int)(DialogueNouveauPlanView.getInstance().getDimensionYSalle().getValue()));
								try {
									PlanData.addPlan(p1);
									JPanel panel = new JPanel();
									panel.setLayout(null);
									panel.setPreferredSize(new Dimension(p1.getX(), p1.getY()));
									panel.setBackground(new Color(255, 255, 255));
									
									// destination de transfert de donnees
									panel.setTransferHandler(TransferHandlerJTreeObjets.getInstance());
									panel.addMouseWheelListener(PlanCtrl.getInstance());
									panel.addMouseMotionListener(PlanCtrl.getInstance());
									panel.addMouseListener(PlanCtrl.getInstance());
									try {
										panel.getDropTarget().addDropTargetListener(PlanCtrl.getInstance());
									} catch (TooManyListenersException e1) {
										e1.printStackTrace();
									}
									
									PlanView.getInstance().addNewPlan(
										DialogueNouveauPlanView.getInstance().getNom().getText(), 
										panel
									);
									
								}
								catch(RuntimeException e1) {
									PlanView.displayToast("Un plan nomme \""+p1.getNom()+"\" existe deja", PlanView.MESSAGE_ERROR);
								}
								DialogueNouveauPlanView.getInstance().setVisible(false);
							}
						}
					}
				}
				if(DialogueNouveauPlanView.getInstance().getChoixFeu().isSelected()==true) {
					if(DialogueNouveauPlanView.getInstance().getChoixFeuVierge().isSelected()==true) {
						if( (int)(DialogueNouveauPlanView.getInstance().getDimensionXFeu().getValue()) >-1) {
							if( (int)(DialogueNouveauPlanView.getInstance().getDimensionYFeu().getValue()) >-1) {
								PlanFeu p1 = new PlanFeu();
								p1.setNom(DialogueNouveauPlanView.getInstance().getNom().getText());
								p1.setX((int)(DialogueNouveauPlanView.getInstance().getDimensionXFeu().getValue()));
								p1.setY((int)(DialogueNouveauPlanView.getInstance().getDimensionYFeu().getValue()));
								try {
									PlanData.addPlan(p1);
									JPanel panel = new JPanel();
									panel.setLayout(null);
									panel.setBackground(new Color(255, 255, 255));
									try {
										PlanSalle ps = (PlanSalle)ArboFeuSalleData.chargerPlanSalle(p1.getPlanSalle());
										panel.setPreferredSize(new Dimension(ps.getX(), ps.getY()) );
									}
									catch (IOException e2) {
										panel.setPreferredSize(new Dimension(p1.getX(), p1.getY()));
									}
									// destination de transfert de donn�es
									panel.setTransferHandler(TransferHandlerJTreeObjets.getInstance());
									panel.addMouseWheelListener(PlanCtrl.getInstance());
									panel.addMouseListener(PlanCtrl.getInstance());
									try {
										panel.getDropTarget().addDropTargetListener(PlanCtrl.getInstance());
									} catch (TooManyListenersException e1) {
										e1.printStackTrace();
									}
									PlanView.getInstance().addNewPlan(DialogueNouveauPlanView.getInstance().getNom().getText(), panel);
								}
								catch(RuntimeException e1) {
									PlanView.displayToast("Un plan nomm� \""+p1.getNom()+"\" existe d�j�", PlanView.MESSAGE_ERROR);
								}
								DialogueNouveauPlanView.getInstance().setVisible(false);
							}
						}
					}
					if(DialogueNouveauPlanView.getInstance().getChoixFeuBaseSurSalle().isSelected()==true) {
						if(!DialogueNouveauPlanView.getInstance().getArboSalle().getLastSelectedPathComponent().equals("")) {
							PlanFeu p1 = new PlanFeu();
							p1.setNom(DialogueNouveauPlanView.getInstance().getNom().getText());
							p1.setPlanSalle(
								DialogueNouveauPlanView.getInstance().getArboSalle().getName()+
								DialogueNouveauPlanView.getInstance().getArboSalle().getLastSelectedPathComponent().toString()
							);
							try {
								PlanData.addPlan(p1);
								PlanView.chargerPlanFeu(p1);
							}
							catch(RuntimeException e1) {
								PlanView.displayToast("Un plan nomm� \""+p1.getNom()+"\" existe deja", PlanView.MESSAGE_ERROR);
							}
							DialogueNouveauPlanView.getInstance().setVisible(false);
						}
					}
				}
				String idCurrentPanel = (PlanView.getInstance().getTitleActivePanel());
				PlanView.getInstance().getCancelButton().setEnabled(
						!(PlanData.getCurrentModification(PlanView.getInstance().getTitleActivePanel())<0)
						);
				PlanView.getInstance().getRestoreButton().setEnabled(
						!(PlanData.getCurrentModification(idCurrentPanel)>PlanData.getModifications().get(idCurrentPanel).size()-2)
						);
			}
		}
	}

	public static DialogueNouveauPlanCtrl getInstance() { return dialogueNouveauPlanCtrl; }

	@Override
	public void keyPressed(KeyEvent e) {		
	}

	@Override
	public void keyReleased(KeyEvent e) {		
		if (DialogueNouveauPlanView.getInstance().getNom().getText().equals("")) DialogueNouveauPlanView.getInstance().getCreer().setEnabled(false);
		else DialogueNouveauPlanView.getInstance().getCreer().setEnabled(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

}