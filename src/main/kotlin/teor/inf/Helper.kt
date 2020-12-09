package teor.inf

import java.io.File
import kotlin.system.exitProcess

fun getFiles(dirName: String, mode: String): List<File> {
    val files = ArrayList<File>()
    val dir = File(dirName)
    if (!dir.exists() || dir.isFile) {
        println("$mode directory $dirName was not found (full path: ${dir.absoluteFile})")
        exitProcess(1)
    }
    if (dir.list()?.size == 0) {
        println("Encoder directory $dirName is empty")
        exitProcess(1)
    } else {
        for (fileName in dir.list()) {
            val file = File(dir.path + "/$fileName")
            if (file.isFile)
                files.add(file)
        }
    }
    return files
}

