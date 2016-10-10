package mahappdev.caresilabs.com.myfriends.views;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mahappdev.caresilabs.com.myfriends.CameraHelper;
import mahappdev.caresilabs.com.myfriends.ChatListAdapter;
import mahappdev.caresilabs.com.myfriends.R;
import mahappdev.caresilabs.com.myfriends.controllers.MainController;
import mahappdev.caresilabs.com.myfriends.models.DataModel;

public class ChatFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0xfe;

    @BindView(R.id.lwChat)
    ListView lwChat;

    @BindView(R.id.etChat)
    EditText etChat;

    @BindView(R.id.ibPhoto)
    ImageButton ibPhoto;

    private MainController  controller;
    private ChatListAdapter chatListAdaper;
    private String          pictureUri;
    private Bitmap          nextImage;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatListAdaper = new ChatListAdapter(getActivity(), new ArrayList<DataModel.ChatModel>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        lwChat.setAdapter(chatListAdaper);

        return view;
    }

    @OnClick(R.id.btnChatSend)
    void onChatSend() {
        if (etChat.getText().toString().trim().equals(""))
            return;

        controller.sendChatMessage(etChat.getText().toString(), nextImage != null);

        etChat.getText().clear();
    }

    @OnClick(R.id.ibPhoto)
    void onTakePhoto() {
        if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            pictureUri = CameraHelper.startCamera(this);
        }
    }

    public void addChatMessage(DataModel.ChatModel chat) {
        chatListAdaper.add(chat);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void clearChats() {
        chatListAdaper.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraHelper.PICTURE && resultCode == Activity.RESULT_OK) {
            String pathToPicture = pictureUri;

            nextImage = CameraHelper.getScaled(pathToPicture, 480, 480);
            ibPhoto.setImageBitmap(nextImage);
        }
    }

    public byte[] consumeNextPhoto() {
        byte[] image = null;

        if (nextImage != null) {
            ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
            nextImage.compress(Bitmap.CompressFormat.JPEG, 60, baoStream);
            image = baoStream.toByteArray();
            nextImage.recycle();
            nextImage = null;
        }

        ibPhoto.setImageResource(android.R.drawable.ic_menu_camera);
        return image;
    }

    public String saveBitmap(Bitmap bitmap) {
        try {
            File dest = CameraHelper.createFile(this);
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return  dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   onTakePhoto();
                }
                return;
            }
            default:
                break;
        }
    }
}
