package gateway;

import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) throws IOException {

        new Gateway();
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
