package com.elegion.myfirstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

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

                ApiUtils.getApiService(true).getUser().enqueue(new retrofit2.Callback<User>() {
                    Handler mHandler = new Handler(getActivity().getMainLooper());

                    @Override

                    public void onResponse(retrofit2.Call<User> call, final retrofit2.Response<User> response) {
                        mHandler.post(() -> {
                            Log.d(TAG, "onResponse: " + response.code());
                            if (!response.isSuccessful()) {

                                showMessage(R.string.auth_error);
                                getFragmentManager().popBackStack();

                            } else {
                                try {
                                    User user = new User(response.body().getData().getEmail(), response.body().getData().getName(), "");
                                    startActivity(new Intent(getActivity(), AlbumsActivity.class));
                                    getActivity().finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(retrofit2.Call<User> call, Throwable t) {
                        mHandler.post(() -> showMessage(R.string.request_error));
                    }
                });
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
