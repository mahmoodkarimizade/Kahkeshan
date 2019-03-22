package ir.galaxycell.kahkeshan.Models;


import java.io.Serializable;

/**
 * Created by Admin on 21/08/2017.
 */
public class ResUserProfileInfoModel implements Serializable {

    public String profileImage,biography,username,phone,link,block;
    public int countLibrary,countFavorites,countFollowing,countFollower;

    public ResUserProfileInfoModel()
    {
        this.profileImage="";
        this.biography="";
        this.username="";
        this.username="";
        this.link="";
        this.block="";
        this.countLibrary=0;
        this.countFavorites=0;
        this.countFollowing=0;
        this.countFollower=0;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getCountfFollower() {
        return countFollower;
    }

    public void setCountfFollower(int countfFollower) {
        this.countFollower = countfFollower;
    }

    public int getCountFollowing() {
        return countFollowing;
    }

    public void setCountFollowing(int countFollowing) {
        this.countFollowing = countFollowing;
    }

    public int getCountFavorites() {
        return countFavorites;
    }

    public void setCountFavorites(int countFavorites) {
        this.countFavorites = countFavorites;
    }

    public int getCountLibrary() {
        return countLibrary;
    }

    public void setCountLibrary(int countLibrary) {
        this.countLibrary = countLibrary;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
