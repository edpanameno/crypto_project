// By Dr. Ramirez
// Demonstration of the MD5 algorithm in Java.  See also MessageDigest
// in the Java API for more information
import java.security.*;

public class MD {
	
	public static void main(String[] args) throws Exception {
		
		String S1 = new String("Here is the original string!  Cool!");
		String S2 = new String("Here is the original string!  Cool!");
		String S3 = new String("Here is che original string!  Cool!");

		MessageDigest m1 = MessageDigest.getInstance("MD5");
		MessageDigest m2 = MessageDigest.getInstance("MD5");
		MessageDigest m3 = MessageDigest.getInstance("MD5");

		byte[] b1 = S1.getBytes();
		byte[] b2 = S2.getBytes();
		byte[] b3 = S3.getBytes();

		m1.update(b1);
		m2.update(b2);
		m3.update(b3);

		byte[] digest1 = m1.digest();
		byte[] digest2 = m2.digest();
		byte[] digest3 = m3.digest();

		System.out.println(digest1.length);

		if (MessageDigest.isEqual(digest1, digest2))
			System.out.println("Equal");
		if (MessageDigest.isEqual(digest1, digest3))
			System.out.println("Equal");
		else
			System.out.println("Not equal");
	}
}
