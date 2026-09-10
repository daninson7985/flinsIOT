package com.example.myappeva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private var userList: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userListFull: List<User> = userList

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreItem)
        val tvEmail: TextView = view.findViewById(R.id.tvEmailItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario_list, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvNombre.text = "${user.nombre} ${user.apellidos}"
        holder.tvEmail.text = user.email
        
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int = userList.size

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            userListFull
        } else {
            val filterPattern = normalizeStr(query.lowercase().trim())
            userListFull.filter {
                normalizeStr(it.nombre.lowercase()).contains(filterPattern) || 
                normalizeStr(it.apellidos.lowercase()).contains(filterPattern)
            }
        }
        userList = filteredList
        notifyDataSetChanged()
    }

    private fun normalizeStr(str: String): String {
        val nfdNormalizedString = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD)
        val regex = Regex("\\p{InCombiningDiacriticalMarks}+")
        return regex.replace(nfdNormalizedString, "")
    }
    
    fun updateData(newList: List<User>) {
        userList = newList
        userListFull = newList
        notifyDataSetChanged()
    }
}