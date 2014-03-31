package net.xuset.objectIO.connections;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;


/**
 * Connection that can be used to send and receive MarkupMsg to and from a file. Sending
 * a message writes the contents of the message to file, and the file is read to receive
 * a message. Buffered streams are used for I/O. Calling {@code flush()} is sometimes
 * needed before data is actually written to the disk.
 * 
 * @author xuset
 * @since 1.0
 */

public class FileCon extends StreamCon {
	
	/**
	 * Constructs a FileCon object.
	 * This constructor calls the FileCon(File, id) constructor after it creates
	 * the file object.
	 * 
	 * @param filePath the path to the file
	 * @param id the id of the connection
	 * @throws FileNotFoundException if the file is not found
	 */
	public FileCon(String filePath, long id) throws FileNotFoundException {
		this(new File(filePath), id);
	}
	
	
	/**
	 * Constructs a FileCon object. The I/O streams are created using the specified
	 * file. The I/O streams are buffered streams.
	 * 
	 * @param file specifies the file for the connection to use
	 * @param id the id of the connection
	 * @throws FileNotFoundException if the file is not found
	 */
	public FileCon(File file, long id) throws FileNotFoundException {
		super(
				new BufferedInputStream(new FileInputStream(file)),
				new BufferedOutputStream(new FileOutputStream(file)),
				id);
	}
	
	
	/**
	 * Creates a new FileCon object for the given file and id.
	 * This method differs in the FileCon constructors in that it does not throw
	 * a FileNotFoundException. If the file is not found an IllegalArgumentException
	 * is thrown instead.
	 * 
	 * @param file used for constructing the FileCon object
	 * @param id the id of the connection
	 * @return the newly created FileCon object
	 * @throws IllegalArgumentException if file is null or does not exist.
	 */
	public static FileCon openFile(File file, long id) {
		if (file == null)
			throw new IllegalArgumentException("File cannot be null");
		if (!file.isFile())
			throw new IllegalArgumentException("The file must exist");
		
		try {
			FileCon con = new FileCon(file, id);
			return con;
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The file must exist");
		}
	}
}