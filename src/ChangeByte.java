import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class ChangeByte {

	/**
	 * This method will be used to change the file that was
	 * sent.  If the modification was successfully, then this
	 * method will return true.  If the modification was not
	 * successful this method will return false.
	 * @param fileName Full Path of the file to change
	 * @param indexToChange The index of the file to change
	 * @throws IOException 
	 */
	public static void modifyFile(String fileName, int indexToChange) throws IOException {

		// This random object will be used to generate 
		// a random number that will be written to the file
		Random r = new Random();
		
		// Creating a RandomAccessFile object (with read/write) 
		// to be able to skip bytes (i.e. change the file pointer)
		// to the index where the user wants a random value to 
		// be written to.
		RandomAccessFile file = new RandomAccessFile(fileName, "rw");
		
		// This changes the current file pointer, I am going to place
		// this at the index that will be the location to change to
		// a random value.
		if(indexToChange != 0) {
			file.skipBytes(indexToChange - 1); 
		}
		
		// Writing a random integer between 1 and 26
		file.write(r.nextInt() + 26);
		file.close();
	}
}
