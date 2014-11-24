package Crypto.E_CashCrypto;

import java.util.Random;

public class Generators {
	
	public String generateUniquenessString(int size){
		String uniquenessString = "";
		String oneAndZeroArray = "10101010101010101010101010";
		
		Random rand = new Random();
		int randomNumber = 0;
		
		for(int i = 0; i < size; i++){
			randomNumber = rand.nextInt(25);
			uniquenessString += oneAndZeroArray.charAt(randomNumber);
	    }
		return uniquenessString;
	}
	
	public int randomNumberGeneratorSeed(int seed){
		
		int randomNumber = 0;
		
		Random generator = new Random(seed);
		randomNumber = generator.nextInt() * (40);

	    return randomNumber;
	}
	
}
