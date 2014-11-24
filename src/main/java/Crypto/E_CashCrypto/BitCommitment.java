package Crypto.E_CashCrypto;

public class BitCommitment {
	private int seed;
	private String randomBitString;
	private String bitCommitmentResult;
	
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}
	public String getRandomBitString() {
		return randomBitString;
	}
	public void setRandomBitString(String randomBitString) {
		this.randomBitString = randomBitString;
	}
	public String getBitCommitmentResult() {
		return bitCommitmentResult;
	}
	public void setBitCommitmentResult(String bitCommitmentResult) {
		this.bitCommitmentResult = bitCommitmentResult;
	}
	
}
