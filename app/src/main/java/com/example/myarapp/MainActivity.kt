package com.example.myarapp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private var selectedObject: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneform_fragment as ArFragment

        img_chair.setOnClickListener { selectedObject = Uri.fromFile( File("//android_asset/chair.sfb")) }
        img_couch.setOnClickListener { selectedObject = Uri.parse("couch") }
        img_lamp.setOnClickListener { selectedObject = Uri.parse("lamp_post") }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            plane.type.takeIf { plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING }.apply {
                placeObject(arFragment, hitResult.createAnchor(), selectedObject)
            }
        }
    }

    private fun placeObject(arFragment: ArFragment, anchor: Anchor, uri: Uri?) {
        ModelRenderable.builder()
            .setSource(arFragment.context, uri)
            .build()
            .thenAccept { addNodeToScene(arFragment, anchor, it) }
            .exceptionally {
                Toast.makeText(arFragment.context, it.message, Toast.LENGTH_LONG).show()
                null
            }
    }

    private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
    }
}
