package Crypto.E_CashCrypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Merchant {
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
			Random random = new Random();
			randomNIdentityString = random.nextInt(identityStrings.size());
			randomSide = random.nextInt(2);
			System.out.println();
			System.out.println("Great!, please show me " + randomNIdentityString + " of your IdentityList");
			System.out.println("And the " + randomSide + " side");
			
			if(merchant.checkSignatureAuthenticity(bankPublicKeys, signedMoneyOrder) == true){
				if(randomSide == 1){
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
	
}
