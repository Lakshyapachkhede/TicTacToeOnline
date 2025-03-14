package com.pachkhede.tictactoeonline


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileSelectDialog(val list : List<Int>) : DialogFragment() {

    interface InputListener {
        fun sendInput(name: String, img : Int)

    }

    private var inputListener: InputListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.player_profile_update_dialog, null)

            val nameEditText = view.findViewById<EditText>(R.id.inputNameEditText)

            val imageRecyclerView = view.findViewById<RecyclerView>(R.id.imageSelectRecyclerView)
            val adapter = ImageSelectAdapter(requireContext(), list)
            imageRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            imageRecyclerView.adapter = adapter

            val cancelButton = view.findViewById<Button>(R.id.buttonCancel)
            cancelButton.setOnClickListener {
                dialog?.dismiss()
            }

            val okButton = view.findViewById<Button>(R.id.buttonOk)
            okButton.setOnClickListener {
                val name: String = nameEditText.text.toString().trim()

                if (name == ""){
                    nameEditText.error = "Name cannot be empty"

                }else{
                    val selectedImage = adapter.selectedImage
                    Toast.makeText(context, "$name, $selectedImage", Toast.LENGTH_SHORT).show()
                    inputListener?.sendInput(name, selectedImage)
                    dialog?.dismiss()
                }

            }





            builder.setView(view)

            builder.create()


        }?: throw IllegalStateException("Activity cannot be null")


    }

    override fun onAttach(context : Context) {
        super.onAttach(context)

        try {
            inputListener = context as? InputListener

            if (inputListener == null) {
                throw ClassCastException("$context must implement InputListener")
            }
        } catch (e:ClassCastException){
            Toast.makeText(context, "input listener error", Toast.LENGTH_SHORT).show()
        }


    }
}