package com.bk.bkskup3.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by SG0891787 on 4/18/2016.
 */
public class Intents {
    private Intents() {
    }

    public static Intent makeExplicit(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();



        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfos == null || resolveInfos.size() == 0) {
            return null;
        }

        String thisPackageName = context.getPackageName();

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = null;


        if(resolveInfos.size() == 1) {
            serviceInfo = resolveInfos.get(0);
        } else {
            for(ResolveInfo info : resolveInfos)
            {
                if(thisPackageName.equals(info.serviceInfo.packageName))
                {
                    serviceInfo = info;
                    break;
                }
            }
        }

        if(serviceInfo == null)
        {
            serviceInfo = resolveInfos.get(0);
        }

        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
