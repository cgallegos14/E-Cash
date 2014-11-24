package Crypto.E_CashCrypto;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.lang.SerializationUtils;

public class Bank {
	static ArrayList<BigInteger> unBlindedMoneyOrders = new ArrayList<BigInteger>();
	static ArrayList<MoneyOrder> deserializeMoneyOrders = new ArrayList<MoneyOrder>();
	
	public void checkAllButOneBlindedMoneyOrders(ArrayList<BigInteger> blindedMoneyOrders, BigInteger blindingFactor){
		Bank bank = new Bank();
		bank.unBlindMoneyOrders(blindedMoneyOrders, blindingFactor);
		bank.deserializeMoneyOrders();
		System.out.println(bank.checkMoneyOrders());
	}
	
	//TODO randomize moneyOrderChoosing
	public void unBlindMoneyOrders(ArrayList<BigInteger> blindedMoneyOrders, BigInteger blindingFactor){
		for(BigInteger order : blindedMoneyOrders){
			unBlindedMoneyOrders.add(order.divide(blindingFactor));
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
	
	public boolean checkMoneyOrders(){
		boolean areMoneyOrdersValid = true;
		double amount = deserializeMoneyOrders.get(0).getMoneyOrderAmount();
		long customerIdenity = deserializeMoneyOrders.get(0).getCustomerIdenity();
		
		for(MoneyOrder moneyOrders : deserializeMoneyOrders){
			if(moneyOrders.getMoneyOrderAmount() != amount && moneyOrders.getCustomerIdenity() != customerIdenity){
				return false;
			}
		}
		
		return areMoneyOrdersValid;
	}
}
