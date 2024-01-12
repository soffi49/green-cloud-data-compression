package	org.greencloud.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    /**
     * Compresses the given byte array using the specific compression algorithm.
     *
     * @param data The byte array to be compressed
     * @return Compressed binary data
     * @throws IOException If an I/O error occurs during compression
     */
    public byte[] compress(byte[] data) throws IOException {
		return data;
	}

    /**
     * Decompresses the given compressed binary data using the specific decompression algorithm.
     *
     * @param compressedData The compressed binary data to be decompressed
     * @return Decompressed byte array
     * @throws IOException If an I/O error occurs during decompression
     */
    public byte[] decompress(byte[] compressedData) throws IOException {
		return compressedData;
	}

	/**
	 * Converts a binary string to a byte array.
	 *
	 * @param s The binary string to be converted
	 * @return The resulting byte array
	 */
	protected static byte[] toByteArray(String s) {
		byte[] result = new byte[s.length() / 8];
		for (int i = 8; i < s.length(); i += 8) {

			String byteString = s.substring(i - 8, i);
			result[i / 8 - 1] = (byte) Integer.parseInt(byteString, 2);
		}
		return result;
	}

	/**
	 * Converts a byte array to a binary string.
	 *
	 * @param bytes The byte array to be converted
	 * @return The resulting binary string
	 */
	protected static String fromByteArray(byte[] bytes) {
		StringBuilder binaryString = new StringBuilder();
		for (byte b : bytes) {
			binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		return binaryString.toString();
	}

	/**
	 * Converts a list of integers to a byte array.
	 *
	 * @param intList The list of integers to be converted
	 * @return The resulting byte array
	 */
	protected static byte[] intListToByteArray(List<Integer> intList) {
		byte[] result = new byte[intList.size()];
		for (int i = 0; i < intList.size(); i++) {
			result[i] = intList.get(i).byteValue();
		}
		return result;
	}

	/**
	 * Converts an array of integers to a byte array.
	 *
	 * @param intList The list of integers to be converted
	 * @return The resulting byte array
	 */
	protected static byte[] intArrayToByteArray(int[] intList) {
		byte[] result = new byte[intList.length];
		for (int i = 0; i < intList.length; i++) {
			Integer tmp = intList[i];
			result[i] = tmp.byteValue();
		}
		return result;
	}

	/**
	 * Converts a byte array to a list of integers.
	 *
	 * @param byteArray The byte array to be converted
	 * @return The resulting list of integers
	 */
	protected static List<Integer> byteArrayToIntList(byte[] byteArray) {
		List<Integer> intList = new ArrayList<>();
		for (byte b : byteArray) {
			intList.add((int) b & 0xFF);
		}
		return intList;
	}
}
