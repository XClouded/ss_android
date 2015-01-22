package com.myandb.singsong.secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Encryption {
	
	public String getSha512Convert(String rawString) {
		String result = "";
		
		try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            md.update(rawString.getBytes());
            byte[] mb = md.digest();
            for (int i = 0, l = mb.length; i < l; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(Byte.valueOf(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                result += s;
            }
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        }
		
		return result;
	}
	
	public String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

	    String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}

}
