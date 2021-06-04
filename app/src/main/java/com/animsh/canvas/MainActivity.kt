package com.animsh.canvas

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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

        buttonUndoStep.setOnClickListener {
            canvasView.undoSteps()
        }

        buttonRedoStep.setOnClickListener {
            canvasView.redoSteps()
        }

        buttonSaveCanvas.setOnClickListener {
            if (isStoragePermissionGranted()) {
                BitmapAsyncTask(getBitmap(canvasContainer)).execute()
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

    private fun getBitmap(view: View): Bitmap {
        val resultBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val backgroundDrawable = view.background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return resultBitmap
    }

    @SuppressLint("StaticFieldLeak")
    private inner class BitmapAsyncTask(val mBitmap: Bitmap?) :
        AsyncTask<Any, Void, String>() {
        private lateinit var mProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any): String {
            var result = ""
            if (mBitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()

                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(
                        externalCacheDir!!.absoluteFile.toString()
                                + File.separator + "canvas_" + System.currentTimeMillis() / 1000 + ".jpg"
                    )
                    val fo =
                        FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result = f.absolutePath
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            cancelProgressDialog()
            if (result.isNotEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "File saved successfully :$result",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Something went wrong while saving the file.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(result)
            )
            shareIntent.type =
                "image/*"
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share"
                )
            )

        }

        private fun showProgressDialog() {
            mProgressDialog = Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.dialog_custom_progress)
            mProgressDialog.show()
        }

        private fun cancelProgressDialog() {
            mProgressDialog.dismiss()
        }
    }

}