package com.ds.feige.im.common.util;

import java.lang.management.ManagementFactory;

public class RuntimeUtils {
    private static int pid=0;
    public static int getPid(){
        if(pid==0){
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pidStr = name.split("@")[0];
            pid=Integer.parseInt(pidStr);
        }
        return pid;
    }
}
