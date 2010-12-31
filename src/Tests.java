import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Tests {

	private static final int NUMBER_OF_BITS = 512;

	public static void main(String[] args) throws FileNotFoundException, IOException, NoSuchAlgorithmException, ClassNotFoundException {
		
		//firstTests();
		//KeyGen.generateData();
		//DigitalSignature.signAndSendFile("test.txt", "privkey.rsa");
		DigitalSignature.hasFileBeenModified("test.txt.signed", "pubkey.rsa");
		//readKeys();
	}
	
	private static void readKeys() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		BigInteger e, d, nPriv, nPub;
		
		ObjectInputStream privKey = new ObjectInputStream(new FileInputStream("privkey.rsa"));
		ObjectInputStream pubKey = new ObjectInputStream(new FileInputStream("pubkey.rsa"));
	
		e = (BigInteger)privKey.readObject();
		nPriv = (BigInteger)privKey.readObject();
		privKey.close();
		
		d = (BigInteger)pubKey.readObject();
		nPub = (BigInteger)pubKey.readObject();
		pubKey.close();
		
		System.out.println("private key stuff");
		System.out.println("e: " + e);
		System.out.println("nPriv: " + nPriv);
		
		System.out.println("public key stuff");
		System.out.println("d: " + d);
		System.out.println("nPub: " + nPub);
		
		System.out.println("are nPub and nPriv the same: " + nPriv.equals(nPub));
	}

	private static void firstTests() {
		
		BigInteger p = BigInteger.probablePrime(NUMBER_OF_BITS, new Random());
		BigInteger q = BigInteger.probablePrime(NUMBER_OF_BITS, new Random());
		BigInteger n = p.multiply(q);
		BigInteger phiOfN = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));

		Random r = new Random();
		
		BigInteger e;
		Integer randomInt;
		
		// We need to make sure that whatever number we 
		// randomly generate has gcd(phiOfN, e) = 1
		while(true) {
			
			int rInt = r.nextInt(NUMBER_OF_BITS);			
			randomInt = new Integer(rInt);
			
			// This should allow e to always be greater than 1
			// which is a requirement for this assignment
			e = phiOfN.subtract(new BigInteger(randomInt.toString()));
		
			// If the gcd(phiOfN,e) = 1, then break out
			// of this while loop. This means that we have 
			// generated a value of e that is acceptable.
			// If not continue with the next randomly generated 
			// number
			if(phiOfN.gcd(e).equals(BigInteger.ONE)) {
				break;
			}
				
		}
		
		System.out.println("e: " + e);
		System.out.println("number of bits in e: " + e.bitCount());
		System.out.println("phiOfN: " + phiOfN);
		System.out.println("number of bits in phiOfN: " + phiOfN.bitCount());
		System.out.println("gcd(phiOfN,e) = " + phiOfN.gcd(e));
		
		BigInteger d = e.modInverse(phiOfN);
	
		System.out.println("d: " + d);
		System.out.println("mod(e*d,phiOfN): " + e.multiply(d).mod(phiOfN));
	}
}
