package com.sanmiaderibigbe.travelmantics

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.sanmiaderibigbe.travelmantics.databinding.DealItemListBinding
import com.squareup.picasso.Picasso

class DealAdapter(val context : Context) : RecyclerView.Adapter<DealListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealListViewHolder {
        val dealBinding = DealItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DealListViewHolder(dealBinding)
    }

    override fun getItemCount(): Int {
        return dealsList?.size ?: 0
    }

    override fun onBindViewHolder(holder: DealListViewHolder, position: Int) {
       val deal = dealsList?.get(position)
        holder.dealBinding.travelDeal = deal

        val width  = Resources.getSystem().displayMetrics.widthPixels

        Picasso.get().load(deal?.imageUrl?.toUri())
            .into(holder.dealBinding.imageView)
    }

    private var dealsList : List<TravelDeals>? = null

    fun setDealList(data : List<TravelDeals>){
        dealsList = data
        notifyDataSetChanged()
    }


}