package teor.inf

import com.sun.org.apache.bcel.internal.Const
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Math.abs


class Encoder {
    fun encode(encoderDirName: String) {
        for (file in getFiles(encoderDirName, Constants.ENCODER_MODE)) {
            var byteArray = FileInputStream(file).readBytes().map(Byte::toUByte)

            var encodeTable = ArrayList<StringShift>()
            byteArray.forEachIndexed { index, it -> encodeTable.add(StringShift(it, byteArray[((index + byteArray.size - 1) % byteArray.size)], index)) }
            encodeTable.sortWith(Comparator { o1, o2 ->
                when {
                    o1.first < o2.first -> -1
                    o1.first > o2.first -> 1
                    else -> restoreAndCompare(o1, o2, byteArray)
                }
            })

            val encodedSequence = StringBuilder()
            val strBuilder = StringBuilder(Constants.ASCII)
            for (i in encodeTable) {
                encodedSequence.append(generateNextEl(i.last, strBuilder))
                strBuilder.append(i.last.toByte().toChar())
            }
            val fOut = FileOutputStream(File("encoder_input/out.ascii"))
            for (i in byteArray) {
                fOut.write(byteArrayOf(i.toByte()))
            }
//            println(encodedSequence.toString())
            println("Start size of ${file.name}: ${byteArray.size}, compressed size is ${encodedSequence.length / 8}")
        }

//        Constants.ASCII.forEach { println (it.toByte().toUByte()) }


    }

    private fun generateNextEl(last: UByte, strBuilder: StringBuilder): String {
        val counter = HashSet<UByte>()
        for (i in strBuilder.toString().reversed()) {
            if (i.toByte().toUByte() == last) {
                if (counter.size > 1) {
                    var str = counter.size.toString(2)
                    return "0${str.subSequence(1..str.lastIndex)}".padStart(str.length * 2 - 2, '1')
                }
                return "0${counter.size.toString(2)}"
            } else {
                counter.add(i.toByte().toUByte())
            }
        }
        return counter.size.toString(2)
    }

    /*
            val fOut = FileOutputStream(File("encoder_input/out.ascii"))
            for (i in Constants.ASCII) {
                fOut.write(byteArrayOf(i.toByte()))
            }*/
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