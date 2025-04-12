package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ConnectedDeviceDetailsListItemBinding
import com.example.myapplication.dataclasses.connectedDeviceDetailsDataClass

class connectedDeviceDetailsAdapter(private val deviceInfo : List<connectedDeviceDetailsDataClass>) :
    RecyclerView.Adapter<connectedDeviceDetailsAdapter.connectedDeviceDetailsViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): connectedDeviceDetailsAdapter.connectedDeviceDetailsViewHolder {
        val binding = ConnectedDeviceDetailsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return connectedDeviceDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: connectedDeviceDetailsViewHolder,
        position: Int
    ) {
        holder.bind(deviceInfo[position])
    }

    override fun getItemCount(): Int {
        return deviceInfo.size
    }

    inner class connectedDeviceDetailsViewHolder(private val binding: ConnectedDeviceDetailsListItemBinding) :
            RecyclerView.ViewHolder(binding.root){

        fun bind(deviceInfo : connectedDeviceDetailsDataClass){
            binding.connectedDevicePropertyId.text = deviceInfo.deviceProperties
            binding.connectedDeviceValueId.text = deviceInfo.deviceValues
        }

    }

}