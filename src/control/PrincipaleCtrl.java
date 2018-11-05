package control;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeModel;

import objet.Objet;
import objet.ObjetFeu;
import objet.ObjetSalle;
import objet.ObjetSupport;
import plan.Plan;
import plan.PlanFeu;
import plan.PlanSalle;
import view.ArboFeuSalleView;
import view.DialogueAProposView;
import view.DialogueAdministrationView;
import view.DialogueAideView;
import view.DialogueConnexionAdministrationView;
import view.DialogueDescriptionObjetView;
import view.DialogueEnvoyerView;
import view.DialogueNouveauPlanView;
import view.DialogueNouvelObjetView;
import view.PlanView;
import view.PrincipaleView;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.mysql.jdbc.Blob;

import data.ArboFeuSalleData;
import data.PlanData;
import data.PrincipaleData;
import data.ReferenceImage;

public class PrincipaleCtrl implements ActionListener {

	private static PrincipaleCtrl principaleCtrl = new PrincipaleCtrl();

	public static void main(String[] args) {

		initGui();
		initListener();

		// temps de chargement pour les images
		Thread threadChargement = new Thread() {
			public void run() {
				ReferenceImage.chargerImages();
			}
		};
		threadChargement.start();
		long chrono = System.currentTimeMillis();
		while(threadChargement.isAlive() && System.currentTimeMillis()-chrono<500) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		if(threadChargement.isAlive()) {
			PlanView.getInstance().printMessage("Chargement des ressources ...", PlanView.MESSAGE_INFORMATION);
		}
		while(threadChargement.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		PlanView.getInstance().hideMessage();
	}
	
	public static void loadFeaturePlan(Plan plan, Document document) {

		if(plan instanceof PlanFeu) {
			try {
				try {
					document.add(new Paragraph("Liste Objets Feu :\n"));
				} catch (DocumentException e1) {}
				for(ObjetFeu o : ((PlanFeu)plan).getObjetsFeu()) {
					loadFeatureObjet(o, document);
				}try {
					document.add(new Paragraph("\n"));
				} catch (DocumentException e1) {}try {
					document.add(new Paragraph("Liste des Objets Support du plan de Feu :\n"));
				} catch (DocumentException e1) {}
				for(ObjetSupport o : plan.getObjetsSupport()) {
					loadFeatureObjet(o, document);
				}
				try {
					document.add(new Paragraph("\n"));
				} catch (DocumentException e) {}
				if(!((PlanFeu)plan).getPlanSalle().equals("")) {
					try {
						loadFeaturePlan(ArboFeuSalleData.chargerPlanSalle(((PlanFeu)plan).getPlanSalle()), document);
						document.add(new Paragraph("\n"));
					} catch (DocumentException e1) {}
				} 
			} catch (IOException e) {}
		}
		else if(plan instanceof PlanSalle) {
			try {
				document.add(new Paragraph("Liste Objets Salle :\n"));
			} catch (DocumentException e) {}
			for(ObjetSalle o : ((PlanSalle)plan).getObjetsSalle()) {
				loadFeatureObjet(o, document);
			}
			try {
				document.add(new Paragraph("\n"));
			} catch (DocumentException e) {}
			try {
				document.add(new Paragraph("Liste des Objets Support du plan de Salle :\n"));
			} catch (DocumentException e) {}
			for(ObjetSupport o : plan.getObjetsSupport()) {
				loadFeatureObjet(o, document);
			}
			try {
				document.add(new Paragraph("\n"));
			} catch (DocumentException e) {}
		}
	}

	public static void loadFeatureObjet(Objet o, Document document) {
		
		Paragraph paragraph = new Paragraph();
		paragraph.setSpacingBefore(5);
		paragraph.setSpacingAfter(5);

		// ajout de l'image de l'objet
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIcon imageIconObjet = ReferenceImage.getMiniMiniature(o.getNom(), 18, 18);
		BufferedImage biObjet = new BufferedImage(imageIconObjet.getIconWidth(), imageIconObjet.getIconHeight(), BufferedImage.TYPE_INT_ARGB); 
		Graphics g = biObjet.getGraphics();
		imageIconObjet.paintIcon(null, g, 0, 0);
		try {
			ImageIO.write(biObjet, "png", baos);
		} catch (IOException e1) {}
		try {
			paragraph.add(new Chunk(Image.getInstance(baos.toByteArray()), 0, 0));
		} catch (DocumentException | IOException e1) {}
		
		String texte = "";
		for(Field tmpField : PrincipaleData.getFields(o, "fixedFeatureNom")) {
			try {
				String nameFeature = tmpField.getName().substring(12, tmpField.getName().length());
				String valueFeature = tmpField.get(o).toString();
				if(nameFeature.equals("Nom")) {
					texte += " |"+valueFeature+"| ";
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
		}
		for(Field tmpField : PrincipaleData.getFields(o, "fixedFeature")) {
			try {
				String nameFeature = tmpField.getName().substring(12, tmpField.getName().length());
				String valueFeature = tmpField.get(o).toString();
				if(!nameFeature.equals("Nom")) {
					texte += "["+nameFeature+" : "+valueFeature+"]";
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
		}
		paragraph.add(new Phrase(texte));
		try {
			document.add(paragraph);
		} catch (DocumentException e) {}
	}

	public static void initGui() {
		PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(false);
		PrincipaleView.getInstance().getEnregistrerMI().setEnabled(false);
		PrincipaleView.getInstance().getExporterEnPdfMI().setEnabled(false);
		PrincipaleView.getInstance().getEnregistrerSousMI().setEnabled(false);
		PlanView.getInstance().getSaveButton().setEnabled(false);
		PlanView.getInstance().getCancelButton().setEnabled(false);
		PlanView.getInstance().getRestoreButton().setEnabled(false);
	}

	public static void initListener() {
		
		PrincipaleView.getInstance().getNouveauMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getQuitterMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getAideMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getaProposMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getDocumentationMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getOuvrirMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getEnregistrerMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getExporterEnImageMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getEnregistrerSousMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getExporterEnPdfMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getImprimerMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getEnvoyerMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getMajMI().addActionListener(principaleCtrl);
		PrincipaleView.getInstance().getConnexionAdministrateurMI().addActionListener(principaleCtrl);
		
		DialogueAideView.getInstance().getFermer().addActionListener(DialogueAideCtrl.getInstance());

		DialogueAProposView.getInstance().getFermer().addActionListener(DialogueAProposCtrl.getInstance());
		
		DialogueDescriptionObjetView.getInstance().getFermer().addActionListener(DialogueDescriptionObjetCtrl.getInstance());
		DialogueDescriptionObjetView.getInstance().addWindowListener(DialogueDescriptionObjetCtrl.getInstance());

		DialogueNouveauPlanView.getInstance().getFermer().addActionListener(DialogueNouveauPlanCtrl.getInstance());
		DialogueNouveauPlanView.getInstance().getChoixSalle().addActionListener(DialogueNouveauPlanCtrl.getInstance());
		DialogueNouveauPlanView.getInstance().getChoixFeu().addActionListener(DialogueNouveauPlanCtrl.getInstance());
		DialogueNouveauPlanView.getInstance().getChoixFeuVierge().addActionListener(DialogueNouveauPlanCtrl.getInstance());
		DialogueNouveauPlanView.getInstance().getChoixFeuBaseSurSalle().addActionListener(DialogueNouveauPlanCtrl.getInstance());
		DialogueNouveauPlanView.getInstance().getCreer().addActionListener(DialogueNouveauPlanCtrl.getInstance());

		DialogueNouvelObjetView.getInstance().addWindowListener(DialogueNouvelObjetCtrl.getInstance());
		DialogueNouvelObjetView.getInstance().getListeObjet().addItemListener(DialogueNouvelObjetCtrl.getInstance());
		DialogueNouvelObjetView.getInstance().getFeuButton().addActionListener(DialogueNouvelObjetCtrl.getInstance());
		DialogueNouvelObjetView.getInstance().getSalleButton().addActionListener(DialogueNouvelObjetCtrl.getInstance());
		DialogueNouvelObjetView.getInstance().getButtonCancel().addActionListener(DialogueNouvelObjetCtrl.getInstance());
		DialogueNouvelObjetView.getInstance().getButtonCreate().addActionListener(DialogueNouvelObjetCtrl.getInstance());
		
		DialogueEnvoyerView.getInstance().getTypeEnvoi().addActionListener(DialogueEnvoyerCtrl.getInstance());
		DialogueEnvoyerView.getInstance().getEnvoyer().addActionListener(DialogueEnvoyerCtrl.getInstance());
		DialogueEnvoyerView.getInstance().getAnnuler().addActionListener(DialogueEnvoyerCtrl.getInstance());

		DialogueConnexionAdministrationView.getInstance().getAnnuler().addActionListener(DialogueConnexionAdministrationCtrl.getInstance());
		DialogueConnexionAdministrationView.getInstance().getConnexion().addActionListener(DialogueConnexionAdministrationCtrl.getInstance());

		DialogueAdministrationView.getInstance().getQuitter().addActionListener(DialogueAdministrationCtrl.getInstance());
		DialogueAdministrationView.getInstance().getChoixType().addActionListener(DialogueAdministrationCtrl.getInstance());
		DialogueAdministrationView.getInstance().getTriValidation().addActionListener(DialogueAdministrationCtrl.getInstance());
		DialogueAdministrationView.getInstance().getButtonOk().addActionListener(DialogueAdministrationCtrl.getInstance());
		DialogueAdministrationView.getInstance().getTable().getSelectionModel().addListSelectionListener(DialogueAdministrationCtrl.getInstance());
		
		PlanView.getInstance().getTabPan().addMouseListener(PlanCtrl.getInstance());
		PlanView.getInstance().getTabPan().addChangeListener(PlanCtrl.getInstance());
		PlanView.getInstance().getSaveButton().addActionListener(PlanCtrl.getInstance());
		PlanView.getInstance().getCancelButton().addActionListener(PlanCtrl.getInstance());
		PlanView.getInstance().getRestoreButton().addActionListener(PlanCtrl.getInstance());
		PlanView.getInstance().getOpenButton().addActionListener(PlanCtrl.getInstance());

		PlanView.getInstance().setTransferHandler(TransferHandlerJTreePlans.getInstance());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==PrincipaleView.getInstance().getNouveauMI()) {
			DefaultTreeModel model = (DefaultTreeModel)DialogueNouveauPlanView.getInstance().getArboSalle().getModel();
			model.setRoot(PrincipaleView.loadTree("local/plan/salle", ""));
			DialogueNouveauPlanView.getInstance().setVisible(true);

		}
		else if(src==PrincipaleView.getInstance().getEnregistrerMI()) {
			PrincipaleView.reloadJTrees();
			PlanView.displayToast("Enregistrement reussi", PlanView.MESSAGE_INFORMATION);
			PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();
		}
		else if(src==DialogueNouveauPlanView.getInstance().getFermer()) {
			DialogueNouveauPlanView.getInstance().setVisible(false);
		}
		else if(src==PrincipaleView.getInstance().getQuitterMI()) {
			PrincipaleView.getInstance().quit();
		}
		else if(src==PrincipaleView.getInstance().getAideMI()) {
			DialogueAideView.getInstance().setVisible(true);
		}
		else if(src==PrincipaleView.getInstance().getaProposMI()) {
			DialogueAProposView.getInstance().setVisible(true);
		}
		else if(src==PrincipaleView.getInstance().getEnvoyerMI()) {
			DialogueEnvoyerView.getInstance().setVisible(true);
		}
		else if(src==PrincipaleView.getInstance().getConnexionAdministrateurMI()) {
			DialogueConnexionAdministrationView.getInstance().setVisible(true);
		}
		else if(src==PrincipaleView.getInstance().getDocumentationMI()) {
			URI uri = URI.create("http://www.adec56.org/spip/spip.php?article124&PHPSESSID=6492fb9684850fb2504eddb3d54c66a1");
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e1) {
				PlanView.displayToast("Un probl�me est survenu lors de l'ouverture de la documentation", PlanView.MESSAGE_ERROR);
			}
		}
		else if(src==PrincipaleView.getInstance().getEnregistrerSousMI()){
			JFileChooser chooser = new JFileChooser();
			chooser.setApproveButtonText("Enregistrer sous");
			chooser.setDialogTitle("Enregistrer sous");
			if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {	
				String filtreRecherche = ArboFeuSalleView.getInstance().getZoneRechercher().getText();
				if(ArboFeuSalleView.getInstance().getZoneRechercher().getFont().equals(new Font("Arial", Font.ITALIC, 12))) {
					filtreRecherche = "";
				}
				String nom = chooser.getSelectedFile().getName();
				PlanData.getPlan(PlanView.getInstance().getTitleActivePanel()).setNom(nom);
				PrincipaleData.sauverXml(PlanData.getPlan(PlanView.getInstance().getTitleActivePanel()),chooser.getSelectedFile().getParent());
				// rechargement de la jtree concerne par l'ajout d'objet
				DefaultTreeModel modelLocalObjetFeu = (DefaultTreeModel)ArboFeuSalleView.getInstance().getArbreFeuPerso().getModel();
				modelLocalObjetFeu.setRoot(PrincipaleView.loadTree("local/plan/feu", filtreRecherche));
				PlanView.displayToast("Enregistrement reussi", PlanView.MESSAGE_INFORMATION);
				PlanView.getInstance().setTitleAlreadySavedOfCurrentPlan();

			}
		}
		else if(src==PrincipaleView.getInstance().getExporterEnPdfMI()){
			JFileChooser chooser = new JFileChooser();
			chooser.setApproveButtonText("Exporter en pdf");
			chooser.setDialogTitle("Exporter en pdf");

			if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				PlanView.getInstance().clearLabelSelection();
				BufferedImage bi = new BufferedImage(PlanView.getInstance().getActivePlan().getSize().width, PlanView.getInstance().getActivePlan().getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();;  //this == JComponent
				PlanView.getInstance().getActivePlan().paint(g);
				g.dispose();
				bi = ReferenceImage.getResizedImage(bi, 500, 500);
				Document document = new Document();
				String output = chooser.getSelectedFile().getAbsolutePath()+".pdf";
				try {
					FileOutputStream fos = new FileOutputStream(output);
					PdfWriter writer = PdfWriter.getInstance(document, fos);
					writer.open();
					document.open();
					Plan p = PlanData.getPlan(PlanView.getInstance().getTitleActivePanel());

					if(p instanceof PlanFeu) {
						document.add(new Paragraph("Nom plan feu : "+p.getNom()+"\n"+"Longueur (x) : "+p.getX()+"\n"+"Largeur (y) : "+p.getY()+"\n\n"));
						if(!((PlanFeu)p).getPlanSalle().equals("")) {
							try {
								PlanSalle ps = ArboFeuSalleData.chargerPlanSalle(((PlanFeu)p).getPlanSalle());
								document.add(new Paragraph("Nom plan salle : "+ps.getNom()+"\n"+"Longueur (x) : "+ps.getX()+"\n"+"Largeur (y) : "+ps.getY()+"\n\n"));
							}
							catch(IOException e2) {}
						}
					}
					if(p instanceof PlanSalle) {
						document.add(new Paragraph("Nom plan salle : "+p.getNom()+"\n"+"Longueur (x) : "+p.getX()+"\n"+"Largeur (y) : "+p.getY()+"\n\n"));
					}
					
					// ajout de l'image du plan
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bi, "png", baos);
					document.add(Image.getInstance(baos.toByteArray()));
					baos.close();
					
					loadFeaturePlan(p, document);
					document.close();
					writer.close();
				}
				catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		else if(src==PrincipaleView.getInstance().getExporterEnImageMI()) {
			JFileChooser chooser = new JFileChooser();
			chooser.setApproveButtonText("Exporter en image");
			chooser.setDialogTitle("Exporter en image");
			if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {	
				PlanView.getInstance().clearLabelSelection();
				BufferedImage bi = new BufferedImage(PlanView.getInstance().getActivePlan().getSize().width, PlanView.getInstance().getActivePlan().getSize().height, BufferedImage.TYPE_INT_ARGB); 
				Graphics g = bi.createGraphics();
				PlanView.getInstance().getActivePlan().paint(g);  //this == JComponent
				g.dispose();
				try {
					ImageIO.write(bi,"png",new File(chooser.getSelectedFile().getAbsolutePath()+".png"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
		else if(src==PrincipaleView.getInstance().getImprimerMI()){
			if(Desktop.isDesktopSupported()){
				if(Desktop.getDesktop().isSupported(java.awt.Desktop.Action.PRINT)){
					PlanView.getInstance().clearLabelSelection();
					BufferedImage bi = new BufferedImage(PlanView.getInstance().getActivePlan().getSize().width, PlanView.getInstance().getActivePlan().getSize().height, BufferedImage.TYPE_INT_ARGB); 
					Graphics g = bi.createGraphics();;  //this == JComponent
					PlanView.getInstance().getActivePlan().paint(g);
					g.dispose();
					bi = ReferenceImage.getResizedImage(bi, 500, 500);
					Document document = new Document();
					try {
						FileOutputStream fos = new FileOutputStream("tmp.pdf");
						PdfWriter writer = PdfWriter.getInstance(document, fos);
						writer.open();
						document.open();
						Plan p = PlanData.getPlan(PlanView.getInstance().getTitleActivePanel());

						if(p instanceof PlanFeu) {
							document.add(new Paragraph("Nom plan feu : "+p.getNom()+"\n"+"Longueur (x) : "+p.getX()+"\n"+"Largeur (y) : "+p.getY()+"\n\n"));
							if(!((PlanFeu)p).getPlanSalle().equals("")) {
								try {
									PlanSalle ps = ArboFeuSalleData.chargerPlanSalle(((PlanFeu)p).getPlanSalle());
									document.add(new Paragraph("Nom plan salle : "+ps.getNom()+"\n"+"Longueur (x) : "+ps.getX()+"\n"+"Largeur (y) : "+ps.getY()+"\n\n"));
								}
								catch(IOException e2) {}
							}
						}
						if(p instanceof PlanSalle) {
							document.add(new Paragraph("Nom plan salle : "+p.getNom()+"\n"+"Longueur (x) : "+p.getX()+"\n"+"Largeur (y) : "+p.getY()+"\n\n"));
						}

						// ajout de l'image du plan
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(bi, "png", baos);
						document.add(Image.getInstance(baos.toByteArray()));
						baos.close();

						loadFeaturePlan(p, document);
						document.close();
						writer.close();
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						java.awt.Desktop.getDesktop().print(new File("tmp.pdf"));
					} catch (IOException ex) {
						System.out.println("Traitement de l'exception");
					}
				} else {
					System.out.println("La fonction n'est pas support�e par votre syst�me d'exploitation");
				}
			}else {
				System.out.println("Desktop pas support�e par votre syst�me d'exploitation");
			}

		}
		else if(src==PrincipaleView.getInstance().getOuvrirMI()) {
			JFileChooser chooser = new JFileChooser();//cr�ation dun nouveau filechosser
			chooser.setCurrentDirectory(new File("local/plan"));
			chooser.setFileFilter(new FileNameExtensionFilter("Fichier plan", "pf","ps"));
			chooser.setApproveButtonText("Ouvrir un plan"); //intitul� du bouton
			if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {	
				PrincipaleView.getInstance().getExporterEnImageMI().setEnabled(true);
				PrincipaleView.getInstance().getEnregistrerMI().setEnabled(true);
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
else if(src==PrincipaleView.getInstance().getMajMI()) {
			
		    System.out.println("Connexion r�ussie");

			//Connexion à la BDD
			String url = "jdbc:mysql://localhost/adec56";
			String login = "user";
			String passwd = "azerty";
			Connection cn = null;
			Statement st = null;
			
			try{
				Class.forName("com.mysql.jdbc.Driver");
				cn = DriverManager.getConnection(url,login,passwd);
			    System.out.println("Connexion r�ussie");

			    System.out.println("Téléchargement des plans...");
			    st = cn.createStatement();
	    		String sql = "SELECT * FROM Plan_de_salle";
	    		ResultSet result = st.executeQuery(sql);
				while(result.next()){
					//récupère le champ autorise, si = 1 alors l'administration a autorisé le plan
					if((boolean)result.getObject(3) == true){
			    		PrincipaleData.stringToXml(result.getObject(2).toString());
					}
	    	    }
				
			    System.out.println("Téléchargement des objets...");
			    st = cn.createStatement();
	    		sql = "SELECT * FROM Objet";
	    		result = st.executeQuery(sql);
				while(result.next()){
					if((boolean)result.getObject(4) == true){
			    		//�criture du xml
			    		PrincipaleData.stringToXml(result.getObject(2).toString());
			    		//�criture de l'image
		    		    Blob blob = (Blob)result.getBlob("image");
		    		    System.out.println("Read "+ blob.length() + " bytes ");
		    		    byte [] array = blob.getBytes( 1, ( int ) blob.length() );
		    		    File file = new File("res/img_objets/"+result.getObject(1).toString()+".png");
		    		    FileOutputStream out = new FileOutputStream( file );
		    		    out.write( array );
		    		    out.close();
			    	}
	    	    }				    
			    System.out.println("Téléchargement terminé!");
			    PrincipaleView.reloadJTrees();
			}
			catch(SQLException e1){
				e1.printStackTrace();
			}
			catch(ClassNotFoundException e1){
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			finally{
				try{
					cn.close();
				}
				catch(SQLException e1){
					e1.printStackTrace();
				}
			}
		}
	}
}
