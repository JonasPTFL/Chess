package game

import console.Menu
import console.MenuOption
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import online.ChessAPI
import online.LoginData
import online.SocketConnection

/**
 *
 * @author Jonas Pollpeter
 */

class OnlineGameManager {

    private val chessAPI = ChessAPI()
    private var socketConnection: SocketConnection? = null

    private val menu = Menu(
        title = "Welcome to Online Chess! Please select an option:",
        menuOptions = listOf(
            MenuOption("Create a new game", this::createGame),
            MenuOption("Join a game", this::joinGame),
            MenuOption("Quit") { println("Goodbye!") }
        )
    )

    private fun joinGame() {
        println("Enter the game id:")
        val gameId = readln()
        runBlocking {
            launch {
                val game = chessAPI.joinGame(gameId)
                socketConnection?.currentlyPlayingGame = game
                println("Game joined, starting game...")
            }
        }
    }

    private fun createGame() {
        runBlocking {
            launch {
                val game = chessAPI.createGame()
                socketConnection?.currentlyPlayingGame = game
                println("Game (${game.id}) created, waiting for opponent to join...")
            }
        }
    }

    private fun connectToServer(
        username: String,
        password: String,
        onConnected: () -> Unit,
        onConnectionFailed: () -> Unit
    ) {
        runBlocking {
            launch {
                val result = chessAPI.login(LoginData(username, password))

                socketConnection = SocketConnection(
                    result,
                    username,
                    onConnected = onConnected,
                    onConnectionFailed = onConnectionFailed
                )
                socketConnection!!.setup()
            }
        }
    }

    fun start(returnToMainMenu: () -> Unit) {
        println("Enter your username:")
        val username = readln()
        println("Enter your password:")
        val password = readln()
        connectToServer(username, password, onConnected = { menu.start() }, onConnectionFailed = returnToMainMenu)
    }
}