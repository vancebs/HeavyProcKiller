package com.hf.heavyprockiller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fan on 2016/1/24.
 *
 * Process Info
 */
public class Proc {
    public static final String KEY_PID = "PID";
    public static final String KEY_PR = "PR";
    public static final String KEY_CPU = "CPU%";
    public static final String KEY_S = "S";
    public static final String KEY_THR = "#THR";
    public static final String KEY_VSS = "VSS";
    public static final String KEY_RSS = "RSS";
    public static final String KEY_PCY = "PCY";
    public static final String KEY_UID = "UID";
    public static final String KEY_NAME = "Name";

    private Map<String, String> mMap = new HashMap<>();

    public Proc(String line) {
        parser(line);
    }

    public void parser(String line) {
        String[] list = line.trim().split(" +");
        if (list.length == 10) {
            mMap.put(KEY_PID, list[0]);
            mMap.put(KEY_PR, list[1]);
            mMap.put(KEY_CPU, list[2]);
            mMap.put(KEY_S, list[3]);
            mMap.put(KEY_THR, list[4]);
            mMap.put(KEY_VSS, list[5]);
            mMap.put(KEY_RSS, list[6]);
            mMap.put(KEY_PCY, list[7]);
            mMap.put(KEY_UID, list[8]);
            mMap.put(KEY_NAME, list[9]);
        } else if (list.length == 9) {
            mMap.put(KEY_PID, list[0]);
            mMap.put(KEY_PR, list[1]);
            mMap.put(KEY_CPU, list[2]);
            mMap.put(KEY_S, list[3]);
            mMap.put(KEY_THR, list[4]);
            mMap.put(KEY_VSS, list[5]);
            mMap.put(KEY_RSS, list[6]);
            mMap.put(KEY_PCY, "");
            mMap.put(KEY_UID, list[7]);
            mMap.put(KEY_NAME, list[8]);
        }
    }

    @Override
    public String toString() {
        return mMap.get(KEY_PID)
                + ", " + mMap.get(KEY_PR)
                + ", " + mMap.get(KEY_CPU)
                + ", " + mMap.get(KEY_S)
                + ", " + mMap.get(KEY_THR)
                + ", " + mMap.get(KEY_VSS)
                + ", " + mMap.get(KEY_RSS)
                + ", " + mMap.get(KEY_PCY)
                + ", " + mMap.get(KEY_UID)
                + ", " + mMap.get(KEY_NAME);
    }

    public int getPID() {
        return Integer.valueOf(mMap.get(KEY_PID));
    }

    public int getPercent() {
        String cpu = mMap.get(KEY_CPU);
        try {
            return Integer.valueOf(cpu.substring(0, cpu.length() - 1));
        } catch (Exception e) {
            Log.e("Exception.", e);
            return 0;
        }
    }

    public String getName() {
        return mMap.get(KEY_NAME);
    }

    @SuppressWarnings("unused")
    public String get(String key) {
        return mMap.get(key);
    }
}
