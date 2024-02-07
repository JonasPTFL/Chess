package online

import io.socket.client.IO
import io.socket.client.Socket
import java.util.*


/**
 *
 * @author Jonas Pollpeter
 */

class SocketConnection(
    loginReturnData: LoginReturnData,
    private val username: String,
    private val onConnected: () -> Unit,
    private val onConnectionFailed: () -> Unit,
    private val onGameStart: () -> Unit,
    private val onOpponentMove: (String) -> Unit
) {
    private val websocketHost = "localhost"
    private val websocketPort = 3000
    private val authTokenKey = "token"
    private val socketURI = "http://$websocketHost:$websocketPort"
    var currentlyPlayingGame: OnlineGame? = null

    object Events {
        const val OPPONENT_MOVE = "opponent_move"
        const val MOVE = "move"
        const val GAME_STATE = "game_state"
    }

    enum class GameState(val stringRepresentation: String) {
        START("start"),
        WHITE_TURN("white_turn"),
        BLACK_TURN("black_turn"),
        END("end");

        companion object {
            fun fromString(stringVal: String) = values().first { it.stringRepresentation == stringVal }
        }
    }

    private val socketOptions = IO.Options.builder()
        .setAuth(Collections.singletonMap(authTokenKey, loginReturnData.token))
        .build()
    private val socket: Socket = IO.socket(socketURI, socketOptions)

    fun setup() {
        onEvent(Socket.EVENT_CONNECT) {
            println("Connected to chess socket")
            onConnected()
        }
        onEvent(Socket.EVENT_DISCONNECT) {
            println("Disconnected from chess socket: $it")
            onConnectionFailed()
        }
        onEvent(Socket.EVENT_CONNECT_ERROR) {
            println("Connection error: $it")
            onConnectionFailed()
        }
        onEvent(Events.GAME_STATE) {
            when (GameState.fromString(it)) {
                GameState.START -> onGameStart()
                GameState.WHITE_TURN -> checkTurn(true)
                GameState.BLACK_TURN -> checkTurn(false)
                GameState.END -> println("Game over")
            }
        }
        onEvent(Events.OPPONENT_MOVE) {
            println("New opponent move: $it")
            onOpponentMove(it)
        }

        socket.connect()
    }

    private fun checkTurn(isWhiteTurn: Boolean) {
        if (isWhitePlayerInCurrentGame() && isWhiteTurn || !isWhitePlayerInCurrentGame() && !isWhiteTurn) {
            println("Your turn, make a move...")
        } else {
            println("Waiting for opponent to make a move...")
        }
    }

    fun sendMove(move: String) {
        socket.emit(Events.MOVE, move)
    }

    fun isWhitePlayerInCurrentGame() = username == currentlyPlayingGame?.whitePlayer

    private fun onEvent(event: String, callback: (String) -> Unit) {
        socket.on(event) {
            callback(it.firstOrNull().toString())
        }
    }
}