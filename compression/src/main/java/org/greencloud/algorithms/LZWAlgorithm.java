package org.greencloud.algorithms;

import java.io.*;
import java.util.*;

import com.github.jaiimageio.impl.common.LZWCompressor;

import org.apache.commons.compress.compressors.lzw.*;
import java.io.*;

public class LZWAlgorithm extends Algorithm {

	public int bitLength;
	public int tableMaxSize;

	public LZWAlgorithm(int bitLength) {
		this.bitLength = bitLength;
		this.tableMaxSize = (int) Math.pow(2, bitLength);
	}

	@Override
	public byte[] compress(byte[] inputData) {
		Hashtable<String, Integer> table = new Hashtable<>(tableMaxSize);
		for (int i = 0; i < 256; i++) {
			String key = String.valueOf((char) i);
			table.put(key, i);
		}

		ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
		String string = "";
		int value = 256;

		try {
			for (int r : inputData) {
				if (table.containsKey(string + (char) r)) {
					string = string.concat(String.valueOf((char) r));
				} else {
					compressedStream.write(table.get(string));
					compressedStream.flush();
					if (table.size() < tableMaxSize) {
						string = string.concat(String.valueOf((char) r));
						table.put(string, value++);
						string = String.valueOf((char) r);
					} else {
						System.out.println("Table limit reached! Exiting!");
						return compressedStream.toByteArray();
					}
				}
			}
			compressedStream.write(table.get(string));
			compressedStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return compressedStream.toByteArray();
	}

	@Override
	public byte[] decompress(byte[] compressedData) {
		Hashtable<Integer, String> table = new Hashtable<>(tableMaxSize);
		for (int i = 0; i < 256; i++) {
			String value = String.valueOf((char) i);
			table.put(i, value);
		}

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
			 InputStreamReader reader = new InputStreamReader(inputStream);
			 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			 Writer outWriter = new OutputStreamWriter(outputStream)) {

			int keys = 255;
			int code = reader.read();
			String string = table.get(code);
			outWriter.write(string);
			outWriter.flush();

			while ((code = reader.read()) != -1) {
				String newString = "";
				if (!table.containsKey(code)) {
					newString = newString.concat(string + string.charAt(0));
				} else {
					newString = table.get(code);
				}
				outWriter.write(newString);
				outWriter.flush();
				if (table.size() < tableMaxSize) {
					table.put(++keys, string.concat(String.valueOf(newString.charAt(0))));
				}
				string = newString;
			}

			return outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

//	@Override
//	public byte[] compress(byte[] data) throws IOException {
//		Map<String, Integer> dictionary = new HashMap<>();
//		for (int i = -127; i <= 128; i++) {
//			dictionary.put("" + (char)i, i);
//		}
//
//		String currentString = "";
//		List<Integer> compressedData = new ArrayList<>();
//
//		for (byte b : data) {
//			String currentStringPlusChar = currentString + (char)b;
//			if (dictionary.containsKey(currentStringPlusChar)) {
//				currentString = currentStringPlusChar;
//			} else {
//				if (dictionary.containsKey(currentString)) {
//					compressedData.add(dictionary.get(currentString));
//				}
//				if (dictionary.size() < MAX_TABLE_SIZE) {
//					dictionary.put(currentStringPlusChar, dictionary.size());
//				}
//				currentString = "" + (char)b;
//			}
//		}
//
//		if (!currentString.equals("") && dictionary.containsKey(currentString)) {
//			compressedData.add(dictionary.get(currentString));
//		}
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		DataOutputStream dos = new DataOutputStream(baos);
//		for (int i : compressedData) {
//			dos.writeByte(i);
//		}
//		dos.close();
//		return baos.toByteArray();
//	}
//
//	@Override
//	public byte[] decompress(byte[] compressedData) throws IOException {
//		Map<Integer, String> dictionary = new HashMap<>();
//		for (int i = -127; i <= 128; i++) {
//			dictionary.put(i, "" + (char)i);
//		}
//
//		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
//		DataInputStream dis = new DataInputStream(bais);
//		List<Integer> compressedDataList = new ArrayList<>();
//		while (dis.available() > 0) {
//			compressedDataList.add((int)dis.readByte());
//		}
//		dis.close();
//
//		int previous = compressedDataList.remove(0);
//		StringBuilder decompressedData = new StringBuilder(dictionary.get(previous));
//
//		for (int current : compressedDataList) {
//			String s;
//			if (dictionary.containsKey(current)) {
//				s = dictionary.get(current);
//			} else if (current == dictionary.size()) {
//				s = dictionary.get(previous) + dictionary.get(previous).charAt(0);
//			} else {
//				throw new IllegalArgumentException("Bad compressed data");
//			}
//
//			decompressedData.append(s);
//
//			if (dictionary.size() < MAX_TABLE_SIZE) {
//				dictionary.put(dictionary.size(), dictionary.get(previous) + s.charAt(0));
//			}
//
//			previous = current;
//		}
//
//		return decompressedData.toString().getBytes();
//	}
}
