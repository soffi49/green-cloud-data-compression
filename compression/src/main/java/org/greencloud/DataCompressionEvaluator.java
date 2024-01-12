package org.greencloud;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import javax.imageio.ImageIO;


import ij.ImageJ;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

import org.greencloud.algorithms.Algorithm;
import org.greencloud.algorithms.GzipAlgorithm;
import org.greencloud.algorithms.HuffmanAlgorithm;
import org.greencloud.algorithms.JPEGAlgorithm;
import org.greencloud.algorithms.LZWAlgorithm;
import org.greencloud.algorithms.TIFFAlgorithm;
import org.greencloud.algorithms.ZipAlgorithm;


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
			//evaluateAlgorithm(huffmanAlgorithm, imageFiles, "Huffman");
			//evaluateAlgorithm(jpegAlgorithm, imageFiles, "JPEG");
			//evaluateAlgorithm(gzipAlgorithm, imageFiles, "GZIP");
			//evaluateAlgorithm(zipAlgorithm, imageFiles, "ZIP");
			evaluateAlgorithm(tiffAlgorithm, imageFiles, "TIFF");
			// LZW Compression
			// TODO Fix
			// evaluateAlgorithm(lzwAlgorithm, imageFiles, "LZW");
		} else {
			System.out.println("No image files found in the specified folder.");
		}
	}

	private static void evaluateAlgorithm(Algorithm algorithm, File[] imageFiles, String algorithmName) {
		long totalCompressionTime = 0;
		long totalDecompressionTime = 0;
		float totalCompressedSizePercentage = 0;
		float totalPSNR = 0;
		float totalSSIM = 0;
		int totalImages = imageFiles.length;

		int i = 0;
		for (File imageFile : imageFiles) {
			try {
				byte[] originalData = Files.readAllBytes(imageFile.toPath());

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

				// Collect statistics
				totalCompressionTime += compressionTime;
				totalDecompressionTime += decompressionTime;
				totalCompressedSizePercentage += ((float) originalData.length * 10 / (float) compressedData.length); // * 100

				double psnr = calculatePSNR(originalData, decompressedData);
				totalPSNR += psnr;

				// Calculate SSIM
				// double ssim = calculateSSIM2(originalData, decompressedData);
				// totalSSIM += ssim;

				if(i == 0 && algorithmName.equals("JPEG")) {
					HistogramPlotter p1 = new HistogramPlotter("Test1", originalData);
					HistogramPlotter p2 = new HistogramPlotter("Test2", decompressedData);
					i++;
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Calculate and print average statistics
		System.out.println("Average Statistics for " + algorithmName + ":");
		System.out.println("Average Compression Time: " + (totalCompressionTime / totalImages) + " ms");
		System.out.println("Average Decompression Time: " + (totalDecompressionTime / totalImages) + " ms");
		System.out.println("Average Compression ratio: " + (totalCompressedSizePercentage / totalImages));
		System.out.println("Average PSNR: " + (totalPSNR / totalImages));
		System.out.println("------------------------------------------");
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

	private static double calculatePSNR(byte[] original, byte[] decompressed) throws IOException {
		BufferedImage imgA = toBufferedImage(original);
		BufferedImage imgB = toBufferedImage(decompressed);

		int width = imgA.getWidth();
		int height = imgA.getHeight();

		double mseRed = 0;
		double mseGreen = 0;
		double mseBlue = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgbA = imgA.getRGB(x, y);
				int rgbB = imgB.getRGB(x, y);

				int redA = (rgbA >> 16) & 0xFF;
				int greenA = (rgbA >> 8) & 0xFF;
				int blueA = rgbA & 0xFF;

				int redB = (rgbB >> 16) & 0xFF;
				int greenB = (rgbB >> 8) & 0xFF;
				int blueB = rgbB & 0xFF;

				mseRed += Math.pow(redA - redB, 2);
				mseGreen += Math.pow(greenA - greenB, 2);
				mseBlue += Math.pow(blueA - blueB, 2);
			}
		}

		mseRed /= (width * height);
		mseGreen /= (width * height);
		mseBlue /= (width * height);

		double mse = (mseRed + mseGreen + mseBlue) / 3.0;

		if (mse == 0) {
			return Double.POSITIVE_INFINITY;
		}

		double maxPixelValue = 255.0;
		return 20 * Math.log10(maxPixelValue / Math.sqrt(mse));
	}

//	private static double calculateSSIM(byte[] original, byte[] decompressed) throws IOException {
//		BufferedImage imgA = toBufferedImage(original);
//		BufferedImage imgB = toBufferedImage(decompressed);
//
//		int width = imgA.getWidth();
//		int height = imgA.getHeight();
//
//		double[] ssimArray = new double[3];
//		for (int i = 0; i < 3; i++) {
//			ssimArray[i] = SSIM.calculateSSIM(imgA.getRGB(0, 0), imgB.getRGB(0, 0), width, height, i);
//		}
//
//		// Taking the average of the SSIM values for all three channels
//		return (ssimArray[0] + ssimArray[1] + ssimArray[2]) / 3.0;
//	}

	private static BufferedImage toBufferedImage(byte[] imageBytes) throws IOException {
		return ImageIO.read(new ByteArrayInputStream(imageBytes));
	}
}
