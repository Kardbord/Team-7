package userInterface;

import communicators.UdpCommunicator;
import player.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class ClientConsole {

    private static Scanner scanner = new Scanner(System.in);

    private static void printMenu() {
        System.out.println(
                "\t\t+-----------------------------+\t\t\n" +
                        "\t\t|                             |\t\t\n" +
                        "\t\t|    Please Enter Name to     |\t\t\n" +
                        "\t\t|          Register           |\t\t\n" +
                        "\t\t|                             |\t\t\n" +
                        "\t\t+-----------------------------+\t\t\n"
        );
    }

    public static void printPlayerMenu(Player player) {
        System.out.printf(
                "\t\t+-------------------------------------+\t\t\n" +
                        "\t\t|Name: %-15s                |\t\t\n" +
                        "\t\t|Player ID: %-15d           |\t\t\n" +
                        "\t\t|Cash: %-15d                |\t\t\n" +
                        "\t\t+-------------------------------------+\t\t\n" +
                        "\t\t(B)uy, (S)ell, (E)xit, (P)ortfolio\t\t\n",
                player.getName(),
                player.getPlayerId(),
                player.getCash()
        );
        String input = getUserInput("$> ");
        switch (input) {
            case "E":
                System.exit(0);
                break;
            case "e":
                System.exit(0);
                break;

        }
    }

    private static String getUserInput(String prompt) {
        String toReturn;

        System.out.print(prompt);
        // TODO: Add error checking here.
        toReturn = scanner.nextLine();

        return toReturn;
    }


    public static void main(String[] args) throws IOException {
        // TODO: This is solely for working on HW3. Will need to be redone later.
        printMenu();
        Player player;
        if (args.length < 2) {
            player = new Player(
                    getUserInput("Name: "),
                    new UdpCommunicator(
                            DatagramChannel.open(),
                            new InetSocketAddress(0)
                    )
            );
        } else {
            player = new Player(
                    getUserInput("Name: "),
                    new UdpCommunicator(
                            DatagramChannel.open(),
                            new InetSocketAddress(0)
                    ),
                    args[0]
            );
        }

    }
}
