package com.google.codelabs.mdc.kotlin.shrine.staggeredgridlayout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.codelabs.mdc.kotlin.shrine.R
//import com.google.codelabs.mdc.kotlin.shrine.network.ImageRequester
import com.google.codelabs.mdc.kotlin.shrine.network.api
import com.google.codelabs.mdc.kotlin.shrine.network.ProductEntry
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject


/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
class StaggeredProductCardRecyclerViewAdapter(private val productList: List<ProductEntry>?) : RecyclerView.Adapter<StaggeredProductCardViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return position % 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredProductCardViewHolder {
        var layoutId = R.layout.shr_staggered_product_card_first
        if (viewType == 1) {
            layoutId = R.layout.shr_staggered_product_card_second
        } else if (viewType == 2) {
            layoutId = R.layout.shr_staggered_product_card_third
        }

        val layoutView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return StaggeredProductCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: StaggeredProductCardViewHolder, position: Int) {
        if (productList != null && position < productList.size) {
            val product = productList[position]
            //holder.productTitle.text = "hello"//product.title
            val url = "http://3.17.70.143:8069/recurring_task/recurring_task"

            val data = mapOf<String, String>("barcode" to "4813882329758")

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, JSONObject(data), Response.Listener { response ->
                //val gson = Gson()
                //val res = gson.fromJson<JsonObjectRequest>(response.get("result").toString(),JsonObjectRequest.class))
                holder.productTitle.text = response.get("result").toString()
            }, Response.ErrorListener { error ->
                holder.productTitle.text = "Error: %s".format(error.toString())
            })
            api.addJsonreq(jsonObjectRequest)
            holder.productPrice.text = product.price
            api.setImageFromUrl(holder.productImage, product.url)
        }
    }

    override fun getItemCount(): Int {
        return productList?.size ?: 0
    }
}

