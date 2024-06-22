package console

import game.OfflineGameManager
import game.OnlineGameManager
import game.PlayerType

/**
 *
 * @author Jonas Pollpeter
 */

class ConsoleMainMenu {
    private val offlineGameManager = OfflineGameManager()
    private val onlineGameManager = OnlineGameManager(this::start)

    private val menu = Menu(
        title = "Welcome to Chess! Please select an option:",
        menuOptions = listOf(
            MenuOption("Play against the computer") { offlineGameManager.playAgainst(PlayerType.Engine) },
            MenuOption("Play against random moves")  { offlineGameManager.playAgainst(PlayerType.Random) },
            MenuOption("Play against stockfish") { offlineGameManager.playAgainst(PlayerType.Stockfish) },
            MenuOption("Play online") {
                onlineGameManager.start()
            },
            MenuOption("Quit") { println("Goodbye!") }
        )
    )

    fun start() {
        menu.start()
    }
}