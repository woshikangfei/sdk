package android.app;

import android.os.Binder;
import android.os.IBinder;

public abstract class ApplicationThreadNative extends Binder implements IApplicationThread {

    @Override
    public IBinder asBinder() {
        throw new RuntimeException("Stub!");
    }

}
