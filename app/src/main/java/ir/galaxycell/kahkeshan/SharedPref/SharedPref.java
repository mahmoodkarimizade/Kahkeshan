package ir.galaxycell.kahkeshan.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Admin on 11/08/2017.
 */
public class SharedPref {
    public SharedPreferences datauser;
    public String statusUser,userid,password,phone;

    public SharedPref(Context context)
    {
        datauser=context.getSharedPreferences("datauser",0);
        if(datauser.getString("user_status","").length()==0)
        {
            datauser.edit()
                    .putString("user_status","")
                    .apply();
        }

    }

    public String getStatusUser()
    {

        return datauser.getString("user_status","false");
    }

    public void setStatusUser(String statusUser)
    {
        datauser.edit().putString("user_status",statusUser).apply();
    }


    public String getUserid() {
        return datauser.getString("user_id","");
    }

    public void setUserid(String userid) {
        datauser.edit().putString("user_id",userid).apply();
    }

    public void logout()
    {
        datauser.edit().putString("user_status","false").apply();
    }

    public String getPassword() {
        return datauser.getString("password","");
    }

    public void setPassword(String password) {
        datauser.edit().putString("password",password).apply();
    }

    public String getPhone() {
        return datauser.getString("phone","");
    }

    public void setPhone(String phone) {
        datauser.edit().putString("phone",phone).apply();
    }
}
