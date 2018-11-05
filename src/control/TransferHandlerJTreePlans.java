package control;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.TransferHandler;

import plan.PlanFeu;
import plan.PlanSalle;
import view.PlanView;
import view.PrincipaleView;
import data.ArboFeuSalleData;
import data.PlanData;

public class TransferHandlerJTreePlans extends TransferHandler {

	private static final long serialVersionUID = 1L;

	private static TransferHandlerJTreePlans transferHandlerJTreePlans = new TransferHandlerJTreePlans();

	private JTree derniereJTreeCliquer;

	public boolean importData(TransferSupport support) {
		
		if(!canImport(support)) { return false; }
		Transferable transferable = support.getTransferable();

		String nomJLeaf = null;
		try {
			nomJLeaf = (String)transferable.getTransferData(DataFlavor.stringFlavor);
		}
		catch(Exception e) { return false; }
		
		if(nomJLeaf.endsWith(".pf")) {
			try {
				PlanFeu pf = ArboFeuSalleData.chargerPlanFeu(derniereJTreeCliquer.getName()+nomJLeaf);
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
			catch(IOException e) {}
		}
		else if(nomJLeaf.endsWith(".ps")) {
			try {
				PlanSalle ps = ArboFeuSalleData.chargerPlanSalle(derniereJTreeCliquer.getName()+nomJLeaf);
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
			catch(IOException e) { e.printStackTrace(); }
		}
		return true;
	}
	
	public boolean canImport(TransferSupport support) {
		if(!support.isDrop()) { return false; }
		return support.isDataFlavorSupported(DataFlavor.stringFlavor);
	}

	public JTree getDernierJTreeCliquer() { return derniereJTreeCliquer; }
	public void setDernierJTreeCliquer(JTree dernierJTreeCliquer) { this.derniereJTreeCliquer = dernierJTreeCliquer; }
	public static TransferHandlerJTreePlans getInstance() { return transferHandlerJTreePlans; }
	
}
