package com.elegion.myfirstapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elegion.myfirstapplication.model.User;
import com.google.gson.Gson;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;

public class RegistrationFragment extends Fragment {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private EditText mPasswordAgain;
    private Button mRegistration;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    private View.OnClickListener mOnRegistrationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isInputValid()) {
                User user = new User(
                        mEmail.getText().toString(),
                        mName.getText().toString(),
                        mPassword.getText().toString());

                ApiUtils.getApiService(false)
                        .registration(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            showMessage(R.string.login_register_success);
                            getFragmentManager().popBackStack();
                        }, throwable -> showMessage(R.string.request_error));
            } else {
                showMessage(R.string.input_error);
            }
        }
    };


    private void setErrorsHints(ResponseErrors errors) {
        String[] email = errors.getErrors().getEmailError();
        String[] name = errors.getErrors().getNameError();
        String[] password = errors.getErrors().getPasswordError();
        StringBuilder fullError = new StringBuilder();
        if (email != null && email.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String i : email) {
                builder.append(i).append(" ");
                fullError.append(i).append(" ");
            }
            mEmail.setError(builder.toString());
            fullError.append("\n");
        }
        if (name != null && name.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String i : name) {
                builder.append(i).append(" ");
                fullError.append(i).append(" ");
            }
            mName.setError(builder.toString());
            fullError.append("\n");
        }
        if (password != null && password.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String i : password) {
                builder.append(i).append(" ");
                fullError.append(i).append(" ");
            }
            mPassword.setError(builder.toString());
        }

        if (!fullError.toString().isEmpty()) {
            Toast.makeText(getActivity(), fullError.toString(), Toast.LENGTH_LONG).show();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_registration, container, false);

        mEmail = view.findViewById(R.id.etEmail);
        mName = view.findViewById(R.id.etName);
        mPassword = view.findViewById(R.id.etPassword);
        mPasswordAgain = view.findViewById(R.id.tvPasswordAgain);
        mRegistration = view.findViewById(R.id.btnRegistration);

        mRegistration.setOnClickListener(mOnRegistrationClickListener);

        return view;
    }

    private boolean isInputValid() {
        return isEmailValid(mEmail.getText().toString())
                && !TextUtils.isEmpty(mName.getText())
                && isPasswordsValid();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordsValid() {
        String password = mPassword.getText().toString();
        String passwordAgain = mPasswordAgain.getText().toString();

        return password.equals(passwordAgain)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(passwordAgain);
    }

    private void showMessage(@StringRes int string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

}
