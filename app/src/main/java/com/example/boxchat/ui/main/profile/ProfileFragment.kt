

package com.example.boxchat.ui.main.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.model.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

const val PICK_IMAGE_REQUEST = 101


class ProfileFragment: BaseFragment() {

    private lateinit var firebaseUser: FirebaseUser

    //import to read/write data from firebase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private var filePath: Uri? = null

    private lateinit var mUserName: TextView
    private lateinit var mUserAvatar: CircleImageView
    private lateinit var mBtnBackProfile: ImageView
    private lateinit var mBtnSaveProfile: Button
    private lateinit var mProgress: ProgressBar
    private lateinit var mProfile: LinearLayout
    private lateinit var mSignOut: ImageView

    override fun getLayoutID() = R.layout.fragment_profile

    override fun onViewReady(view: View) {

        mUserName = view.findViewById(R.id.mUserName)
        mUserAvatar = view.findViewById(R.id.mAvatarProfile)
        mBtnSaveProfile = view.findViewById(R.id.mBtnSaveProfile)
        mProgress = view.findViewById(R.id.mProgressLoadProfile)
        mProfile = view.findViewById(R.id.mProfile)

        //Gets the FirebaseAuth instance for the default FirebaseApp.
        auth = FirebaseAuth.getInstance()

        //get information of current user
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        //databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        storageRef = storage.reference

        Log.d("Storage ref", storageRef.toString())

        //To read data at a path and listen to read the changes
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)
                mUserName.text = user?.userName

                if (user?.userProfileImage == "") {
                    mUserAvatar.setImageResource(R.mipmap.ic_avatar)
                } else {
                    Glide.with(context!!)
                        .load(user?.userProfileImage)
                        .fitCenter()
                        .circleCrop()
                        .into(mUserAvatar)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        mUserAvatar.setOnClickListener {
            //chooseImage()
            dialogViewProfile()
        }
        mBtnSaveProfile.setOnClickListener {
            uploadImage()
        }
    }
    //choose a image to upload (using bitmap)
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            filePath = data?.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                mUserAvatar.setImageBitmap(bitmap)
                mBtnSaveProfile.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //upload image of use to database(Firebase)
    private fun uploadImage() {
        if (filePath != null) {
            mProgress.visibility = View.VISIBLE
            // Create a reference to "image/*"
            val ref: StorageReference = storageRef.child("image/*" + UUID.randomUUID().toString())

            ref.putFile(filePath!!)
                .addOnSuccessListener {

                    val hashMap: HashMap<String, String> = HashMap()
                    //  hashMap["userName"] = mUserName.text.toString()

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("Link?", it.toString())
                        saveUser(it.toString())
                    }

                    Log.d("Hash Map", hashMap["userProfileImage"].toString())
                    mProgress.visibility = View.GONE
                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
                    mBtnSaveProfile.visibility = View.GONE

                    Log.d("Link2", filePath.toString())
                }
                .addOnFailureListener {
                    mProgress.visibility = View.GONE
                    Toast.makeText(context, "Error + ${it.message}", Toast.LENGTH_SHORT).show()
                }
            Log.d("Link2", ref.toString())
        }

    }

    //save picture of user
    private fun saveUser(urlString: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["userProfileImage"] = urlString
        databaseReference.updateChildren(hashMap as HashMap<String, Any>)
    }


    //Custom Dialog show Image and update image
    @SuppressLint("InflateParams")
    fun dialogViewProfile() {
        val bottomSheetDialog = BottomSheetDialog(
            requireContext(), R.style.BottomSheetDialogTheme
        )
        val mLinearLayout = view?.findViewById<LinearLayout>(R.id.mDialog)

        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.view_profile_image, mLinearLayout)
//        val mBuilder = AlertDialog.Builder(this)
//            .setView(mDialogView)
//        val mAlertDialog = mBuilder.show()
        bottomSheetDialog.setContentView(mDialogView)
        bottomSheetDialog.show()

        mDialogView.findViewById<LinearLayout>(R.id.mDialogChangeProfilePicture)
            .setOnClickListener {
                chooseImage()
                bottomSheetDialog.dismiss()
                Log.d("Bitmap",chooseImage().toString())
            }
        mDialogView.findViewById<LinearLayout>(R.id.mDialogViewProfilePicture).setOnClickListener {
            databaseReference.addValueEventListener(object : ValueEventListener {
                @SuppressLint("ResourceType")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.dialog_show_image)
                    val img = dialog.findViewById<ImageView>(R.id.showImage)
                    val btnBack = dialog.findViewById<ImageView>(R.id.mBtnBackZoom)

                    Glide.with(context!!)
                        .load(user?.userProfileImage)
                        .fitCenter()
                        .into(img)
                    btnBack.setOnClickListener {
                        dialog.cancel()
                    }
                    dialog.show()

                    Log.d("Get Id", user?.userProfileImage.toString())
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            bottomSheetDialog.dismiss()
        }
    }

}

