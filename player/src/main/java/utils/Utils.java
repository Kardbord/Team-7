// This file was used by Dr. Clyde in his
// HW1 examples.


package utils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Utils {
    public static int PORT = 12001;

    public static String getLocalIp() {
        return "127.0.0.1";
    }
    public static void setPort() {
        PORT = 12001;
    }

    /**
     * This will be set to our AWS server.
     * @return
     */
    public static String getServerIp() { return "35.163.138.220"; }

    public static String getCurrentIp() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface intf = nets.nextElement();
                // ignore the non-virtual, non-loopback and zero-interface networks
                if (!intf.isVirtual() && !intf.isLoopback() && intf.getInterfaceAddresses().size() > 0 &&
                        !intf.getDisplayName().toLowerCase().contains("virtual")) {
                    Enumeration<InetAddress> addrs = intf.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress net = addrs.nextElement();
                        if (net instanceof Inet4Address) {
                            // return the first network IP that found
                            return net.getHostAddress();
                        }
                    }
                }
            }
            return "";
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}