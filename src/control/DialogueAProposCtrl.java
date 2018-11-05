package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.DialogueAProposView;

public class DialogueAProposCtrl implements ActionListener {
	
	private static DialogueAProposCtrl dialogueAProposCtrl = new DialogueAProposCtrl();

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(src==DialogueAProposView.getInstance().getFermer()) {
			DialogueAProposView.getInstance().setVisible(false);
		}
	}

	public static DialogueAProposCtrl getInstance() { return dialogueAProposCtrl; }

}
