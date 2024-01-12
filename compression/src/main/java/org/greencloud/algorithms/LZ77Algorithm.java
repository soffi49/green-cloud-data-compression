package org.greencloud.algorithms;

import java.util.ArrayList;
import java.util.List;

public class LZ77Algorithm {

	public static List<LZ77Token> compress(byte[] input) {
		List<LZ77Token> compressedTokens = new ArrayList<>();

		int windowSize = 256; // Adjust window size as needed
		int lookAheadBufferSize = 16; // Adjust look-ahead buffer size as needed

		int currentIndex = 0;

		while (currentIndex < input.length) {
			int matchIndex = findLongestMatch(input, currentIndex, windowSize, lookAheadBufferSize);

			if (matchIndex != -1) {
				int distance = currentIndex - matchIndex;
				int length = Math.min(lookAheadBufferSize, input.length - currentIndex);
				compressedTokens.add(new LZ77Token(distance, length, input[currentIndex + length]));

				currentIndex += length + 1;
			} else {
				compressedTokens.add(new LZ77Token(0, 0, input[currentIndex]));
				currentIndex++;
			}
		}

		return compressedTokens;
	}

	public static byte[] decompress(List<LZ77Token> compressedTokens) {
		List<Byte> decompressedBytes = new ArrayList<>();

		for (LZ77Token token : compressedTokens) {
			if (token.getDistance() == 0 && token.getLength() == 0) {
				decompressedBytes.add(token.getLiteral());
			} else {
				int startIndex = decompressedBytes.size() - token.getDistance();
				int endIndex = startIndex + token.getLength();

				for (int i = startIndex; i < endIndex; i++) {
					decompressedBytes.add(decompressedBytes.get(i));
				}

				decompressedBytes.add(token.getLiteral());
			}
		}

		byte[] result = new byte[decompressedBytes.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = decompressedBytes.get(i);
		}

		return result;
	}

	private static int findLongestMatch(byte[] input, int currentIndex, int windowSize, int lookAheadBufferSize) {
		int bestMatchIndex = -1;
		int maxLength = 0;

		int startSearchIndex = Math.max(0, currentIndex - windowSize);
		int endSearchIndex = Math.min(currentIndex + lookAheadBufferSize, input.length);

		for (int i = startSearchIndex; i < currentIndex; i++) {
			int currentLength = 0;

			while (currentLength < lookAheadBufferSize && currentIndex + currentLength < input.length
					&& input[i + currentLength] == input[currentIndex + currentLength]) {
				currentLength++;
			}

			if (currentLength > maxLength) {
				maxLength = currentLength;
				bestMatchIndex = i;
			}
		}

		return bestMatchIndex;
	}

	public static class LZ77Token {
		private final int distance;
		private final int length;
		private final byte literal;

		public LZ77Token(int distance, int length, byte literal) {
			this.distance = distance;
			this.length = length;
			this.literal = literal;
		}

		public int getDistance() {
			return distance;
		}

		public int getLength() {
			return length;
		}

		public byte getLiteral() {
			return literal;
		}
	}
}
