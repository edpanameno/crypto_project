import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Random;

public class KeyGen {

	private static final int NUMBER_OF_BITS = 512;
	private static final String PRIVATE_KEY_FILE = "privkey.rsa";
	private static final String PUBLIC_KEY_FILE = "pubkey.rsa";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		KeyGen.generateData();
	}
	
	/*
	 * This method will generate the data required for the public
	 * and private keys.  The data will then be saved into two files.
	 */
	public static void generateData() throws FileNotFoundException, IOException {
		
		// Need to make sure that these numbers are prime
		BigInteger p = BigInteger.probablePrime(NUMBER_OF_BITS, new Random());
		BigInteger q = BigInteger.probablePrime(NUMBER_OF_BITS, new Random());
		
		// Check to make sure that both p and q are prime numbers
		while(true) {
			
			if(p.isProbablePrime(10) && q.isProbablePrime(10)) {
				
				// If both p and q are prime, we have the two
				// required numbers.
				break;
			}
			else {
				
				// Generating a new set of random BigIntegers which 
				// will be tested to make sure they are prime.
				p = BigInteger.probablePrime(NUMBER_OF_BITS, new Random());
				q = BigInteger.probablePrime(NUMBER_OF_BITS, new Random());
			}
		}
		
		BigInteger n = p.multiply(q);
		
		// phiOfN = (p-1)x(q-1)
		BigInteger phiOfN = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));
		
		BigInteger e;
		Integer randomInt;
		Random r = new Random();
		
		// Pick e to be random prime, where 1 < e < phiOfN
		// and gcd(e, phiOfN) = 1. The while loop will make 
		// sure that the value generated gives the correct
		// e value, it then exits the loop.
		while(true) {
			
			int rInt = r.nextInt(NUMBER_OF_BITS) + 1;			
			randomInt = new Integer(rInt);
			
			// This should allow e to always be greater than 1
			// which is a requirement for this assignment
			e = phiOfN.subtract(new BigInteger(randomInt.toString()));
		
			// If the gcd(phiOfN,e) = 1, then break out
			// of this while loop. This means that we have 
			// generated a value of e that is acceptable.
			// If not continue with the next randomly generated 
			// number.
			if(phiOfN.gcd(e).equals(BigInteger.ONE)) {
				break;
			}
				
		}
		
		BigInteger d = e.modInverse(phiOfN);
		
		System.out.println("Value of e: ");
		System.out.println(e);
		System.out.println();
		
		System.out.println("Value of d: ");
		System.out.println(d);
		System.out.println();
		
		System.out.println("Value of n:");
		System.out.println(n);
		System.out.println();
	
		System.out.print("Outputing d and n to privkey.rsa ... ");
		ObjectOutputStream privKey = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
		privKey.writeObject(d.toString()); 
		privKey.writeObject(n.toString()); 
		privKey.flush();
		privKey.close();
		System.out.println("done!");
		
		System.out.print("Outputing e and n to pubkey.rsa ... ");
		ObjectOutputStream pubKey = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
		pubKey.writeObject(e.toString());
		pubKey.writeObject(n.toString());
		pubKey.flush();
		pubKey.close();
		System.out.println("done!");
	}
}

