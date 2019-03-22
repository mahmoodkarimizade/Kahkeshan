package ir.galaxycell.kahkeshan.Models;

/**
 * Created by Admin on 11/08/2017.
 */
public class ResVerifiedModel {

    public boolean verifiedtype;
    public String userid,password,phone;

    public boolean isVerifiedtype() {
        return verifiedtype;
    }

    public void setVerifiedtype(boolean verifiedtype) {
        this.verifiedtype = verifiedtype;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
