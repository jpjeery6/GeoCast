package jeeryweb.geocast.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Debo#Paul on 4/23/2018.
 */

public class ImeiExtractor {

    Context _c;

    public ImeiExtractor(Context c) {
        _c = c;
    }

    public String getPhoneIEMINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) _c.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(_c, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(_c, "Cannot read phone state. Please allow permission to read IMEI number", Toast.LENGTH_SHORT).show();
        }
        return telephonyManager.getDeviceId();
    }

}
