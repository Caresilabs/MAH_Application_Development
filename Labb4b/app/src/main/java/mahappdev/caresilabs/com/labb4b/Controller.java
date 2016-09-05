package mahappdev.caresilabs.com.labb4b;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.app.FragmentManager;

/**
 * Created by Simon on 9/5/2016.
 */
public class Controller {
    private MainActivity activity;
    private BatteryFragment batteryFragment;

    public Controller(MainActivity activity) {
        this.activity = activity;

        FragmentManager fgManager = activity.getSupportFragmentManager();
        batteryFragment = (BatteryFragment) fgManager.findFragmentById(R.id.fragBattery);

        this.activity.registerReceiver(new BatteryReciever(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void updateBatteryStatus(int level, int chargePlug) {
        batteryFragment.updateBatteryStatus(level, chargePlug);
    }

    private class BatteryReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            updateBatteryStatus(level, chargePlug);
        }
    }
}
