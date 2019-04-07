package gateway;

import communicators.UdpCommunicator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class App {

    public static void main(String[] args) throws IOException {
        new Gateway(
                new UdpCommunicator(
                        DatagramChannel.open(),
                        new InetSocketAddress("0.0.0.0", Gateway.PORT)
                )
        );
        System.out.println("Gateway is listening.");
        System.out.println("Type Q to quit.");

        ArrayList<String> exitInputs = new ArrayList<>() {
            {
                add("Q");
                add("QUIT");
            }
        };
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (exitInputs.contains(input.toUpperCase())) {
                System.exit(0);
            }
            System.out.print("\033[H\033[2J");
            System.out.println("Gateway is listening.");
            System.out.println("Type Q to quit.");
        }
    }

}
