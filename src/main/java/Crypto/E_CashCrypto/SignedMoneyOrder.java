package Crypto.E_CashCrypto;

import java.math.BigInteger;
import java.security.SignedObject;

public class SignedMoneyOrder {
	private BigInteger blindedMoneyOrder;
	private SignedObject bankSignedObject;
	private MoneyOrder moneyOrder;
	
	public BigInteger getBlindedMoneyOrder() {
		return blindedMoneyOrder;
	}
	public void setBlindedMoneyOrder(BigInteger unblindedMoneyOrder) {
		this.blindedMoneyOrder = unblindedMoneyOrder;
	}
	
	public SignedObject getBankSignedObject() {
		return bankSignedObject;
	}
	public void setBankSignedObject(SignedObject bankSignedObject) {
		this.bankSignedObject = bankSignedObject;
	}
	public MoneyOrder getMoneyOrder() {
		return moneyOrder;
	}
	public void setMoneyOrder(MoneyOrder moneyOrder) {
		this.moneyOrder = moneyOrder;
	}

}
