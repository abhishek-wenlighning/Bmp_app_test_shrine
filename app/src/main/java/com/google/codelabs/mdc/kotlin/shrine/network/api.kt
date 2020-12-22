package com.google.codelabs.mdc.kotlin.shrine.network

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import android.content.res.Resources
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.google.codelabs.mdc.kotlin.shrine.application.ShrineApplication
import com.google.codelabs.mdc.kotlin.shrine.staggeredgridlayout.StaggeredProductCardRecyclerViewAdapter
import org.json.JSONObject


/**
 * Class that handles image requests using Volley.
 */
object api {
    private val requestQueue: RequestQueue
    private val imageLoader: ImageLoader
    private val maxByteSize: Int

    init {
        val context = ShrineApplication.instance
        this.requestQueue = Volley.newRequestQueue(context)
        this.requestQueue.start()
        this.maxByteSize = calculateMaxByteSize(context)
        this.imageLoader = ImageLoader(
                requestQueue,
                object : ImageLoader.ImageCache {
                    private val lruCache = object : LruCache<String, Bitmap>(maxByteSize) {
                        override fun sizeOf(url: String, bitmap: Bitmap): Int {
                            return bitmap.byteCount
                        }
                    }

                    @Synchronized
                    override fun getBitmap(url: String): Bitmap? {
                        return lruCache.get(url)
                    }

                    @Synchronized
                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        lruCache.put(url, bitmap)
                    }
                })
    }
    fun setImageFromUrl(networkImageView: NetworkImageView, url: String) {
        networkImageView.setImageUrl(url, imageLoader)
    }

    fun addJsonreq(req: JsonObjectRequest){
        this.requestQueue.add(req)
    }

    fun getproductlist(recyclerview: RecyclerView, resources: Resources){
        val url = "http://3.17.70.143:8069/recurring_task/recurring_task"

        val data = mapOf<String, String>("barcode" to "4813882329758")
        var jsonProductsString = ""//"[{\"title\": \"Vagabond sack\",\"url\": \"https://storage.googleapis.com/material-vignettes.appspot.com/image/0-0.jpg\",\"price\": \"$120\"}]"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject(data), { response ->
            println("\n\n\n\n\n"+response)
            jsonProductsString = response.get("result").toString()
            println("\n\n\n\n\n"+jsonProductsString+jsonProductsString)
            recyclerview.adapter = StaggeredProductCardRecyclerViewAdapter(
                    ProductEntry.initProductEntryList(jsonProductsString))


        }, { error ->
            println("\n\n\n\n\n"+error)
            recyclerview.adapter = StaggeredProductCardRecyclerViewAdapter(
                ProductEntry.initProductEntryList(resources))

            jsonProductsString = error.toString()
        })
        this.requestQueue.add(jsonObjectRequest)
    }

    private fun calculateMaxByteSize(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
        return screenBytes * 3
    }
}