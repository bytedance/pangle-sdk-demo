package com.union_test.new_api.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class TToast {
    private static Toast pangleT;

    public static void show(Context context, String toastMsg, int duration) {
        if (context != null) {
            pangleT = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
            if (pangleT != null) {
                pangleT.setDuration(duration);
                pangleT.setText(toastMsg);
                pangleT.show();
                return;
            }
        }
        Log.i("PangleToast", "msg:" + toastMsg);
    }

    public static void clearOnDestroy() {
        pangleT = null;
    }

    public static void show(Context c, String toastMsg) {
        show(c, toastMsg, Toast.LENGTH_SHORT);
    }
}
