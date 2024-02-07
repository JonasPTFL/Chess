package online

import io.ktor.util.date.*
import io.socket.client.IO
import io.socket.client.Socket
import java.security.MessageDigest
import java.util.*


/**
 *
 * @author Jonas Pollpeter
 */

class SocketConnection(loginReturnData: LoginReturnData, private val onConnected: () -> Unit) {
    private val websocketHost = "localhost"
    private val websocketPort = 3000
    private val authTokenKey = "token"
    private val socketURI = "http://$websocketHost:$websocketPort"

    private val socketOptions = IO.Options.builder()
        .setAuth(Collections.singletonMap(authTokenKey, loginReturnData.token))
        .build()
    private val socket: Socket = IO.socket(socketURI, socketOptions)

    fun setup() {
        socket.on(Socket.EVENT_CONNECT) {
            println("Connected to chess socket")
            onConnected()
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            println("Disconnected from chess socket")
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            println("Connection error: " + it.joinToString())
        }

        socket.connect()
    }

    private fun onEvent(event: String, callback: (String) -> Unit) {
        socket.on(event) {
            callback(it.first().toString())
        }
    }

    companion object {
        fun createGameID(): String {
            val timeMillis = getTimeMillis()
            val bytes = MessageDigest.getInstance("SHA-256").digest(timeMillis.toString().toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }.substring(0, 6).uppercase()
        }
    }
}