package com.pachkhede.tictactoeonline

import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketManager  {

    private var socket : Socket? = null


    init {
        try {
            socket = IO.socket("http://192.168.131.128:3000/")
        } catch (e: URISyntaxException){
            e.printStackTrace()
        }
    }

    fun connect(){
        socket?.connect()
    }

    fun disconnect(){
        socket?.disconnect()
    }

    fun isConnected() : Boolean{
        return socket?.connected() ?: false
    }

    fun createUser(name:String, image:String) {
        val json = JSONObject()
        json.put("name", name)
        json.put("image", image)
        socket?.emit("create_user", json)
    }


    fun createRoom(){
        socket?.emit("create_room")
    }

    fun setRoomCreatedListener(onRoomCreated: (String) -> Unit){
        socket?.on("room_created") { args ->
            val data = args[0] as JSONObject
            val id = data.getString("id")
            onRoomCreated(id)
        }
    }


    fun joinRoom(id : String) {
        val data = JSONObject()
        data.put("id", id)
        socket?.emit("join_room", data)
    }


    fun setRoomJoinedListener(onRoomJoined: (String, String) -> Unit){
        socket?.on("user_joined") { args ->
            val data = args[0] as JSONObject
            val name = data.getString("name")
            val img = data.getString("image")
            onRoomJoined(name, img)
        }
    }



}