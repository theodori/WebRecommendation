package ehupatras.webrecommendation.usage.preprocess.log;

import ehupatras.webrecommendation.structures.Request;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.Page;

public abstract class LogReader {
	
	public abstract void readLogFile(String[] logfilenamesA);
	
	// Identify the URLs that appears more than some threshold
	// for example, URLs which appear at least 10 times
	public void identifyFrequentURLs(int minimunFrequency){
		// Identify frequent URLs
		int nFrequent = 0;
		String[] keys = Website.getAllFormatedUrlNames();
		for(int i=0; i<keys.length; i++){
			Page pag = Website.getPage(keys[i]);
			String urlname = pag.getFormatedUrlName();
			pag.setIsFrequent(minimunFrequency);
			Website.putURL(urlname, pag);
			if(pag.getIsFrequent()){ nFrequent++; }
		}
		System.out.println("  " + nFrequent + "/" + Website.size() + 
				" pages have an URL that appears at least " + minimunFrequency + " times.");
	}
	
	
	// Identify the URLs that appears during all the time
	// for example, those URLs have to appear 10 times in every 10 days period.
	// the information provide this URLs are not volatiles
	public void identifyStaticURLs(int days, int times, float minimunPeriodFrequencyProportion){
		// compute the maximum URL-index value
		int maxurlid = Website.getMaximumUrlID();
		
		// for each period of time see if it is accessed
		int nperiods = 0;
		long starttime = 0;
		long endtime = Long.MAX_VALUE;
		int[] urlfrequenciesi = new int[maxurlid+1];
		int[] urlInPeriods = new int[maxurlid+1];
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			
			// define the new period of time
			long actualtime = req.getTimeInMillis();
			if(starttime==0 || actualtime>=endtime){
				//end the periods analysis
				for(int j=0; j<urlInPeriods.length; j++){
					int freq = urlfrequenciesi[j];
					if(freq>=times){
						urlInPeriods[j]++;
					}
				}
				// prepare the variables for a new periods
				starttime = actualtime;
				endtime = starttime + (long)((long)days*24*3600*1000);
				nperiods++;
				urlfrequenciesi = new int[maxurlid+1];
			}
			
			// compute the frequencies for this period
			String urlname = req.getFormatedUrlName();
			Page pag = Website.getPage(urlname);
			int urlid = pag.getUrlIDusage();
			urlfrequenciesi[urlid]++;
		}
		//end the periods analysis
		for(int j=0; j<urlInPeriods.length; j++){
			int freq = urlfrequenciesi[j];
			if(freq>=times){
				urlInPeriods[j]++;
			}
		}
		
		// Update the Website information with staticness data
		for(int i=0; i<urlInPeriods.length; i++){
			int freqInPeriods = urlInPeriods[i];
			if(freqInPeriods>0){
				Page pag = Website.getPage(i);
				String urlname = pag.getFormatedUrlName();
				pag.setNumPeriod(freqInPeriods);
				Website.putURL(urlname, pag);
			}
		}
		
		// compute if the URLs appears enough during the time to take them into account
		// they do not have volatile information
		// so, the nature of the URL would be static
		int nstatics = 0;
		int minimumperiods = Math.round((float)nperiods*minimunPeriodFrequencyProportion);
		String[] keys = Website.getAllFormatedUrlNames();
		for(int i=0; i<keys.length; i++){
			Page pag = Website.getPage(keys[i]);
			String urlname = pag.getFormatedUrlName();
			pag.setIsStatic(minimumperiods);
			Website.putURL(urlname, pag);
			if(pag.getIsStatic()){ nstatics++; }
		}
		
		System.out.println("  " + nstatics + "/" + Website.size() + 
				" requests have an URL that appears (at least " + times + " times) in " + 
				minimumperiods + "-" + nperiods + 
				" periods of " + days + " days.");
	}
	
}
