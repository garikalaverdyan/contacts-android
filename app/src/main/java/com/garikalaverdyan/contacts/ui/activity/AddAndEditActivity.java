package com.garikalaverdyan.contacts.ui.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.garikalaverdyan.contacts.data.Contact;
import com.garikalaverdyan.contacts.data.ContactRepository;
import com.garikalaverdyan.contacts.ui.fragment.DatePickerFragment;
import com.garikalaverdyan.contacts.App;
import com.garikalaverdyan.contacts.R;
import com.garikalaverdyan.contacts.utils.ChangeStatusBarColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAndEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.nameID)
    EditText nameText;
    @BindView(R.id.surnameID)
    EditText surnameText;
    @BindView(R.id.phoneID)
    EditText numberText;
    @BindView(R.id.button_calendar_view)
    Button dateButton;
    @BindView(R.id.calendar_view_day)
    TextView dayView;
    @BindView(R.id.calendar_view_month)
    TextView monthView;
    @BindView(R.id.calendar_view_year)
    TextView yearView;
    @BindView(R.id.gender_spinner)
    Spinner spinner;
    @BindView(R.id.addAndEdit_activity_toolbar)
    Toolbar toolbar;
    public static final String ACTION_ADD = "action-add";
    public static final String ACTION_EDIT = "action-edit";
    private String[] gender;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        ArrayAdapter adapter =
                ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        gender = getResources().getStringArray(R.array.gender);

        Intent intent = getIntent();

        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_EDIT)) {
            Contact contact = getContactRepository().getContact(intent.getIntExtra("id", 0));
            nameText.setText(contact.getName());
            surnameText.setText(contact.getSurname());
            numberText.setText(contact.getNumber());
            dayView.setText(contact.getDay());
            monthView.setText(contact.getMonth());
            yearView.setText(contact.getYear());
            isFavorite = contact.isFavorite();
            spinner.setSelection(adapter.getPosition(contact.getGender()));
        }

        new ChangeStatusBarColor(getWindow(), this);
    }

    @OnClick(R.id.button_calendar_view)
    void openCalendar() {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "date_picker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_done: {
                if (TextUtils.isEmpty(nameText.getText())) {
                    showError(nameText);
                    break;
                } else if (TextUtils.isEmpty(surnameText.getText())) {
                    showError(surnameText);
                    break;
                } else if (TextUtils.isEmpty(numberText.getText())) {
                    showError(numberText);
                    break;
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("Contact", new Contact(nameText.getText().toString(),
                            surnameText.getText().toString(), numberText.getText().toString(),
                            gender[spinner.getSelectedItemPosition()], dayView.getText().toString(),
                            monthView.getText().toString(), yearView.getText().toString(), isFavorite, null));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    break;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dayView.setText(String.valueOf(dayOfMonth));
        monthView.setText(String.valueOf(month));
        yearView.setText(String.valueOf(year));
    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        editText.startAnimation(shake);
    }

    private ContactRepository getContactRepository() {
        return ((App) getApplication()).getContactRepository();
    }
}