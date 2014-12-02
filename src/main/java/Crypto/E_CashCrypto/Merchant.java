package Crypto.E_CashCrypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class allows a merchant to accept a money order from a customer
 * Checks to insure bank signature is valid. If so asks customer for
 * One half of identity string and stores it to later give to the bank
 * Provides an option for the merchant to try and cheat the bank
 */

public class Merchant {
	static int test = -1;
	public void retrieveMoneyOrder(SignedMoneyOrder signedMoneyOrder, ArrayList<IdentityString> identityStrings, RSAKeyGeneration bankPublicKeys) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
		Merchant merchant = new Merchant();
		int choice;
		long customerIdentitySection;
		Scanner scanner = new Scanner(System.in);
		System.out.println();
		System.out.println("Welcome to TACO Cobana, one taco $" + signedMoneyOrder.getMoneyOrder().getMoneyOrderAmount());
		System.out.println("They will change your life!");
		System.out.print("Would you like one? 1 for yes 2 for no ==> ");
		choice = scanner.nextInt();
		
		if(choice == 1){
			int randomNIdentityString = 0;
			int randomSide = 0;
			test++;
			Random random = new Random();
			randomNIdentityString = random.nextInt(identityStrings.size());
			randomSide = random.nextInt(2);
			
			System.out.println();
			System.out.println("Great!, please show me " + randomNIdentityString + " of your IdentityList");
			System.out.println("And the " + randomSide + " side");
			
			if(merchant.checkSignatureAuthenticity(bankPublicKeys, signedMoneyOrder) == true){
				if(test == 1){
					System.out.println();
					System.out.println("Sure it here it is!");
					customerIdentitySection = identityStrings.get(randomNIdentityString).getRightHalf();
					System.out.println(identityStrings.get(randomNIdentityString).getRightHalf());
					System.out.println();
					System.out.println("O.. A money order let me verify signature");
					System.out.println("Signature looks valid");
					System.out.println("Thank you, here is your Taco!");
				}
				else{
					System.out.println();
					System.out.println("Sure it here it is!");
					customerIdentitySection = identityStrings.get(randomNIdentityString).getLeftHalf();
					System.out.println(identityStrings.get(randomNIdentityString).getLeftHalf());
					System.out.println();
					System.out.println("O.. A money order let me verify signature");
					System.out.println("Signature looks valid");
					System.out.println("Thank you, here is your Taco!");
				}
				merchant.giveMoneyOrderToBank(signedMoneyOrder, customerIdentitySection);
			}
			else{
				System.out.println("Sorry Money Order Authentication Failed...");
			}
		}
		else{
			System.out.println("Awwwww goodbye!");
		}
	}
	
	public boolean checkSignatureAuthenticity(RSAKeyGeneration bankPublicKeys, SignedMoneyOrder signedMoneyOrder) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException{
		boolean isSignatureValid = false;
		Signature signingEngine = Signature.getInstance("SHA1withRSA");
		
		isSignatureValid = signedMoneyOrder.getBankSignedObject().verify(bankPublicKeys.getPublicKey(), signingEngine);
		
		return isSignatureValid;
	}
	
	
	public void giveMoneyOrderToBank(SignedMoneyOrder signedMoneyOrder, long customerIdentitySection) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
		Bank bank = new Bank();
		bank.retrieveMoneyOrderFromMerchant(signedMoneyOrder, customerIdentitySection);
	}
	
	public void merchantTryToCheatBank(SignedMoneyOrder signedMoneyOrder, long customerIdentitySection, RSAKeyGeneration bankPublicKeys) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
		Bank bank = new Bank();
		Customer customer = new Customer();
		Scanner scanner = new Scanner(System.in);
		
		System.out.println();
		System.out.println("Well times have been rough.. No one wants to buy expensive tacos...");
		System.out.println("Should I (merchant) attempt to cash the money order again?");
		System.out.print("1 for yes 2 for no ==> ");
		int choice = scanner.nextInt();
		
		if(choice == 1){
			bank.retrieveMoneyOrderFromMerchant(signedMoneyOrder, customerIdentitySection);
		}
		else{
			customer.tryToCheatUseMoneyOrderTwice(signedMoneyOrder, bankPublicKeys);
		}
	}
	
}
