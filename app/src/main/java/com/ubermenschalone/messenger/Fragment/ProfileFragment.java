package com.ubermenschalone.messenger.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.squareup.picasso.Picasso;
import com.ubermenschalone.messenger.Activity.AuthenticationActivity;
import com.ubermenschalone.messenger.Interface.SwitchFragment;
import com.ubermenschalone.messenger.Model.User;
import com.ubermenschalone.messenger.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    SwitchFragment switchFragment;

    ImageView imageViewProfileImage;
    TextView textViewSetProfileImage;
    TextView textViewEmail;
    TextView textViewName;
    TextView textViewLastname;
    Button buttonLogout;


    DatabaseReference myRef;

    private static final int IMAGE_REQUEST = 1;
    private Uri filePath;
    private StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseStorage storage;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container,false);

        imageViewProfileImage = rootView.findViewById(R.id.imageViewProfileImage);
        textViewSetProfileImage = rootView.findViewById(R.id.textViewSetProfileImage);
        textViewEmail = rootView.findViewById(R.id.textViewEmail);
        textViewName = rootView.findViewById(R.id.textViewName);
        textViewLastname = rootView.findViewById(R.id.textViewLastname);
        buttonLogout = rootView.findViewById(R.id.buttonLogout);

        textViewSetProfileImage.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        SharedPreferences SharedPreferencesID;
        SharedPreferencesID = getActivity().getSharedPreferences("UserID", Context.MODE_PRIVATE);
        String UserID = SharedPreferencesID.getString("UserID", String.valueOf(Context.MODE_PRIVATE));


        myRef = database.getReference("Users/" + UserID);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                Picasso.get().load(value.userProfileImageURL).resize(300, 300).centerCrop().into(imageViewProfileImage);
                textViewEmail.setText(value.userEmail);
                textViewName.setText(value.userName);
                textViewLastname.setText(value.userLastname);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Log.w(TAG, "Не удалось прочитать значение", error.toException());
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLogout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.textViewSetProfileImage:
                SelectImage();
                break;
        }
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageViewProfileImage.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (filePath != null) {
            final StorageReference fileReference = storageReference.child(UUID.randomUUID().toString() + "." + getFileExtension(filePath));

            uploadTask = fileReference.putFile(filePath);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        myRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("userProfileImageURL", "" + mUri);
                        myRef.updateChildren(map);

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
            pd.dismiss();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof SwitchFragment){
            switchFragment = (SwitchFragment) context;
        }
    }

    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }
}
