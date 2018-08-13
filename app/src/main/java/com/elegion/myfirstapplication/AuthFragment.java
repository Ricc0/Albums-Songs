package com.elegion.myfirstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elegion.myfirstapplication.albums.AlbumsActivity;
import com.elegion.myfirstapplication.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;

public class AuthFragment extends Fragment {
    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private Button mEnter;
    private Button mRegister;
    private User user;

    public static AuthFragment newInstance() {
        Bundle args = new Bundle();
        AuthFragment fragment = new AuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener mOnEnterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isEmailValid() && isPasswordValid()) {

                ApiUtils.getBasicAuthClient(
                        mEmail.getText().toString(),
                        mPassword.getText().toString(),
                        true
                );

                String credentials = Credentials.basic(mEmail.getText().toString(), mPassword.getText().toString());

                // Example for authorization and get User data on Profile Activity.
                /*ApiUtils.getApiService(true)
                        .getUser(credentials)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dataBean -> {
                            User user = new User(dataBean.getData().getEmail(), dataBean.getData().getName(), "");
                            Intent startProfileIntent = new Intent(getActivity(), ProfileActivity.class);
                            startProfileIntent.putExtra(ProfileActivity.USER_KEY, user);
                            startActivity(startProfileIntent);
                            getActivity().finish();
                        }, throwable -> showMessage(R.string.auth_error));*/

                // Example for get albums from the server
                ApiUtils.getApiService(true)
                        .getUser(credentials)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            startActivity(new Intent(getActivity(), AlbumsActivity.class));
                            getActivity().finish();
                        }, throwable -> showMessage(R.string.request_error));

            } else {

                if (!isEmailValid()) {
                    mEmail.requestFocus();
                    mEmail.setError("Не корректный email");
                } else if (!isPasswordValid()) {
                    mPassword.requestFocus();
                    mPassword.setError("Не корректный пароль");
                } else
                    showMessage(R.string.input_error);
            }
        }
    };

    private View.OnClickListener mOnRegisterClickListener = view -> getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, RegistrationFragment.newInstance())
            .addToBackStack(RegistrationFragment.class.getName())
            .commit();

    private View.OnFocusChangeListener mOnEmailFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mEmail.showDropDown();
            }
        }
    };

    private boolean isEmailValid() {
        return !TextUtils.isEmpty(mEmail.getText())
                && Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches();
    }

    private boolean isPasswordValid() {
        return !TextUtils.isEmpty(mPassword.getText());
    }

    private void showMessage(@StringRes int string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_auth, container, false);

        mEmail = v.findViewById(R.id.etEmail);
        mPassword = v.findViewById(R.id.etPassword);
        mEnter = v.findViewById(R.id.buttonEnter);
        mRegister = v.findViewById(R.id.buttonRegister);

        mEnter.setOnClickListener(mOnEnterClickListener);
        mRegister.setOnClickListener(mOnRegisterClickListener);
        mEmail.setOnFocusChangeListener(mOnEmailFocusChangeListener);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEnter.setOnClickListener(mOnEnterClickListener);
        mRegister.setOnClickListener(mOnRegisterClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        mEnter.setOnClickListener(null);
        mRegister.setOnClickListener(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEmail = null;
        mPassword = null;
        mEnter = null;
        mRegister = null;
        user = null;
    }
}
