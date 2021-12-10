package hu.itatti.saunamaster.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.itatti.saunamaster.data.MasterList
import hu.itatti.saunamaster.databinding.MasterlistRowBinding

class MasterListAdapter(uid: String) :
    RecyclerView.Adapter<MasterListAdapter.ViewHolder>() {

    private var masterList = mutableListOf<MasterList>()
    private var masterKeys = mutableListOf<String>()

    private var currentUid:String = uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterListAdapter.ViewHolder {
        val binding = MasterlistRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return masterList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val masterListRow = masterList[holder.adapterPosition]
        holder.tvName.text = masterListRow.name
        holder.tvDate.text = "${masterListRow.date},   ${masterListRow.hour}"
        holder.tvAddress.text = masterListRow.address

        if(currentUid == masterListRow.uid) {
            holder.btnDelete.visibility = View.VISIBLE
        }else{
            holder.btnDelete.visibility = View.GONE
        }
        holder.btnDelete.setOnClickListener {
            removeEvent(holder.adapterPosition)
        }
        holder.btnMap.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${holder.tvAddress.text}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            val context=holder.btnMap.context
            context.startActivity(mapIntent)

        }
    }

    fun addEvent (masterList1: MasterList, key:String){
        masterList.add(masterList1)
        masterKeys.add(key)
        notifyItemInserted(masterList.lastIndex)
    }

    private fun removeEvent(adapterPosition: Int) {
        FirebaseFirestore.getInstance().collection("events").document(
            masterKeys[adapterPosition]
        ).delete()

        masterList.removeAt(adapterPosition)
        masterKeys.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    fun removeEventByKey (key: String){
        val index= masterKeys.indexOf(key)
        if(index!= -1){
            masterList.removeAt(index)
            masterKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(binding: MasterlistRowBinding): RecyclerView.ViewHolder(binding.root) {
       var tvName = binding.tvName
       var tvDate = binding.tvDate
       var tvAddress = binding.tvAddress
       var btnDelete = binding.btnDelete
       var btnMap = binding.btnMap
    }
}