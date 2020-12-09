package teor.inf;

public class Constants {
    public static final String CONFIG_FILE_NAME = "config.properties";
    public static final String DEFAULT_CONFIG_FILE_NAME = "src/main/resources/default_config.properties";


    public static final String DEFAULT_ENCODER_DIRECTORY_NAME = "encoder_input";
    public static final String ENCODER_DIRECTORY = "encoder_dir";
    public static final String DEFAULT_DECODER_DIRECTORY_NAME = "decoder_input";
    public static final String DECODER_DIRECTORY = "decoder_dir";
    public static final String ENCODER_DIRECTORY_NAME = "encoder_input";

    public static final String ENCODER_MODE = "Encoder";

    public static String ASCII;
    static {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            strBuilder.append((char)i);
        }
        ASCII = strBuilder.toString();
    }
}
