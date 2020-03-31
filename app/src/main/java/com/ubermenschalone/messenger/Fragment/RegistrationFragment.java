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
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ubermenschalone.messenger.Activity.TabbedActivity;
import com.ubermenschalone.messenger.Interface.SwitchFragment;
import com.ubermenschalone.messenger.Model.User;
import com.ubermenschalone.messenger.R;

import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private SwitchFragment switchFragment;

    //UI
    ImageView imageViewProfileImage;
    TextView textViewSetProfileImage;
    EditText editTextUsername;
    EditText editTextName;
    EditText editTextLastname;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonSignUp;

    //Firebase
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference storageReference;
    String authenticationID;

    //Add Image
    private static final int IMAGE_REQUEST = 1;
    private Uri filePath;
    String imageProfileRef = "STANDARD";
    private StorageTask uploadTask;
    Boolean imagePick = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);

        imageViewProfileImage = rootView.findViewById(R.id.imageViewProfileImage);
        textViewSetProfileImage = rootView.findViewById(R.id.textViewSetProfileImage);
        editTextUsername = rootView.findViewById(R.id.editTextUsername);
        editTextName = rootView.findViewById(R.id.editTextName);
        editTextLastname = rootView.findViewById(R.id.editTextLastname);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        buttonSignUp = rootView.findViewById(R.id.buttonSignUp);

        textViewSetProfileImage.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSetProfileImage:
                SelectImage();
                break;
            case R.id.buttonSignUp:
                checkField();
                break;
        }
    }

    public void checkField() {
        if ((editTextUsername.getText().length() != 0)) {
            if ((editTextName.getText().length() != 0)) {
                if ((editTextLastname.getText().length() != 0)) {
                    if ((editTextEmail.getText().length() != 0)) {
                        if ((editTextPassword.getText().length() != 0)) {
                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            onCreateAcc(String.valueOf(editTextEmail.getText()), String.valueOf(editTextPassword.getText()));
                        } else {
                            Toast.makeText(getContext(), "Ошибка: заполните поле \"Пароль\"", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Ошибка: заполните поле \"Email\"", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Ошибка: заполните поле \"Lastname\"", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Ошибка: заполните поле \"Name\"", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Ошибка: заполните поле \"Username\"", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateAcc(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                    authenticationID = user.getUid();

                    SharedPreferences sharedPreferencesID = getActivity().getSharedPreferences("UserID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesID.edit();
                    editor.putString("UserID", authenticationID).apply();

                    if (imagePick) {
                        uploadImage();
                    } else {
                        Login();
                    }

                } else {
                    Toast.makeText(getContext(), "Ошибка: некорректный Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeNewUser() {
        User user = new User();
        user.userID = authenticationID;
        user.userProfileImageURL = imageProfileRef;
        user.userUsername = String.valueOf(editTextUsername.getText());
        user.userUsernameSearch = String.valueOf(editTextUsername.getText()).toLowerCase();
        user.userEmail = String.valueOf(editTextEmail.getText());
        user.userName = String.valueOf(editTextName.getText());
        user.userLastname = String.valueOf(editTextLastname.getText());
        user.userPassword = String.valueOf(editTextPassword.getText());
        user.userStatus = "offline";
        myRef.child("Users/" + authenticationID).setValue(user);
    }

    private void SelectImage() {
        imagePick = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
                        imageProfileRef = mUri;
                        Login();
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

    public void Login() {
        writeNewUser();
        Intent intent = new Intent(getActivity(), TabbedActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                Glide.with(getContext()).load(bitmap).override(512, 512).into(imageViewProfileImage);
                imageViewProfileImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SwitchFragment) {
            switchFragment = (SwitchFragment) context;
        }
    }

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }
}
