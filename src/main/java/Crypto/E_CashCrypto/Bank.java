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

/**
 * This class allows a bank to accept a money order from a customer/Merchant
 * Also Uses the clone money orders to check for customer fraud. 
 * Also Handles key generation, and stores needed information to 
 * Insure money isn't used twice, and if used twice catches the
 * Merchant or customer, depending on who cheated
 * Returns a signed money order to the customer
 * Gives the money to the merchant if all is well
 */

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
		RSAKeyGeneration bankPublicKeys = new RSAKeyGeneration();
		Bank bank = new Bank();
		
		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		
		bankKeys.setPrivateKey(privateKey);
		bankKeys.setPublicKey(publicKey);
		bankPublicKeys.setPublicKey(publicKey);
		
		Signature signingEngine = Signature.getInstance("SHA1withRSA");
		SignedObject signedObject = new SignedObject(blindedMoneyOrders.get(0), privateKey, signingEngine);
		
		signedMoneyOrder.setBankSignedObject(signedObject);
		signedMoneyOrder.setBlindedMoneyOrder(blindedMoneyOrders.get(0));
				
		bank.storeCustomerInfoAndReturnSignedMoneyOrder(signedMoneyOrder, bankPublicKeys);
	}
	
	public void storeCustomerInfoAndReturnSignedMoneyOrder(SignedMoneyOrder signedMoneyOrder, RSAKeyGeneration bankPublicKeys){
		BankStoredInfomation tempInfo = new BankStoredInfomation();
		Generators generators = new Generators();
		Customer customer = new Customer();
		BitCommitment bitCommitment = new BitCommitment();
		Bank bank = new Bank();
		
		System.out.println("Welcome to the Bank of Socorro");
		System.out.println("You want a money order, ok lets check all but one for security");

		if(bank.checkMoneyOrders() == true){
			System.out.println("All looks good Here is your money order. Thank You!");
			String randomBitString = generators.generateUniquenessString(20);
			bitCommitment = customer.generateBitCommitment(randomBitString);
			
			tempInfo.setCustomerIdentity(deserializeMoneyOrders.get(0).getCustomerIdenity());
			tempInfo.setBitCommitmentResult(bitCommitment.getBitCommitmentResult());
			storedInformation.add(tempInfo);
			
			customer.getBackSignedMoneyOrder(signedMoneyOrder, bankPublicKeys);
		}
		else{
			System.out.println("Your money orders were not valid! Police on the way!");
		}
	}
	
	public void retrieveMoneyOrderFromMerchant(SignedMoneyOrder signedMoneyOrder, long customerIdentitySection) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
		Bank bank = new Bank();
		RSAKeyGeneration bankPublicKeys = new RSAKeyGeneration();
		bankPublicKeys.setPublicKey(bankKeys.getPublicKey());
		System.out.println();
		System.out.println("Hello, welcome to Bank of Socorro");
		System.out.println("I see you have a money order, lets have a look");
		System.out.println();
		
		Signature signingEngine = Signature.getInstance("SHA1withRSA");
		boolean isSignatureValid = signedMoneyOrder.getBankSignedObject().verify(bankKeys.getPublicKey(), signingEngine);
		
		if(isSignatureValid == true){
			System.out.println("That's our signature! Lets make sure it's not a duplicate");
			if(bank.checkForDuplicateMoneyOrder(signedMoneyOrder) == false){
				Merchant merchant = new Merchant();
				System.out.println("All checks out here is your money!");
				storedInformation.get(0).setUniquenessString(signedMoneyOrder.getMoneyOrder().getUniquenessString());
				storedInformation.get(0).setCustomerHalfIdentity(customerIdentitySection);
				merchant.merchantTryToCheatBank(signedMoneyOrder, customerIdentitySection, bankPublicKeys);
			}
			else{
				System.out.println("WE HAVE A PROBLEM!!!, please give us the customer Identity half");
				if(bank.checkIfMerchantCheated(signedMoneyOrder, customerIdentitySection) == true){
					System.out.println("BAD MOVE, you already have cashed this. Guard Take him away!");
				}
				else{
					System.out.println("It appears the customer used this money order twice.");
					System.out.println("We will find out who it is now!");
					
					System.out.println("The thief is...");
					System.out.println(storedInformation.get(0).getCustomerHalfIdentity() ^ customerIdentitySection);
				}
			}
		}
		else{
			System.out.println("Well... Sorry that's not our signature...");
		}
	}
	
	public boolean checkForDuplicateMoneyOrder(SignedMoneyOrder signedMoneyOrder){
		boolean isMoneyOrderDuplicate = false;
		
		if(storedInformation.get(0).getUniquenessString() == null){
			isMoneyOrderDuplicate = false;
			return isMoneyOrderDuplicate;
		}
		
		else{
			for(int i = 0; i < storedInformation.size(); i++){
				if(storedInformation.get(i).getUniquenessString() == signedMoneyOrder.getMoneyOrder().getUniquenessString()){
					isMoneyOrderDuplicate = true;
					return isMoneyOrderDuplicate;
				}
			}
		}
		return isMoneyOrderDuplicate;
	}
	
	public boolean checkIfMerchantCheated(SignedMoneyOrder signedMoneyOrder, long customerIdentitySection){
		boolean isMerchantACheater = true;
		
		if(storedInformation.get(0).getCustomerHalfIdentity() == customerIdentitySection){
			isMerchantACheater = true;
			return isMerchantACheater;
		}
		else{
			isMerchantACheater = false;
			return isMerchantACheater;
		}
	}
}
