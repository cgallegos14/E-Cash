package Crypto.E_CashCrypto;

import java.io.Serializable;

public class MoneyOrder implements Serializable{
	private static final long serialVersionUID = -2669990566477912592L;
	
	private double moneyOrderAmount = 0;
	private String uniquenessString = "";
	long customerIdenity = 11001010100101L;
	private boolean bankSignature = false;
	
	public MoneyOrder(){
		//BigInteger identityString = new BigInteger(Integer.toString(idenString));
	}

	public double getMoneyOrderAmount() {
		return moneyOrderAmount;
	}

	public void setMoneyOrderAmount(double moneyOrderAmount) {
		this.moneyOrderAmount = moneyOrderAmount;
	}

	public String getUniquenessString() {
		return uniquenessString;
	}

	public void setUniquenessString(String uniquenessString) {
		this.uniquenessString = uniquenessString;
	}

	public long getCustomerIdenity() {
		return customerIdenity;
	}

	public void setcustomerIdenity(long customerIdenity) {
		this.customerIdenity = customerIdenity;
	}

	public boolean isBankSignature() {
		return bankSignature;
	}

	public void setBankSignature(boolean bankSignature) {
		this.bankSignature = bankSignature;
	}

	@Override
	public String toString() {
		return "MoneyOrder [moneyOrderAmount=" + moneyOrderAmount
				+ ", uniquenessString=" + uniquenessString
				+ ", customerIdenity=" + customerIdenity + ", bankSignature="
				+ bankSignature + "]";
	}
	
}
