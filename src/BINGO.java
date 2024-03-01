import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);
    static final String BINGO = "BINGO";
    static final int ASCIIMIN = 1, ASCIIMAX = 75;
    static String rolledNumbersRepr = "";
    static String bingoCardRepr = "";
    static String bingoWinningPattern;
    // SHAKER TOO BIG?
    static String  BingoShake = "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                                                                    \r\n" + //
                "                           @                                                                        \r\n" + //
                "                          @@@                                                                       \r\n" + //
                "                        @@  @@                          @@   @@@@@                                  \r\n" + //
                "                       @@   @@@@                                   @@@                              \r\n" + //
                "                     @@    @   @@                                     @@@                           \r\n" + //
                "                    @@   @@     @@@                                      @@                         \r\n" + //
                "                   @@@@@          @@                             @@        @@                       \r\n" + //
                "                       @@          +@@               @@@@@@        @@        @@                     \r\n" + //
                "                         @@           @@@     @@@@@@@    @@          @         @@                   \r\n" + //
                "                           @@           @@@@@@-         @  @          @@         @                  \r\n" + //
                "                             @@         @@             @@  @@           @         @@                \r\n" + //
                "                               @@       .             @@    @            @                          \r\n" + //
                "                                 @@@@@@              @       @                                      \r\n" + //
                "                                 @                 @@        =@                                     \r\n" + //
                "                                 @                @@          @                                     \r\n" + //
                "                                 @               @@            @%                                   \r\n" + //
                "                                 @             @@               @                                   \r\n" + //
                "                                 @          @@@                  @                                  \r\n" + //
                "                                @@       @@@                      @                                 \r\n" + //
                "                                @     @@@                         @@                                \r\n" + //
                "                               @@ @@@@                           @@                                 \r\n" + //
                "                                @@@                             @@                                  \r\n" + //
                "                                    @@@@                       @                                    \r\n" + //
                "                     @                  @@@@                 @@                                     \r\n" + //
                "                     @                      @@@@@          @@                                       \r\n" + //
                "                     @       @                   @@@@@   @@                                         \r\n" + //
                "                     @@      #@                       @@@                                           \r\n" + //
                "                      @        @                                                                    \r\n" + //
                "                       @        @@                                                                  \r\n" + //
                "                        @         @@@                                                               \r\n" + //
                "                         @@          @@@                                                            \r\n" + //
                "                           @              @                                                         \r\n" + //
                "                            @@                                                                      \r\n" + //
                "                              @@                                                                    \r\n" + //
                "                                @@@                                                                 \r\n" + //
                "                                   @@@                                                              \r\n" + //
                "                                       @@@@                                                         \r\n" + //
                "                                               @@                                                   \r\n" + //
                "                                                                                                    \r\n" + //
                "                                                             ";
    
    public static void main(String[] args) {

        // WELCOME SCREEN

        // HELP MODULE
        System.out.println("something");
        System.out.println("test");
        // INITIALIZATION OF VARIABLES
        createBingoCard();

        // MAIN GAME LOOP
        letsPlayBingo();

        // 5*5 grid, center is blank
        String numbers = "";
        String alreadyRolledNumbers = "";
    }

    static void letsPlayBingo() {
        char randomNumberRepr;
        int randomNumber;

        while (true) {
            char letter;
            randomNumberRepr = getRandomNumberRepr(0, ASCIIMAX);
            randomNumber = randomNumberRepr - 33;
            if (rolledNumbersRepr.contains(randomNumberRepr+"")) {
                continue;
            }
            if (randomNumber > 1 && randomNumber <= 15) {
                letter = 'B';
            } else if (randomNumber > 15 && randomNumber <= 30) {
                letter = 'I';
            } else if (randomNumber > 30 && randomNumber <= 45) {
                letter = 'N';
            } else if (randomNumber > 45 && randomNumber <= 60) {
                letter = 'G';
            } else {
                letter = 'O';
            }
            if (rowCheck() == true){
                System.out.println("BINGO!");
                break;
            }
            //System.out.println(BingoShake); 
            System.out.println(" Sa Lettra SANG! " + letter + "!" + "... " + randomNumber + "!");
            if (bingoCardRepr.contains(randomNumberRepr+"")) {
                System.out.println("May " + randomNumber + "!");
            } else {
                System.out.println("Wala " + randomNumber + ".");

            }
            rolledNumbersRepr += (randomNumberRepr) + " ";
            printBingoCard();
            System.out.println("\nPress enter to roll again. ");
            scanner.nextLine();
        }
    }

    static void createBingoCard() {
        int max, min;
        char randNumberRepr;
        String sep = "\t\t\t\t\t";

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; ) {
                if (i == 2 && j == 2) {
                    bingoCardRepr += " " + sep;
                    j++;
                    continue;
                }
                max = (j + 1) * 15;
                min = j * 15 + 1;
                randNumberRepr = getRandomNumberRepr(min, max); // (rand.nextInt(107 - 33) + 33);
                if (bingoCardRepr.indexOf(randNumberRepr) == -1) {
                    bingoCardRepr += randNumberRepr + sep;
                    j++;
                }
            }
            bingoCardRepr += "\n\n";
        }
    }

    static void printBingoCard() {
        char currentNumRepr;
        int currentNum;

        for (int i = 0; i < bingoCardRepr.length(); i++) {
            currentNumRepr = bingoCardRepr.charAt(i);
            currentNum = currentNumRepr - 33;

            if (currentNumRepr == ' ') {
                System.out.print("B");
            } else if (currentNumRepr == '\n' || currentNumRepr == '\t') {
                System.out.print(currentNumRepr);
            } else if (rolledNumbersRepr.contains(currentNumRepr+"")) {
                System.out.print("(" + currentNum + ")");
            } else {
                System.out.print(currentNum);
            }
        }
    }

    static char getRandomNumberRepr(int min, int max) {
        return (char) ((rand.nextInt(max - min) + min) + 33);
    }

    static void contains(char repr) {
        
    }
    static boolean rowCheck(){
        boolean completemark = true;

        for (int i = 0; i < bingoCardRepr.length(); i++){
            if  ( ! rolledNumbersRepr.contains(bingoCardRepr.charAt(i)+"")) {
                completemark = false;
                
            if ( bingoCardRepr.charAt(i) == '\n'){
                continue;
            }
            else {return completemark;
                
            }
        }
    }
    


    }
}
