package com.tri.felipe.safeback.View.Skeleton;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.tri.felipe.safeback.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageDrawerFragment extends Fragment {

    private static final int LOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static Uri fileUri;
    private Button mLoadImage;
    private Button mCaptureImage;
    private ImageView mImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View rootView = inflater.inflate(R.layout.fragment_image_drawer, container, false);
        mImage = (ImageView) rootView.findViewById(R.id.image_drawer_image);

        mLoadImage = (Button) rootView.findViewById(R.id.image_drawer_select_image_button);
        mLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadImage = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(loadImage, LOAD_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        mCaptureImage = (Button) rootView.findViewById(R.id.image_drawer_camera_button);
        mCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePicture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //Update gallery to show newly captured image
            scanFile(fileUri.getPath());

            mImage.setImageURI(fileUri);
            Log.d("test", "fileUri path:  " + fileUri.toString());

        } else if (requestCode == LOAD_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            mImage.setImageURI(selectedImage);
            Log.d("test", "selectedImage path:  " + selectedImage.toString());
        }
    }

    private Uri getOutputMediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SafeBack");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("SafeBack", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return Uri.fromFile(mediaFile);
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(getActivity(),
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }
}
