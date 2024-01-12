package org.greencloud.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipAlgorithm extends Algorithm {

	/**
	 * Compresses the given binary input using the ZIP compression algorithm.
	 *
	 * @param input The binary input to be compressed
	 * @return Compressed binary data
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public byte[] compress(byte[] input) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			 ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

			ZipEntry entry = new ZipEntry("data.png");
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.write(input);
			zipOutputStream.closeEntry();
			zipOutputStream.finish();

			return outputStream.toByteArray();
		}
	}

	/**
	 * Decompresses the given binary input using the ZIP compression algorithm.
	 *
	 * @param compressedData The compressed binary data to be decompressed
	 * @return Decompressed binary data
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public byte[] decompress(byte[] compressedData) throws IOException {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
			 ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;

			ZipEntry entry = zipInputStream.getNextEntry();
			while ((bytesRead = zipInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			zipInputStream.closeEntry();

			return outputStream.toByteArray();
		}
	}
}
