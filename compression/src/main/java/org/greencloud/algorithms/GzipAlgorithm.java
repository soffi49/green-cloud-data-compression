package org.greencloud.algorithms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipAlgorithm extends Algorithm {

    /**
     * Compresses the given binary input using the GZIP compression algorithm.
     *
     * @param input The binary input to be compressed
     * @return Compressed binary data
     * @throws IOException If an I/O error occurs
     */
    @Override
    public byte[] compress(byte[] input) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {

            gzipOutputStream.write(input);
            gzipOutputStream.finish();

            return outputStream.toByteArray();
        }
    }

    /**
     * Decompresses the given binary input using the GZIP compression algorithm.
     *
     * @param compressedData The compressed binary data to be decompressed
     * @return Decompressed binary data
     * @throws IOException If an I/O error occurs
     */
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }
}
