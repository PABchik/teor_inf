package teor.inf

import PropertyHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.Duration
import java.time.Instant


class Encoder {
    var blockSize: Int = if (PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.BLOCK_SIZE).matches(Regex("^[0-9]+$")))
        Integer.parseInt(PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.BLOCK_SIZE)) else Constants.DEFAULT_BLOCK_SIZE
    var empted = false
    fun encode(encoderDirName: String) {
        for (file in getFiles(encoderDirName, Constants.ENCODER_MODE)) {

            val start = Instant.now()

            var byteArray = FileInputStream(file).readBytes().map(Byte::toUByte)

            var startSize = byteArray.size
            var compressedSize = 0

//            byteArray = encodeRLE(byteArray)
//            if ()

            var outFile =
                File("${PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.ENCODER_OUTPUT_DIRECTORY)}/${file.name}_encoded")
            if (!File(outFile.path.replace("\\${outFile.name}", "")).exists())
                File(outFile.path.replace("\\${outFile.name}", "")).mkdirs()
            else if (!empted){
                File(outFile.path.replace("\\${outFile.name}", "")).deleteRecursively()
                File(outFile.path.replace("\\${outFile.name}", "")).mkdirs()
                empted = true
            }
            val fOut = FileOutputStream(outFile)


            var fullBinCode = StringBuilder()
            for (i in 0..byteArray.lastIndex step blockSize) {

                var encodedSequence = encodeBlock(byteArray.subList(i, if (byteArray.size - i < blockSize) byteArray.lastIndex + 1 else i + blockSize))


                encodedSequence = encodeNumber(byteArray.subList(i, if (byteArray.size - i < blockSize) byteArray.lastIndex + 1 else i + blockSize).size + 1) + encodedSequence

                if (i == 0) {
                    encodedSequence = encodeNumber(if (byteArray.size == startSize) 0 else 1) + encodedSequence
                }
                fullBinCode.append(encodedSequence)
            }

            var code = fullBinCode.padEnd(
                if (fullBinCode.length % 8 != 0) fullBinCode.length + 8 - fullBinCode.length % 8 else fullBinCode.length,
                '1'
            )

            compressedSize += code.length

            for (i in 0 until code.lastIndex step 8) {
                fOut.write(byteArrayOf(Integer.parseInt(code.substring(i..i + 7), 2).toByte()))
            }

            println("Start size of ${file.name}: ${startSize}, compressed size is ${compressedSize / 8}")
            println("Time spent: ${Duration.between(start, Instant.now()).toMillis()} ms\n")

        }
    }

    private fun encodeBlock(byteArray: List<UByte>): String {
        var encodeTable = ArrayList<StringShift>()
        byteArray.forEachIndexed { index, it ->
            encodeTable.add(
                StringShift(
                    it,
                    byteArray[((index + byteArray.size - 1) % byteArray.size)],
                    index
                )
            )
        }
        encodeTable.sortWith(Comparator { o1, o2 ->
            when {
                o1.first < o2.first -> -1
                o1.first > o2.first -> 1
                else -> restoreAndCompare(o1, o2, byteArray)
            }
        })

        val encodedSequence = StringBuilder()
        encodeTable.forEachIndexed { index, it -> if (it.shift == 0) encodedSequence.append(encodeNumber(index)) }
        val strBuilder = StringBuilder(Constants.ASCII)
        for (i in encodeTable) {
            encodedSequence.append(generateNextEl(i.last, strBuilder))
            strBuilder.append(i.last.toByte().toChar())
        }
//        println()
        return encodedSequence.toString()
    }

    private fun encodeRLE(byteArray: List<UByte>): List<UByte> {
        var strBuilder = StringBuilder()
        byteArray.forEach { strBuilder.append(it.toByte().toChar()) }
        var strEncoded = StringBuilder()

        var curSym = strBuilder[0]
        var counter = 0
        for (i in strBuilder) {
            if (i == curSym)
                counter++
            else {
                strEncoded.append(counter)
                strEncoded.append(curSym)
                counter = 1
                curSym = i
            }
        }
        if (strBuilder.length < byteArray.size) {
            var result = ArrayList<UByte>()
            strEncoded.forEach { result.add(it.toInt().toUByte()) }
            return result
        } else
            return byteArray
    }

    private fun encodeNumber(num: Int): String {
        if (num <= 1)
            return "0${num.toString(2)}"
        val code = Integer.toBinaryString(num).substring(1..Integer.toBinaryString(num).lastIndex)
        return "0${code}".padStart(code.length * 2 + 1, '1')
    }

    private fun generateNextEl(last: UByte, strBuilder: StringBuilder): String {
        val counter = HashSet<UByte>()
        for (i in strBuilder.toString().reversed()) {
            if (i.toByte().toUByte() == last) {
                if (counter.size > 1) {
                    var str = counter.size.toString(2)
//                    print(" ${counter.size}")
                    return "0${str.subSequence(1..str.lastIndex)}".padStart(str.length * 2 - 1, '1')
                }
//                print(" ${counter.size}")
                return "0${counter.size.toString(2)}"
            } else {
                counter.add(i.toByte().toUByte())
            }
        }
        return counter.size.toString(2)
    }

    private fun restoreAndCompare(o1: StringShift?, o2: StringShift?, byteArray: List<UByte>): Int {
        for (i in 1 until byteArray.size) {
            var a = byteArray[(i + o1!!.shift) % byteArray.size]
            var b = byteArray[(i + o2!!.shift) % byteArray.size]
            if (a != b) {
                return if (a < b) -1 else 1
            }
        }
        return 0
    }

}