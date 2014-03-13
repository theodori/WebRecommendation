package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class A010MainClassDistanceMatrixEuclidean {

	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Integer> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		System.out.println("DISTANCE MATRIX");
		m_matrix = new SimilarityMatrixEuclidean();
		m_matrix.computeMatrix(sampleSessionIDs, sequencesUHC, roleWeights);
		m_matrix.save(databaseWD);
		m_matrix.writeMatrix(databaseWD + "/distance_matrix.txt");
	}
	
	public void loadDistanceMatrix(String databaseWD){
		System.out.println("DISTANCE MATRIX");
		m_matrix = new SimilarityMatrixEuclidean();
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		//preprocessingWD = args[0];
		//logfile = args[1];
		//databaseWD = args[2];
		//validationWD = args[3];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		//A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		//preprocess.preprocessLogs(preprocessingWD, logfile);
		//preprocess.loadPreprocess();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Integer> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();
		
		
		// DISTANCE MATRIX //
		float[][] roleW1 = {{ 1f, 1f, 1f},
				            { 1f, 1f, 1f},
				            { 1f, 1f, 1f}};
		float[][] roleW2 = {{ 1f,-1f,-1f},
		          		    {-1f, 1f,-1f},
		          		    {-1f,-1f, 1f}};
		float[][] roleW3 = {{ 1f,-1f,-1f},
		          		    {-1f, 1f, 1f},
		          		    {-1f, 1f, 1f}};
		float[][] roleW4 = {{-1f,   -1f,   -1f},
        		  		    {-1f,    1f, 0.75f},
        		  		    {-1f, 0.75f,    1f}};
		A010MainClassDistanceMatrixEuclidean dm = new A010MainClassDistanceMatrixEuclidean();
		dm.createDistanceMatrix(databaseWD, sampleSessionIDs, sequencesUHC, roleW1);
		Matrix m = dm.getMatrix();
		float[][] distmatrix = m.getMatrix();
		
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}