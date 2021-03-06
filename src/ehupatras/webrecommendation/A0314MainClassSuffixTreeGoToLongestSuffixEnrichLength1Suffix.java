package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class A0314MainClassSuffixTreeGoToLongestSuffixEnrichLength1Suffix {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String dmWD = "/DM_00_no_role";
		//dmWD = "";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		
		// LOAD PREPROCESSED LOGS //
		//A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		//preprocess.preprocessLogs(preprocessingWD, logfile);
		//preprocess.loadPreprocess();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Long> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();
		
		
		// DISTANCE MATRIX //
		A010MainClassDistanceMatrixEuclidean dm = new A010MainClassDistanceMatrixEuclidean();
		dm.loadDistanceMatrix(databaseWD + dmWD);
		Matrix matrix = dm.getMatrix();

		
		// HOLD-OUT //
		A020MainClassHoldOut ho = new A020MainClassHoldOut();
		ho.loadParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();
		
		
		// MODEL VALIDATION //

		// initialize the model evaluator
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC, null,
				matrix, trainAL, valAL, testAL);
		modelev.setFmeasureBeta(0.5f);
		modelev.setConfusionPoints(confusionPoints);
		
		// Markov Chain uses one of failure functions
		// so just in case we computed it
		modelev.buildMarkovChains();
		
		// SUFFIX TREE //
		modelev.buildSuffixTreesFromOriginalSequences();		
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Experimentation string
		String esperimentationStr = "suffixtree";
			
		// Evaluation
		String results;

		//int[] failmodesA = new int[]{0, 1, 2};
		int[] failmodesA = new int[]{1};
		for(int fmodei=0; fmodei<failmodesA.length; fmodei++){
			int fmode = failmodesA[fmodei];
			String esperimentationStr2 = esperimentationStr + "_failure" + fmode;
			
			//int[] goToMemA = new int[]{1,2,3,4,5, 100};
			int[] goToMemA = new int[]{1000};
			for(int gt=0; gt<goToMemA.length; gt++){
				int gtmem = goToMemA[gt];
				String esperimentationStr3 = esperimentationStr2 + "_gt" + gtmem; 
		
				/*
				// random
				int[] nrecsRST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsRST.length; ind++ ){
					int nrec = nrecsRST[ind];
					results = modelev.computeEvaluationTest(0, nrec, (long)0, fmode, gtmem);
					System.out.print(esperimentationStr3 + "_random" + nrec + ",");
					System.out.print(results);
				}
				*/
		
				/*
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					results = modelev.computeEvaluationTest(3, nrec, (long)0, fmode, gtmem, false, null);
					System.out.print(esperimentationStr3 + "_weighted" + nrec + ",");
					System.out.print(results);
				}
				*/
				
				// weighted by construction sequences (& test sequences)
				// enrich with step1 urls in the suffix tree
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					results = modelev.computeEvaluationTest(6, nrec, (long)0, fmode, gtmem, 0, false, null);
					System.out.print(esperimentationStr3 + "_weighted" + nrec + ",");
					System.out.print(results);
				}
			
				// unbounded
				results = modelev.computeEvaluationTest(6, 1000, (long)0, fmode, gtmem, 0, false, null);
				System.out.print(esperimentationStr3 + "_unbounded,");
				System.out.print(results);
			}
		}
		
		/*
		 * OTHER METHOD TRIED TO WEIGHT THE SUFFIX TREE
		 * 
			// weightedTrain
		int[] nrecsWST1 = new int[]{2,3,4,5,10,20};
		for(int ind=0; ind<nrecsWST1.length; ind++ ){
			int nrec = nrecsWST1[ind];
			results = modelev.computeEvaluationTest(1, nrec, (long)0);
			System.out.print(esperimentationStr2 + "_TrainWeighted" + nrec + ",");
			System.out.print(results);
		}
					
			// weightedTest
		int[] nrecsWST2 = new int[]{2,3,4,5,10,20};
		for(int ind=0; ind<nrecsWST2.length; ind++ ){
			int nrec = nrecsWST2[ind];
			results = modelev.computeEvaluationTest(2, nrec, (long)0);
			System.out.print(esperimentationStr2 + "_TestWeighted" + nrec + ",");
			System.out.print(results);
		}

			// weighted with markov
		int[] nrecsWSTM = new int[]{2,3,4,5,10,20};
		for(int ind=0; ind<nrecsWSTM.length; ind++ ){
			int nrec = nrecsWSTM[ind];
			results = modelev.computeEvaluationTest(4, nrec, (long)0);
			System.out.print(esperimentationStr2 + "_withMarkov" + nrec + ",");
			System.out.print(results);
		}

			// weighted with original sequences
		int[] nrecsWSTOrig = new int[]{2,3,4,5,10,20};
		for(int ind=0; ind<nrecsWSTOrig.length; ind++ ){
			int nrec = nrecsWSTOrig[ind];
			results = modelev.computeEvaluationTest(5, nrec, (long)0);
			System.out.print(esperimentationStr2 + "_WeightedOrig" + nrec + ",");
			System.out.print(results);
		}
		*/
					
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}