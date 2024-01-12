package org.greencloud.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TIFFAlgorithm extends Algorithm {

	@Override
	public byte[] compress(byte[] data) throws IOException {
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(data));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(originalImage, "TIFF", baos);
		baos.flush();
		byte[] compressedData = baos.toByteArray();
		baos.close();
		return compressedData;
	}

	@Override
	public byte[] decompress(byte[] compressedData) throws IOException {
		BufferedImage tiffImage = ImageIO.read(new ByteArrayInputStream(compressedData));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(tiffImage, "PNG", baos);
		baos.flush();
		byte[] decompressedData = baos.toByteArray();
		baos.close();
		return decompressedData;
	}
}
