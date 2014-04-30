package foreverse.ksynthesis.actions;

import java.util.HashSet;
import java.util.Set;

import foreverse.ksynthesis.InteractiveFMSynthesizer;



public class SelectParentAction implements KSynthesisAction {

	private InteractiveFMSynthesizer synthesizer;
	private Set<String> children;
	private String parent;

	public SelectParentAction(InteractiveFMSynthesizer synthesizer, Set<String> children, String parent) {
		this.synthesizer = synthesizer;
		this.children = children;
		this.parent = parent;
	}
	
	public SelectParentAction(InteractiveFMSynthesizer synthesizer, String child, String parent) {
		this.synthesizer = synthesizer;
		this.children = new HashSet<String>();
		children.add(child);
		this.parent = parent;
	}
	
	@Override
	public void execute() {
		for (String child : children) {
			synthesizer.selectParentUnrecorded(child, parent);	
		}
	}
}
