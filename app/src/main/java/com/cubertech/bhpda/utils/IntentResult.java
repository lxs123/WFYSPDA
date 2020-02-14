package com.cubertech.bhpda.utils;

/**
 * @author LiQi
 */
public final class IntentResult {

    private final String contents;
    private final String formatName;
    private final byte[] rawBytes;
    private final Integer orientation;
    private final String errorCorrectionLevel;
    private final String barcodeImagePath;

    IntentResult() {
        this(null, null, null, null, null, null);
    }

    IntentResult(String contents,
                 String formatName,
                 byte[] rawBytes,
                 Integer orientation,
                 String errorCorrectionLevel,
                 String barcodeImagePath) {
        this.contents = contents;
        this.formatName = formatName;
        this.rawBytes = rawBytes;
        this.orientation = orientation;
        this.errorCorrectionLevel = errorCorrectionLevel;
        this.barcodeImagePath = barcodeImagePath;
    }

    /**
     * @return raw content of barcode
     */
    public String getContents() {
        return contents;
    }

    /**
     * @return name of format, like "QR_CODE", "UPC_A". See {@code BarcodeFormat} for more format names.
     */
    public String getFormatName() {
        return formatName;
    }

    /**
     * @return raw bytes of the barcode content, if applicable, or null otherwise
     */
    public byte[] getRawBytes() {
        return rawBytes;
    }

    /**
     * @return rotation of the image, in degrees, which resulted in a successful scan. May be null.
     */
    public Integer getOrientation() {
        return orientation;
    }

    /**
     * @return name of the error correction level used in the barcode, if applicable
     */
    public String getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    /**
     * @return path to a temporary file containing the barcode image, if applicable, or null otherwise
     */
    public String getBarcodeImagePath() {
        return barcodeImagePath;
    }

    @Override
    public String toString() {
        StringBuilder dialogText = new StringBuilder(120);
        dialogText.append("Format: ").append(formatName).append('\n');
        dialogText.append("Contents: ").append(contents).append('\n');
        int rawBytesLength = rawBytes == null ? 0 : rawBytes.length;
        dialogText.append("Raw bytes: (").append(rawBytesLength).append(" bytes)\n");
        dialogText.append("Orientation: ").append(orientation).append('\n');
        dialogText.append("EC level: ").append(errorCorrectionLevel).append('\n');
        dialogText.append("Barcode image: ").append(barcodeImagePath).append('\n');
        return dialogText.toString();
    }

}
