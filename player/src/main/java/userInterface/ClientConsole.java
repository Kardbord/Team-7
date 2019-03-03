package userInterface;

import player.Player;

import java.io.IOException;
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

    private static void printPlayerMenu(Player player) {
        System.out.printf(
                        "\t\t+-------------------------------------+\t\t\n" +
                        "\t\t|Name: %-15s                |\t\t\n" +
                        "\t\t|Player ID: %-15d           |\t\t\n" +
                        "\t\t|Cash: %-15d                |\t\t\n" +
                        "\t\t+-------------------------------------+\t\t\n" +
                        "\t\t(B)uy, (S)ell, (E)xit, (P)ortfolio\t\t\n" +
                        "$> ",
                player.getName(),
                player.getPlayerId(),
                player.getCash()
        );
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
        Player player = new Player(getUserInput("Name: "));
        printPlayerMenu(player);

    }
}
