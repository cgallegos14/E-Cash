package Crypto.E_CashCrypto;

public class BankStoredInfomation {
	private long customerIdentity;
	private String uniquenessString;
	private String bitCommitmentResult;
	
	public long getCustomerIdentity() {
		return customerIdentity;
	}
	public void setCustomerIdentity(long customerIdentity) {
		this.customerIdentity = customerIdentity;
	}
	public String getUniquenessString() {
		return uniquenessString;
	}
	public void setUniquenessString(String uniquenessString) {
		this.uniquenessString = uniquenessString;
	}
	public String getBitCommitmentResult() {
		return bitCommitmentResult;
	}
	public void setBitCommitmentResult(String bitCommitmentResult) {
		this.bitCommitmentResult = bitCommitmentResult;
	}
}
