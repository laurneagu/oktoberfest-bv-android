package larc.ludiconprod.Utils.util;

/**
 * Created by Ciprian on 7/28/2016.
 */
public class Utils {

    public static void quit() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
}
}
