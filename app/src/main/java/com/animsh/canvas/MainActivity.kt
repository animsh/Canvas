package com.animsh.canvas

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_dialog_brush_size.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
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
    }

    override fun onClick(view: View?) {
        if (view is ImageView) {
            canvasView.setBrushColor(view.tag.toString())
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
}