package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.sampling.*;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.usage.preprocess.*;
import ehupatras.webrecommendation.usage.preprocess.log.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.clustering.*;
import ehupatras.suffixtree.stringarray.test.*;

import java.util.*;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/all_esperimentation";
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		//String filename1 = "/kk.log";
		String basedirectory = args[0];
		String filename1 = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
	if(false){ // do not read the logs, load them	
		// READ THE LOG FILE(S) //
		
		LogReader logreader = new LogReaderBidasoaTurismo();
		
		// It reads the log file and store the valid requests in [ehupatras.webrecommendation.structures.WebAccessSequences]
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start reading the log files and analyzing the URLs.");
			String[] logfilesA = new String[1];
			logfilesA[0] = basedirectory + filename1;
		logreader.readLogFile(logfilesA);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");

		// ensure a minimum amount of apparitions of URLs.
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start identifying frequent URLs.");
		logreader.identifyFrequentURLs(10);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure that the URLs are static or it keeps interest during the time
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start identifying static URLs.");
		logreader.identifyStaticURLs(10, 5, (float)0.75);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
			
		// save the structures
		WebAccessSequences.save();
		Website.save();
	} else {
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start reading preprocessing data.");
		WebAccessSequences.loadStructure();
		Website.load();
	}
		

		// SESSIONING //
	if(false){	
		Sessioning ses = new Sessioning();
		
		// create sessions
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start spliting up into sessions.");
		ses.createSessions(10); // maximum period of inactivity
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");

		// join consecutive same URLs
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start joining consecutive same URLs.");
		ses.joinConsecutiveSameUrls();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// create sequences
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start creating sequences.");
		ses.createSequences();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure a minimun activity in each sequence
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start ensuring a minimun activity in each sequence.");
		ses.ensureMinimumActivityInEachSequence(3);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// remove long sequences, the activity web robots generate
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start removing long sequences.");
		ses.removeLongSequences((float)98);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		
		// compute the each web page role
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start computing pages' role.");
		ses.computePageRoleUHC_time(5, 20, 10*60);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// write preprocessed logs
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start writing processed logs.");
		WebAccessSequences.writeFilteredLog(basedirectory + "/filteredLog.log");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
			
		// write the sequences we have created
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start writing created sequences of request indexes.");
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		
		// write the sequence instantiation1: 
		// sequence of urlID with each role: Unimportant (U), Hub (H), Content (C)
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start writing created sequences of urlID+(U,H,C).");
		WebAccessSequencesUHC.writeSequencesInstanciated(basedirectory + "/sequences_urlIDurlRole.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
			
		// save the sessions structure we have created
		WebAccessSequences.save();
		WebAccessSequences.saveSequences();
	} else {
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start reading sessioning data.");
		WebAccessSequences.save();
		WebAccessSequences.loadSequences();	
	}

//WebAccessSequences.computePageRank();
			
		ModelValidationHoldOut modelval = new ModelValidationHoldOut();
	if(false){
		// SAMPLING //
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start sampling & Hold-outing.");
		Sampling samp = new Sampling();
		ArrayList<Integer> sampleSessionIDs = samp.getSample(10000, (long)0, false);
	
		// MODEL VALIDATION //
		// split up the database to the evaluation process
		modelval.prepareData(sampleSessionIDs, 70, 0, 30);
		modelval.save(basedirectory);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
	} else {
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start reading sampling data.");
		modelval.load(basedirectory);
	}
		ArrayList<Integer> train = modelval.getTrain();
		ArrayList<Integer> val   = modelval.getValidation();
		ArrayList<Integer> test  = modelval.getTest();
	
		// get training sequences
		ArrayList<String[]> sequencesUHC = WebAccessSequencesUHC.getSequencesInstanciated(train);

		// DISTANCE MATRIX //
		Matrix matrix = new SimilarityMatrix();
	if(false){
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start computing the similarity matrix.");
		matrix.computeMatrix(sequencesUHC);
		matrix.save(basedirectory);
		matrix.writeMatrix(basedirectory + "/distance_matrix.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
	} else {
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start reading the distance matrix.");
		matrix.load(basedirectory);
	}		
		float[][] distmatrix = matrix.getMatrix();

	
	// HIERARCHICAL CLUSTERING //
		ClusteringHierarchical clustering = new ClusteringHierarchical();
	if(true){
		// hierarchical clustering: http://sape.inf.usi.ch/hac
			System.out.println("[" + starttime + "] Start hierarchical clustering.");
			starttime = System.currentTimeMillis();
		clustering.computeHierarchicalClustering(distmatrix);
			System.out.println("  The dendrogram was generated.");
		//clustering.save(basedirectory);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
	} else {
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start reading the dendrogram.");
		clustering.load(basedirectory) ;
	}

		//clustering.writeDendrogram();
	
	
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
		// Suffix Tree
		SuffixTreeStringArray gst = new SuffixTreeStringArray();
		for(int i=0; i<clustersA.length; i++){
			//if(clustersA[i]==15){
				
				//System.out.print(i + " : " + train.get(i) + " : " + clustersA[i] + " ");
				String[] seq = sequencesUHC.get(i);
				gst.putSequence(seq, i);
				//System.out.println(seqstr);
			//}
		}
		gst.printSuffixTree();	
		*/
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}
