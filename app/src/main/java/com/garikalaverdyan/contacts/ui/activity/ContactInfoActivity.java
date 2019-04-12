package com.garikalaverdyan.contacts.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.esafirm.imagepicker.features.ImagePicker;
import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.data.ContactRepository;
import com.garikalaverdyan.contacts.App;
import com.garikalaverdyan.contacts.ui.dialog.DialogDeleteContact;
import com.garikalaverdyan.contacts.R;
import com.garikalaverdyan.contacts.ui.dialog.AddPhotoDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ContactInfoActivity extends AppCompatActivity {

    public static Intent createIntent(Context context, int contactId){
        return new Intent(context, ContactInfoActivity.class).putExtra("contact_id", contactId);
    }

    @BindView(R.id.tel_text_view)
    TextView numberTextView;
    @BindView(R.id.contactInfoCircle)
    TextView firstLetterOfTheNameTextView;
    @BindView(R.id.gender_text_view)
    TextView genderTextView;
    @BindView(R.id.day_month_year_text_view)
    TextView dayMonthYearTextView;
    @BindView(R.id.imageCall)
    ImageView callImageView;
    @BindView(R.id.favorite_image_view)
    ImageButton favoriteImageButton;
    @BindView(R.id.editContactsActionBar)
    FloatingActionButton editButton;
    @BindView(R.id.contact_view_header)
    ImageView contactInfoHeaderView;
    public static final int INFO_ACTIVITY_REQUEST_CODE_EDIT = 2;
    private static final int REQUEST_PHONE_CALL = 99;
    private Contact contact;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        ButterKnife.bind(this);

        contact = getContactRepository().getContact(getIntent().getIntExtra("contact_id", 0));
        setInfoView();
    }

    @OnClick(R.id.contact_view_header)
    void openDialog() {
        boolean hasPhoto = false;
        if (contact.getImagePath() != null) {
            hasPhoto = true;
        }
        AddPhotoDialog addPhotoDialog = new AddPhotoDialog(this, new AddPhotoDialog.ResultListener() {
            @Override
            public void onOpenGallery() {
                ContactInfoActivityPermissionsDispatcher.openImagePickerWithPermissionCheck(ContactInfoActivity.this);
            }

            @Override
            public void onRemovePhoto() {
                contactInfoHeaderView.setImageResource(R.drawable.andriod_header);
                getContactRepository().changeImagePath(contact.getId(), null);
                contact.setImagePath(null);
            }
        }, hasPhoto);
        addPhotoDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ContactInfoActivityPermissionsDispatcher.onRequestPermissionsResult(ContactInfoActivity.this, requestCode, grantResults);
    }

    @OnClick(R.id.favorite_image_view)
    void favoriteViewClick() {
        toggleFavorite();
    }

    @OnClick(R.id.editContactsActionBar)
    void editButtonViewClick() {
        Intent intent = new Intent
                (ContactInfoActivity.this, AddAndEditActivity.class);
        intent.putExtra("id", contact.getId());
        intent.setAction(AddAndEditActivity.ACTION_EDIT);
        startActivityForResult(intent, INFO_ACTIVITY_REQUEST_CODE_EDIT);
    }

    @OnClick(R.id.imageCall)
    void imageCallViewClick() {
        callToContact();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                new DialogDeleteContact(this, new DialogDeleteContact.ActionListener() {
                    @Override
                    public void onOkay() {
                        getContactRepository().removeContact(contact.getId());
                        finish();
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
                break;
            }
            case android.R.id.home: {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INFO_ACTIVITY_REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            Contact c = data.getParcelableExtra("Contact");
            contact = new Contact(c.getName(),
                    c.getSurname(),
                    c.getNumber(),
                    c.getGender(),
                    c.getDay(),
                    c.getMonth(),
                    c.getYear(),
                    c.isFavorite(),
                    c.getImagePath());
            contact.setId(getIntent().getIntExtra("contact_id", 0));

            getContactRepository().changeContact(contact);
            setInfoView();

            Toast.makeText(getApplicationContext(),
                    "Change saved", Toast.LENGTH_SHORT).show();
        } else if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            getContactRepository().changeImagePath(contact.getId(), image.getPath());
            contactInfoHeaderView.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
            getContactRepository().changeImagePath(contact.getId(), image.getPath());
            contact.setImagePath(image.getPath());
        }
    }

    @NeedsPermission(value = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void openImagePicker(){
        ImagePicker.create(ContactInfoActivity.this)
                .single()
                .showCamera(true)
                .start();
    }

    private void callToContact() {
        if (ActivityCompat.checkSelfPermission
                (ContactInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions
                    (ContactInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String temp = "tel:" + contact.getNumber();
            callIntent.setData(Uri.parse(temp));
            startActivity(callIntent);
        }
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        setFavoriteStarColor(isFavorite);
        getContactRepository().changeContactFavoriteValue(contact.getId(), isFavorite);
    }

    private void setFavoriteStarColor(boolean colorValue) {
        if (colorValue) {
            favoriteImageButton.setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), R.color.gold)
            );
        } else {
            favoriteImageButton.setColorFilter(
                    ContextCompat.getColor(getApplicationContext(), R.color.icons)
            );
        }
    }

    public void setInfoView() {
        this.isFavorite = contact.isFavorite();
        setTitle(getString(R.string.name_surname, contact.getName(),
                contact.getSurname()));
        numberTextView.setText(getString(R.string.tel, contact.getNumber()));
        genderTextView.setText(getString(R.string.is_gender, contact.getGender()));
        String name = contact.getName().toUpperCase();
        char c = name.charAt(0);
        firstLetterOfTheNameTextView.setText(String.valueOf(c));
        dayMonthYearTextView.setText(getString(R.string.day_month_year_text_view, contact.getDay(),
                contact.getMonth(), contact.getYear()));
        setFavoriteStarColor(isFavorite);

        String path = contact.getImagePath();
        if (path != null) {
            contactInfoHeaderView.setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }

    private ContactRepository getContactRepository() {
        return ((App) getApplication()).getContactRepository();
    }
}