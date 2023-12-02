package utils;

// import statements  
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

// A Java program to find the SHA-256 hash value  
public class Hashing {
	public static byte[] obtainSHA(String s) throws NoSuchAlgorithmException{
		// Static getInstance() method is invoked with the hashing SHA-256
		MessageDigest msgDgst = MessageDigest.getInstance("SHA-256");

		// the digest() method is invoked to compute the message digest of the input
		// and returns an array of byte
		return msgDgst.digest(s.getBytes(StandardCharsets.UTF_8));
	}

	public static String toHexStr(byte[] hash) {
		// Converting the byte array in the signum representation
		BigInteger no = new BigInteger(1, hash);

		// Converting the message digest into the hex value
		StringBuilder hexStr = new StringBuilder(no.toString(16));

		// Padding with tbe leading zeros
		while (hexStr.length() < 32) {
			hexStr.insert(0, '0');
		}

		return hexStr.toString();
	}

	public static String digestString(String plainText)  throws NoSuchAlgorithmException{
		return toHexStr(obtainSHA(plainText));
	}
}