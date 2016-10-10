package mahappdev.caresilabs.com.myfriends;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Simon on 10/6/2016.
 */

public class CameraHelper {
    public static final int PICTURE = 0xf6;

    public static String startCamera(Fragment fragment) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {

                File image = createFile(fragment);

                //String mCurrentPhotoPath = "file:" + image.getAbsolutePath();

                Uri pictureUri = FileProvider.getUriForFile(fragment.getActivity(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        image);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                fragment.startActivityForResult(intent, PICTURE);
                return image.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getScaled(String pathToPicture, int targetW, int targetH) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToPicture, bmOptions);


        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(pathToPicture, bmOptions);
        return bitmap;
    }

    public static File createFile(Fragment fragment) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String filename = "chatmsg_" + timeStamp;
        File dir = fragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(
                    filename,  /* prefix */
                    ".jpg",         /* suffix */
                    dir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        return image;
    }
}
