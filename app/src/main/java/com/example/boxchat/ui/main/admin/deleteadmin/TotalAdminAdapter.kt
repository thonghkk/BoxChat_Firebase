package com.example.boxchat.ui.main.admin.deleteadmin

import android.app.Dialog
import android.provider.Settings.Secure.getString
import android.view.*
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.commom.Firebase
import com.example.boxchat.model.User

class TotalAdminAdapter(var user: List<User>) :
    RecyclerView.Adapter<TotalAdminAdapter.ViewHolder>(),Filterable {
    private val userOld: List<User> = user

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mAvatar: ImageView = view.findViewById(R.id.mAvatar)
        private val mName: TextView = view.findViewById(R.id.mNameUser)
        private val mAddAdmin: ImageView = view.findViewById(R.id.mAddAdmin)
        private val mDeleteAdmin: ImageView = view.findViewById(R.id.mDeleteAdmin)

        fun bindItems(users: User) {
            mAddAdmin.visibility = View.GONE
            mDeleteAdmin.visibility = View.VISIBLE
            mName.text = users.userName
            Glide.with(itemView)
                .load(users.userProfileImage)
                .placeholder(R.mipmap.ic_avatar)
                .fitCenter()
                .into(mAvatar)

            mDeleteAdmin.setOnClickListener {
                dialog(users.userId)
            }
        }

        private fun dialog(userId:String) {
            val mDialog = Dialog(itemView.context)
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog.setContentView(R.layout.dialog_sign_out)

            val mTitle = mDialog.findViewById<TextView>(R.id.mTitleDialog)
            val mQuestion = mDialog.findViewById<TextView>(R.id.mQuestionDialog)
            mTitle.visibility = View.GONE
            mQuestion.setText(R.string.textView_text_delete_admin)

            val window = mDialog.window
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            val windowAtt: WindowManager.LayoutParams = window?.attributes!!
            windowAtt.gravity = View.TEXT_ALIGNMENT_GRAVITY
            window.attributes = windowAtt
            mDialog.show()

            mDialog.findViewById<Button>(R.id.mBtnNo).setOnClickListener {
                mDialog.dismiss()
            }

            mDialog.findViewById<Button>(R.id.mBtnYes).setOnClickListener {
                addAdmin(userId)
                mDialog.dismiss()
            }
        }

        private fun addAdmin(userId: String) {
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["userId"] = userId
            Firebase.firebaseDatabase.getReference("Admin").child(userId).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(itemView.context, "Delete Admin Successful", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manager_new_admin, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(user[position])
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