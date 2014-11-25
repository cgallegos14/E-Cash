package Crypto.E_CashCrypto;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.ArrayList;

import org.apache.commons.lang.SerializationUtils;

public class Bank {
	static ArrayList<BigInteger> unBlindedMoneyOrders = new ArrayList<BigInteger>();
	static ArrayList<MoneyOrder> deserializeMoneyOrders = new ArrayList<MoneyOrder>();
	static RSAKeyGeneration bankKeys = new RSAKeyGeneration();
	static ArrayList<BankStoredInfomation> storedInformation = new ArrayList<BankStoredInfomation>();
	
	public void checkAllButOneBlindedMoneyOrders(ArrayList<BigInteger> blindedMoneyOrders, BigInteger blindingFactor){
		Bank bank = new Bank();
		
		bank.unBlindMoneyOrders(blindedMoneyOrders, blindingFactor);
		bank.deserializeMoneyOrders();
		
		try {
			bank.signMoneyOrder(blindedMoneyOrders);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public void signMoneyOrder(ArrayList<BigInteger> blindedMoneyOrders) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException{
		SignedMoneyOrder signedMoneyOrder = new SignedMoneyOrder();
		Bank bank = new Bank();
		
		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		
		bankKeys.setPrivateKey(privateKey);
		bankKeys.setPublicKey(publicKey);
		
		Signature signingEngine = Signature.getInstance("SHA1withRSA");
		SignedObject signedObject = new SignedObject(blindedMoneyOrders.get(0), privateKey, signingEngine);
		
		signedMoneyOrder.setBankSignedObject(signedObject);
		signedMoneyOrder.setBlindedMoneyOrder(blindedMoneyOrders.get(0));
				
		bank.storeCustomerInfoAndReturnSignedMoneyOrder(signedMoneyOrder);
	}
	
	public void storeCustomerInfoAndReturnSignedMoneyOrder(SignedMoneyOrder signedMoneyOrder){
		BankStoredInfomation tempInfo = new BankStoredInfomation();
		Generators generators = new Generators();
		Customer customer = new Customer();
		BitCommitment bitCommitment = new BitCommitment();
		Bank bank = new Bank();
		
		if(bank.checkMoneyOrders() == true){
			String randomBitString = generators.generateUniquenessString(20);
			bitCommitment = customer.generateBitCommitment(randomBitString);
			
			tempInfo.setCustomerIdentity(deserializeMoneyOrders.get(0).getCustomerIdenity());
			tempInfo.setBitCommitmentResult(bitCommitment.getBitCommitmentResult());
			storedInformation.add(tempInfo);
			
			customer.getBackSignedMoneyOrder(signedMoneyOrder);
		}
		else{
			System.out.println("Your money orders were not valid! Police on the way!");
		}
	}
}
