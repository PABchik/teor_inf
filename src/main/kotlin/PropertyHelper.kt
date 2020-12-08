import teor.inf.Constants
import java.io.File
import java.io.FileInputStream
import java.util.*

class PropertyHelper {
    companion object{
        @JvmField
        val APPLICATION_PROPERTIES = Properties()

        init {
            val configFile = File(Constants.ENCODER_DIRECTORY_NAME)
            if (configFile.exists() && configFile.isFile)
                APPLICATION_PROPERTIES.load(FileInputStream(configFile))
            else
                println("Config file was not found!")
        }
    }
}