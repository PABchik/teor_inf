import teor.inf.Constants
import java.io.File
import java.io.FileInputStream
import java.util.*

class PropertyHelper {
    companion object{
        @JvmField
        val APPLICATION_PROPERTIES = Properties()

        init {
            APPLICATION_PROPERTIES.load(FileInputStream(File(Constants.DEFAULT_CONFIG_FILE_NAME)))
            val configFile = File(Constants.CONFIG_FILE_NAME)
            if (configFile.exists() && configFile.isFile)
                APPLICATION_PROPERTIES.load(FileInputStream(configFile))
        }
    }
}