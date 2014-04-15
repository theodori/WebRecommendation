package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.DistanceMatrixEditTopics;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A112MainClassDistanceMatrixEDTopics {
	
	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Integer> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			String dmFile,
			float urlsEqualnessThreshold){
		m_matrix = new DistanceMatrixEditTopics(dmFile, urlsEqualnessThreshold);
		m_matrix.computeMatrix(sampleSessionIDs, sequencesUHC, roleWeights);
		m_matrix.save(databaseWD);
		m_matrix.writeMatrix(databaseWD + "/distance_matrix.txt");
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new DistanceMatrixEditTopics(null, 0.6f);
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
		String dmWD = "/DM_00_no_role";
		dmWD = "";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Integer> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();		
		
		
		// DISTANCE MATRIX //
		A112MainClassDistanceMatrixEDTopics dm;
		

		// No role
		float[][] roleW1 = {{ 0f, 0f, 0f},
				            { 0f, 0f, 0f},
				            { 0f, 0f, 0f}};
		dm = new A112MainClassDistanceMatrixEDTopics();
		dm.createDistanceMatrix(databaseWD + "/DM_04_edit_dist_topics", 
				sampleSessionIDs, sequencesUHC, 
				roleW1,
				preprocessingWD + "/URLs_DM.txt", 0.6f);
		
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
}
