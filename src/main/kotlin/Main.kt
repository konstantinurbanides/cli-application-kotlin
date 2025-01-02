import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import java.time.LocalDate

/**
 * The main command of the New Year's resolution tool.
 */
class Resolution : CliktCommand() {

    override fun help(context: Context): String = """
    Resolution is a command line tool that can be used to manage your
    New Year's resolutions.
    
    You can create, edit, delete and list your New Year's resolutions.
    """.trimIndent()

    override fun run() = Unit
}

/**
 * The 'create' subcommand for the 'resolution' command.
 */
class Create : CliktCommand() {

    override fun help(context: Context): String = """
    Creates a New Year's resolution.

    This will create a New Year's resolution and adds it to the existing ones.
    """.trimIndent()

    private val text: String by argument(help = "Description of the New Year's resolution.")
    private val priority: Int by option("-p", "--priority", help = "Priority of the New Year's resolution. Must be between 1 and 10.")
        .int()
        .default(1)
    private val deadline: String? by option(
        "-d",
        "--deadline",
        help = "Sets a deadline in the yyyy-MM-dd format for the New Year's resolution."
    )

    override fun run() {
        // Validate input
        if (priority < 1 || priority > 10) {
            echo("The priority $priority is not valid. Please use a priority between 1 and 10.")
            return
        }

        if (deadline != null) {
            Regex("(20)\\d{2}-(0[1-9]|1[1,2])-(0[1-9]|[12][0-9]|3[01])").matches(deadline!!)
                .takeIf { it } ?: run {
                echo("The deadline $deadline is not valid. Please use the format yyyy-MM-dd.")
                return
            }

            LocalDate.parse(deadline!!).takeIf { it.isAfter(LocalDate.now()) } ?: run {
                echo("The deadline $deadline is not valid. Please use a date after today.")
                return
            }
        }

        CsvHandler().writeCsv(ResolutionEntry(text, priority, deadline))

        echo(
            "The following New Year's resolution has been created:\n" +
                    " - Text: $text\n" +
                    " - Priority: $priority" +
                    printDeadline(deadline, true)
        )
    }
}

/**
 * Configuration object for the edit and remove subcommands.
 */
data class EditRemoveConfig(var position: Int = -1)

/**
 * The 'edit' subcommand for the 'resolution' command.
 */
class Edit : CliktCommand() {

    override fun help(context: Context): String = """
    Updates a New Year's resolution.

    This will update an existing New Year's resolution by
    editing properties as well as deleting optional ones.
    """.trimIndent()

    override val invokeWithoutSubcommand = true
    private val position: Int by argument(help = "Position of the New Year's resolution in the list.")
        .int()
    private val text: String? by option("-t", "--text", help = "Description of the New Year's resolution.")
    private val priority: Int? by option("-p", "--priority", help = "Priority of the New Year's resolution.")
        .int()
    private val deadline: String? by option("-d", "--deadline", help = "Sets a deadline for the New Year's resolution.")
    private val config by findOrSetObject { EditRemoveConfig() }

    override fun run() {
        val resolutions = CsvHandler().readCsv()

        // Validate input
        if (position < 1 || position > resolutions.size) {
            echo("The id $position is not valid. Please use a valid id.")
            return
        }

        config.position = position
        val subcommand = currentContext.invokedSubcommand

        if (subcommand == null) {
            val resolution = resolutions[position - 1]
            val oldResolution = resolution.copy()
            resolution.text = text ?: resolution.text
            resolution.priority = priority ?: resolution.priority
            resolution.deadline = deadline ?: resolution.deadline

            resolutions.apply { CsvHandler().writeCsv(this) }

            echo(
                "The year's resolution has been updated with following properties:\n" +
                        printProperty(oldResolution.text, resolution.text, "Text") +
                        printProperty(oldResolution.priority, resolution.priority, "Priority") +
                        printProperty(oldResolution.deadline, resolution.deadline, "Deadline")
            )
        }
    }

    private fun printProperty(oldValue: Any?, newValue: Any?, property: String): String {
        return if (oldValue != newValue) {
            " - Old $property: $oldValue -> New $property: $newValue\n"
        } else {
            " - Unchanged $property: $oldValue\n"
        }

    }
}

/**
 * The 'remove' subcommand for the 'edit' command.
 */
class Remove : CliktCommand() {

    override fun help(context: Context): String = """
    Removes optional properties of a New Year's resolution.

    This will remove optional properties of an existing New Year's resolution instead of updating their values.
    """.trimIndent()

    private val removePriority: Boolean by option("-p", "--priority", help = "Removes the priority and sets it to 1.")
        .flag()
    private val removeDeadline: Boolean by option("-d", "--deadline", help = "Removes the deadline.")
        .flag()
    private val config by requireObject<EditRemoveConfig>()

    override fun run() {
        // Validate input

        if (config.position == -1) {
            echo("The position of the New Year's resolution is not set. Please use the position of the resolution.")
            return
        }

        val resolutions = CsvHandler().readCsv()

        val resolution = resolutions[config.position - 1]
        resolution.priority = if (removePriority) 1 else resolution.priority
        resolution.deadline = if (removeDeadline) null else resolution.deadline

        resolutions.apply { CsvHandler().writeCsv(this) }

        if (!removePriority && !removeDeadline) {
            echo("No properties have been removed from the New Year's resolution.")
            return
        }

        echo("The following properties have been removed from the New Year's resolution:")
        if (removePriority) echo(" - Set priority to default value (1)")
        if (removeDeadline) echo(" - Removed deadline")
    }
}

/**
 * The 'delete' subcommand for the resolution command.
 */
class Delete : CliktCommand() {

    override fun help(context: Context): String = """
    Deletes an existing New Year's resolution.

    This will delete an existing New Year's resolution by specifying it's position.
    """.trimIndent()

    private val position: Int by argument(help = "Position of the New Year's resolution in the list.")
        .int()

    override fun run() {
        val resolutions = CsvHandler().readCsv()

        // Validate input
        if (position < 1 || position > resolutions.size) {
            echo("The id $position is not valid. Please use a valid id.")
            return
        }

        resolutions.filterIndexed { index, _ -> index != position - 1 }.apply { CsvHandler().writeCsv(this) }
        echo("The New Year's resolution on position '$position' has been deleted.")
    }
}

/**
 * The 'list' subcommand for the resolution command.
 */
class List : CliktCommand() {

    override fun help(context: Context): String = """
    Shows a list of all added New Year's resolutions.

    This will show a list of New Year's resolutions which can be numbered and ordered by their priority.
    """.trimIndent()

    private val numbered: Boolean by option(
        "-n",
        "--numbered",
        help = "Numbers the New Year's resolutions according to their insertion order starting from 1."
    )
        .flag()

    private val orderedByPriority: Boolean by option(
        "-o",
        "--orderedByPriority",
        help = "Orders the New Year's resolutions by their priority."
    ).flag()

    override fun run() {
        val resolutions = CsvHandler().readCsv()

        if (resolutions.isEmpty()) {
            echo("No New Year's resolutions have been added yet. Use command `resolution create` to add a new one.")
            return
        } else {
            echo("New Year's resolutions:")

            val sortedResolutions = if (orderedByPriority) {
                resolutions.mapIndexed { index, entry -> Pair(index + 1, entry) }
                    .sortedByDescending { it.second.priority }
            } else {
                resolutions.mapIndexed { index, entry -> Pair(index + 1, entry) }
            }

            if (numbered) {
                sortedResolutions.forEach { pair ->
                    echo(
                        " [${pair.first}] Text: ${pair.second.text}," +
                                " Priority ${pair.second.priority}," +
                                printDeadline(pair.second.deadline, false)
                    )
                }
            } else {
                sortedResolutions.forEach { pair ->
                    echo(
                        " - Text: ${pair.second.text}," +
                                " Priority ${pair.second.priority}," +
                                printDeadline(pair.second.deadline, false)
                    )
                }
            }
        }
    }
}

private fun printDeadline(deadline: String?, inNewLine: Boolean): String {
    return if (deadline != null) {
        if (inNewLine) "\n - Deadline: $deadline" else " Deadline: $deadline"
    } else {
        ""
    }
}

fun main(args: Array<String>) {
    Resolution()
        .subcommands(Create(), Edit().subcommands(Remove()), Delete(), List())
        .main(args)
}