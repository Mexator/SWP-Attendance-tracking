package com.swp.attendancelogger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.swp.attendancelogger.system_logic.AttendanceBackend;
import com.swp.attendancelogger.system_logic.BluetoothConnectionService;

import org.json.JSONObject;

import java.util.UUID;

public class StartGatheringAttendanceFragment extends Fragment implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener{
    private EditText classIdEdit, activityIdEdit, weekEdit;
    private AttendanceBackend backend;
    private View progressBar;
    private static final String TAG = "BluetoothConnection";
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    int pressed = 0;

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBTDevice;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            Toast.makeText(getActivity(), "Bluetooth turned on",Toast.LENGTH_LONG).show();
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            Toast.makeText(getActivity(), "Bluetooth turned off",Toast.LENGTH_LONG).show();
        }

    }

    public StartGatheringAttendanceFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        backend = AttendanceBackend.getInstance(getContext());
        return inflater.inflate(R.layout.fragment_start_gathering_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classIdEdit = view.findViewById(R.id.class_id_edit);
        activityIdEdit=view.findViewById(R.id.activity_id_edit);
        weekEdit = view.findViewById(R.id.week_edit);

        view.findViewById(R.id.button_start_gathering_attendance_bluetooth).setOnClickListener(this);
        view.findViewById(R.id.btnONOFF).setOnClickListener(this);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver3, filter);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout l = getView().findViewById(R.id.bluetooth_gathering_layout);
        l.addView(progressBar, params);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start_gathering_attendance_bluetooth:
                if(pressed == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    Long classId = Long.parseLong(classIdEdit.getText().toString());
                    Long activityId = Long.parseLong(activityIdEdit.getText().toString());
                    Long userId = backend.getUser().getID();
                    Integer weekNumber = Integer.parseInt(weekEdit.getText().toString());
                    Toast.makeText(getActivity(), "Start Gathering",Toast.LENGTH_LONG).show();
                    pressed = 1;
                }
                else
                {
                    Toast.makeText(getActivity(), "Stop Gathering",Toast.LENGTH_LONG).show();
                    pressed = 0;
                }
                break;
            case R.id.btnONOFF:
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
                break;
        }
    }

    //create method for starting connection
//***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

    @Override
    public void onResponse(JSONObject response) {
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }
}