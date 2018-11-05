package control;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import objet.Objet;
import view.DialogueNouvelObjetView;
import view.PrincipaleView;
import data.PrincipaleData;
import data.ReferenceImage;

public class DialogueNouvelObjetCtrl implements ActionListener, ItemListener, WindowListener, KeyListener {

	private static DialogueNouvelObjetCtrl dialogueNouvelObjetCtrl = new DialogueNouvelObjetCtrl();

	private JTextField jtImage;

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueNouvelObjetView.getInstance().getFeuButton()) {
			DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
			DialogueNouvelObjetView.getInstance().getListeObjet().removeAllItems();
			DialogueNouvelObjetView.getInstance().getListeObjet().revalidate();
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Choisissez un objet");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Barre");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Projecteur");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("ItemFeu");


			DialogueNouvelObjetView.getInstance().setTypeObjetASauver("feu");
		}
		else if(src==DialogueNouvelObjetView.getInstance().getSalleButton()) {
			DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
			DialogueNouvelObjetView.getInstance().getListeObjet().removeAllItems();
			DialogueNouvelObjetView.getInstance().getListeObjet().revalidate();
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Choisissez un objet");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Barre");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("ItemSalle");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Console");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Gradateur");
			DialogueNouvelObjetView.getInstance().getListeObjet().addItem("Gradin");

			DialogueNouvelObjetView.getInstance().setTypeObjetASauver("salle");
		}
		else if(src==DialogueNouvelObjetView.getInstance().getButtonCancel()) {
			DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
			DialogueNouvelObjetView.getInstance().dispose();
			DialogueNouvelObjetView.getInstance().getListeObjet().removeAllItems();
			DialogueNouvelObjetView.getInstance().getTextFields().clear();
			DialogueNouvelObjetView.getInstance().setTypeObjetASauver(null);
			DialogueNouvelObjetView.getInstance().setNameObject(null);
			DialogueNouvelObjetView.getInstance().setFileImg(null);
			DialogueNouvelObjetView.getInstance().getRadioGroup().clearSelection();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().removeAll();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().revalidate();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().repaint();
		}
		else if(src==DialogueNouvelObjetView.getInstance().getButtonCreate()) {
			DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
			DialogueNouvelObjetView.getInstance().dispose();
			DialogueNouvelObjetView.getInstance().getListeObjet().removeAllItems();
			DialogueNouvelObjetView.getInstance().getRadioGroup().clearSelection();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().removeAll();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().revalidate();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().repaint();

			Objet newObjet = null;
			try {
				// on recupere le type de l'objet a charger
				Class<?> classe = Class.forName("objet."+DialogueNouvelObjetView.getInstance().getNameObject());
				try {
					newObjet = (Objet)classe.newInstance();
					for(Field tmpField : PrincipaleData.getFields(classe, "fixedFeature")) {
						try {
							for(JTextField tmpTextField : DialogueNouvelObjetView.getInstance().getTextFields()) {
								if(tmpTextField.getName().equals(tmpField.getName())) {
									switch(tmpField.getType().getSimpleName()) {
									case "String" :
										tmpField.set(newObjet, tmpTextField.getText());
										break;
									case "int" :
										tmpField.set(newObjet, Integer.parseInt(tmpTextField.getText()));
										break;
									case "double" :
										tmpField.set(newObjet, Double.parseDouble(tmpTextField.getText()));
										break;
									case "boolean" :
										tmpField.set(newObjet, Boolean.parseBoolean(tmpTextField.getText()));
										break;
									}
								}
							}
						}
						catch (IllegalArgumentException e3) { e3.printStackTrace(); }
						catch (IllegalAccessException e4) { e4.printStackTrace(); }
					}

				}
				catch (InstantiationException e1) { e1.printStackTrace(); }
				catch (IllegalAccessException e1) { e1.printStackTrace(); }
			}
			catch (ClassNotFoundException e2) { e2.printStackTrace(); }

			PrincipaleData.sauverXml(newObjet, "local/objet/"+DialogueNouvelObjetView.getInstance().getTypeObjetASauver()+"/"+newObjet.getNom()+".xml");

			FileChannel in = null; // canal d'entrée
			FileChannel out = null; // canal de sortie

			try {
				in = new FileInputStream(jtImage.getText()).getChannel();
				out = new FileOutputStream("res/img_objets/"+newObjet.getNom()+controleValeur(jtImage.getText())).getChannel();
				in.transferTo(0, in.size(), out); // Copie depuis le in vers le out
				ReferenceImage.chargerImages(); //ne marche pas xD (ie l'image ne s'affiche pas lors du drag&drop)
			}
			catch (Exception e2) {} 

			finally { // on ferme
				if(in != null) {
					try {
						in.close();
					} catch (IOException e3) {}
				}
				if(out != null) {
					try {
						out.close();
					} catch (IOException e4) {}
				}
			}

			// rechargement de la jtree concerne par l'ajout d'objet
			PrincipaleView.reloadJTrees();

			DialogueNouvelObjetView.getInstance().getTextFields().clear();
			DialogueNouvelObjetView.getInstance().setTypeObjetASauver(null);
			DialogueNouvelObjetView.getInstance().setNameObject(null);
			DialogueNouvelObjetView.getInstance().setFileImg(null);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().removeAll();
			DialogueNouvelObjetView.getInstance().getTextFields().clear();

			DialogueNouvelObjetView.getInstance().setNameObject(String.valueOf(DialogueNouvelObjetView.getInstance().getListeObjet().getSelectedItem()));

			DialogueNouvelObjetView.getInstance().setGbc(new GridBagConstraints());
			DialogueNouvelObjetView.getInstance().getGbc().insets = new Insets(10, 10, 10, 10);

			try {
				int i = 0;
				for(Field tmpField : PrincipaleData.getFields(Class.forName(("objet."+DialogueNouvelObjetView.getInstance().getListeObjet().getItemAt(DialogueNouvelObjetView.getInstance().getListeObjet().getSelectedIndex()))), "fixedFeature")){
					DialogueNouvelObjetView.getInstance().getGbc().gridx = 0; 
					DialogueNouvelObjetView.getInstance().getGbc().gridy = i; 
					DialogueNouvelObjetView.getInstance().getGbc().weightx = 0;
					DialogueNouvelObjetView.getInstance().getGbc().gridwidth = DialogueNouvelObjetView.getInstance().getGbc().gridheight = 1;
					DialogueNouvelObjetView.getInstance().getGbc().fill = GridBagConstraints.NONE;
					DialogueNouvelObjetView.getInstance().getGbc().anchor = GridBagConstraints.BASELINE_LEADING;
					JLabel labelField = new JLabel(tmpField.getName().substring(12, tmpField.getName().length())+" :");
					DialogueNouvelObjetView.getInstance().getPanCaracteristiques().add(labelField, DialogueNouvelObjetView.getInstance().getGbc());

					DialogueNouvelObjetView.getInstance().getGbc().gridx = 1;
					DialogueNouvelObjetView.getInstance().getGbc().weightx = 1;
					DialogueNouvelObjetView.getInstance().getGbc().gridwidth = GridBagConstraints.REMAINDER;
					DialogueNouvelObjetView.getInstance().getGbc().fill = GridBagConstraints.HORIZONTAL;
					DialogueNouvelObjetView.getInstance().getGbc().anchor = GridBagConstraints.BASELINE;
					JTextField jt = new JTextField();
					DialogueNouvelObjetView.getInstance().getPanCaracteristiques().add(jt, DialogueNouvelObjetView.getInstance().getGbc());
					jt.setName(tmpField.getName());
					DialogueNouvelObjetView.getInstance().getTextFields().add(jt);
					if(tmpField.getName().equals("fixedFeatureNom")) jt.addKeyListener(this);

					i++;
				}

				DialogueNouvelObjetView.getInstance().getGbc().gridx = 0;
				DialogueNouvelObjetView.getInstance().getGbc().gridy = i;
				DialogueNouvelObjetView.getInstance().getGbc().weightx = 0;
				DialogueNouvelObjetView.getInstance().getGbc().gridwidth = DialogueNouvelObjetView.getInstance().getGbc().gridheight = 1;
				DialogueNouvelObjetView.getInstance().getGbc().fill = GridBagConstraints.NONE;
				DialogueNouvelObjetView.getInstance().getGbc().anchor = GridBagConstraints.BASELINE_LEADING;
				JLabel labelImage = new JLabel("Image :");
				DialogueNouvelObjetView.getInstance().getPanCaracteristiques().add(labelImage, DialogueNouvelObjetView.getInstance().getGbc());

				DialogueNouvelObjetView.getInstance().getGbc().gridx = 1;
				DialogueNouvelObjetView.getInstance().getGbc().weightx = 1;
				DialogueNouvelObjetView.getInstance().getGbc().gridwidth = GridBagConstraints.RELATIVE;
				DialogueNouvelObjetView.getInstance().getGbc().fill = GridBagConstraints.HORIZONTAL;
				DialogueNouvelObjetView.getInstance().getGbc().anchor = GridBagConstraints.BASELINE;
				jtImage = new JTextField();
				DialogueNouvelObjetView.getInstance().getPanCaracteristiques().add(jtImage, DialogueNouvelObjetView.getInstance().getGbc());

				DialogueNouvelObjetView.getInstance().getGbc().gridx = 2;
				DialogueNouvelObjetView.getInstance().getGbc().weightx = 0;
				DialogueNouvelObjetView.getInstance().getGbc().gridwidth = GridBagConstraints.REMAINDER;
				DialogueNouvelObjetView.getInstance().getGbc().fill = GridBagConstraints.NONE;
				JButton boutonParcourir = new JButton("...");
				DialogueNouvelObjetView.getInstance().getPanCaracteristiques().add(boutonParcourir, DialogueNouvelObjetView.getInstance().getGbc());
				boutonParcourir.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser("..");
						fileChooser.setFileFilter(new FileNameExtensionFilter("Toutes les images", "png", "jpg", "gif"));
						int returnVal = fileChooser.showOpenDialog(null);
						if(returnVal == JFileChooser.APPROVE_OPTION){
							DialogueNouvelObjetView.getInstance().setFileImg(fileChooser.getSelectedFile());
							jtImage.setText(DialogueNouvelObjetView.getInstance().getFileImg().getPath());
						}
					}
				});						

			} catch (ClassNotFoundException e2) {
			}

			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().revalidate();
			DialogueNouvelObjetView.getInstance().getPanCaracteristiques().repaint();
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
		DialogueNouvelObjetView.getInstance().getListeObjet().removeAllItems();
		DialogueNouvelObjetView.getInstance().getTextFields().clear();
		DialogueNouvelObjetView.getInstance().setTypeObjetASauver(null);
		DialogueNouvelObjetView.getInstance().setNameObject(null);
		DialogueNouvelObjetView.getInstance().setFileImg(null);
		DialogueNouvelObjetView.getInstance().getRadioGroup().clearSelection();
		DialogueNouvelObjetView.getInstance().getPanCaracteristiques().removeAll();
		DialogueNouvelObjetView.getInstance().getPanCaracteristiques().revalidate();
		DialogueNouvelObjetView.getInstance().getPanCaracteristiques().repaint();
	}

	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}

	/**
	 * control the extension of the selected file,
	 * accept only ".jpg", ".png" and ".jpeg"
	 * @param explorer the JFileChooser where the file is selected
	 * @return the extension of the choosen file
	 */
	private String controleValeur (String nomFichier) throws Exception{
		String extension = nomFichier.substring(nomFichier.lastIndexOf("."));
		if(!(extension.equals(".jpg") || extension.equals(".png") || extension.equals(".gif"))){
			throw new Exception("Extension invalide");
		}
		return extension;
	}

	public static DialogueNouvelObjetCtrl getInstance() { return dialogueNouvelObjetCtrl; }

	@Override
	public void keyPressed(KeyEvent e) {		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for(JTextField tmpTextField : DialogueNouvelObjetView.getInstance().getTextFields()) {
			if(tmpTextField.getName().equals("fixedFeatureNom")) {
				if (tmpTextField.getText().equals("")) DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(false);
				else DialogueNouvelObjetView.getInstance().getButtonCreate().setEnabled(true);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

}