package org.greencloud.algorithms;

import java.io.ByteArrayOutputStream;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.io.IOException;

public class HuffmanAlgorithm extends Algorithm {
	private HashMap<Integer, String> charPrefixHashMap = new HashMap<>();
	private SimpleCharPriorityQueueNode rootNode;

	public byte[] compress(byte[] input) throws IOException {
		int[] charFreqs = new int[256];
		for (byte b : input)
			charFreqs[b & 0xFF]++; // Convert byte to unsigned int
		rootNode = buildTree(charFreqs);

		setPrefixCodes(rootNode, new StringBuilder());
		StringBuilder sBuilder = new StringBuilder();
		for (byte b : input)
			sBuilder.append(charPrefixHashMap.get(b & 0xFF)); // Convert byte to unsigned int
		return sBuilder.toString().getBytes();
	}

	public byte[] decompress(byte[] compressedData) throws IOException {
		String s = new String(compressedData);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SimpleCharPriorityQueueNode tempNode = rootNode;
		for (int i = 0; i < s.length(); i++) {
			int j = Integer.parseInt(String.valueOf(s.charAt(i)));
			if (j == 0)
				tempNode = tempNode.left;
			else
				tempNode = tempNode.right;

			if (tempNode.left == null && tempNode.right == null) {
				baos.write(tempNode.character); // Write character as byte
				tempNode = rootNode;
			}
		}
		return baos.toByteArray();
	}

	private SimpleCharPriorityQueueNode buildTree(int[] charFreqs) {
		PriorityQueue<SimpleCharPriorityQueueNode> priorityQueue = new PriorityQueue<>();
		for (int i = 0; i < charFreqs.length; i++)
			if (charFreqs[i] > 0)
				priorityQueue.offer(new SimpleCharPriorityQueueNode((char) i, charFreqs[i], null, null));

		while (priorityQueue.size() > 1) {
			SimpleCharPriorityQueueNode left = priorityQueue.poll();
			SimpleCharPriorityQueueNode right = priorityQueue.poll();
			SimpleCharPriorityQueueNode parent = new SimpleCharPriorityQueueNode('\0', left.frequency + right.frequency, left, right);
			priorityQueue.offer(parent);
		}
		return priorityQueue.poll();
	}

	private void setPrefixCodes(SimpleCharPriorityQueueNode node, StringBuilder prefix) {
		if (node != null) {
			if (node.left == null && node.right == null)
				charPrefixHashMap.put(node.character, prefix.toString());

			prefix.append('0');
			setPrefixCodes(node.left, prefix);
			prefix.deleteCharAt(prefix.length() - 1);

			prefix.append('1');
			setPrefixCodes(node.right, prefix);
			prefix.deleteCharAt(prefix.length() - 1);
		}
	}
}

class SimpleCharPriorityQueueNode implements Comparable<SimpleCharPriorityQueueNode> {
	int character; // Change char to int
	int frequency;
	SimpleCharPriorityQueueNode left = null, right = null;

	SimpleCharPriorityQueueNode(int character, int frequency, SimpleCharPriorityQueueNode left, SimpleCharPriorityQueueNode right) {
		this.character = character;
		this.frequency = frequency;
		this.left = left;
		this.right = right;
	}

	public int compareTo(SimpleCharPriorityQueueNode node) {
		return frequency - node.frequency;
	}
}
