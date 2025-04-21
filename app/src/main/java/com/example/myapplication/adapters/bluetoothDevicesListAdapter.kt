package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.DevicesAvailableListItemBinding
import com.example.myapplication.dataclasses.bluetoothDevicesListDataClass

class bluetoothDevicesListAdapter(
    private val devices: List<bluetoothDevicesListDataClass>,
    private val onItemClick : (bluetoothDevicesListDataClass) -> Unit
) :
    RecyclerView.Adapter<bluetoothDevicesListAdapter.bluetoothDevicesListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): bluetoothDevicesListAdapter.bluetoothDevicesListViewHolder {
        val binding = DevicesAvailableListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return bluetoothDevicesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: bluetoothDevicesListViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    inner class bluetoothDevicesListViewHolder(private val binding: DevicesAvailableListItemBinding) :
            RecyclerView.ViewHolder(binding.root){

        fun bind(devices: bluetoothDevicesListDataClass){
            binding.availableDeviceNameId.text = devices.name
            binding.availableDeviceMaccodeId.text = devices.mac_code

            binding.root.setOnClickListener{
                onItemClick(devices)
            }

        }

    }

}
