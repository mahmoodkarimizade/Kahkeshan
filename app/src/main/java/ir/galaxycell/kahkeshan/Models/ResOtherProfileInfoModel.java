package ir.galaxycell.kahkeshan.Models;

import org.json.JSONArray;

/**
 * Created by Admin on 10/09/2017.
 */
public class ResOtherProfileInfoModel {

    public String profileImage,biography,username,link,block;
    public int countLibrary,countFollower,countFollowing;
    public boolean flagMark;
    public JSONArray Following;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getCountLibrary() {
        return countLibrary;
    }

    public void setCountLibrary(int countLibrary) {
        this.countLibrary = countLibrary;
    }

    public int getCountFollower() {
        return countFollower;
    }

    public void setCountFollower(int countFollower) {
        this.countFollower = countFollower;
    }

    public JSONArray getFollowing() {
        return Following;
    }

    public void setFollowing(JSONArray following) {
        Following = following;
    }


    public int getCountFollowing() {
        return countFollowing;
    }

    public void setCountFollowing(int countFollowing) {
        this.countFollowing = countFollowing;
    }

    public boolean isFlagMark() {
        return flagMark;
    }

    public void setFlagMark(boolean flagMark) {
        this.flagMark = flagMark;
    }
}
