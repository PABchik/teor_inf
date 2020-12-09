package teor.inf

import org.junit.After
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.util.*

class EncoderTest {

    @After
    fun tearDown() {

    }

    @Test
    fun encode() {
        val properties = Properties()
        properties.load(FileInputStream(File("src/test/resources/default_config.properties")))
        Encoder().encode(properties.getProperty(Constants.ENCODER_DIRECTORY))
        Decoder().decode(properties.getProperty(Constants.DECODER_DIRECTORY))

    }
}