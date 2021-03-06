package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluator;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorUHC;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A052MainClassHclustSpadeKnnED {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String dmWD = "/DM_00_no_role";
		dmWD = "";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String clustWD = "/CL_00_no_role";
		clustWD = "";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		clustWD = args[5];
		
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
		ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();


		
		// MODEL VALIDATION //
	
		// Parameters to play with
		float[] cutthA = {4, 10, 15, 20, 25};
		//float[] seqweights = {0.05f, 0.10f, 0.15f, 0.20f};
		//float[] seqweights = {0.25f, 0.30f, 0.40f, 0.50f};
		//float[] seqweights = {0.01f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f};
		float[] seqweights = {0.15f, 0.20f, 0.25f, 0.30f};
		float[][] rolesW = {{ 0f, 0f, 0f},
				  			{ 0f, 0f, 0f},
				  			{ 0f, 0f, 0f}};
		
		// initialize the model evaluator
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC, null, 
				matrix, trainAL, valAL, testAL);
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		modelev.buildMarkovChains();
		
		

	
		
		// Hclust + MySPADE //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Start generating and evaluating the model
		int i = 5; // Hclust - linkage method
		for(int j=0; j<cutthA.length; j++){
			float cutth = cutthA[j];
				
			String esperimentationStr = "agglo" + i + "_cl" + cutth;
			
			// Load clustering
			modelev.loadClusters(validationWD + clustWD + "/" + esperimentationStr + ".javaData");
			
			// SPADE
			for(int k=0; k<seqweights.length; k++){
				float minsup = seqweights[k];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				
				// MEDOIDS models //
				modelev.buildMedoidsModels(minsup);
				
				// Evaluation
				String results;
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					results = modelev.computeEvaluationTest(2, nrec, (long)0, 1, 1, 0, true, rolesW);
					System.out.print(esperimentationStr2 + "_weighted" + nrec + ",");
					System.out.print(results);
				}
			
				// unbounded
				results = modelev.computeEvaluationTest(-1, -1, (long)0, 1, 1, 0, true, rolesW);
				System.out.print(esperimentationStr2 + "_unbounded,");
				System.out.print(results);
			}

		}
		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
