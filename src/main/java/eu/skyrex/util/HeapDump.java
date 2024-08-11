package eu.skyrex.util;

import com.sun.management.HotSpotDiagnosticMXBean;
import eu.skyrex.Main;

import javax.management.MBeanServer;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class HeapDump {

    public static void dumpHeap(String filePath, boolean live) throws IOException {
        File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();

        file = new File(file, filePath + ".hprof");

        if(file.exists()) file.delete();

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
        mxBean.dumpHeap(file.getPath(), live);
    }
}
