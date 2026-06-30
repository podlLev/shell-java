import java.util.Scanner;

public class Shell {

    public static void main(String[] args) {
        System.out.print("$ ");

        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        System.out.println(command + ": command not found");
    }

}
