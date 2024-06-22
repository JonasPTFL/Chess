package console


/**
 *
 * @author Jonas Pollpeter
 */

class Menu(
    private val title: String,
    private val menuOptions: List<MenuOption>
) {

    fun start() {
        println(title)
        menuOptions.forEachIndexed { index, menuOption ->
            println("${index + 1}. ${menuOption.description}")
        }
        val input = readlnOrNull() ?: ""
        val option = input.toIntOrNull()
        if (option != null && option in 1..menuOptions.size) {
            menuOptions[option - 1].action()
        } else {
            println("Invalid input")
            start()
        }
    }
}