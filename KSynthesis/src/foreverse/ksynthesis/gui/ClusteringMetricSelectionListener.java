package foreverse.ksynthesis.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import foreverse.ksynthesis.metrics.AlwaysZeroMetric;
import foreverse.ksynthesis.metrics.LevenshteinMetric;
import foreverse.ksynthesis.metrics.MetricName;
import foreverse.ksynthesis.metrics.PathLengthMetric;
import foreverse.ksynthesis.metrics.RandomMetric;
import foreverse.ksynthesis.metrics.SmithWatermanMetric;
import foreverse.ksynthesis.metrics.WikipediaMinerMetric;
import foreverse.ksynthesis.metrics.WuPalmerMetric;
import fr.familiar.gui.FamiliarEditor;
import fr.familiar.gui.Tab2EnvVar;

public class ClusteringMetricSelectionListener implements ActionListener {

	private MetricName metric;

	public ClusteringMetricSelectionListener(MetricName metric) {
		this.metric = metric;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Component tab = Tab2EnvVar.INSTANCE.getTab().getSelectedComponent();

		if (tab instanceof FMSynthesisEnvironment) {
			FMSynthesisEnvironment environment = (FMSynthesisEnvironment) tab;
			switch (metric) {
			case ALWAYS_ZERO:
				environment.setClusteringMetric(new AlwaysZeroMetric());
				break;
			case RANDOM:
				environment.setClusteringMetric(new RandomMetric());
				break;
			case SIMMETRICS_SMITHWATERMAN:
//				environment.setClusteringMetric(new SimmetricsMetric(metric));
				environment.setClusteringMetric(new SmithWatermanMetric());
				break;
			case SIMMETRICS_LEVENSHTEIN:
//				environment.setClusteringMetric(new SimmetricsMetric(metric));
				environment.setClusteringMetric(new LevenshteinMetric());
				break;
			case WORDNET_WUP:
				File dictionaryWUP = WordNetPropertyFileChooser.getInstance();
				if (dictionaryWUP != null) {
					WuPalmerMetric wup = new WuPalmerMetric();
					wup.init(dictionaryWUP);
					environment.setClusteringMetric(wup);	
				}
				break;
			case WORDNET_PATHLENGTH:
				File dictionaryPL = WordNetPropertyFileChooser.getInstance();
				if (dictionaryPL != null) {
					PathLengthMetric pl = new PathLengthMetric();
					pl.init(dictionaryPL);
					environment.setClusteringMetric(pl);	
				}
				break;
			case WIKIPEDIA_MINER:
				JFileChooser fileChooser = new JFileChooser();
				int choice = fileChooser.showOpenDialog(FamiliarEditor.INSTANCE);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File propertiesFile = fileChooser.getSelectedFile();
					try {
						WikipediaMinerMetric wikipediaMinerMetric = new WikipediaMinerMetric();
						wikipediaMinerMetric.init(propertiesFile);
						environment.setClusteringMetric(wikipediaMinerMetric);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}	
				break;
				
				// TODO : add LSA when ready
			default:
				break;

			}
			
		}

	}

}
