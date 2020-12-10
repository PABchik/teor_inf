package teor.inf;

import org.jetbrains.annotations.NotNull;

public class Constants {
    public static final String CONFIG_FILE_NAME = "config.properties";
    public static final String DEFAULT_CONFIG_FILE_NAME = "src/main/resources/default_config.properties";


    public static final String ENCODER_DIRECTORY = "encoder_dir";
    public static final String ENCODER_OUTPUT_DIRECTORY = "encoder_out_dir";
    public static final String DECODER_DIRECTORY = "decoder_dir";
    public static final String DECODER_OUTPUT_DIRECTORY = "decoder_out_dir";

    public static final String BLOCK_SIZE = "block_size";
    public static final int DEFAULT_BLOCK_SIZE = 20000;


    public static final String ENCODER_MODE = "Encoder";

    public static String ASCII;
    static {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            strBuilder.append((char)i);
        }
        ASCII = strBuilder.toString();
    }

    @NotNull
    public static final String DECODER_MODE = "Decoder";
}
