import jdk.nashorn.tools.ShellFunctions.input
import teor.inf.Encoder

fun main(args: Array<String>) {
    println("Choose mode:\nEnter 1 to encode files from encoder_input directory or enter 2 to decode files from decoder_input")
    val i = readLine()!!.trim()
    if (Regex("^[1|2]$").matches(i)) {
        if (i == "1") Encoder().encode()
    } else {
        println("Incorrect input. You should enter only 1 or 2")
    }
}