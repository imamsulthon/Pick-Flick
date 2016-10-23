package sadboy.pickflick.notification;

/**
 * Created by Varun Kumar on 8/20/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import sadboy.pickflick.R;
import sadboy.pickflick.activities.MainActivity;


public class MyAlarmService extends Service
{

    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent,flags,startId);

        Log.d("Raj", "Triggered");

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),MainActivity.class);

        intent1.setAction(Long.toString(System.currentTimeMillis()));
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new Notification.Builder(this.getApplicationContext())
                .setContentIntent(pendingIntent)
                .setContentText("Hey")
                .setContentTitle("Ho")
                .setTicker("bye")
                .setSmallIcon(R.drawable.ic_local_movies)
                .build();

        mManager.notify(0, notification);


        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
