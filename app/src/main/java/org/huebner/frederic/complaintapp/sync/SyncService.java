package org.huebner.frederic.complaintapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

    private static final Object syncAdapterLock = new Object();
    private static SyncAdapter syncAdpater = null;

    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (syncAdpater == null) {
                syncAdpater = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdpater.getSyncAdapterBinder();
    }
}

