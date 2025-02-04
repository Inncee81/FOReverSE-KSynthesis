package foreverse.ksynthesis.actions;

import foreverse.ksynthesis.InteractiveFMSynthesizer;


public class IgnoreParentAction implements KSynthesisAction {

	private InteractiveFMSynthesizer synthesizer;
	private String child;
	private String parent;
	
	
	public IgnoreParentAction(InteractiveFMSynthesizer synthesizer, String child, String parent) {
		this.synthesizer = synthesizer;
		this.child = child;
		this.parent = parent;
	}


	@Override
	public void execute() {
		synthesizer.ignoreParentUnrecorded(child, parent);
	}
}
