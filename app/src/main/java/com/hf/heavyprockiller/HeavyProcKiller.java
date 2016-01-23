package com.hf.heavyprockiller;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fan on 2016/1/23.
 */
public class HeavyProcKiller {
    public static final String TAG = "HeavyProcKiller";

    private static final int PERCENT_KILL = 10;

    public static final String BLACK_LIST[] = {
            "app_process"
    };

    /**
     *   PID PR CPU% S  #THR     VSS     RSS PCY UID      Name
     * 22850  1  34% R    10 696276K  15220K  fg root     app_process
     * 22717  0  17% R    10 696276K  15104K  fg root     app_process
     * 22962  0  17% R    10 696264K  15236K  fg root     app_process
     * 2501  1   4% S    20  78716K   2908K  fg system   /system/bin/surfaceflinger
     * 24037  0   4% S    48 887344K 103532K  fg u0_a38   hdpfans.com
     * 2504  0   3% S    12 181968K  85128K  fg media    /system/bin/mediaserver
     * 25211  1   3% R     1   1236K    512K     shell    top
     * 1476  1   0% S     1      0K      0K     root     aml_nftld
     * 84  0   0% D     1      0K      0K     root     kthread_hdmi_mo
     * 1390  0   0% S     1      0K      0K     root     ge2d_monitor
     */
    public static class ProcInfo {
        public static final String TAG = "ProcInfo";

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

        public ProcInfo() {
        }

        public ProcInfo(String line) {
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

        public int getId() {
            return Integer.valueOf(mMap.get(KEY_PID));
        }

        public int getPercent() {
            String cpu = mMap.get(KEY_CPU);
            try {
                return Integer.valueOf(cpu.substring(0, cpu.length() - 1));
            } catch (Exception e) {
                Log.e(TAG, "getPercent()# excention.", e);
                return 0;
            }
        }

        public String getName() {
            return mMap.get(KEY_NAME);
        }

        public String get(String key) {
            return mMap.get(key);
        }

        public static List<ProcInfo> top() {
            Process proc = null;
            List<ProcInfo> list = null;

            try {
                proc = Runtime.getRuntime().exec("su -c top -d 1");
                SystemClock.sleep(1200); // wait 1.2s
                list = parserTop(proc.getInputStream());
                return list;
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            } finally {
                if (list != null) {
                    for (int i=list.size()-1; i>=0; i--) {
                        ProcInfo info = list.get(i);
                        if ("top".equals(info.getName())) {
                            Log.i("==MyTest==", "top()# top process killed: " + info.getId());
                            info.kill();
                            list.remove(i);
                        }
                    }
                }
            }
        }

        public boolean kill() {
            try {
                Process proc = Runtime.getRuntime().exec("su -c kill " + mMap.get(KEY_PID));
                proc.waitFor();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        private static List<ProcInfo> parserTop(InputStream istream) {
            InputStreamReader isr = new InputStreamReader(istream);
            BufferedReader br = new BufferedReader(isr);
            List<ProcInfo> list = new ArrayList<>();

            try {
                String line = br.readLine(); // drop first line
                line = br.readLine(); // drop first line
                line = br.readLine(); // drop first line
                line = br.readLine(); // drop first line
                line = br.readLine(); // drop first line
                line = br.readLine(); // drop first line
                line = br.readLine(); // drop first line

                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        break;
                    }

                    list.add(new ProcInfo(line));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return list;
        }
    }

    public int killHeavyProc() {
        int count = 0;
        List<ProcInfo> list =  ProcInfo.top();
        for (ProcInfo info : list) {
            Log.i("==MyTest==", "killHeavyProc()# checking: " + info);
            if (info.getPercent() < PERCENT_KILL) {
                // all the processes are less than current process. Ignore them.
                break;
            }

            if (isInBlackList(info.getName())) {
                Log.i("==MyTest==", "killHeavyProc()# in black list");
                info.kill();
                count ++;
            }
        }

        return count;
    }

    private static boolean isInBlackList(String name) {
        for (String n : BLACK_LIST) {
            if (n.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
