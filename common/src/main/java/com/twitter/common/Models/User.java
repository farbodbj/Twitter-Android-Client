package com.twitter.common.Models;

import com.twitter.common.Models.Messages.Visuals.Image;

import java.io.Serializable;
import java.util.Date;


public class User implements Serializable {
    private final static long legalAge = 18 * 365 * 24 * 3600 * 1000L;
    private int userId;
    private String displayName;
    private String username;
    private String passwordHash;
    private String email;
    private Date dateOfBirth;
    private Date accountMade;
    private Image profilePic;
    private Image headerPic;
    private String  bio;
    private String location;
    private String country;
    private String phoneNumber;
    private String Jwt;


    public User() {
    }

    public User(int userId) {
        this.userId = userId;
    }

    public User(String displayName, String username, String passwordHash, String email, Date dateOfBirth) {
        this.displayName = displayName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public User(int userId,
                String username,
                String displayName,
                Image profilePic) {
        this.userId = userId;
        this.displayName = displayName;
        this.username = username;
        this.profilePic = profilePic;
    }

    public User(int userId,
                String displayName,
                String username,
                String email,
                java.sql.Date dateOfBirth,
                java.sql.Date accountMade,
                Image profilePic,
                Image headerPic,
                String bio,
                String location) {
        this.userId = userId;
        this.displayName = displayName;
        this.username = username;
        this.email = email;
        setDateOfBirth(dateOfBirth);
        setAccountMade(accountMade);
        this.profilePic = profilePic;
        this.headerPic = headerPic;
        this.bio = bio;
        this.location = location;
    }

    public int getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public static java.sql.Date getLegalAge() {
        return new java.sql.Date(new Date().getTime() - legalAge);
    }

    public java.sql.Date getDateOfBirth() {
        return new java.sql.Date(dateOfBirth.getTime());
    }

    public java.sql.Date getAccountMade() {
        return new java.sql.Date(new Date().getTime());
    }

    public Image getProfilePic() {
        return profilePic;
    }

    public Image getHeaderPic() {
        return headerPic;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getJwt() {
        return Jwt;
    }

    public void setJwt(String jwt) {
        this.Jwt = jwt;
    }

    public void setAccountMade(java.sql.Date accountMade) {this.accountMade = new Date(accountMade.getTime());}

    public void setDateOfBirth(java.sql.Date dateOfBirth) {this.dateOfBirth = new Date(dateOfBirth.getTime());}

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePic(Image profilePic) {
        this.profilePic = profilePic;
    }

    public void setHeaderPic(Image headerPic) {
        this.headerPic = headerPic;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", displayName='" + displayName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", accountMade=" + accountMade +
                '}';
    }
}
