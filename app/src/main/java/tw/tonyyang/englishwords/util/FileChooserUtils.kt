package tw.tonyyang.englishwords.util

import android.content.Context
import android.net.Uri
import jxl.Workbook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import tw.tonyyang.englishwords.App
import tw.tonyyang.englishwords.Logger
import tw.tonyyang.englishwords.database.Word
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class FileChooserUtils private constructor() {

    companion object {
        private val TAG = FileChooserUtils::class.java.simpleName
        private const val TMP_FILE_NAME = "vocabulary.xls"

        suspend fun importExcelDataToDb(context: Context, fileUrl: String) = coroutineScope {
            Logger.d(TAG, "[importExcelDataToDb] start")
            withContext(Dispatchers.IO) {
                val data = if (fileUrl.contains("content://") || fileUrl.contains("file:///")) {
                    readFile(context, fileUrl)
                } else {
                    readFileFromInternet(fileUrl)
                }
                storeDataToTempFile(context, data)
                getWorkbookFromTempFile(context)?.let { book ->
                    if (book.sheets.isEmpty()) {
                        throw Exception("sheets is empty")
                    }
                    val sheet = book.getSheet(0)
                    val rows = sheet.rows
                    for (i in 0 until rows) {
                        if (sheet.getCell(0, i).contents[0].toString() == "#") continue
                        val word = Word(
                                word = sheet.getCell(0, i).contents,
                                wordMean = sheet.getCell(1, i).contents,
                                category = sheet.getCell(2, i).contents,
                                wordStar = sheet.getCell(3, i).contents,
                                wordSentence = sheet.getCell(4, i).contents
                        )
                        App.db?.userDao()?.insertAll(word)
                    }
                    book.close()
                }
            }
            Logger.d(TAG, "[importExcelDataToDb] end")
        }

        private fun readFile(context: Context, filePath: String): ByteArray {
            Logger.d(TAG, "[readFile] start")
            val arrayOutputStream = ByteArrayOutputStream()
            val uri = Uri.parse(filePath)
            context.contentResolver?.openInputStream(uri)?.use {
                val buffer = ByteArray(10 * 1024)
                while (true) {
                    val len = it.read(buffer)
                    if (len == -1) {
                        break
                    }
                    arrayOutputStream.write(buffer, 0, len)
                }
                arrayOutputStream.close()
            }
            Logger.d(TAG, "[readFile] end")
            return arrayOutputStream.toByteArray()
        }

        private fun readFileFromInternet(fileUrl: String): ByteArray {
            Logger.d(TAG, "[readFileFromInternet] start")
            val url = URL(fileUrl)
            val arrayOutputStream = ByteArrayOutputStream()
            val connection = url.openConnection() as? HttpURLConnection
            connection?.connectTimeout = 10 * 1000
            connection?.connect()
            if (connection?.responseCode == 200) {
                val inputStream = connection.inputStream
                val buffer = ByteArray(10 * 1024)
                while (true) {
                    val len = inputStream.read(buffer)
                    if (len == -1) {
                        break
                    }
                    arrayOutputStream.write(buffer, 0, len)
                }
                arrayOutputStream.close()
                inputStream.close()
            }
            Logger.d(TAG, "[readFileFromInternet] end")
            return arrayOutputStream.toByteArray()
        }

        private fun storeDataToTempFile(context: Context, data: ByteArray) {
            context.openFileOutput(TMP_FILE_NAME, Context.MODE_PRIVATE)?.use {
                it.write(data)
            }
        }

        private fun getWorkbookFromTempFile(context: Context): Workbook? {
            context.openFileInput(TMP_FILE_NAME).use {
                return Workbook.getWorkbook(it)
            }
        }
    }
}