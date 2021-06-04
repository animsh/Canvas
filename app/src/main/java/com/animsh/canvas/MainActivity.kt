package com.animsh.canvas

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_dialog_brush_size.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val STORAGE_PERMISSION = 1
        const val GALLERY_INTENT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonBrushSize.setOnClickListener {
            showBrushSizeDialog(canvasView, this)
        }

        for (i in 0..7) {
            colorPalate[i].setOnClickListener(this)
        }

        buttonAddImage.setOnClickListener {
            if (isStoragePermissionGranted()) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY_INTENT)
            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onClick(view: View?) {
        if (view is ImageView) {
            canvasView.setBrushColor(view.tag.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permission is granted!!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission is denied!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_INTENT) {
                try {
                    if (data!!.data != null) {
                        canvasImage.visibility = View.VISIBLE
                        canvasImage.setImageURI(data.data)
                    } else {
                        Toast.makeText(
                            this,
                            "Error in parsing image or it's corrupted",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showBrushSizeDialog(canvasView: CanvasView, context: Context) {
        val dialog = Dialog(context)
        dialog.apply {
            setContentView(R.layout.layout_dialog_brush_size)
            setCanceledOnTouchOutside(true)
            buttonBrushSizeSmall.setOnClickListener {
                canvasView.setBrushSize(10f)
                dismiss()
            }
            buttonBrushSizeMedium.setOnClickListener {
                canvasView.setBrushSize(20f)
                dismiss()
            }
            buttonBrushSizeLarge.setOnClickListener {
                canvasView.setBrushSize(30f)
                dismiss()
            }
            show()
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).toString()
            )
        ) {
            Toast.makeText(
                this,
                "Permission is needed to make app work normally",
                Toast.LENGTH_LONG
            ).show()
        }

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), STORAGE_PERMISSION
        )
    }
}