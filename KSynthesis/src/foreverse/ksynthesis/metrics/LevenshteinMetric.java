package foreverse.ksynthesis.metrics;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import foreverse.ksynthesis.KSynthesisPlugin;
import foreverse.ksynthesis.SimpleHeuristic;

public class LevenshteinMetric extends SimpleHeuristic implements KSynthesisPlugin {

	private Levenshtein metric = new Levenshtein();
	
	@Override
	public String getName() {
		return "Levenshtein (Simmetrics)";
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
