package com.pachkhede.tictactoeonline

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.droidsonroids.gif.GifImageView


class RoomCreateJoinDialog : DialogFragment() {

    private var view: View? = null

    private var isCreateLayoutOpen = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            view = inflater.inflate(R.layout.create_join_room_dialog, null)


            val sharedPref = requireActivity().getSharedPreferences(
                getString(R.string.shared_pref_main),
                Context.MODE_PRIVATE
            )
            val name = sharedPref.getString(getString(R.string.profileName), "Player1")!!
            val img = sharedPref.getString(getString(R.string.profileImg), "a")!!

            setPlayer1Info(name, img)
            setLoadingIconOnPlayer2()


            SocketManager.connect { success ->
                if (success) {


                    SocketManager.createUser(name, img)


                    SocketManager.setRoomCreatedListener { id ->
                        activity?.runOnUiThread {
                            view?.findViewById<TextView>(R.id.roomCode)?.text = ("Room Id: " + id)
                        }
                    }

                    SocketManager.createRoom()

                    SocketManager.setRoomJoinedListener { name, img ->
                        setPlayer2Info(name, img)
                    }
                } else {
                    activity?.runOnUiThread {
                        view?.findViewById<TextView>(R.id.roomCode)?.text = ("Error Connecting")
                    }
                }
            }





            view?.findViewById<Button>(R.id.createLayoutButton)?.setOnClickListener {
                showCreateLayout()
            }


            view?.findViewById<Button>(R.id.joinLayoutButton)?.setOnClickListener {
                showJoinLayout()
            }

            view?.findViewById<Button>(R.id.buttonOk)?.setOnClickListener {
                if (!isCreateLayoutOpen) {
                    val id = view?.findViewById<EditText>(R.id.inputRoomIdEditText)?.text.toString()
                    Toast.makeText(context, id, Toast.LENGTH_SHORT).show()
                    SocketManager.joinRoom(id)
                }
            }


            val cancelButton = view?.findViewById<Button>(R.id.buttonCancel)
            cancelButton?.setOnClickListener {
                dialog?.dismiss()
            }

            builder.setView(view)

            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        SocketManager.disconnect()

    }

    private fun showCreateLayout() {
        isCreateLayoutOpen = true
        view?.findViewById<LinearLayout>(R.id.createLayout)?.visibility = View.VISIBLE
        view?.findViewById<LinearLayout>(R.id.joinLayout)?.visibility = View.GONE
    }

    private fun showJoinLayout() {
        isCreateLayoutOpen = false
        view?.findViewById<LinearLayout>(R.id.createLayout)?.visibility = View.GONE
        view?.findViewById<LinearLayout>(R.id.joinLayout)?.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setLoadingIconOnPlayer2() {
        view?.findViewById<TextView>(R.id.playerName2)?.visibility = View.GONE
        view?.findViewById<ImageView>(R.id.playerImage2)?.visibility = View.GONE
        view?.findViewById<GifImageView>(R.id.loadingGif)?.visibility = View.VISIBLE

    }

    private fun setPlayer2Info(name: String, img: String) {
        view?.findViewById<TextView>(R.id.playerName2)?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.playerImage2)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.playerName2)?.text = name
        val imgId = resources.getIdentifier(img, "drawable", activity?.packageName)
        view?.findViewById<ImageView>(R.id.playerImage2)?.setImageResource(imgId)
        view?.findViewById<GifImageView>(R.id.loadingGif)?.visibility = View.GONE

    }

    private fun setPlayer1Info(name: String, img: String) {
        view?.findViewById<TextView>(R.id.playerName1)?.text = name
        val imgId = resources.getIdentifier(img, "drawable", activity?.packageName)
        view?.findViewById<ImageView>(R.id.playerImage1)?.setImageResource(imgId)

    }


    private fun joinRoom() {

    }


}