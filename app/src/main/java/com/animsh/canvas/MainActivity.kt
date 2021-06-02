package com.animsh.canvas

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_dialog_brush_size.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonBrushSize.setOnClickListener {
            showBrushSizeDialog(canvaView)
        }
    }

    private fun showBrushSizeDialog(canvasView: CanvasView) {
        val dialog = Dialog(this)
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
}