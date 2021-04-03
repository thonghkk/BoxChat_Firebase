package com.example.boxchat.ui.main.users

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.databaselocal.entity.UserLocal
import com.example.boxchat.model.User
import com.example.boxchat.ui.main.chat.ChatActivity

class UserAdapter(private var user: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(),Filterable {
    private val userOld: List<User> = user
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUserName: TextView = itemView.findViewById(R.id.mNameUser)
        private val imgAvatar: ImageView = itemView.findViewById(R.id.mAvatar)
        private val mLayoutUser: LinearLayout = itemView.findViewById(R.id.mLayoutUser)
        fun bindUser(user: User) {
            txtUserName.text = user.userName
            val url = user.userProfileImage
            Glide.with(itemView)
                .load(url)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .into(imgAvatar)
            Log.d("test", url)
            mLayoutUser.setOnClickListener {
                val intent = Intent(itemView.context, ViewStrangerActivity::class.java)
                intent.putExtra("userId", user.userId)
                intent.putExtra("userName", user.userName)
                intent.putExtra("userImage", user.userProfileImage)
                intent.putExtra("userHomeTown", user.userHomeTown)
                intent.putExtra("userBirthDay", user.userBirthDay)
                intent.putExtra("userEnglishCertificate", user.userEnglishCertificate)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.items_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindUser(user[position])
    }

    override fun getItemCount() = user.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val strSearch = constraint.toString()
                if (strSearch.isEmpty()) {
                    user = userOld
                } else {
                    val list = mutableListOf<User>()
                    for (i in userOld) {
                        if (i.userName.toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(i)
                        }
                    }
                    user = list
                }

                val filterResult = FilterResults()
                filterResult.values = user
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                user = results?.values as List<User>
                notifyDataSetChanged()
            }

        }
    }

}