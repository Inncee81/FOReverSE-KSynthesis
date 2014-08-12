package foreverse.ksynthesis.test;

import static org.junit.Assert.assertNotNull;
import foreverse.ksynthesis.ConfigurableHeuristicPlugin;
import foreverse.ksynthesis.Heuristic;
import foreverse.ksynthesis.KSynthesisPlugin;
import foreverse.ksynthesis.KSynthesisPluginLoader;
import fr.familiar.experimental.FGroup;
import gsd.graph.ImplicationGraph;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class HeuristicPluginTest {

	@Test
	public void testHeuristicPluginLoader() {
		KSynthesisPluginLoader loader = new KSynthesisPluginLoader();
		List<KSynthesisPlugin> plugins = loader.loadFromDirectory(new File("plugins/heuristics"));
		assertNotNull(plugins);
		for (KSynthesisPlugin plugin : plugins) {
			if (!(plugin instanceof ConfigurableHeuristicPlugin) && plugin instanceof Heuristic) {
				System.out.println(plugin.getName());
				Heuristic heuristic = (Heuristic) plugin;
				heuristic.setImplicationGraph(new ImplicationGraph<String>());
				heuristic.setGroups(new HashSet<FGroup>());
				System.out.println(heuristic.similarity("car", "wheel"));	
			}
		}	
		
	}
	
}
