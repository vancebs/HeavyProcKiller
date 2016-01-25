package com.hf.heavyprockiller;

import java.util.List;

/**
 * Created by Fan on 2016/1/23.
 *
 * Heavy Process Killer
 */
public class HeavyProcKiller {
    private static final int PERCENT_KILL = 10;

    public static final String BLACK_LIST[] = {
            "app_process"
    };

    public int killHeavyProc() {
        int count = 0;
        List<Proc> list =  Shell.top();
        for (Proc info : list) {
            Log.i("checking: " + info);
            if (info.getPercent() < PERCENT_KILL) {
                // all the processes are less than current process. Ignore them.
                break;
            }

            if (isInBlackList(info.getName())) {
                boolean result = Shell.kill(info);
                Log.i("kill proc: " + info.getName() + ", killed: " + result);
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
