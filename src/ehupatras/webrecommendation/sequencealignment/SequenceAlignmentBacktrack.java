package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public abstract class SequenceAlignmentBacktrack 
						implements SequenceAlignment {
    protected String[] mSeqA;
    protected String[] mSeqB;
    protected float[][] mD;
    protected float mScore;
    protected String mAlignmentSeqA = "";
    protected String mAlignmentSeqB = "";
    protected String[] m_alignSeqA;
    protected String[] m_alignSeqB;
    protected String m_gap = "-";
    
    // weights of roles
    protected float[][] m_roleW = {{ 1f, 1f, 1f},  // Unimportant
    					 		   { 1f, 1f, 1f},  // Hub
    					 		   { 1f, 1f, 1f}}; // Content

    // Functions to get the standard sequence alignment score
    
    protected abstract void init(String[] seqA, String[] seqB);
    protected abstract void process();
    protected abstract void backtrack();
    
    public abstract float getScore(String[] seqA, String[] seqB);
    
    protected void computeAlignment(String[] seqA, String[] seqB){
    	// initialize all class attributes
    	mAlignmentSeqA = "";
    	mAlignmentSeqB = "";
    	m_gap = "-";
    	// create the gap String
    	int gaplen = seqA[0].length();
    	for(int i=1; i<gaplen; i++){ m_gap = m_gap + "-"; }
    	
    	// compute the score
        init(seqA, seqB);
        process();
        backtrack();
    }
    
    // Functions to get the Dimopoulos2010 string alignment variation
    
    protected abstract ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2);
    
    public Integer[] getAlignmentOperations(String[] seqA, String[] seqB){
    	// compute match / mismatch / gaps / spaces
    	computeAlignment(seqA,seqB);
    	ArrayList<String[]> trimmedSeqs = getTrimedAlignedSequences(mAlignmentSeqA, mAlignmentSeqB);
    	m_alignSeqA = trimmedSeqs.get(0);
    	m_alignSeqB = trimmedSeqs.get(1);
    	int alignLen = m_alignSeqA.length;
    	int nmatches = 0;
    	int nmismatches = 0;
    	int ngaps = 0;
    	int nspaces = 0;
    	String previousElemA = "";
    	String previousElemB = "";
    	for(int i=0; i<alignLen; i++){
    		String elemA = m_alignSeqA[i];
    		String elemB = m_alignSeqB[i];
    		if(elemA.equals(elemB)){
    			if(!elemA.equals(m_gap) && !elemB.equals(m_gap)){
    				nmatches++;
    			} else { // gaps
    				boolean iscounted = false;
    				if(elemA.equals(m_gap)){
    					if(previousElemA.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(elemB.equals(m_gap)){
    					if(previousElemB.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(!iscounted){
    					ngaps++;
    				}
    			}
    		} else {
       			if(!elemA.equals(m_gap) && !elemB.equals(m_gap)){
       				nmismatches++;
       			} else { // gaps
    				boolean iscounted = false;
    				if(elemA.equals(m_gap)){
    					if(previousElemA.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(elemB.equals(m_gap)){
    					if(previousElemB.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(!iscounted){
    					ngaps++;
    				}
    			}
    		}
    		previousElemA = elemA;
    		previousElemB = elemB;
    	}
    	
    	// return the count values
    	Integer[] counts = new Integer[4];
    	counts[0] = nmatches;
    	counts[1] = nmismatches;
    	counts[2] = ngaps;
    	counts[3] = nspaces;
    	return counts;
    }
    
    protected String[] getStringArrayRepresentation(String str){
    	int alignLen = str.length()/m_gap.length();
    	String[] seq = new String[alignLen];
    	for(int i=0; i<alignLen; i++){
    		int startind = i*m_gap.length();
    		seq[i] = str.substring(startind, startind+m_gap.length());
    	}
    	return seq;
    }
    
    public void printMatrix() {
        System.out.print("D =       ");
        for (int i = 0; i < mSeqB.length; i++) {
                System.out.print(String.format("%4s ", mSeqB[i]));
        }
        System.out.println();
        for (int i = 0; i < mSeqA.length + 1; i++) {
                if (i > 0) {
                        System.out.print(String.format("%4s ", mSeqA[i-1]));
                } else {
                        System.out.print("     ");
                }
                for (int j = 0; j < mSeqB.length + 1; j++) {
                        System.out.print(String.format("%4f ", mD[i][j]));
                }
                System.out.println();
        }
        System.out.println();
    }
    
    public void printScoreAndAlignments() {
        System.out.println("Score: " + mScore);
        System.out.println("Sequence A: " + mAlignmentSeqA);
        System.out.println("Sequence B: " + mAlignmentSeqB);
        System.out.println();
    }

    public String[] getAlignSeqA(){
    	return m_alignSeqA;
    }
    
    public String[] getAlignSeqB(){
    	return m_alignSeqB;
    }
    
    protected int weightOld(int i, int j) {
        if (mSeqA[i - 1].equals(mSeqB[j - 1])) {
                return 1;
        } else {
                return -1;
        }
    }
    
    protected float weight(int i, int j) {
    	int len = m_gap.length();
    	String urlA = mSeqA[i-1].substring(0,len-1);
    	String rolA = mSeqA[i-1].substring(len-1,len);
    	int rolAi = this.role2int(rolA);
    	String urlB = mSeqB[j-1].substring(0,len-1);
    	String rolB = mSeqB[j-1].substring(len-1,len);
    	int rolBi = this.role2int(rolB);
    	
        if (urlA.equals(urlB)){
        	return m_roleW[rolAi][rolBi];
        } else {
        	return -1f;
        }
    }
    
    private int role2int(String role){
    	int roli = 0;
    	if(role.equals("U")){ roli = 0; }
    	else if(role.equals("H")){ roli = 1;}
    	else if(role.equals("C")){ roli = 2;}
    	return roli;
    }
    
    public void setRoleWeights(float[][] roleweights){
    	m_roleW = roleweights;
    }
    
}
