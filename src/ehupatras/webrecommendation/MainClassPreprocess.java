package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.usage.preprocess.*;
import ehupatras.webrecommendation.usage.preprocess.log.LogReader;
import ehupatras.webrecommendation.usage.preprocess.log.LogReaderBidasoaTurismo;

public class MainClassPreprocess {

	public void preprocessLogs(String basedirectory, String logfile){
		long starttime;
		long endtime;
		
		// start preprocessing
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] PREPROCESSING.");
		
		
		
		// FILTER LOGS //
		LogReader logreader = new LogReaderBidasoaTurismo();
		
		// It reads the log file and store the valid requests in [ehupatras.webrecommendation.structures.WebAccessSequences]
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start reading the log files and analyzing the URLs.");
			String[] logfilesA = new String[1];
			logfilesA[0] = basedirectory + logfile;
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
		

		
		// SESSIONING //
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
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start saving the preprocesssing.");
		WebAccessSequences.saveStructure();
		WebAccessSequences.saveSequences();
		Website.save();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");

	}
	
	public void loadPreprocess(){
		long starttime;
		long endtime;
		
		// start loading preprocessing data
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] LOADING PREPROCESSED DATA.");
		
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start reading preprocessed data.");
		Website.load();
		WebAccessSequences.loadStructure();
		WebAccessSequences.loadSequences();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/all_esperimentation";
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		String filename1 = "/kk.log";
		//String basedirectory = args[0];
		//String filename1 = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		MainClassPreprocess main = new MainClassPreprocess(); 
	if(true){ // read the logs or load them	
		// READ THE LOG FILE(S) //
		main.preprocessLogs(basedirectory, filename1);
	} else {
		main.loadPreprocess();
	}
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}