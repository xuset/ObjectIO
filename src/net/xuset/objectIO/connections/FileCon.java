package net.xuset.objectIO.connections;

import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import net.xuset.objectIO.connections.Connection;
import net.xuset.objectIO.markupMsg.MarkupMsg;



public class FileCon extends Connection{
	private static final char[] newLine = { 13 };

	private final BufferedReader reader;
	private final BufferedWriter writer;
	
	public FileCon(String path) throws IOException {
		this(new File(path));
	}
	
	public FileCon(File f) throws IOException {
		if (f.isFile()) {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		} else
			reader = null;

		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
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
			if (s != null) {
				MarkupMsg msg = new MarkupMsg(s);
				if (msg.parsedProperly())
					msgQueue().add(msg);
				return true;
			}
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