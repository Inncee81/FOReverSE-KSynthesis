package foreverse.ksynthesis.metrics;

import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import foreverse.ksynthesis.KSynthesisPlugin;
import foreverse.ksynthesis.SimpleHeuristic;

public class SmithWatermanMetric extends SimpleHeuristic implements KSynthesisPlugin {

	private SmithWaterman metric = new SmithWaterman();
	
	@Override
	public String getName() {
		return "Smith Waterman (Simmetrics)";
	}

	@Override
	public double similarity(String child, String parent) {
		return metric.getSimilarity(child, parent);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
