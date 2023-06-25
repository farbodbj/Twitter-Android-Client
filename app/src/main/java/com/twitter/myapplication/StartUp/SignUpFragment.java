package com.twitter.myapplication.StartUp;

import static com.twitter.common.Utils.SafeCall.safe;
import static com.twitter.myapplication.Utils.ValidationUtils.calculatePasswordSecurityLevel;
import static com.twitter.myapplication.Utils.ValidationUtils.isAccountNameValid;
import static com.twitter.myapplication.Utils.ValidationUtils.isEmailValid;

import static java.text.DateFormat.getDateInstance;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.twitter.client.Controllers.UserActionsManager;
import com.twitter.common.Models.User;
import com.twitter.myapplication.R;
import com.twitter.myapplication.StandardFormats.StandardFragmentFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SignUpFragment extends Fragment implements StandardFragmentFormat {
    private final static int MINIMUM_PASSWORD_SECURITY_LEVEL = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initializeUIComponents(view);
        return view;
    }

    @Override
    public void initializeUIComponents(@NonNull View view) {
        Button signUpComplete = view.findViewById(R.id.sign_up_complete);
        TextInputEditText etAccountName = view.findViewById(R.id.etSignUpAccountName);
        TextInputEditText etEmail = view.findViewById(R.id.etSignUpEmail);
        TextInputEditText etPassword = view.findViewById(R.id.etSignUpPassword);
        TextInputEditText etPasswordRepeat = view.findViewById(R.id.etSignUpPasswordRepeat);
        TextInputEditText etUsername = view.findViewById(R.id.etSignUpUsername);
        MaterialTextView datePickerTextView = view.findViewById(R.id.date_picker);
        Spinner countries = view.findViewById(R.id.spinnerCountry);

        User user = new User();

        setupDatePicker(datePickerTextView);
        setupPasswordValidation(etPassword);
        setupPasswordRepeatValidation(etPasswordRepeat, etPassword);

        signUpComplete.setOnClickListener(v -> {
            if (isFormValid(etAccountName, etEmail, etPassword, etPasswordRepeat)) {
                safe(()->
                    updateUser(
                        user,
                        etAccountName,
                        etEmail,
                        etUsername,
                        etPassword,
                        datePickerTextView,
                        countries));

                checkEmailExists(user.getEmail(), etEmail);
                checkUsernameExists(user.getUsername(), etUsername);

                if(etEmail.getError() == null && etUsername.getError() == null) {
                    doSignUp(user, view);
                }
            }
        });
    }

    private void setupDatePicker(MaterialTextView datePickerTextView) {
        datePickerTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(new Locale(""), "%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        datePickerTextView.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void setupPasswordValidation(TextInputEditText etPassword) {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (calculatePasswordSecurityLevel(editable.toString()) <= MINIMUM_PASSWORD_SECURITY_LEVEL) {
                    etPassword.setError(getString(R.string.weak_password));
                }
            }
        });
    }

    private void setupPasswordRepeatValidation(TextInputEditText etPasswordRepeat, TextInputEditText etPassword) {
        etPasswordRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString();
                String passwordRepeat = etPasswordRepeat.getText().toString();

                if (!password.equals(passwordRepeat)) {
                    etPasswordRepeat.setError(getString(R.string.passwords_dont_match));
                }
            }
        });
    }

    private boolean isFormValid(TextInputEditText etAccountName,
                                TextInputEditText etEmail,
                                TextInputEditText etPassword,
                                TextInputEditText etPasswordRepeat) {

        String accountName = etAccountName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordRepeat = etPasswordRepeat.getText().toString();

        return isAccountNameValid(accountName) &&
                isEmailValid(email) &&
                password.equals(passwordRepeat) &&
                calculatePasswordSecurityLevel(password) > MINIMUM_PASSWORD_SECURITY_LEVEL;
    }

    private void updateUser(User user,
                            TextInputEditText etAccountName,
                            TextInputEditText etEmail,
                            TextInputEditText etUsername,
                            TextInputEditText etPassword,
                            MaterialTextView datePickerTextView,
                            Spinner countries
    ) throws ParseException {

        String accountName = etAccountName.getText().toString();
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        Date dateOfBirth =  new SimpleDateFormat("yyyy-MM-dd").parse(datePickerTextView.getText().toString());
        String country = countries.getSelectedItem().toString();

        user.setDisplayName(accountName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setDateOfBirth(dateOfBirth);
        user.setCountry(country);
    }

    private void checkUsernameExists(String username, TextInputEditText etUsername) {
        UserActionsManager.getInstance().usernameExists(
                username,
                result -> {
                    if (result) {
                        etUsername.setError(getString(R.string.username_taken));
                    }
                },
                exception -> {
                    System.out.println(exception.getMessage());
                    // Handle exception
                });
    }

    private void checkEmailExists(String email, TextInputEditText etEmail) {
        UserActionsManager.getInstance().emailExists(
                email,
                result -> {
                    if (result) {
                        etEmail.setError(getString(R.string.email_taken));
                    }
                },
                exception -> {
                    System.out.println(exception.getMessage());
                    // Handle exception
                });
    }


    private void doSignUp(User user, View view) {
        UserActionsManager.getInstance().signUp(
                user,
                result -> {
                    if (result) {
                        view.findViewById(R.id.sign_up_successful).setVisibility(View.VISIBLE);
                    }
                },
                exception -> {
                    // Handle exception
                });
    }

}
