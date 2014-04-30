package foreverse.ksynthesis.gui.actions;

import foreverse.ksynthesis.InteractiveFMSynthesizer;


public class SetRootAction implements SynthesisAction {
	
	private InteractiveFMSynthesizer synthesizer;
	private String root;

	public SetRootAction(InteractiveFMSynthesizer synthesizer, String root) {
		this.synthesizer = synthesizer;
		this.root = root;
		
	}

	@Override
	public void undo() {
		synthesizer.resetParentCandidates(root);
	}

}
