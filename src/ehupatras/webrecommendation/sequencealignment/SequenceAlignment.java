package ehupatras.webrecommendation.sequencealignment;

public interface SequenceAlignment {
	public float getScore(String[] seqA, String[] seqB);
	public void setRoleWeights(float[][] roleweights);
}
