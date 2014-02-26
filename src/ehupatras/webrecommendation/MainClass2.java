package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.sampling.*;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.clustering.*;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.Node;
import ehupatras.suffixtree.stringarray.EdgeBag;
import ehupatras.suffixtree.stringarray.Edge;
import java.util.*;

public class MainClass2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		basedirectory = args[0];
		logfile = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		MainClassPreprocess preprocess = new MainClassPreprocess();
		//preprocess.loadPreprocess();
		preprocess.preprocessLogs(basedirectory, logfile);
		
		
		// SAMPLING //
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start sampling.");
		Sampling samp = new Sampling();
		ArrayList<Integer> sampleSessionIDs = samp.getSample(10000, (long)0, false);

		
		// DISTANCE MATRIX //
		// get training sequences
		ArrayList<String[]> sequencesUHC = WebAccessSequencesUHC.getSequencesInstanciated(sampleSessionIDs);
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start computing the similarity matrix.");
		Matrix matrix = new SimilarityMatrix();
		matrix.computeMatrix(sampleSessionIDs, sequencesUHC);
		matrix.save(basedirectory);
		matrix.writeMatrix(basedirectory + "/distance_matrix.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		float[][] distmatrix = matrix.getMatrix();
		
		// HOLD-OUT //
		ModelValidationHoldOut modelval = new ModelValidationHoldOut();
		modelval.prepareData(sampleSessionIDs, 70, 0, 30);
		modelval.save(basedirectory);
		ArrayList<Integer> train = modelval.getTrain();
		ArrayList<Integer> val   = modelval.getValidation();
		ArrayList<Integer> test  = modelval.getTest();
		
		
		// HIERARCHICAL CLUSTERING //
		ClusteringHierarchical clustering = new ClusteringHierarchical();
		// hierarchical clustering: http://sape.inf.usi.ch/hac
			System.out.println("[" + starttime + "] Start hierarchical clustering.");
			starttime = System.currentTimeMillis();
		int[] trainDMindexes = matrix.getSessionIDsIndexes(train);
		clustering.computeHierarchicalClustering(distmatrix,trainDMindexes);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		clustering.writeDendrogram();
		

		// CUT THE DENDROGRAM
		int[] clustersA;
		SaveLoadObjects slo = new SaveLoadObjects();
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)0);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters000.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)10);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters010.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)20);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters020.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)25);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters025.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)30);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters030.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)40);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters040.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)50);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters050.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)60);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters060.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)70);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters070.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)75);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters075.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)80);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters080.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)90);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters090.javaData");
	
		clustersA = clustering.cutDendrogramByDissimilarity((float)100);
		Arrays.sort(clustersA);
		System.out.println(clustersA[clustersA.length-1]);
		slo.save(clustersA, basedirectory + "/_clusters100.javaData");
	
	
	/*
	// SUFFIX TREE
	clustersA = (int[])slo.load(basedirectory + "/_clusters050.javaData");
	Arrays.sort(clustersA);
	int maxClInd = clustersA[clustersA.length-1];
	
	// silly clustering
	int[] clustersA2 = new int[clustersA.length];
	int kk = 0;
	for(int i=0; i<clustersA.length; i++){
		if(i % 10 == 0){ kk++; }
		clustersA2[i] = kk;
	}
	clustersA = clustersA2;
	Arrays.sort(clustersA);
	maxClInd = clustersA[clustersA.length-1];
	
		// compute suffix trees
		ArrayList<SuffixTreeStringArray> suffixtrees = new ArrayList<SuffixTreeStringArray>();
		for(int i=0; i<maxClInd; i++){ // for each cluster
			SuffixTreeStringArray st = new SuffixTreeStringArray();
			int size = 0;
			for(int j=0; j<clustersA.length; j++){ // for each case
				if(clustersA[j]==i){
					String[] seq = sequencesUHC.get(i);
					st.putSequence(seq, j);
					size++;
				}
			}
			System.out.println("cluster: " + i + "; size: " + size);
			suffixtrees.add(st);
		}
		
		
		*/
		
		/*
		// print suffix trees
		for(int i=0; i<maxClInd; i++){ // for each cluster
			System.out.println("=========" + i + "=========");
			SuffixTreeStringArray st = suffixtrees.get(i);
			st.printSuffixTree();
		}
		
		
		
		// create weighted sequences
		//for(int i=0; i<maxClInd; i++){ // for each cluster
		int i = 1;	
		
			SuffixTreeStringArray st = suffixtrees.get(i);
			// get the root node
			Node root = st.getRoot(); // root node
			//root.computeAndCacheCount();
			//int totalfreq = root.getResultCount();
			// create an object-array to save all node data we need
			Object[] obj = new Object[4];
			obj[0] = root;
			obj[1] = 0; // tree level in the suffix tree
			obj[2] = -1; // the parents index. No parents is the root
			obj[3] = (float)1.0; // accumulated probability of appearance
			// save the array object
			ArrayList<Object[]> nodesToAnalyze = new ArrayList<Object[]>();
			nodesToAnalyze.add(obj);
			for(int j=0; j<nodesToAnalyze.size(); j++){
				// get the node information
				obj = nodesToAnalyze.get(j);
				Node nod = (Node)obj[0];
				int treelevel = (int)obj[1];
				int parentindex = (int)obj[2];
				float accumProb = (float)obj[3];
				//nod.computeAndCacheCount();
				
				System.out.println(	" ind: " + j +
						  			" treelevel: " + treelevel +
									" parentindex: " + parentindex +
									" acccumProb: " + accumProb);
				
				// get the edges information
				EdgeBag edges = nod.getEdges();
				ArrayList<Edge> edgesA = edges.values();
				for(int k=0; k<edgesA.size(); k++){ // for each edge
					Edge e = edgesA.get(k);
					ArrayList<String> label = e.getLabel();
					Node dest = e.getDest();
					//int freq = dest.getResultCount();
					obj = new Object[4];
					obj[0] = dest;
					obj[1] = treelevel + 1;
					obj[2] = j;
					obj[3] = (float)-1;
					nodesToAnalyze.add(obj);
				}
			}			
		//}
		
		*/
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}