package com.practice.socketio_try

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class ChatApplication : Application() {

    private  var mSocket: Socket = try {
        IO.socket(Constants.CHAT_SERVER_URL)
    } catch (e: URISyntaxException) {
        throw RuntimeException(e)
    }//? = null

    fun getSocket(): Socket {
        return mSocket
    }
}
