package com.koushik.fileconverter.constant;

public final class ApiConstants {
    // API Endpoints
    public static final String API_BASE_PATH = "/api/v1";
    public static final String CONVERT_ENDPOINT = API_BASE_PATH + "/convert";
    public static final String COMPRESS_ENDPOINT = API_BASE_PATH + "/compress";
    public static final String PROGRESS_ENDPOINT = API_BASE_PATH + "/progress/{jobId}";
    public static final String DOWNLOAD_ENDPOINT = API_BASE_PATH + "/download/{fileId}";

    // Request Parameters
    public static final String TARGET_FORMAT_PARAM = "targetFormat";
    public static final String COMPRESSION_LEVEL_PARAM = "compressionLevel";
    public static final String FILE_PARAM = "file";
    public static final String JOB_ID_PARAM = "jobId";
    public static final String FILE_ID_PARAM = "fileId";

    // Error Messages
    public static final String ERROR_FILE_NOT_FOUND = "File not found";
    public static final String ERROR_INVALID_FILE_FORMAT = "Invalid file format";
    public static final String ERROR_UNSUPPORTED_CONVERSION = "Unsupported conversion combination";
    public static final String ERROR_FILE_TOO_LARGE = "File size exceeds maximum limit";
    public static final String ERROR_PROCESSING_FAILED = "File processing failed";
    public static final String ERROR_INVALID_COMPRESSION_LEVEL = "Invalid compression level";

    // Validation Constants
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final int MIN_COMPRESSION_LEVEL = 1;
    public static final int MAX_COMPRESSION_LEVEL = 100;
    public static final int DEFAULT_COMPRESSION_LEVEL = 50;

    // Cache Control
    public static final String CACHE_CONTROL_HEADER = "Cache-Control";
    public static final String NO_CACHE_VALUE = "no-cache, no-store, must-revalidate";

    private ApiConstants() {
        // Private constructor to prevent instantiation
    }
}
