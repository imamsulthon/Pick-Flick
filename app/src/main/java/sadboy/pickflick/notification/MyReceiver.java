package sadboy.pickflick.notification;

/**
 * Created by Varun Kumar on 8/20/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, MyAlarmService.class);

        context.startService(service1);

    }
}