import java.util.Scanner;

public class Shell {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");
            String input = sc.nextLine();

            System.out.printf("%s: command not found", input);
        }
    }

}
