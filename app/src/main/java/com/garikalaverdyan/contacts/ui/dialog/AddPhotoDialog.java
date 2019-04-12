package com.garikalaverdyan.contacts.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.view.View;
import android.widget.TextView;
import com.garikalaverdyan.contacts.R;

public class AddPhotoDialog extends Dialog {
    @BindView(R.id.choose_photo)
    TextView choosePhotoView;
    @BindView(R.id.change_photo_cancel_view)
    TextView cancelView;
    @BindView(R.id.remove_photo)
    TextView textView;
    private final ResultListener resultListener;
    private final boolean hasPhoto;

    public interface ResultListener {
        void onOpenGallery();
        void onRemovePhoto();
    }

    public AddPhotoDialog(Context context, ResultListener resultListener, Boolean hasPhoto) {
        super(context, R.style.AddPhotoDialog);
        this.resultListener = resultListener;
        this.hasPhoto = hasPhoto;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.contact_info_popup_window);
        ButterKnife.bind(this);
        if (hasPhoto) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.choose_photo)
    void openGallery() {
        resultListener.onOpenGallery();
        dismiss();
    }

    @OnClick(R.id.change_photo_cancel_view)
    void closeDialog() {
        dismiss();
    }

    @OnClick(R.id.remove_photo)
    void removePhoto() {
        resultListener.onRemovePhoto();
        dismiss();
    }
}