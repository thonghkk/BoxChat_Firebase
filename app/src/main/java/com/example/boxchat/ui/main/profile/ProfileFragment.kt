package com.example.boxchat.ui.main.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.boxchat.R
import com.example.boxchat.base.BaseFragment
import com.example.boxchat.commom.Firebase
import com.example.boxchat.utils.CheckNetwork.Companion.checkNetwork
import com.example.boxchat.utils.CheckNetwork.Companion.getContextThis
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

const val PICK_IMAGE_REQUEST = 88

class ProfileFragment : BaseFragment() {
    //import to read/write data from firebase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var filePath: Uri? = null
    private lateinit var mProfileViewModel: ProfileViewModel
    private lateinit var mUserName: TextView
    private lateinit var mUserAvatar: CircleImageView
    private lateinit var mBtnSaveProfile: Button
    private lateinit var mProgress: ProgressBar
    private lateinit var mHomeTown: TextView
    private lateinit var mBirthDay: TextView
    private lateinit var mTxtDescriptionProfile: TextView
    private lateinit var mEnglishCertificate: TextView

    override fun getLayoutID() = R.layout.fragment_profile
    override fun onViewReady(view: View) {
        mUserName = view.findViewById(R.id.mUserName)
        mUserAvatar = view.findViewById(R.id.mAvatarProfile)
        mBtnSaveProfile = view.findViewById(R.id.mBtnSaveProfile)
        mProgress = view.findViewById(R.id.mProgressLoadProfile)
        mHomeTown = view.findViewById(R.id.mTxtHomeTown)
        mBirthDay = view.findViewById(R.id.mTxtBirthDay)
        mTxtDescriptionProfile = view.findViewById(R.id.mTxtDescriptionProfile)
        mEnglishCertificate = view.findViewById(R.id.mTxtEnglishCertificate)
        mProfileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        storageRef = storage.reference
        Log.d("Storage ref", storageRef.toString())

        //To read data at a path and listen to read the changes
        getContextThis(requireContext())
        if (checkNetwork()) {
            getYourself()
        } else {
            getYourselfLocal()
        }

        mBtnSaveProfile.setOnClickListener {
            uploadImage()
        }
        mUserAvatar.setOnClickListener {
            dialogViewProfile()
        }
    }

    private fun getYourself() {
        mProfileViewModel.me.observe(this, Observer { me ->
            for (i in me) {
                mUserName.text = i.userName
                mHomeTown.text = i.userHomeTown
                mBirthDay.text = i.userBirthDay
                mTxtDescriptionProfile.text = i.userDescription
                if (i.userProfileImage == "") {
                    mUserAvatar.setImageResource(R.mipmap.ic_avatar)
                } else {
                    Glide.with(requireContext())
                        .load(i.userProfileImage)
                        .fitCenter()
                        .circleCrop()
                        .into(mUserAvatar)
                }
            }
        })
    }

    private fun getYourselfLocal() {
        mProfileViewModel.readAllDataFromMe.observe(
            viewLifecycleOwner, Observer { me ->
                for (i in me) {
                    mUserName.text = i.userName
                    if (i.userProfileImage == "") {
                        mUserAvatar.setImageResource(R.mipmap.ic_avatar)
                    } else {
                        Glide.with(this)
                            .load(i.userProfileImage)
                            .fitCenter()
                            .circleCrop()
                            .into(mUserAvatar)
                    }
                }
            })
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            filePath = data?.data
            try {
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
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
                    ref.downloadUrl.addOnSuccessListener {
                        saveUser(it.toString())
                    }
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
        mProfileViewModel.databaseReferenceProfile.child(Firebase.auth.uid!!)
            .updateChildren(hashMap as Map<String, Any>)
    }

    //Custom Dialog show Image and update image
    private fun dialogViewProfile() {
        val bottomSheetDialog = BottomSheetDialog(
            requireContext(), R.style.BottomSheetDialogTheme
        )
        val mLinearLayout = view?.findViewById<LinearLayout>(R.id.mDialog)
        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_change_profile_image, mLinearLayout)
        bottomSheetDialog.setContentView(mDialogView)
        bottomSheetDialog.show()

        //change avatar
        mDialogView.findViewById<LinearLayout>(R.id.mDialogChangeProfilePicture)
            .setOnClickListener {
                if (checkNetwork()) {
                    chooseImage()
                    bottomSheetDialog.dismiss()
                } else {
                    Toast.makeText(context, "Connect Internet To Change !", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        //view avatar
        mDialogView.findViewById<LinearLayout>(R.id.mDialogViewProfilePicture).setOnClickListener {
            if (checkNetwork()) {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_show_image)
                val img = dialog.findViewById<ImageView>(R.id.showImage)
                val btnBack = dialog.findViewById<ImageView>(R.id.mBtnBackZoom)

                mProfileViewModel.me.observe(this, Observer { me ->
                    for (i in me) {
                        Glide.with(requireContext())
                            .load(i.userProfileImage)
                            .fitCenter()
                            .into(img)
                    }
                })
                btnBack.setOnClickListener {
                    dialog.cancel()
                }
                dialog.show()
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(context, "Connect Internet To View !", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

