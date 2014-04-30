package foreverse.ksynthesis.actions;

import foreverse.ksynthesis.InteractiveFMSynthesizer;


public class CompleteFMAction implements KSynthesisAction {

	private InteractiveFMSynthesizer synthesizer;

	public CompleteFMAction(InteractiveFMSynthesizer synthesizer) {
		this.synthesizer = synthesizer;
		
	}
	
	@Override
	public void execute() {
		synthesizer.computeCompleteFeatureModelUnrecorded();
	}
}
