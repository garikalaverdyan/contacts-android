package com.garikalaverdyan.contacts.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.garikalaverdyan.contacts.R;

public class DialogDeleteContact extends Dialog {
    @BindView(R.id.action_alert_dialog_yes_button)
    Button yesButton;
    @BindView(R.id.action_alert_dialog_no_button)
    Button noButton;
    private ActionListener actionListener;

    public interface ActionListener {
        void onOkay();
        void onCancel();
    }

    public DialogDeleteContact(Context context, ActionListener listener) {
        super(context, R.style.DeleteContactDialog);
        this.actionListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_alert_dialog);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.action_alert_dialog_yes_button)
    void deleteContact(){
        actionListener.onOkay();
        dismiss();
    }

    @OnClick(R.id.action_alert_dialog_no_button)
    void closeDialog(){
        actionListener.onCancel();
        dismiss();
    }
}