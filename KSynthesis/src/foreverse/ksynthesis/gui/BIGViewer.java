package foreverse.ksynthesis.gui;

import javax.swing.JPanel;

import foreverse.ksynthesis.mst.WeightedImplicationGraph;


public abstract class BIGViewer extends JPanel{

	public abstract void updateBIG(WeightedImplicationGraph<String> big);
	
}
