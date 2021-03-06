package Crypto.E_CashCrypto;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang.SerializationUtils;

/**
 * This class allows a customer to great a Money order for a set amount
 * Also the number of clone money orders can be changed at run time
 * Clone money orders are used for the bank to verify the one blind money order they
 * Don't UN-blind has a low chance of being different than the others
 * This class also handles identity splitting and Bit Commitment Protocols
 * Lastly allows the customer to try and cheat and use the money twice
 */

public class Customer {
	static ArrayList<MoneyOrder> moneyOrders = new ArrayList<MoneyOrder>();
	static ArrayList<IdentityString> identityStrings = new ArrayList<IdentityString>();
	static ArrayList<BigInteger> blindedMoneyOrders = new ArrayList<BigInteger>();
	static int seed;
	static BigInteger randomBigInt;
	
	public void getAmount(){
		Customer customer = new Customer();
		Scanner scanner = new Scanner(System.in);
		double amount = 0.0;
		int numberOfCloneMoneyOrders = 0;
		System.out.print("Please enter amount for money order ==> ");
		amount = scanner.nextDouble();		
		
		System.out.print("Please enter how many clone money orders ==> ");
		numberOfCloneMoneyOrders = scanner.nextInt();
		
		customer.generateNMoneyOrders(numberOfCloneMoneyOrders, amount);
	}
	
	public void generateNMoneyOrders(int numberOfCloneMoneyOrders, double value){
		Generators generators = new Generators();
		Customer customer = new Customer();
		MoneyOrder moneyOrder = new MoneyOrder();
		Bank bank = new Bank();
				
		Random random = new Random();
		long randomNumber = 0;
		int maxRandom = 25000;
		randomNumber = (long) random.nextInt(maxRandom);
		Integer temp = random.nextInt(15);
		randomBigInt = BigInteger.valueOf(temp.intValue());
		
		
		for(int i = 0; i < numberOfCloneMoneyOrders; i++) {
		   MoneyOrder tempMoneyOrder = new MoneyOrder();
		   tempMoneyOrder.setMoneyOrderAmount(value);
		   tempMoneyOrder.setUniquenessString(generators.generateUniquenessString(25));
		   moneyOrders.add(tempMoneyOrder);
		   customer.generateNIdentityPairs(randomNumber, moneyOrder.getCustomerIdenity(), i);
		   customer.blindMoneyOrders(tempMoneyOrder, i);
		}
		
		//System.out.println(moneyOrders[0].getUniquenessString());
		//System.out.println(moneyOrders[1].getUniquenessString());
		//System.out.println(moneyOrders[2].getUniquenessString());
		//System.out.println(generators.randomNumberGeneratorSeed(60));
		//bitCommitment = customer.generateBitCommitment();
		//customer.bitCommitmentReverser(bitCommitment);
		
		BigInteger blindingFactor = randomBigInt; //TODO make this work by not recreating it
		
		bank.checkAllButOneBlindedMoneyOrders(blindedMoneyOrders, blindingFactor);
	}
	
	public void generateNIdentityPairs(long randomNumber, long customerIdenity, int count){
		IdentityString tempIdentityString = new IdentityString();
		
		tempIdentityString.setLeftHalf(randomNumber);
		tempIdentityString.setRightHalf(randomNumber ^ customerIdenity);
		identityStrings.add(tempIdentityString);
		
		//System.out.println(identityStrings[count].getLeftHalf() ^ identityStrings[count].getRightHalf());
	}
	
	public BitCommitment generateBitCommitment(String randomBitString){
		BitCommitment bitCommitment = new BitCommitment();
		
		int randomSeed = 0;
		Random random = new Random();
		randomSeed = random.nextInt(250);
		seed = randomSeed;
		
		bitCommitment.setRandomBitString(randomBitString);
		bitCommitment.setSeed(randomSeed);
		String bitCommitmentResult = bitCommitmentCalculator(bitCommitment.getRandomBitString(), randomSeed);
		bitCommitment.setBitCommitmentResult(bitCommitmentResult);
		
		//System.out.println(bitCommitment.getRandomBitString());
		//System.out.println(bitCommitment.getSeed());
		//System.out.println(bitCommitment.getBitCommitmentResult());
		
		return bitCommitment;
	}
	
	public String bitCommitmentCalculator(String randomString, int randomSeed){
		Generators generators = new Generators();
		String bitCommitmentString = "";
		String psuedoRandomNumberFromSeedString = "";
		int psuedoRandomNumberFromSeed = 0;
		
		psuedoRandomNumberFromSeed = generators.randomNumberGeneratorSeed(randomSeed);
		psuedoRandomNumberFromSeedString = Integer.toBinaryString(psuedoRandomNumberFromSeed);
		
		for(int i = 0; i < randomString.length(); i++){
			if(randomString.charAt(i) == '0'){
				bitCommitmentString += psuedoRandomNumberFromSeedString.charAt(i);
			}
			else{
				bitCommitmentString += (psuedoRandomNumberFromSeedString.charAt(i) ^ randomString.charAt(i));
			}
		}
		
		//System.out.println("This is psuedoR ==> " + psuedoRandomNumberFromSeedString);
		return bitCommitmentString;
	}
	
	public String bitCommitmentReverser(BitCommitment bitCommitment){
		Generators generators = new Generators();
		String bitCommitmentResult = "";
		String psuedoRandomNumberFromSeedString = "";
		int psuedoRandomNumberFromSeed = 0;
		
		psuedoRandomNumberFromSeed = generators.randomNumberGeneratorSeed(bitCommitment.getSeed());
		psuedoRandomNumberFromSeedString = Integer.toBinaryString(psuedoRandomNumberFromSeed);
		
		for(int i = 0; i < bitCommitment.getRandomBitString().length(); i++){
			if(bitCommitment.getRandomBitString().charAt(i) == '0'){
				bitCommitmentResult += psuedoRandomNumberFromSeedString.charAt(i);
			}
			else{
				bitCommitmentResult += (psuedoRandomNumberFromSeedString.charAt(i) ^ bitCommitment.getRandomBitString().charAt(i));
			}
		}
		System.out.println("This is result ==> " + bitCommitmentResult);
		return bitCommitmentResult;
	}
	
	public void blindMoneyOrders(MoneyOrder moneyOrder, int count){
		byte[] serializedMoneyOrder = SerializationUtils.serialize(moneyOrder);
		BigInteger serializedMoneyOrderInt = new BigInteger(serializedMoneyOrder);
		
		blindedMoneyOrders.add(serializedMoneyOrderInt.multiply(randomBigInt));
		System.out.println(blindedMoneyOrders.get(count));
	}
	
	public void getBackSignedMoneyOrder(SignedMoneyOrder signedMoneyOrder, RSAKeyGeneration bankPublicKeys){
		Customer customer = new Customer();
		BigInteger unBlindedMoneyOrder;
		BigInteger blindingFactor = randomBigInt;
		unBlindedMoneyOrder = signedMoneyOrder.getBlindedMoneyOrder().divide(blindingFactor);
		
		byte[] moneyOrderBigIntToBitArray = unBlindedMoneyOrder.toByteArray();
		MoneyOrder tempObject = (MoneyOrder) SerializationUtils.deserialize(moneyOrderBigIntToBitArray);
		signedMoneyOrder.setMoneyOrder(tempObject);
		
		//System.out.println(signedMoneyOrder.getMoneyOrder().getMoneyOrderAmount());
		customer.useMoneyOrder(signedMoneyOrder, bankPublicKeys);
	}
	
	public void useMoneyOrder(SignedMoneyOrder signedMoneyOrder, RSAKeyGeneration bankPublicKeys){
		Merchant merchant = new Merchant();
		try {
			merchant.retrieveMoneyOrder(signedMoneyOrder, identityStrings, bankPublicKeys);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void tryToCheatUseMoneyOrderTwice(SignedMoneyOrder signedMoneyOrder, RSAKeyGeneration bankPublicKeys) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
		Merchant merchant = new Merchant();
		Scanner scanner = new Scanner(System.in);
		int choice;
		System.out.println();
		System.out.println("Well the system looks stupid, Should I (customer) try to use the money Order again?");
		System.out.print("1 for yes 2 for no ==> ");
		
		choice = scanner.nextInt();
		
		if(choice == 1){
			merchant.retrieveMoneyOrder(signedMoneyOrder, identityStrings, bankPublicKeys);
		}
		else{
			System.out.println("Nah not worth the jail time ^_^");
		}
	}
	
}
