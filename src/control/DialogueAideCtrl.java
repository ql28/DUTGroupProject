package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.DialogueAideView;

public class DialogueAideCtrl implements ActionListener {
	
	private static DialogueAideCtrl dialogueAideCtrl = new DialogueAideCtrl();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueAideView.getInstance().getFermer()) {
			DialogueAideView.getInstance().setVisible(false);
		}
	}

	public static DialogueAideCtrl getInstance() { return dialogueAideCtrl; }
	
}
