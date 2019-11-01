package com.app.imageandfilepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView iv_image;
    TextView tv_image_string;

    int onClickedImageId;
    String str_profile_pic = "", str_medical_history = "";
    private Uri fileUri = null;
    private String picturePath = null;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PHOTO_REQUEST_CUT = 300;
    private static final int GELLARY_PICK_IMAGE = 200;
    public static final String IMAGE_DIRECTORY_NAME = "ImagePicker";
    public static final int REQUEST_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_image = findViewById(R.id.iv_image);
        tv_image_string = findViewById(R.id.tv_image_string);
    }

    public void selectImage(View view) {
        requestPermission();
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now

                            showImagePickerOptions();

//                            CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//                            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            cdd.show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            Toast.makeText(MainActivity.this, "Please allow us to use this features", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

//    public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {
//
//        public Activity c;
//        public LinearLayout camera, file;
//
//        public CustomDialogClass(Activity a) {
//            super(a);
//            this.c = a;
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.file_picker_dialog);
//
//            camera = findViewById(R.id.camera);
//            file = findViewById(R.id.file);
//            camera.setOnClickListener(this);
//            file.setOnClickListener(this);
//
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.camera:
//                    // open camera
//                    //Toast.makeText(MainActivity.this, "Please capture lower resolution image", Toast.LENGTH_SHORT).show();
//                    //cameraIntent();
//
//                    showImagePickerOptions();
//
//                    dismiss();
//                    break;
//
//                case R.id.file:
//                    // open file picker
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GELLARY_PICK_IMAGE);
//
//                    Toast.makeText(MainActivity.this, "Please select small file", Toast.LENGTH_SHORT).show();
//
//                    dismiss();
//                    break;
//                default:
//                    break;
//            }
//            dismiss();
//        }
//    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    String encodedImage = SharedMethods.encodeImageBitmap(bitmap);
                    // loading profile image from local cache
                    //loadProfile(uri.toString());

                    setImage(bitmap, encodedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setImage(Bitmap selectedImage, String encodedImage) {
        iv_image.setImageBitmap(selectedImage);

        str_profile_pic = encodedImage;
        tv_image_string.setText(str_profile_pic);
    }
}
