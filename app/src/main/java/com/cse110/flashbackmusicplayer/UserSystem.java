package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class UserSystem {
    private BroadcastReceiver broadcastReceiver;
    private Activity root;
    private String[] profile_data;
    private String[] contacts_data;
    private boolean ready = false;

    public UserSystem(Activity root) {this.root = root;}

    public void initService() {
        Intent serviceIntent = new Intent(root, PersonService.class);
        serviceIntent.setAction("GET PERSON");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                profile_data = intent.getStringArrayExtra("PROFILE");
                contacts_data = intent.getStringArrayExtra("CONTACTS");
                ready = true;
            }
        };
        root.registerReceiver(broadcastReceiver, new IntentFilter(PersonService.ACTION_BROADCAST));
        root.startService(serviceIntent);
    }

    public boolean isReady() {
        return ready;
    }

    public String[] getProfileInfo() {
        return profile_data;
    }

    public String[] getContactsInfo() {
        return contacts_data;
    }

    public void destroy() {
        Intent serviceIntent = new Intent(root, PersonService.class);
        try {
            root.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        root.stopService(serviceIntent);
    }

}
