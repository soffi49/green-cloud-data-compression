package org.greencloud.enums;

import org.greencloud.algorithms.Algorithm;
import org.greencloud.algorithms.GzipAlgorithm;
import org.greencloud.algorithms.HuffmanAlgorithm;
import org.greencloud.algorithms.JPEGAlgorithm;
import org.greencloud.algorithms.LZWAlgorithm;
import org.greencloud.algorithms.TIFFAlgorithm;
import org.greencloud.algorithms.ZipAlgorithm;

import lombok.Getter;

/**
 * Types of available compression methods
 */
public enum CompressionMethodEnum {

	GZIP(new GzipAlgorithm()),
	HUFFMAN(new HuffmanAlgorithm()),
	JPEG(new JPEGAlgorithm(0.8f)),
	LZW(new LZWAlgorithm(12)),
	TIFF(new TIFFAlgorithm()),
	ZIP(new ZipAlgorithm()),
	NONE(new Algorithm());

	@Getter
	private final Algorithm algorithm;

	CompressionMethodEnum(final Algorithm algorithm) {
		this.algorithm = algorithm;
	}
}
