package teor.inf

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.time.Duration
import java.time.Instant

class Decoder {
    fun decode(decoderDirName: String) {
        var empted = false
        for (file in getFiles(decoderDirName, Constants.DECODER_MODE)) {
            var start = Instant.now()
            var byteArray = FileInputStream(file).readBytes().map(Byte::toUByte)
            var binCode = buildBinaryCode(byteArray)

            val decodedSequenceBeforeBWT = decodeBinStr(binCode)

            var fullDecodedSequence = ArrayList<UByte>()

            var counter = 1
            while (counter < decodedSequenceBeforeBWT.lastIndex) {
                fullDecodedSequence.addAll(decode(decodedSequenceBeforeBWT.subList(counter + 1, counter + decodedSequenceBeforeBWT[counter] + 1)))
                counter += decodedSequenceBeforeBWT[counter] + 1
            }

            var outFile = File("${PropertyHelper.APPLICATION_PROPERTIES.getProperty(Constants.DECODER_OUTPUT_DIRECTORY)}/${file.name}_decoded")
            if (!File(outFile.path.replace("\\${outFile.name}", "")).exists())
                File(outFile.path.replace("\\${outFile.name}", "")).mkdirs()
            else if (!empted){
                File(outFile.path.replace("\\${outFile.name}", "")).deleteRecursively()
                File(outFile.path.replace("\\${outFile.name}", "")).mkdirs()
                empted = true
            }
            val fOut = FileOutputStream(outFile)
            fullDecodedSequence.forEach { fOut.write(it.toInt()) }

            println("File ${file.name} was decoded in ${Duration.between(start, Instant.now()).toMillis()} ms")
            println("Decoded size is ${fullDecodedSequence.size}\n")
        }
    }

    private fun decode(codeSequence: List<Int>):ArrayList<UByte> {
        val strBuilder = StringBuilder(Constants.ASCII)
        var lastColumn = buildLastColumn(codeSequence, strBuilder)
        var firstColumn = lastColumn.sorted()
        var decodedByteArray = decodeBWT(firstColumn, lastColumn, codeSequence.first())
        return decodedByteArray
    }

    private fun decodeBWT(firstColumn: List<Int>, lastColumn: List<Int>, first: Int): ArrayList<UByte> {
        var decodedByteArray = ArrayList<UByte>()
        var curIndex = first
        while (decodedByteArray.size != lastColumn.size) {
            decodedByteArray.add(firstColumn[curIndex].toUByte())
            curIndex = getNextRowIndex(curIndex, firstColumn, lastColumn)
        }
        return decodedByteArray
    }

    private fun getNextRowIndex(curIndex: Int, firstColumn: List<Int>, lastColumn: List<Int>): Int {
        var curSym = firstColumn[curIndex]
        var sameCounter = 0
        for (i in 0 until curIndex)
            if (curSym == firstColumn[i]) sameCounter++
        lastColumn.forEachIndexed {index, it -> if (it == curSym) if (sameCounter > 0) sameCounter-- else return index}
        return 0
    }

    private fun buildLastColumn(decodedSequenceBeforeBW: List<Int>, strBuilder: StringBuilder): List<Int> {
        var lastColumn = ArrayList<Int>()
        for (i in decodedSequenceBeforeBW.subList(1, decodedSequenceBeforeBW.size)) {
            var diffSet = HashSet<UByte>()
            var counter = strBuilder.lastIndex
            while (diffSet.size <= i && counter > -1)
                diffSet.add(strBuilder[counter--].toByte().toUByte())
            strBuilder.append(strBuilder[counter + 1])
        }
        strBuilder.toString().subSequence(256..strBuilder.lastIndex).forEach { lastColumn.add(it.toInt()) }
        return lastColumn
    }

    public fun decodeBinStr(binCode: String): List<Int> {
        val decodedSequence = ArrayList<Int>()
        var counter = 0
        var mode = 0
        var temp = StringBuilder()

        for (i in binCode) {
            if (mode == 0) {
                counter++
                if (i == '0')
                    mode = 2
            } else {
                temp.append(i)
                counter--
                if (counter < 2) {
                    decodedSequence.add(Integer.parseInt(if (counter == 1) "1${temp}" else temp.toString(), 2))
                    temp.clear()
                    mode = 0
                    counter = 0
                }
            }

        }
        /*while (binCode.length - counter > 7 || binCode.length - counter < 8 && binCode.length - counter != 0 && !binCode.substring(counter..binCode.lastIndex).matches(Regex("^1+$"))) {
            val countBitsForRead = getUnarCode(binCode, counter) + 1
            var code = binCode.substring(counter + countBitsForRead until counter + countBitsForRead * 2 - if (countBitsForRead == 1) 0 else 1)
            decodedSequence.add(Integer.parseInt(if (countBitsForRead > 1) "1$code" else code, 2))
            counter += countBitsForRead * 2 - if (countBitsForRead == 1) 0 else 1
        }*/
        return decodedSequence
    }

    private fun getUnarCode(binCode: String, counter: Int): Int {
        var result: Int = 0
        for (i in binCode.substring(counter..binCode.lastIndex))
            if (i == '1') result++ else break
        return result
    }


    private fun buildBinaryCode(byteArray: List<UByte>): String {
        val strBuilder = StringBuilder()
        for (i in byteArray)
            strBuilder.append(i.toInt().toString(2).padStart(8, '0'))
        return strBuilder.toString()
    }

}