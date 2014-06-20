package foreverse.ksynthesis.evaluation.ese;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.csvreader.CsvWriter;
import com.google.common.collect.Sets;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import foreverse.ksynthesis.Heuristic;
import foreverse.ksynthesis.InteractiveFMSynthesizer;
import foreverse.ksynthesis.evaluation.commons.FeatureModelLoader;
import foreverse.ksynthesis.evaluation.commons.HeuristicLoader;
import foreverse.ksynthesis.gui.KeyValue;
import foreverse.ksynthesis.metrics.AlwaysZeroMetric;
import foreverse.ksynthesis.metrics.CommonEdgesMetric;
import foreverse.ksynthesis.metrics.ImplicationGraphMetrics;
import foreverse.ksynthesis.metrics.RandomMetric;
import foreverse.ksynthesis.metrics.TransitiveReductionMetric;
import fr.familiar.experimental.FGroup;
import fr.familiar.interpreter.FMLShell;
import fr.familiar.parser.FMLCommandInterpreter;
import fr.familiar.test.FMLTest;
import fr.familiar.variable.FeatureModelVariable;
import gsd.graph.ImplicationGraph;
import gsd.graph.SimpleEdge;
import gsd.graph.TransitiveReduction;
import gsd.synthesis.FeatureEdge;
import gsd.synthesis.FeatureGraph;
import gsd.synthesis.FeatureNode;

public class ESEEvaluation extends FMLTest {
	
	private static final int N_START = 1;
	private static final int N_END = 5;
	
	private static final String OUTPUT_FOLDER = "output/";
	
	private static int RANDOM_ITERATIONS = 1000;
	private static double RANDOM_THRESHOLD = 0.15; // FIXME
	private static HeuristicLoader heuristicLoader;
	private static List<Heuristic> heuristics;
	private static HashMap<Heuristic, Double> thresholds;

	@BeforeClass
	public static void loadResources() {
		System.out.println("Loading heuristics...");
		heuristicLoader = new HeuristicLoader();
		heuristics = heuristicLoader.loadHeuristics();
		System.out.println("Loaded heuristics: ");
		for (Heuristic heuristic : heuristics) {
			System.out.println(heuristic);
		}
		System.out.println();
		thresholds = heuristicLoader.getDefaultClusteringThresholds();
	}
	
	@AfterClass
	public static void releaseResources() {
		heuristicLoader.closeHeuristics();
	}
	
	@Ignore
	@Test
	public void testSPLOTforICSE() throws IOException {
		FeatureModelLoader featureModelLoader = new FeatureModelLoader(_shell, _builder);

		// SPLOT FMs
		System.out.println("Loading SPLOT feature models for ICSE");
		List<FeatureModelVariable> splotFMs = featureModelLoader.getSPLOTFeatureModelsForICSE();

		evaluation(heuristics, thresholds, splotFMs, new File(OUTPUT_FOLDER + "ICSE/"), true);

		System.out.println();
	}
	
	@Ignore
	@Test
	public void testSPLOTforESE() throws IOException {
		FeatureModelLoader featureModelLoader = new FeatureModelLoader(_shell, _builder);

		// SPLOT FMs
		System.out.println("Loading SPLOT feature models for ESE");
		List<FeatureModelVariable> splotFMs = featureModelLoader.getSPLOTFeatureModels();
		
		evaluation(heuristics, thresholds, splotFMs, new File(OUTPUT_FOLDER + "ESE/SPLOT/"), true);
		
		System.out.println();
	}
	
	@Test
	public void testPCMforESE() throws IOException {
		FeatureModelLoader featureModelLoader = new FeatureModelLoader(_shell, _builder);

		// PCM FMs
		System.out.println("Loading PCM feature models");
		List<FeatureModelVariable> pcmFMs = featureModelLoader.getPCMFeatureModels();

		evaluation(heuristics, thresholds, pcmFMs, new File(OUTPUT_FOLDER + "ESE/PCMs/"), false);
		
		System.out.println();
	}
	
	@Ignore
	@Test
	public void testFASEonSPLOT() throws IOException {
		FeatureModelLoader featureModelLoader = new FeatureModelLoader(_shell, _builder);
		List<FeatureModelVariable> splotFeatureModels = featureModelLoader.getSPLOTFeatureModels();
		List<FeatureModelVariable> faseFeatureModels = featureModelLoader.getFASEFeatureModels();

		System.out.println("Computing score for FASE FMs");
		
		// Create output directory
		File output = new File(OUTPUT_FOLDER + "ESE/SPLOT/" + "FASE.csv");
		output.createNewFile();
		
		// Create CSV writer
		CsvWriter writer = new CsvWriter(output.getAbsolutePath());
		writer.write("ID");
		writer.write("FASE - Full Synthesis");
		writer.endRecord();

		// Compare FASE and SPLOT FMs
		for (FeatureModelVariable fmSPLOT : splotFeatureModels) {
			FeatureModelVariable fmFASE = null;
			for (FeatureModelVariable correspondingFM : faseFeatureModels) {
				if (correspondingFM.getCompleteIdentifier().equals(fmSPLOT.getCompleteIdentifier())) {
					fmFASE = correspondingFM;
					break;
				}
			}
			
//			Set<String> featuresSPLOT = fmSPLOT.getFm().getDiagram().features();
//			Set<String> featuresFASE = fmFASE.getFm().getDiagram().features();
//			if (featuresSPLOT.containsAll(featuresFASE) && featuresFASE.containsAll(featuresSPLOT)) {
			
			if (fmFASE != null) {
				double precision = computePercentageOfCommonEdges(fmFASE, fmSPLOT);
				writer.write(fmSPLOT.getCompleteIdentifier());
				writer.write("" + precision);
				writer.endRecord();
			} else {
				System.out.println(fmSPLOT.getCompleteIdentifier() + " was not found in FASE FMs");
			}
		}
		
		// Close CSV writer
		writer.close();
	}
	
	
	public FeatureModelVariable getRunningExample() {
		
		if (_shell == null) {
			_shell = FMLShell.instantiateStandalone(null);
			_environment = _shell.getCurrentEnv();
			_builder = FMLCommandInterpreter.getBuilder();
		}
		
		FeatureModelVariable runningExample = null;
		
		try {
			
			 runningExample = FM ("fm_wiki", 
					" Wiki: Hosting Licence Storage [\"Programming Language\"] ; \n" + 
					"Hosting: (\"Hosted Service\"|Local) ; \n" + 
					"Licence: (\"Proprietary Licence\"|\"Open Source\") ; \n" + 
					"Storage: (PostgreSQL|MySQL) ; \n" + 
					"\"Programming Language\": (Java|PHP) ; \n" + 
					"\"Hosted Service\": [Domain] ; \n" + 
					"(\"Proprietary Licence\" -> !\"Programming Language\");\n" + 
					"(Local -> !\"Proprietary Licence\");\n" + 
					"(PostgreSQL <-> \"Proprietary Licence\");" +
					"PostgreSQL -> Domain ;"
					);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return runningExample;
	}
	
	@Test
	public void reducedBIGOfRunningExample() throws IOException {
		FeatureModelVariable runningExample = getRunningExample();
		
		InteractiveFMSynthesizer synthesizer = new InteractiveFMSynthesizer(runningExample);
		ImplicationGraph<String> big = synthesizer.getImplicationGraph();
		
		new File("output").mkdirs(); // Create output directory
		FileWriter writerBIG = new FileWriter(new File("output/runningExampleBIG.dot"));
		writerBIG.write(big.toString());
		writerBIG.close();
		
		synthesizer.reduceBIG();
		ImplicationGraph<String> rbig = synthesizer.getImplicationGraph();
		
		FileWriter writerRBIG = new FileWriter(new File("output/runningExampleRBIG.dot"));
		writerRBIG.append(rbig.toString());
		writerRBIG.close();
	}

	/**
	 * Evaluate each heuristics on each FM
	 * @param heuristics : heuristics to test
	 * @param thresholds : thresholds for the clustering
	 * @param fms : feature models
	 * @param outputFolder : output folder of CSV files
	 * @param computeOrGroups
	 * @param random : enter "random mode" which redefine heuristics and perform the evaluation on RANDOM_ITERATIONS runs
	 * @throws IOException
	 */
	public void evaluation(List<Heuristic> heuristics, HashMap<Heuristic, Double> thresholds, List<FeatureModelVariable> fms, File outputFolder, boolean computeOrGroups) throws IOException {
		
		
		// Stats
		System.out.println("Computing stats");
		outputFolder.mkdirs();
		
		CsvWriter writerStats = new CsvWriter(outputFolder.getAbsolutePath() + "/stats.csv");
		testFeatureModelStats(writerStats, fms);
		writerStats.close();

		// Full synthesis and TOP N
		boolean random = false;
		System.out.println("Computing top N and full synthesis");
		CsvWriter writerSynthesis = new CsvWriter(outputFolder.getAbsolutePath() + "/fullsynthesis.csv");
		testSynthesis(writerSynthesis, heuristics, fms, false, random, computeOrGroups);
		writerSynthesis.close();

		System.out.println("Computing top N and full synthesis on RBIG");
		CsvWriter writerSynthesisRBIG = new CsvWriter(outputFolder.getAbsolutePath() + "/fullsynthesisRBIG.csv");
		testSynthesis(writerSynthesisRBIG, heuristics, fms, true, random, computeOrGroups);
		writerSynthesisRBIG.close();

		// Clustering
		System.out.println("Computing clusters");
		CsvWriter writerClustering = new CsvWriter(outputFolder.getAbsolutePath() + "/clustering.csv");
		testClustering(writerClustering, heuristics, fms, thresholds, false, random);
		writerClustering.close();

		System.out.println("Computing clusters on RBIG");
		CsvWriter writerClusteringRBIG = new CsvWriter(outputFolder.getAbsolutePath() + "/clusteringRBIG.csv");
		testClustering(writerClusteringRBIG, heuristics, fms, thresholds, true, random);
		writerClusteringRBIG.close();
		
		
		// RANDOM
		random = true;
		List<Heuristic> randomHeuristic = new ArrayList<Heuristic>();
		randomHeuristic.add(new RandomMetric());
		// Full synthesis and TOP N
		System.out.println("Computing top N and full synthesis (random)");
		CsvWriter writerSynthesisRandom = new CsvWriter(outputFolder.getAbsolutePath() + "/random_fullsynthesis.csv");
		testSynthesis(writerSynthesisRandom, randomHeuristic, fms, false, random, computeOrGroups);
		writerSynthesisRandom.close();

		System.out.println("Computing top N and full synthesis on RBIG (random)");
		CsvWriter writerSynthesisRBIGRandom = new CsvWriter(outputFolder.getAbsolutePath() + "/random_fullsynthesisRBIG.csv");
		testSynthesis(writerSynthesisRBIGRandom, randomHeuristic, fms, true, random, computeOrGroups);
		writerSynthesisRBIGRandom.close();

		// Clustering
		System.out.println("Computing clusters (random)");
		CsvWriter writerClusteringRandom = new CsvWriter(outputFolder.getAbsolutePath() + "/random_clustering.csv");
		testClustering(writerClusteringRandom, randomHeuristic, fms, thresholds, false, random);
		writerClusteringRandom.close();

		System.out.println("Computing clusters on RBIG (random)");
		CsvWriter writerClusteringRBIGRandom = new CsvWriter(outputFolder.getAbsolutePath() + "/random_clusteringRBIG.csv");
		testClustering(writerClusteringRBIGRandom, randomHeuristic, fms, thresholds, true, random);
		writerClusteringRBIGRandom.close();
		
		
		
		// Cliques
		System.out.println("Cliques as clusters");
		CsvWriter writerCliques = new CsvWriter(outputFolder.getAbsolutePath() + "/cliques.csv");
		testCliques(writerCliques, fms);
		writerCliques.close();
		
		// Feature groups
		System.out.println("Feature groups as clusters");
		CsvWriter writerFGroups = new CsvWriter(outputFolder.getAbsolutePath() + "/fgroups.csv");
		testFeatureGroups(writerFGroups, fms, false, computeOrGroups);
		writerFGroups.close();
		
		System.out.println("Feature groups as clusters on RBIG");
		CsvWriter writerFGroupsRBIG = new CsvWriter(outputFolder.getAbsolutePath() + "/fgroupsRBIG.csv");
		testFeatureGroups(writerFGroupsRBIG, fms, true, computeOrGroups);
		writerFGroupsRBIG.close();
		
		
	}

	/**
	 * Compute statistics on each fm and export it in CSV	
	 * @param writer
	 * @param fms
	 * @throws IOException
	 */
	public void testFeatureModelStats(CsvWriter writer, List<FeatureModelVariable> fms) throws IOException {
		// Write header
		writeHeaderForStats(writer);
		
		// Get stats for each FM
		for (FeatureModelVariable fm : fms) {
			ImplicationGraph<String> implicationGraph = fm.computeImplicationGraph();
			ImplicationGraph<String> reducedImplicationGraph = computeReducedBIG(fm, implicationGraph);

			FeatureGraph<String> hierarchy = fm.getFm().getDiagram();
			int keptParents = 0;
			for (FeatureEdge edge : hierarchy.edges()) {

				if (edge.getType() == FeatureEdge.HIERARCHY) {
					String source = hierarchy.getSource(edge).getFeature();
					String target = hierarchy.getTarget(edge).getFeature();

					if (reducedImplicationGraph.containsEdge(source, target)) {
						keptParents++;
					}

				}
			}
			
			ImplicationGraphMetrics bigMetrics = new ImplicationGraphMetrics();
					
			writer.write(fm.getCompleteIdentifier());
			writer.write("" + fm.features().size());
			writer.write("" + getDepth(fm));
			writer.write("" + implicationGraph.edges().size());
			writer.write("" + reducedImplicationGraph.edges().size());
			writer.write("" + bigMetrics.minOutdegree(implicationGraph));
			writer.write("" + bigMetrics.maxOutdegree(implicationGraph));
			writer.write("" + bigMetrics.averageOutdegree(implicationGraph));
			writer.write("" + keptParents / ((double) fm.features().size()) );
			writer.endRecord();
		}
		
		
	}
	
	public void writeHeaderForStats(CsvWriter writer) throws IOException {
		writer.write("ID");
		writer.write("#features");
		writer.write("depth of hierarchy");
		writer.write("#edges in BIG");
		writer.write("#edges in reduced BIG");
		writer.write("parent candidates (min)");
		writer.write("parent candidates (max)");
		writer.write("parent candidates (average)");
		writer.write("% parents kept after reducing BIG");
		writer.endRecord();
	}
	
	
	
	/**
	 * Automatically synthesize an FM and compute stats on the generated FM.
	 * @param writer 
	 * @param heuristics
	 * @param fms
	 * @param random 
	 * @throws IOException
	 */
	public void testSynthesis(CsvWriter writer, List<Heuristic> heuristics, List<FeatureModelVariable> fms, boolean reduceBIG, boolean random, boolean computeOrGroups) throws IOException {
		// Write header
		writeHeaderForSynthesis(writer, heuristics, random);
		
		// Perform full synthesis on each couple of heuristic and fm
		int nbRuns = random? RANDOM_ITERATIONS : 1;
		for (FeatureModelVariable fm : fms) {
			
			for (int run=0; run<nbRuns; run++) {
				writer.write(fm.getCompleteIdentifier());
				
				if (random) {
					writer.write("" + run);
					heuristics = new ArrayList<Heuristic>();
					heuristics.add(new RandomMetric());
				}
				
				for (Heuristic heuristic : heuristics) {
					InteractiveFMSynthesizer synthesizer = new InteractiveFMSynthesizer(fm, new AlwaysZeroMetric(), new ArrayList<Heuristic>(), new AlwaysZeroMetric(), -1);
					if (reduceBIG) {
						synthesizer.reduceBIG();
					}
					
					// Define if or groups should be computed for the transitive reduction metric
					if (heuristic instanceof TransitiveReductionMetric) {
						((TransitiveReductionMetric) heuristic).setOrRequired(computeOrGroups);
					}

					synthesizer.setParentSimilarityMetric(heuristic);
					
					// TOP N
					for (int n=N_START; n<=N_END; n++) {
						double topN = countTopNParents(n, fm, synthesizer, heuristic);
						writer.write("" + topN / ((double) fm.features().size() - 1)); // root does not count
					}

					// Full synthesis
					FeatureModelVariable synthesizedFM = synthesizer.computeCompleteFeatureModel();
					writer.write("" + computePercentageOfCommonEdges(synthesizedFM, fm));

				}

				writer.endRecord();
				writer.flush();
			}
		}
		
	}
	
	public void writeHeaderForSynthesis(CsvWriter writer, List<Heuristic> heuristics, boolean random) throws IOException {
		writer.write("ID");
		if (random) {
			writer.write("run");
		}
		for (Heuristic heuristic : heuristics) {
			for (int n=N_START; n<= N_END; n++) {
				writer.write(heuristic.toString() + " - TOP " + n);
			}
			writer.write(heuristic.toString() + " - Full Synthesis");
		}
		writer.endRecord();
	}
	
	/**
	 * Percentage of common edges of 2 FMs
	 * @param synthesizedFM
	 * @param fm
	 * @return
	 */
	public double computePercentageOfCommonEdges(FeatureModelVariable synthesizedFM, FeatureModelVariable fm) {
		CommonEdgesMetric commonEdgesMetric = new CommonEdgesMetric();
		double nbCommonEdges = commonEdgesMetric.commonEdges(synthesizedFM, fm);
		double nbEdges = fm.features().size();
		double averageCommonEdges =  nbCommonEdges / nbEdges;
		return averageCommonEdges;
	}
	
	/**
	 * Count parents from the ground truth that are in the top N positions in the parent candidates 
	 * @param n
	 * @param fm : ground truth
	 * @param synthesizer : synthesizer containing parent candidates
	 * @param metric 
	 * @return
	 */
	public double countTopNParents(int n, FeatureModelVariable fm, InteractiveFMSynthesizer synthesizer, Heuristic metric) {
		List<KeyValue<String,List<String>>> parentCandidateLists = synthesizer.getParentCandidates();
		FeatureGraph<String> hierarchy = fm.getFm().getDiagram();
		
		double nbTopNParents = 0;
		
		for (KeyValue<String, List<String>> parentCandidates : parentCandidateLists) {
			String feature = parentCandidates.getKey();
			List<String> parentCandidatesList = parentCandidates.getValue();
			FeatureNode<String> parent = hierarchy.parents(hierarchy.findVertex(feature)).iterator().next();
			if (!parent.isTop()) {
				int indexOfParent= parentCandidatesList.indexOf(parent.getFeature());

				if (metric instanceof RandomMetric && indexOfParent >= 0) {
					nbTopNParents += Math.min(n / (double) parentCandidatesList.size(), 1);
				} else if (indexOfParent >=0 && indexOfParent < n) {
					nbTopNParents++;
				}
			}
		}
		return nbTopNParents;
	}
	

	
	public void testClustering(CsvWriter writer, List<Heuristic> heuristics, List<FeatureModelVariable> fms, HashMap<Heuristic, Double> thresholds, boolean reduceBIG, boolean random) throws IOException {
		
		// Write header
		writeHeaderForClustering(writer, heuristics, random);
		
		// Perform clustering on each couple of heuristic and fm
		int nbRuns = random? RANDOM_ITERATIONS : 1;
		for (FeatureModelVariable fm : fms) {
			FeatureGraph<String> hierarchy = fm.getFm().getDiagram();
			List<Set<String>> siblingsList = convertInSetOfString(hierarchy.getSiblingSetsInBFS());
			
			for (int run=0; run<nbRuns; run++) {
				writer.write(fm.getCompleteIdentifier());
				if (random) {
					writer.write("" + run);
					heuristics = new ArrayList<Heuristic>();
					Heuristic randomHeuristic = new RandomMetric();
					heuristics.add(randomHeuristic);
					thresholds.put(randomHeuristic, RANDOM_THRESHOLD);
				}

				InteractiveFMSynthesizer synthesizer = new InteractiveFMSynthesizer(fm, new AlwaysZeroMetric(), new ArrayList<Heuristic>(), new AlwaysZeroMetric(), -1);
				if (reduceBIG) {
					synthesizer.reduceBIG();
				}
				
				for (Heuristic heuristic : heuristics) {
					
					double threshold = thresholds.get(heuristic);
					synthesizer.setClusteringParameters(heuristic, threshold);

					Set<Set<String>> clusters = synthesizer.getSimilarityClusters();

					// Gathering stats on clusters
					int sumSize = 0;
					int sumCorrectClusters = 0;
					int sumFeaturesInCorrectClusters = 0;

					for (Set<String> cluster : clusters) {
						sumSize += cluster.size();
						if (checkCluster(cluster, hierarchy, siblingsList)) {
							sumCorrectClusters++;
							sumFeaturesInCorrectClusters += cluster.size();
						}

					}
					int nbClusters = clusters.size();
					double averageSize = sumSize / ((double) clusters.size());
					double percentageCorrectClusters = sumCorrectClusters / ((double) clusters.size());
					double percentageFeaturesInCorrectClusters = sumFeaturesInCorrectClusters / ((double) fm.features().size());

					writer.write("" + nbClusters);
					
					if (nbClusters > 0) {
						writer.write("" + averageSize);
						writer.write("" + percentageCorrectClusters);
						writer.write("" + percentageFeaturesInCorrectClusters);	
					} else {
						writer.write("0");
						writer.write("N/A");
						writer.write("0");
					}
					
					
				}
				writer.endRecord();
				writer.flush();
			}
		}
			
	}
	
	public void writeHeaderForClustering(CsvWriter writer, List<Heuristic> heuristics, boolean random) throws IOException {
		writer.write("ID");
		if (random) {
			writer.write("run");			
		}

		for (Heuristic heuristic : heuristics) {
			writer.write(heuristic.toString() + " - #clusters");
			writer.write(heuristic.toString() + " - average cluster size");
			writer.write(heuristic.toString() + " - % correct clusters");
			writer.write(heuristic.toString() + " - % features in a correct cluster");
		}
		writer.endRecord();
	}
	
	

	private void testCliques(CsvWriter writer, List<FeatureModelVariable> fms) throws IOException {
		writeHeaderForCliques(writer);

		for (FeatureModelVariable fm : fms) {
			FeatureGraph<String> hierarchy = fm.getFm().getDiagram();
			List<Set<String>> siblingsList = convertInSetOfString(hierarchy.getSiblingSetsInBFS());

			writer.write(fm.getCompleteIdentifier());

			InteractiveFMSynthesizer synthesizer = new InteractiveFMSynthesizer(fm, new AlwaysZeroMetric(), new ArrayList<Heuristic>(), new AlwaysZeroMetric(), -1);

			List<Set<String>> clusters = synthesizer.getCliques();

			// Gathering stats on clusters
			int sumSize = 0;
			int sumCorrectClusters = 0;
			int sumFeaturesInCorrectClusters = 0;
			int sumConnectedCliques = 0;
			
			int sumSimpleUnfolding = 0;
			int sumFeaturesInSimpleUnfolding = 0;

			for (Set<String> cluster : clusters) {
				sumSize += cluster.size();
				if (checkCluster(cluster, hierarchy, siblingsList)) {
					sumCorrectClusters++;
					sumFeaturesInCorrectClusters += cluster.size();
				}
				
				if (checkConnectedClique(hierarchy, cluster)) {
					sumConnectedCliques++;
				}
				
				if (checkOneLevelClique(hierarchy, cluster)) {
					sumSimpleUnfolding++;
					sumFeaturesInSimpleUnfolding += cluster.size();
				}

			}
			int nbClusters = clusters.size();
			double averageSize = sumSize / ((double) clusters.size());
			double percentageCorrectClusters = sumCorrectClusters / ((double) clusters.size());
			double percentageFeaturesInCorrectClusters = sumFeaturesInCorrectClusters / ((double) fm.features().size());
			double percentageConnectedCliques = sumConnectedCliques/ ((double) clusters.size());
			double percentageSimpleUnfolding = sumSimpleUnfolding / ((double) clusters.size());
			double percentageFeatureInSimpleUnfolding = sumFeaturesInSimpleUnfolding / ((double) fm.features().size());

			writer.write("" + nbClusters);
			
			if (clusters.size() > 0) {
				writer.write("" + averageSize);
				writer.write("" + percentageCorrectClusters);
				writer.write("" + percentageFeaturesInCorrectClusters);
				writer.write("" + percentageConnectedCliques);
				writer.write("" + percentageSimpleUnfolding);
				writer.write("" + percentageFeatureInSimpleUnfolding);
			} else {
				writer.write("0");
				writer.write("N/A");
				writer.write("N/A");
				writer.write("N/A");
				writer.write("N/A");
				writer.write("N/A");
			}
			
			writer.endRecord();
			writer.flush();
		}
	}
	
	public void writeHeaderForCliques(CsvWriter writer) throws IOException {
		writer.write("ID");
		writer.write("#clusters");
		writer.write("average cluster size");
		writer.write("% correct clusters");
		writer.write("% features in a correct cluster");
		writer.write("% connected cliques in hierarchy");
		writer.write("% simple unfolding");
		writer.write("% features in simple unfolding");
		writer.endRecord();
	}
	
	private void testFeatureGroups(CsvWriter writer, List<FeatureModelVariable> fms, boolean reduceBIG, boolean computeOrGroups) throws IOException {
		if (!computeOrGroups) {
			System.out.println("Or groups are not computed");
		}
		
		writeHeaderForFGroups(writer);
		
		for (FeatureModelVariable fm : fms) {
			writer.write(fm.getCompleteIdentifier());
		
			FeatureGraph<String> hierarchy = fm.getFm().getDiagram();
			List<Set<String>> siblingsList = convertInSetOfString(hierarchy.getSiblingSetsInBFS());
			
			// Mutex
			Set<FGroup> mutexGroups = new HashSet<FGroup>();
			if (reduceBIG && computeOrGroups) {
				mutexGroups = fm.toGeneralizedNotation().getMutexGroups();
			} else if (reduceBIG && !computeOrGroups){
				mutexGroups = fm.toGeneralizedNotationWithoutOR().getMutexGroups();
			} else {
				mutexGroups = fm.computeMutexGroups();
			}
			computeStatsOnGroups(writer, mutexGroups, hierarchy, siblingsList);
			
			// Xor
			Set<FGroup> xorGroups = new HashSet<FGroup>();
			if (reduceBIG && computeOrGroups) {
				xorGroups = fm.toGeneralizedNotation().getXorGroups();
			} else if (reduceBIG && !computeOrGroups){
				xorGroups = fm.toGeneralizedNotationWithoutOR().getXorGroups();
			} else {
				xorGroups = fm.computeXorGroups();
			}
			
			computeStatsOnGroups(writer, xorGroups, hierarchy, siblingsList);
			
			// Or
			Set<FGroup> orGroups = new HashSet<FGroup>();
			if (computeOrGroups) {
				 orGroups = fm.computeOrGroups();
			}
			computeStatsOnGroups(writer, orGroups, hierarchy, siblingsList);
			
			
			// All groups
			Set<FGroup> allGroups = new HashSet<FGroup>();
			allGroups.addAll(mutexGroups);
			allGroups.addAll(xorGroups);
			allGroups.addAll(orGroups);
			computeStatsOnGroups(writer, allGroups, hierarchy, siblingsList);
			
			writer.endRecord();
			writer.flush();
			
//			_builder.reset(); // reset builder to avoid garbage collection
		}
		
	}
	
	public void writeHeaderForFGroups(CsvWriter writer) throws IOException {
		writer.write("ID");
		writeHeaderFGroupUtils(writer, "Mutex");
		writeHeaderFGroupUtils(writer, "Xor");
		writeHeaderFGroupUtils(writer, "Or");
		writeHeaderFGroupUtils(writer, "All");
		writer.endRecord();
		
	}
	
	private void writeHeaderFGroupUtils(CsvWriter writer, String groupType) throws IOException  {
		writer.write(groupType + " - #clusters");
		writer.write(groupType + " - average cluster size");
		writer.write(groupType + " - % correct clusters");
		writer.write(groupType + " - % features in a correct cluster");
	}
	
	
	private void computeStatsOnGroups(CsvWriter writer, Set<FGroup> groups, FeatureGraph<String> diagram, List<Set<String>> siblingsList) throws IOException {
				
		Set<Set<String>> expandedGroups = expandGroups(groups);

		int nbClusters = expandedGroups.size();
		int sumCorrectClusters = 0;
		int sumSize = 0;
		int featuresInCorrectClusters = 0;
		
		for (Set<String> group : expandedGroups) {
			sumSize += group.size();
			if (checkCluster(group, diagram, siblingsList)) {
				sumCorrectClusters++;
				featuresInCorrectClusters += group.size();
			}
		}
		
		writer.write("" + nbClusters);
		if (nbClusters > 0) {
			writer.write("" + sumSize / ((double) nbClusters));
			writer.write("" + sumCorrectClusters / ((double) nbClusters));
			writer.write("" + featuresInCorrectClusters / ((double) diagram.features().size()));	
		} else {
			writer.write("0");
			writer.write("N/A");
			writer.write("0");
		}
		

	}
	
	private Set<Set<String>> expandGroups(Set<FGroup> fGroups) {
		Set<Set<String>> expandedGroups = new HashSet<Set<String>>();

		for (FGroup fGroup : fGroups) {

			List<Set<String>> featureSets = new ArrayList<Set<String>>();
			for (FeatureNode<String> source : fGroup.getSources()) {
				featureSets.add(source.features());
			}

			for (List<String> expandedGroup : Sets.cartesianProduct(featureSets)) {
				expandedGroups.add(new HashSet<String>(expandedGroup));
			}


		}

		return expandedGroups;
	}
	
	/**
	 * Compute the transitively reduced implication graph of an fm
	 * @param fm
	 * @param implicationGraph 
	 * @return reduced BIG of fm
	 */
	public ImplicationGraph<String> computeReducedBIG(FeatureModelVariable fm, ImplicationGraph<String> implicationGraph) {
		ImplicationGraph<Set<String>> reducedGraph = implicationGraph.reduceCliques();
		TransitiveReduction.INSTANCE.reduce(reducedGraph);
		
		ImplicationGraph<String> reducedBIG = new ImplicationGraph<String>();
		
		// Create vertices
		for (String feature : fm.getFm().getDiagram().features()) {
			reducedBIG.addVertex(feature);
		}
		
		// Create edges
		for (SimpleEdge edge : reducedGraph.edges()) {
			Set<String> source = reducedGraph.getSource(edge);
			Set<String> target = reducedGraph.getTarget(edge);
			
			for (String child : source) {
				for (String parent : target) {
					reducedBIG.addEdge(child, parent);
				}
			}
		}
		
		
		return reducedBIG;
	}
	
	/**
	 * Get depth of fm
	 * @param fm
	 * @return
	 */
	private int getDepth(FeatureModelVariable fm) {
		FeatureGraph<String> hierarchy = fm.getFm().getDiagram();
		return getDepth(hierarchy, hierarchy.getTopVertex());
	}

	private int getDepth(FeatureGraph<String> hierarchy, FeatureNode<String> feature) {
		int depth = 0;
		Set<FeatureNode<String>> children = hierarchy.children(feature);
		for (FeatureNode<String> child : children) {
			depth = Math.max(getDepth(hierarchy, child), depth);
		}
		
		if (feature.isTop()) {
			return depth;
		} else {
			return 1 + depth;	
		}
		
		
	}
	
	
	/**
	 * Check that the cluster is correct w.r.t to the ground truth
	 * @param cluster
	 * @param siblingsList : list of siblings representing the ground truth
	 * @return
	 */
	public boolean checkCluster(Set<String> cluster, FeatureGraph<String> hierarchy, List<Set<String>> siblingsList) {
		for (Set<String> siblings : siblingsList) {

			// Check if the cluster contains siblings
			if (siblings.containsAll(cluster)) {
				return true;
			}

			// Check if the cluster contains a parent and its children
			for (String possibleParent : cluster) {
				// Compute the cluster without a possible parent
				Set<String> reducedCluster = new HashSet<String>(cluster);
				reducedCluster.remove(possibleParent);

				// The features of the reduced cluster are siblings and the chosen parent is correct
				boolean undirectlyCorrect = siblings.containsAll(reducedCluster)
						&& hierarchy.containsEdge(
								hierarchy.findVertex(reducedCluster.iterator().next()), 
								hierarchy.findVertex(possibleParent), 
								FeatureEdge.HIERARCHY);
				
				if (undirectlyCorrect) {
					return true;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Convert a list of siblings represented by feature nodes in 
	 * a list of siblings represented by strings
	 * @param siblingSetsInBFS
	 * @return
	 */
	public List<Set<String>> convertInSetOfString(List<Set<FeatureNode<String>>> siblingSetsInBFS) {
		List<Set<String>> siblingsList = new ArrayList<Set<String>>();

		for (Set<FeatureNode<String>> featureNodesiblings : siblingSetsInBFS) {
			Set<String> siblings = new HashSet<String>();
			for (FeatureNode<String> feature : featureNodesiblings) {
				siblings.add(feature.getFeature());
			}
			siblingsList.add(siblings);
		}

		return siblingsList;
	}


	private boolean checkConnectedClique(FeatureGraph<String> diagram, Set<String> clique) {
		Set<String> connectedFeatures = new HashSet<String>();
		Set<String> toVisit = new HashSet<String>(clique); 

		// Init first feature
		String first = clique.iterator().next();
		connectedFeatures.add(first);
		toVisit.remove(first);

		boolean added;
		do {
			added = false;
			Set<String> newToVisit = new HashSet<String>(toVisit);

			for (String feature : toVisit) {
				for (String connectedFeature : connectedFeatures) {
					FeatureNode<String> featureNode = diagram.findVertex(feature);
					FeatureNode<String> connectedFeatureNode = diagram.findVertex(connectedFeature);

					if (diagram.containsEdge(featureNode, connectedFeatureNode, FeatureEdge.HIERARCHY)
							|| diagram.containsEdge(connectedFeatureNode, featureNode, FeatureEdge.HIERARCHY)) {

						connectedFeatures.add(feature);
						newToVisit.remove(feature);
						added = true;
						break;
					}
				}
			}

			toVisit = newToVisit;
		} while (added);

		return connectedFeatures.equals(clique);
	}

	
	private boolean checkOneLevelClique(FeatureGraph<String> diagram, Set<String> clique) {

		for (String parent : clique) {
			boolean oneLevel = true;
			FeatureNode<String> parentNode = diagram.findVertex(parent);

			for (String child : clique) {
				FeatureNode<String> childNode = diagram.findVertex(child);
				if (!parent.equals(child) && !diagram.containsEdge(childNode, parentNode, FeatureEdge.HIERARCHY)) {
					oneLevel = false;
				}
			}

			if (oneLevel) {
				return true;
			}
		}

		return false;
	}

	
}
