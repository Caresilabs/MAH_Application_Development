package mahappdev.caresilabs.com.labb4b;

import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


public class BatteryFragment extends Fragment {
    private TextView batteryText;
    private TextView chargeTypeText;
    private ProgressBar batteryProgress;

    public BatteryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_battery, container, false);
        batteryText = (TextView)view.findViewById(R.id.tvBattery);
        chargeTypeText = (TextView)view.findViewById(R.id.tvChargeType);
        batteryProgress = (ProgressBar)view.findViewById(R.id.pbBattery);
        return view;
    }

    public void updateBatteryStatus(int level, int chargePlug) {
        batteryProgress.setProgress(level);
        batteryText.setText(String.format("Battery Status %d", level) + "%");

        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (usbCharge) {
            chargeTypeText.setText("Charging using USB.");
        } else if(acCharge) {
            chargeTypeText.setText("Charging using AC Adapter.");
        } else {
            chargeTypeText.setText("No Charge.");
        }
    }
}
