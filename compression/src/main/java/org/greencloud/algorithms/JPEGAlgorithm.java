package org.greencloud.algorithms;

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class JPEGAlgorithm extends Algorithm {
	private float quality;

	public JPEGAlgorithm(float quality) {
		this.quality = quality;
	}
	
	@Override
	public byte[] compress(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		BufferedImage image = ImageIO.read(bais);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);

		writer.setOutput(new MemoryCacheImageOutputStream(baos));
		writer.write(null, new IIOImage(image, null, null), param);

		return baos.toByteArray();
	}

	@Override
	public byte[] decompress(byte[] compressedData) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
		BufferedImage image = ImageIO.read(bais);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);

		return baos.toByteArray();
	}
}
