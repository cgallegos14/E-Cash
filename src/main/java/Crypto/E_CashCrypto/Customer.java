package Crypto.E_CashCrypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang.SerializationUtils;

public class Customer {
	static ArrayList<MoneyOrder> moneyOrders = new ArrayList<MoneyOrder>();
	static ArrayList<IdentityString> identityStrings = new ArrayList<IdentityString>();
	static ArrayList<BigInteger> blindedMoneyOrders = new ArrayList<BigInteger>();
	static int seed;
	
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
		
		BitCommitment bitCommitment = new BitCommitment();
		
		Random random = new Random();
		long randomNumber = 0;
		int maxRandom = 25000;
		randomNumber = (long) random.nextInt(maxRandom);
		
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
		
		BigInteger blindingFactor = new BigInteger("8"); //TODO make this work by not recreating it
		
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
		Generators generators = new Generators();
		
		int randomSeed = 0;
		Random random = new Random();
		randomSeed = random.nextInt(250);
		seed = randomSeed;
		
		bitCommitment.setRandomBitString(randomBitString);
		bitCommitment.setSeed(randomSeed);
		String bitCommitmentResult = bitCommitmentCalculator(bitCommitment.getRandomBitString(), randomSeed);
		bitCommitment.setBitCommitmentResult(bitCommitmentResult);
		
		System.out.println(bitCommitment.getRandomBitString());
		System.out.println(bitCommitment.getSeed());
		System.out.println(bitCommitment.getBitCommitmentResult());
		
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
		
		System.out.println("This is psuedoR ==> " + psuedoRandomNumberFromSeedString);
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
		BigInteger random = new BigInteger("8");
		
		blindedMoneyOrders.add(serializedMoneyOrderInt.multiply(random));
		System.out.println(blindedMoneyOrders.get(count));
	}
	
	public void getBackSignedMoneyOrder(SignedMoneyOrder signedMoneyOrder){
		Customer customer = new Customer();
		BigInteger unBlindedMoneyOrder;
		BigInteger blindingFactor = new BigInteger("8");
		unBlindedMoneyOrder = signedMoneyOrder.getBlindedMoneyOrder().divide(blindingFactor);
		
		byte[] moneyOrderBigIntToBitArray = unBlindedMoneyOrder.toByteArray();
		MoneyOrder tempObject = (MoneyOrder) SerializationUtils.deserialize(moneyOrderBigIntToBitArray);
		signedMoneyOrder.setMoneyOrder(tempObject);
		
		//System.out.println(signedMoneyOrder.getMoneyOrder().getMoneyOrderAmount());
		customer.useMoneyOrder(signedMoneyOrder);
	}
	
	public void useMoneyOrder(SignedMoneyOrder signedMoneyOrder){
		
	}
	
}
