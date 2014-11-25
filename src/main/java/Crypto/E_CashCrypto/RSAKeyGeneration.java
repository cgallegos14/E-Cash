package Crypto.E_CashCrypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAKeyGeneration {
	private PublicKey publicKey; 
	private PrivateKey privateKey; 
	
	/*
	public RSAKeyGeneration() throws NoSuchAlgorithmException{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024);
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		publicKey =  keyPair.getPublic();
		privateKey = keyPair.getPrivate();
	}
	*/
	
	public PublicKey getPublicKey(){
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public PrivateKey getPrivateKey(){
		return privateKey;
	}
}
