package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import view.DialogueDescriptionObjetView;

public class DialogueDescriptionObjetCtrl implements ActionListener, WindowListener {
	
	private static DialogueDescriptionObjetCtrl dialogueDescriptionObjetCtrl = new DialogueDescriptionObjetCtrl();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueDescriptionObjetView.getInstance().getFermer()) {
			DialogueDescriptionObjetView.getInstance().repaint();
			DialogueDescriptionObjetView.getInstance().revalidate();
			DialogueDescriptionObjetView.getInstance().getPanDescription().removeAll();
			DialogueDescriptionObjetView.getInstance().setVisible(false);
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		DialogueDescriptionObjetView.getInstance().repaint();
		DialogueDescriptionObjetView.getInstance().revalidate();
		DialogueDescriptionObjetView.getInstance().getPanDescription().removeAll();
		DialogueDescriptionObjetView.getInstance().setVisible(false);
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
	
	public static DialogueDescriptionObjetCtrl getInstance() { return dialogueDescriptionObjetCtrl; }

}