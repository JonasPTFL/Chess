package game

import board.Move
import console.Menu
import console.MenuOption
import gui.Visualizer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import online.ChessAPI
import online.LoginData
import online.SocketConnection
import pieces.PieceColor
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

/**
 *
 * @author Jonas Pollpeter
 */

class OnlineGameManager(private val returnToMainMenu: () -> Unit) {

    private val chessAPI = ChessAPI()
    private var socketConnection: SocketConnection? = null
    private var currentGame: Game = Game()

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
                    onConnectionFailed = onConnectionFailed,
                    onGameStart = { startGame() },
                    onOpponentMove = {
                        currentGame.executeMove(Move.fromAlgebraic(it, currentGame.board))
                    }
                )
                socketConnection!!.setup()
            }
        }
    }

    private fun startGame() {
        socketConnection ?: throw IllegalStateException("Socket connection not initialized")
        currentGame = Game()
        val isWhitePlayerInCurrentGame = socketConnection!!.isWhitePlayerInCurrentGame()
        var visualizer: Visualizer? = null

        // add listener to close visualizer when game is over
        currentGame.addGameStateListener(object : GameStateListener {
            override fun onGameStateChanged(gameState: GameState) {
                if (gameState != GameState.Running) {
                    thread {
                        // wait for 5 seconds before closing the visualizer
                        Thread.sleep(5000)
                        SwingUtilities.invokeLater {
                            visualizer?.close()
                        }
                        returnToMainMenu()
                    }
                }
            }

            override fun onMoveExecuted(move: Move) {
                if (currentGame.board.turn == PieceColor.Black && isWhitePlayerInCurrentGame
                    || currentGame.board.turn == PieceColor.White && !isWhitePlayerInCurrentGame) {
                    socketConnection?.sendMove(move.toAlgebraic())
                }
            }
        })

        currentGame.onWhiteTurn = {}
        currentGame.onBlackTurn = {}

        SwingUtilities.invokeLater {
            visualizer = Visualizer(currentGame, setOf(if (isWhitePlayerInCurrentGame) PieceColor.White else PieceColor.Black))
            visualizer?.isVisible = true

            currentGame.start()
        }
    }


    fun start() {
        println("Enter your username:")
        val username = readln()
        println("Enter your password:")
        val password = readln()
        connectToServer(username, password, onConnected = { menu.start() }, onConnectionFailed = returnToMainMenu)
    }
}