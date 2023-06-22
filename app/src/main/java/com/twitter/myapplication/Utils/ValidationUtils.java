package com.twitter.myapplication.Utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtils {
    private final static int MIN_PASSWORD_LENGTH = 8;
    private final static String USERNAME_VALIDITY_REGEX = "^(\\w){1,15}$";
    private ValidationUtils(){}
    public static boolean isEmailValid(String email) {
        // You can use a regular expression or Android's Patterns utility to check email format
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile(USERNAME_VALIDITY_REGEX);
        return !TextUtils.isEmpty(username) && pattern.matcher(username).matches();
    }

    public static boolean isPasswordValid(String password) {
        // Check if the password is not empty and meets your desired length or complexity requirements
        return !TextUtils.isEmpty(password) && password.length() >= MIN_PASSWORD_LENGTH;
    }
}
