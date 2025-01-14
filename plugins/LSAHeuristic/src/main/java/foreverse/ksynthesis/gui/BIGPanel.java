package foreverse.ksynthesis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import foreverse.ksynthesis.mst.WeightedImplicationGraph;



public class BIGPanel extends BIGViewer {
	
	 private ListenableDirectedWeightedGraph  big_AnalysisGraph; 
	 private JGraphModelAdapter big_jgAdapter;
	 protected JGraph big_Graph;
	 private  static  List<String> existingVertexs = new ArrayList<String>();
	 private static final Dimension DEFAULT_SIZE = new Dimension( 320, 320 );
	 private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
	 
	 public BIGPanel() {
			
		// create a JGraphT FM
		   this.big_AnalysisGraph = new ListenableDirectedWeightedGraph( DefaultWeightedEdge.class );
		// create a visualization using JGraph, via an adapter
	       this.big_jgAdapter = new JGraphModelAdapter( this.big_AnalysisGraph );
	       this.big_Graph = new JGraph( big_jgAdapter );
	   	// Set layout
	        this.setLayout(new GridLayout(1, 1));
			this.add(new JScrollPane(this.big_Graph));
		}
	@Override
	public void updateBIG(WeightedImplicationGraph<String> wbig) {
//		ImplicationGraph<String> big = wbig.getImplicationGraph();
//		Map<SimpleEdge, Double> weights = wbig.getWeights();
//		for (SimpleEdge edge : big.edges()) {
//			
//			String debut = big.getEdgeSource(edge);
//			String fin = big.getEdgeTarget(edge);
//			//double  weight = heuristic.similarity(debut, fin);
//			/*System.out.println("'" + debut.getName() + "' '" + fin.getName()
//					+ "' associated with '" + weight + "'");*/
//			String source = seekVertex(debut,big_AnalysisGraph);
//			String destination = seekVertex(fin,big_AnalysisGraph);
//		
//			big_AnalysisGraph.addEdge(source, destination); 
//			SimpleEdge e = (SimpleEdge) big_AnalysisGraph.getEdge(source, destination);
//			big_AnalysisGraph.setEdgeWeight(e, weights.get(e));
//	}
	}
	static  String seekVertex(String v_name, ListenableDirectedWeightedGraph myAnalysisGraph) {
		
		 boolean existe = false;
		 String newNode = null;
		 
		 for (String n :existingVertexs){
				if (n.equals(v_name) == true){
					existe= true;
					 newNode = n;
					break;
					}
			}
				if(existe == false)
				{
				
				myAnalysisGraph.addVertex(v_name);
				existingVertexs.add(v_name);
				 newNode = v_name;
				}
				return newNode;
				
			}

			

			
	private void adjustDisplaySettings( JGraph jg ) {
	       jg.setPreferredSize( DEFAULT_SIZE );
	       Color  c        = DEFAULT_BG_COLOR;
	       String colorStr = null;
	       if( colorStr != null ) {
	           c = Color.decode( colorStr );
	       }
	       jg.setBackground( c );
	   }


	   private void positionVertexAt( Object vertex, int x, int y ) {
	       DefaultGraphCell cell = big_jgAdapter.getVertexCell( vertex );
	       Map              attr = cell.getAttributes(  );
	       Rectangle        b    = (Rectangle) GraphConstants.getBounds( attr );
	       GraphConstants.setBounds( attr, new Rectangle( x, y, b.width, b.height ) );
	       Map cellAttr = new HashMap(  );
	       cellAttr.put( cell, attr );
	       big_jgAdapter.edit(cellAttr, null, null, null);
	   }
}
