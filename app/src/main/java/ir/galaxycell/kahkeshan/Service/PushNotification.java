package ir.galaxycell.kahkeshan.Service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.UI.MainActivity;
import ir.galaxycell.kahkeshan.Utility.Utility;

/**
 * Created by Admin on 10/11/2017.
 */
public class PushNotification extends Service{

    private NotificationManager mNotificationManager;
    public Socket mSocket;
    {
        try
        {
            mSocket = IO.socket(Utility.BaseUrl);
        } catch (URISyntaxException e) {}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {


        //Toast.makeText(getApplicationContext(),"PushNotification Start",Toast.LENGTH_LONG).show();

        //connect to server by socket
        mSocket.connect();
        //listen to push notification
        mSocket.on("resCreateNews",resCreateNews);


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.off("resCreateNews",resCreateNews);
        //Toast.makeText(getApplicationContext(),"PushNotification Stop",Toast.LENGTH_LONG).show();
    }

    Emitter.Listener resCreateNews=new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            JSONObject data=(JSONObject)args[0];

            try
            {
                if(data.getBoolean("status"))
                {
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(900);

                    if(isForeground("ir.galaxycell.kahkeshan"))
                    {

                    }
                    else
                    {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

                        mBuilder.setSmallIcon(R.drawable.home_off);
                        mBuilder.setContentTitle("Notification Alert, Click Me!");
                        mBuilder.setContentText("Hi, This is Android Notification Detail!");
                        mBuilder.setAutoCancel(true);

                        Intent resultIntent = new Intent(PushNotification.this, MainActivity.class);
                        resultIntent.putExtra("Click","clickService");
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(PushNotification.this);
                        stackBuilder.addParentStack(MainActivity.class);

                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);


                        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(0, mBuilder.build());
                    }

                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };


    //check my activity is running or no??
    public boolean isForeground(String myPackage)
    {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }
}
