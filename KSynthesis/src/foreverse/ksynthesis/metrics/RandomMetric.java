package foreverse.ksynthesis.metrics;

import java.util.Random;

import foreverse.ksynthesis.KSynthesisPlugin;
import foreverse.ksynthesis.SimpleHeuristic;


public class RandomMetric extends SimpleHeuristic implements KSynthesisPlugin {
	
	private Random rand;

	public RandomMetric() {
		rand = new Random();
	}

	@Override
	public String getName() {
		return "Random";
	}

	@Override
	public double similarity(String child, String parent) {
		return rand.nextDouble();
	}

}
