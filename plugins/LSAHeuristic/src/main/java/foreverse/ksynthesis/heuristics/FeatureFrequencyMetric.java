package foreverse.ksynthesis.heuristics;

public interface FeatureFrequencyMetric {

	double support(String featureName1, String featureName2);
	double confidence(String featureName1, String featureName2);
	
	}
