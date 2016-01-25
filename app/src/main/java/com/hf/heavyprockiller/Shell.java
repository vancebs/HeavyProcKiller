package com.hf.heavyprockiller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2016/1/25.
 *
 * Shell
 */
public class Shell {
    private static final String PATH[] = {
            "/system/bin/",
            "/system/xbin/",
            "/system/sbin/",
            "/sbin/",
            "/vendor/bin/" };
    private static final File WORKING_DIR = new File("/");

    public static boolean isRooted() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su -c 'echo > /dev/null'", PATH, WORKING_DIR);
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            Log.e("Failed to check is rooted", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return false;
    }

    public static boolean kill(Proc proc) {
        if (proc == null) {
            return false;
        }

        if (isRooted()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("su -c 'kill " + proc.getPID() + "'", PATH, WORKING_DIR);
                int code = process.waitFor();
                Log.i("Kill process [" + proc.getName() + "] " + ((code == 0) ? "success" : "failed") + " (" + code + ")");
                return code == 0;
            } catch (IOException | InterruptedException e) {
                Log.e("Kill process [" + proc.getName() + "] failed.", e);
                return false;
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        } else {
            // currently we do not support kill for not rooted device
            return false;
        }
    }

    public static List<Proc> top() {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(isRooted() ? "su -c 'top -d 1 -n 1'" : "top -d 1 -n 1", PATH, WORKING_DIR);
            process.waitFor();
            return parserTop(process.getInputStream());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static List<Proc> parserTop(InputStream istream) {
        InputStreamReader isr = new InputStreamReader(istream);
        BufferedReader br = new BufferedReader(isr);
        List<Proc> list = new ArrayList<>();

        try {
            // find the title line
            String line = br.readLine();
            while (!line.trim().startsWith("PID")) {
                line = br.readLine();
            }

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }

                list.add(new Proc(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
