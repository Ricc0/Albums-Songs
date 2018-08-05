package com.elegion.myfirstapplication;

import com.google.gson.annotations.SerializedName;

public class ResponseErrors {
     /*
        errors : {"email":["string"],"name":["string"],"password":["string"]}
     */

    @SerializedName("errors")
    private ErrorsBean mErrors;

    public ErrorsBean getErrors() {
        return mErrors;
    }

    public void setErrors(ErrorsBean errors) {
        mErrors = errors;
    }

    public static class ErrorsBean {
        @SerializedName("email")
        private String[] mEmail;
        @SerializedName("name")
        private String[] mName;
        @SerializedName("password")
        private String[] mPassword;

        public String[] getEmailError() {
            return mEmail;
        }
        public String[] getNameError() {
            return mName;
        }

        public String[] getPasswordError() {
            return mPassword;
        }
    }
}
