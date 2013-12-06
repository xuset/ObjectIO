package objectIO.connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import objectIO.markupMsg.MarkupMsg;

public class FileCon extends Connection{
	private static final Charset charset = Charset.forName("US-ASCII");
	private static final char[] newLine = { 13 };

	private final BufferedReader reader;
	private final BufferedWriter writer;
	
	public FileCon(String path) throws IOException {
		this(new File(path));
	}
	
	public FileCon(File f) throws IOException {
		Path path = f.toPath();
		
		if (f.isFile()) {
			reader = Files.newBufferedReader(path, charset);
		} else
			reader = null;

		writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
	}
	
	public boolean flush() {
		try {
			writer.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void close() {
		try {
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean sendMsg(MarkupMsg msg) {
		try {
			writer.write(msg.toString());
			writer.write(newLine);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean readNextLine() {
		try {
			String s = reader.readLine();
			if (s != null)
				return msgQueue().add(new MarkupMsg(s));
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void readAllLines() {
		while (readNextLine()) { }
	}

}
