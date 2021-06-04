package com.animsh.canvas

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonBrushSize.setOnClickListener {
            showBrushSizeDialog(canvasView, this)
        }

        colorPalate[0].setOnClickListener(this)
        colorPalate[1].setOnClickListener(this)
        colorPalate[2].setOnClickListener(this)
        colorPalate[3].setOnClickListener(this)
        colorPalate[4].setOnClickListener(this)
        colorPalate[5].setOnClickListener(this)
        colorPalate[6].setOnClickListener(this)
        colorPalate[7].setOnClickListener(this)

        buttonAddImage.setOnClickListener {
            if (isStoragePermissionGranted()) {
                Toast.makeText(
                    this,
                    "Permission is Granted",
                    Toast.LENGTH_LONG
                ).show()
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