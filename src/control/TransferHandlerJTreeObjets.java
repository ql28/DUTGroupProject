package control;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSalle;
import objet.ObjetSupport;
import plan.PlanFeu;
import plan.PlanSalle;
import view.PlanView;
import data.ArboFeuSalleData;
import data.ArboObjetData;
import data.PlanData;

public class TransferHandlerJTreeObjets extends TransferHandler {
	
	private static final long serialVersionUID = 1L;

	private static TransferHandlerJTreeObjets transferHandlerJTree = new TransferHandlerJTreeObjets();

	private JTree derniereJTreeCliquer;

	public boolean importData(TransferSupport support) {
		
		if(!canImport(support)) { return false; }
		Transferable transferable = support.getTransferable();

		String nomJLeaf = null;
		try {
			nomJLeaf = (String)transferable.getTransferData(DataFlavor.stringFlavor);
		}
		catch(Exception e) { return false; }

		// ajout de l'objet dans la liste des objets a dessiner sur le panelCentre
		if(nomJLeaf.endsWith(".xml")) {
			// ajout objet
			String nomPanel = PlanView.getInstance().getTitleActivePanel();
			if(derniereJTreeCliquer!=null && nomJLeaf.toString().endsWith(".xml")) {
				try {
					Objet objet = ArboObjetData.chargerObjet(derniereJTreeCliquer.getName()+nomJLeaf);

					objet.setX(PlanData.getLastPositionDraggedX());
					objet.setY(PlanData.getLastPositionDraggedY());

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
				} catch (IOException e) {}
			}
		}
		else if(nomJLeaf.endsWith(".pf")) {
			try {
				PlanFeu pf = ArboFeuSalleData.chargerPlanFeu(TransferHandlerJTreePlans.getInstance().getDernierJTreeCliquer().getName()+nomJLeaf);
				if(!PlanData.getModifications().keySet().contains(pf.getNom())) {
					PlanView.chargerPlanFeu(pf);
					PlanData.addPlan(pf);
					PlanView.getInstance().clearLabelSelection();
					PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
				}
				else {
					PlanView.displayToast("Le plan \""+pf.getNom()+"\" est deja en cours d'edition", PlanView.MESSAGE_ERROR);
				}
			}
			catch(IOException e2) {}
		}
		else if(nomJLeaf.endsWith(".ps")) {
			System.out.println(nomJLeaf);
			try {
				PlanSalle ps = ArboFeuSalleData.chargerPlanSalle(TransferHandlerJTreePlans.getInstance().getDernierJTreeCliquer().getName()+nomJLeaf);
				if(!PlanData.getModifications().keySet().contains(ps.getNom())) {
					PlanView.chargerPlanSalle(ps);
					PlanData.addPlan(ps);
					PlanView.getInstance().clearLabelSelection();
					PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
				}
				else {
					PlanView.displayToast("Le plan \""+ps.getNom()+"\" est deja en cours d'edition", PlanView.MESSAGE_ERROR);
				}
			}
			catch(IOException e2) {}
		}
		return true;
	}
	
	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) { return false; }
		return support.isDataFlavorSupported(DataFlavor.stringFlavor);
	}

	public JTree getDernierJTreeCliquer() { return derniereJTreeCliquer; }
	public void setDernierJTreeCliquer(JTree dernierJTreeCliquer) { this.derniereJTreeCliquer = dernierJTreeCliquer; }
	public static TransferHandlerJTreeObjets getInstance() { return transferHandlerJTree; }
	
}
