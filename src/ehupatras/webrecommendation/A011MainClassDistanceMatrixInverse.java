package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A011MainClassDistanceMatrixInverse {

	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		m_matrix = new SimilarityMatrixInverse(sampleSessionIDs);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
					databaseWD + "/distance_matrix.txt");
		
		m_matrix.save(databaseWD);
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixInverse(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		
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
		A011MainClassDistanceMatrixInverse dm;
		

		// No role
		float[][] roleW1 = {{ 1f, 1f, 1f},
				            { 1f, 1f, 1f},
				            { 1f, 1f, 1f}};
		dm = new A011MainClassDistanceMatrixInverse();
		dm.createDistanceMatrix(databaseWD + "/DM_00_no_role_dist", 
				sampleSessionIDs, sequencesUHC, 
				roleW1);
		
		// Treat the role intelligently2
		float[][] roleW5 = {{ 0f,    0f,    0f},
	  		    			{ 0f,    1f, 0.75f},
	  		    			{ 0f, 0.75f,    1f}};
		dm = new A011MainClassDistanceMatrixInverse();
		dm.createDistanceMatrix(databaseWD + "/DM_03_intelligent2_dist", 
				sampleSessionIDs, sequencesUHC, 
				roleW5);
		
		
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
