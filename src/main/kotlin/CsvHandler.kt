import org.apache.commons.csv.CSVFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.collections.List

class CsvHandler {

    private var fileName: String = "files/resolutions.csv"

    /**
     * Reads the CSV file and returns a list of [ResolutionEntry] objects.
     */
    fun readCsv(): List<ResolutionEntry> {
        if (!File(fileName).exists()) {
            return emptyList()
        }

        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(FileInputStream(fileName).reader()).map {
            ResolutionEntry(it[0], it[1].toInt(), it[2])
        }
    }

    /**
     * Writes a single [ResolutionEntry] object to the CSV file.
     */
    fun writeCsv(resolution: ResolutionEntry) {
        checkFile()

        CSVFormat.DEFAULT.print(FileOutputStream(fileName, true).bufferedWriter()).apply {
            printRecord(resolution.text, resolution.priority, resolution.deadline ?: "-")
        }.flush()
    }

    /**
     * Writes a list of [ResolutionEntry] objects to the CSV file and clears the file before writing.
     */
    fun writeCsv(resolutions: List<ResolutionEntry>) {
        checkFile()
        File(fileName).writeText("") // clear file

        CSVFormat.DEFAULT.print(FileOutputStream(fileName, true).bufferedWriter()).apply {
            resolutions.forEach {
                printRecord(it.text, it.priority, it.deadline ?: "-")
            }
        }.flush()
    }

    /**
     * Creates a new CSV file, and it's parent folder if they do not exist.
     */
    private fun checkFile() {
        if (!File(fileName).exists()) {
            if (!File(fileName).parentFile.exists()) {
                File(fileName).parentFile.mkdirs()
            }

            File(fileName).createNewFile()
        }
    }
}