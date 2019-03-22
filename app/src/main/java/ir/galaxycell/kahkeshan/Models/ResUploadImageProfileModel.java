package ir.galaxycell.kahkeshan.Models;

/**
 * Created by Admin on 20/08/2017.
 */
public class ResUploadImageProfileModel
{
    public String imageProfile;
    public boolean status;

    public String getUserid() {
        return imageProfile;
    }

    public void setUserid(String userid) {
        this.imageProfile = userid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
