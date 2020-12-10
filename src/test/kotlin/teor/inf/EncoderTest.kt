package teor.inf

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.util.*

class EncoderTest {

    @After
    fun tearDown() {

    }

    @Test
    fun encode() {
//        val properties = Properties()
        PropertyHelper.APPLICATION_PROPERTIES.load(FileInputStream(File("src/test/resources/default_config.properties")))
        Encoder().encode(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.ENCODER_DIRECTORY))
        Decoder().decode(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.ENCODER_OUTPUT_DIRECTORY))
        var areEqual = true
        Assert.assertEquals(getFiles(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.DECODER_OUTPUT_DIRECTORY), Constants.ENCODER_MODE).size,
            getFiles(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.ENCODER_DIRECTORY), Constants.ENCODER_MODE).size)

        for (i in getFiles(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.DECODER_OUTPUT_DIRECTORY), Constants.ENCODER_MODE)) {
            for (j in  getFiles(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.ENCODER_DIRECTORY), Constants.ENCODER_MODE)) {
                if (i.name.contains(j.name)) {
                    var fileInputStreamExpected = FileReader(j)
                    var fileInputStreamDecoded = FileReader(j)
                    Assert.assertEquals(fileInputStreamExpected.readText(), fileInputStreamDecoded.readText())

                }
            }
        }
    }
}