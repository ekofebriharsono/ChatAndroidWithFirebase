package com.maseko.root.lugchatv1.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.maseko.root.lugchatv1.MainActivity;
import com.maseko.root.lugchatv1.Model.User;
import com.maseko.root.lugchatv1.R;
import com.maseko.root.lugchatv1.StartActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_image)
    CircleImageView image_profile;

    @BindView(R.id.username)
    TextView username;

    @BindView(R.id.status)
    TextView status;

    DatabaseReference reference;
    FirebaseUser fuser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                status.setText(user.getStatus_msg());
                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.drawable.ic_person_large);
                } else {
                    Glide.with(getActivity()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    @OnClick(R.id.btnKeluar)
    public void userLogout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @OnClick(R.id.status)
    public void changeStatusUser(){
       final Dialog dialogChangeStatusUser = new Dialog(getContext());
        dialogChangeStatusUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangeStatusUser.setContentView(R.layout.layout_dialog_edit_status);

        final EditText editText = dialogChangeStatusUser.findViewById(R.id.edStatus);
        TextView simpan = dialogChangeStatusUser.findViewById(R.id.simpan);

        editText.setText(status.getText().toString());

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage("Uploading");
                pd.show();

                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("status_msg", ""+editText.getText().toString());
                reference.updateChildren(map);

                pd.dismiss();

                dialogChangeStatusUser.dismiss();

            }
        });

        dialogChangeStatusUser.show();
    }

    @OnClick(R.id.username)
    public void changeUsernameUser(){
        final Dialog dialogChangeStatusUser = new Dialog(getContext());
        dialogChangeStatusUser.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangeStatusUser.setContentView(R.layout.layout_dialog_edit_status);

        final EditText editText = dialogChangeStatusUser.findViewById(R.id.edStatus);
        TextView simpan = dialogChangeStatusUser.findViewById(R.id.simpan);

        editText.setText(username.getText().toString());

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage("Uploading");
                pd.show();

                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("username", ""+editText.getText().toString());
                reference.updateChildren(map);

                pd.dismiss();

                dialogChangeStatusUser.dismiss();

            }
        });

        dialogChangeStatusUser.show();
    }

}
