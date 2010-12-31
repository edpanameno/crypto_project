import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigitalSignature {

	/**
	 * This method will sign and send a File.
	 * @param fileName The file to sign. Note: this must be the fully
	 * qualified file name
	 * @param privateKeyFile The file where the private key is located in
	 * @throws NoSuchAlgorithmException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void signAndSendFile(String fileName, String privateKeyFile) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {

		// I am using a File object to get the number of bytes that
		// the file has.  This number of bytes will then be used to
		// create the byte array that will hold it's content.
		File file = new File(fileName);
		byte[] fileBytes = new byte[(int)file.length()];
		FileInputStream originalFile = new FileInputStream(file);
		
		try {
			// Reads the content of the file into 
			// the byte[] fileBytes object
			originalFile.read(fileBytes);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		originalFile.close();
	
		// Get Digest of file byte array
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(fileBytes);
		byte[] fileDigest = md.digest();
		
		// Get Private Key file
		ObjectInputStream publicKey = null;
		BigInteger d, n;
		try {
			publicKey = new ObjectInputStream(new FileInputStream(privateKeyFile));
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
	
		// Get the BigIntegers from the private key file
		d = new BigInteger((String)publicKey.readObject());
		n = new BigInteger((String)publicKey.readObject());
		
		publicKey.close();
		
		// Encrypt the plain text, but first we must convert the 
		// byte array of the file into a big integer.  I am using 
		// the sign-magnitude constructor (1 will generate a positive
		// BigInteger)
		BigInteger p = new BigInteger(1, fileDigest);
		
		// Encrypting using RSA
		BigInteger signature = p.modPow(d,n);
		
		String signedFileName = fileName + ".signed";
		
		ObjectOutputStream outputFile = new ObjectOutputStream(new FileOutputStream(signedFileName));
		outputFile.writeObject(signature.toString());
		outputFile.write(fileBytes);
		outputFile.flush();
		outputFile.close();
	}
	
	/**
	 * This method checks to see if the file sent was modified
	 * before being received. If the file has been modified,
	 * a message will show up stating that it has been tampered.
	 * @param receivedFilePath
	 * @param publicKeyFilePath
	 * @return True if file has been modified, False otherwise
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static boolean hasFileBeenModified(String receivedFilePath, String publicKeyFilePath) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
	
		// Get the contents of the private key
		ObjectInputStream publicKeyFile = null;
		BigInteger e, n;
		
		try {
			publicKeyFile = new ObjectInputStream(new FileInputStream(publicKeyFilePath));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	
		// Get the keys from the public key
		e = new BigInteger((String)publicKeyFile.readObject());
		n = new BigInteger((String)publicKeyFile.readObject());
		
		publicKeyFile.close();
		
		ObjectInputStream sentFile = null;
	
		try {
			
			// Get the sent file to read the signature and
			// the message
			sentFile = new ObjectInputStream(new FileInputStream(receivedFilePath));
	
			// As per the requirement of the assignment, if the change that
			// was made to the file was within the BigInteger object section
			// in the sent file, this will result in an Exception.  This means that
			// the file has been modified, and as a result this method
			// should return false.	If this fails, then the modification has 
			// been made to the BigInteger object section of the file,
			// if so this should return true;
			BigInteger signature = new BigInteger((String)sentFile.readObject());
		
			// At this point, we have verified that no modification has
			// been made to the BigInteger portion of the signed message.
			// What I am doing in here is reading the remaining bytes
			// into a byte array. I am using the available() method 
			// in the ObjectInputStream object to find out the length
			// of the array. I then use the read method (which takes a
			// byte array) to store the content. 
			byte[] fileByteArray = new byte[sentFile.available()];
			sentFile.read(fileByteArray);
			
			sentFile.close();	
			
			// De-crypting the signature using RSA
			BigInteger unencryptedSignature = signature.modPow(e,n);
			
			// Just like we did before, we have to get the digest of the
			// text portion of the sent message.
			MessageDigest textDigest = MessageDigest.getInstance("MD5");
			textDigest.update(fileByteArray);
			byte[] fByteArray = textDigest.digest();
			BigInteger fileByteArrayInteger = new BigInteger(1, fByteArray);
			
			MessageDigest m1 = MessageDigest.getInstance("MD5");
			MessageDigest m2 = MessageDigest.getInstance("MD5");
			
			m1.update(unencryptedSignature.toByteArray());
			m2.update(fileByteArrayInteger.toByteArray());

			// Now we are going to check to see if there has been
			// a modification to the signature or to the text message.
			// If no modification has been made to either, then this
			// method will return false (i.e. no changes have bene made)
			// Other wise, if a change has been made to either the 
			// signature or the text message (or both) then this will 
			// return true (a.k.a they are not the same).
			return (!MessageDigest.isEqual(m1.digest(), m2.digest()));
		}
		catch(StreamCorruptedException streamException) {
			System.out.println("StreamCorruptedException section ...");
			return true;
		}
		catch(Exception exc) {

			// If the BigInteger part of the message has been
			// changed, then this should return true.
			//sentFile.close();
		
			System.out.println("General Exception section ...");
			return true;
		}	
	}
}
