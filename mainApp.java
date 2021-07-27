package Assignment1;

import java.io.IOException;
import java.util.Scanner;

public class mainApp {

    public static void main(String test[]) throws IOException {
        
        Scanner sc = new Scanner(System.in);
        FileServer server = new FileServer();
        String input;

        if (server.getAuthenticated() == true) {

            do {
                //Display available options for user to choose
                System.out.println();
                System.out.println("Options");
                System.out.println("(C)reate");
                System.out.println("(R)ead");
                System.out.println("(W)rite");
                System.out.println("(L)ist");
                System.out.println("(S)ave");
                System.out.println("(E)xit");
                System.out.println();
                System.out.print(">> ");
                
                input = sc.next();
                sc.nextLine();

                switch (input) {
                    case "c":
                    case "C":
                        int fileSecurityLvl;
                        System.out.print("Enter file name: ");
                        String fileName = sc.nextLine();
                        do {
                            System.out.print("Security Level (0/1/2): ");
                            fileSecurityLvl = sc.nextInt();
                        } while (fileSecurityLvl != 0 && fileSecurityLvl != 1 && fileSecurityLvl != 2);


                        server.createFile(fileName, fileSecurityLvl);
                        break;

                    case "R":
                    case "r":
                        System.out.print("Enter file name: ");
                        String name = sc.nextLine();
                        server.readFile(name);

                        break;

                    case "w":
                    case "W":
                        System.out.print("Enter file name: ");
                        fileName = sc.nextLine();
                        server.writeFile(fileName);
                        break;

                    case "L":
                    case "l":
                        server.listFile();
                        break;

                    case "s":
                    case "S":
                        System.out.println("All files saved!");
                        break;

                    case "e":
                    case "E":
                        String result;
                        do{
                        System.out.println("Shut down the server? (Y)es or (N)o");
                        result =sc.next();
                        }while (!result.equals("y") && !result.equals("Y") && !result.equals("n") && !result.equals("N"));
                        if (result.equals("y") || result.equals("Y")) {
                            server.listFile();
                            System.out.println("Good Bye!");
                            System.exit(0);
                        }else if (result.equals("n") || result.equals("N"))
                            //clear input
                            input="";
                        break;

                }
            } while (!input.equals("E") && !input.equals("e"));
        }
    }

}
