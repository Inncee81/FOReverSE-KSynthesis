package foreverse.ksynthesis.gui.actions;

import java.util.Set;

import foreverse.ksynthesis.InteractiveFMSynthesizer;


public class SelectClusterParentAction implements SynthesisAction {

	private InteractiveFMSynthesizer synthesizer;
	private Set<String> children;
	private String parent;

	public SelectClusterParentAction(InteractiveFMSynthesizer synthesizer, Set<String> children, String parent) {
		this.synthesizer = synthesizer;
		this.children = children;
		this.parent = parent;
	}

	@Override
	public void undo() {
		for (String child : children) {
			synthesizer.resetParentCandidates(child);
		}
	}
}
