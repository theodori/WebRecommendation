package ehupatras.suffixtree.stringarray.test;

import ehupatras.suffixtree.stringarray.GeneralizedSuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SuffixTreeStringArray {
    
	private GeneralizedSuffixTreeStringArray m_gST = new GeneralizedSuffixTreeStringArray();

	public void putSequence(String[] seq, int index){
		ArrayList<String> seqAL = StringToArrayList(seq);
		putSequence(seqAL, index);
	}
	
	private ArrayList<String> StringToArrayList(String[] strA){
		ArrayList<String> seqL = new ArrayList<String>();
		for(int i=0; i<strA.length; i++){
			seqL.add(strA[i]);
		}
		return seqL;
	}
	
	public void putSequence(ArrayList<String> seq, int index){
		m_gST.put(seq, index);
	}
	
	public void printSuffixTree(){
		m_gST.print();
	}
	
	public Node getRoot(){
		return m_gST.getRoot();
	}
	
	public ArrayList<Integer> search(String[] sequence){
		ArrayList<String> seqAL = StringToArrayList(sequence);
		return search(seqAL);
	}
	
	public ArrayList<Integer> search(ArrayList<String> sequence){
		Collection<Integer> col = m_gST.search(sequence);
		ArrayList<Integer> searchSeqs = new ArrayList<Integer>();
		if(col == null){
			return searchSeqs;
		} else {
			Iterator<Integer> it = col.iterator();
			while(it.hasNext()){
				int se = it.next();
				searchSeqs.add(se);
			}
			return searchSeqs;
		}
	}
	
    public static void main(String[] args){
        SuffixTreeStringArray in = new SuffixTreeStringArray();
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        String[] word4 = {"c", "a", "r"};
        in.putSequence(word1, 0);
        in.putSequence(word2, 1);
        in.putSequence(word3, 2);
        in.putSequence(word4, 3);
        in.printSuffixTree();
        
        String[] seq1 = {"c", "a"};
        String[] seq2 = {"c", "a", "c", "a"};
        String[] seq3 = {"b", "a"};
        String[] seq4 = {"l", "o"};
        String[] seq5 = {"s", "u", "a"};
        
        ArrayList<Integer> re;
        re = in.search(seq1);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq2);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq3);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq4);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq5);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
    }
}