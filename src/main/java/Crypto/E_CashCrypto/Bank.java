package Crypto.E_CashCrypto;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.lang.SerializationUtils;

public class Bank {
	static ArrayList<BigInteger> unBlindedMoneyOrders = new ArrayList<BigInteger>();
	//static byte[][] deserializeMoneyOrdersArray = new byte[51][];
	static ArrayList<MoneyOrder> deserializeMoneyOrders = new ArrayList<MoneyOrder>();
	
	public void checkAllButOneBlindedMoneyOrders(BigInteger[] blindedMoneyOrders, BigInteger blindingFactor){
		Bank bank = new Bank();
		bank.unBlindMoneyOrders(blindedMoneyOrders, blindingFactor);
		bank.deserializeMoneyOrders();
	}
	
	//TODO randomize moneyOrderChoosing
	public void unBlindMoneyOrders(BigInteger[] blindedMoneyOrders, BigInteger blindingFactor){
		for(int i = 0; i < 4; i++){
			unBlindedMoneyOrders.add(blindedMoneyOrders[i].divide(blindingFactor));
		}
			
		//System.out.println(blindedMoneyOrders[0]);
		//System.out.println(unBlindedMoneyOrders.get(0));
	}
	
	public void deserializeMoneyOrders(){
		
		for(BigInteger order : unBlindedMoneyOrders) {
			byte[] tempBigIntToByte = order.toByteArray();
			MoneyOrder tempObject = (MoneyOrder) SerializationUtils.deserialize(tempBigIntToByte);
			deserializeMoneyOrders.add(tempObject);
		}
		
		System.out.println(deserializeMoneyOrders.get(0));
		System.out.println(deserializeMoneyOrders.get(1));
		System.out.println(deserializeMoneyOrders.get(2));
		System.out.println(deserializeMoneyOrders.get(3));
	}
}
