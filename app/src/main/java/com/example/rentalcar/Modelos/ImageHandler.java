package com.example.rentalcar.Modelos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.annotation.Nullable;

public class ImageHandler {
    public static final int PICK_IMAGE_REQUEST = 1;
    private Activity activity;
    private Bitmap selectedImage; // Agrega esta línea

    public ImageHandler(Activity activity) {
        this.activity = activity;
    }

    public void requestImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void onImageResult(int requestCode, int resultCode, @Nullable Intent data, ImageView imageView) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri); // Almacena la imagen
                imageView.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getImageBytes() {
        if (selectedImage != null) {
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }
}
