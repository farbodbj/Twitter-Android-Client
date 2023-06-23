package com.twitter.myapplication.Utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private final static int MIN_PASSWORD_LENGTH = 8;
    private final static String USERNAME_VALIDITY_REGEX = "^[a-zA-Z0-9_]{1,15}$";
    private final static int MINIMUM_ACCOUNT_NAME_LENGTH = 4;
    private ValidationUtils(){}

    public static boolean isAccountNameValid(String accountName) {
        return !accountName.isBlank() && accountName.length() > MINIMUM_ACCOUNT_NAME_LENGTH;
    }

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

    public static int calculatePasswordSecurityLevel(String password) {
        int securityLevel = 0;

        if (password.length() >= MIN_PASSWORD_LENGTH) {
            securityLevel++;
        }

        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);
        if (lowercaseMatcher.find()) {
            securityLevel++;
        }

        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Matcher uppercaseMatcher = uppercasePattern.matcher(password);
        if (uppercaseMatcher.find()) {
            securityLevel++;
        }

        Pattern digitPattern = Pattern.compile("\\d");
        Matcher digitMatcher = digitPattern.matcher(password);
        if (digitMatcher.find()) {
            securityLevel++;
        }

        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        if (specialCharMatcher.find()) {
            securityLevel++;
        }

        Pattern commonWordPattern = Pattern.compile("(password|admin|123456|qwerty)");
        Matcher commonWordMatcher = commonWordPattern.matcher(password);
        if (!commonWordMatcher.find()) {
            securityLevel++;
        }

        return securityLevel;
    }

}
