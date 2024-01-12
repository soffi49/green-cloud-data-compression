package org.greencloud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.greencloud.algorithms.*;

import static org.greencloud.algorithms.LZ77Algorithm.compress;
import static org.greencloud.algorithms.LZ77Algorithm.decompress;

/**
 * Engine which can be used to provide evaluation of created data compression methods
 */
public class DataCompressionEvaluator {

    public static void main(String[] args) {

        String folderPath = "./data";

        // Get a list of image files in the specified folder
        File folder = new File(folderPath);
        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (imageFiles != null && imageFiles.length > 0) {
            // Create instances of compression algorithms
            HuffmanAlgorithm huffmanAlgorithm = new HuffmanAlgorithm();
            JPEGAlgorithm jpegAlgorithm = new JPEGAlgorithm(0.8f);
            GzipAlgorithm gzipAlgorithm = new GzipAlgorithm();
			ZipAlgorithm zipAlgorithm = new ZipAlgorithm();
            TIFFAlgorithm tiffAlgorithm = new TIFFAlgorithm();
            LZWAlgorithm lzwAlgorithm = new LZWAlgorithm(12);


            // Evaluate compression and decompression time for each image
            for (File imageFile : imageFiles) {
                try {
                    byte[] originalData = Files.readAllBytes(imageFile.toPath());

                    // Huffman Compression
                    evaluateAlgorithm(huffmanAlgorithm, originalData, imageFile.getName(), "Huffman");

                    // JPEG Compression
                    evaluateAlgorithm(jpegAlgorithm, originalData, imageFile.getName(), "JPEG");

                    // GZIP Compression
                    evaluateAlgorithm(gzipAlgorithm, originalData, imageFile.getName(), "GZIP");

					// ZIP Compression
					evaluateAlgorithm(zipAlgorithm, originalData, imageFile.getName(), "ZIP");

                    // TIFF Compression
                    evaluateAlgorithm(tiffAlgorithm, originalData, imageFile.getName(), "TIFF");

                    // LZW Compression
					// TODO Fix
                    // evaluateAlgorithm(lzwAlgorithm, originalData, imageFile.getName(), "LZW");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No image files found in the specified folder.");
        }
    }

    private static void evaluateAlgorithm(Algorithm algorithm, byte[] originalData, String fileName, String algorithmName) {
        try {
            // Compression
            long compressionStartTime = System.currentTimeMillis();
            byte[] compressedData = algorithm.compress(originalData);
            long compressionEndTime = System.currentTimeMillis();
            long compressionTime = compressionEndTime - compressionStartTime;

            // Decompression
            long decompressionStartTime = System.currentTimeMillis();
            byte[] decompressedData = algorithm.decompress(compressedData);
            long decompressionEndTime = System.currentTimeMillis();
            long decompressionTime = decompressionEndTime - decompressionStartTime;

            // Validate if decompressed data matches the original data
            boolean lossless = Arrays.equals(originalData, decompressedData);

            // Display results
            System.out.println("Image: " + fileName);
            System.out.println("Algorithm: " + algorithmName);
            System.out.println("Compression Time: " + compressionTime + " ms");
            System.out.println("Decompression Time: " + decompressionTime + " ms");
			System.out.println("Compressed size percentage: %" + (float)compressedData.length / (float)originalData.length * 10);
			System.out.println("Lossless: " + lossless);
            System.out.println("Percentage difference: " + calculateDifferencePercentage(originalData, decompressedData));
            System.out.println("------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static float calculateDifferencePercentage(byte[] array1, byte[] array2) {
		if (array1.length != array2.length) {
			System.out.println("[WARN] Accuracy of unequal length arrays");
		}

		int differingBytes = 0;

		for (int i = 0; i < Math.max(array1.length, array2.length); i++) {
			byte arr1 =  i < array1.length ? array1[i] : array2[i];
			byte arr2 =  i < array2.length ? array2[i] : array1[i];
			if (arr1 != arr2 || i >= array1.length || i >= array2.length) {
				differingBytes++;
			}
		}

		float differencePercentage = (float) differingBytes / Math.max(array1.length, array2.length) * 100;
		return differencePercentage;
	}
}
