import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BINGO {
    // GLOBAL VARIABLES
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);

    static final int ASCIIMIN = 32, BINGOMAX = 75;
    static String rolledNumbersRepr = "", bingoCardRepr = "";
    static String bingoCardPattern = "", bingoWinningPatterns = "";
    static String sep = "\t\t";

    static String bingo = """
        _______  ___   __    _  _______  _______  __
        |  _    ||   | |  |  | ||       ||       ||  |
        | |_|   ||   | |   |_| ||    ___||   _   ||  |
        |       ||   | |       ||   | __ |  | |  ||  |
        |  _   | |   | |  _    ||   ||  ||  |_|  ||__|
        | |_|   ||   | | | |   ||   |_| ||       | __
        |_______||___| |_|  |__||_______||_______||__|
            """;

    // SHAKER TOO BIG?
    static String  BingoShake = """
        @
        @@@
      @@  @@                          @@   @@@@@
     @@   @@@@                                   @@@
   @@    @   @@                                     @@@
  @@   @@     @@@                                      @@
 @@@@@          @@                             @@        @@
     @@          +@@               @@@@@@        @@        @@
       @@           @@@     @@@@@@@    @@          @         @@
         @@           @@@@@@-         @  @          @@         @
           @@         @@             @@  @@           @         @@
             @@       .             @@    @            @
               @@@@@@              @       @
               @                 @@        =@
               @                @@          @
               @               @@            @%
               @             @@               @
               @          @@@                  @
              @@       @@@                      @
              @     @@@                         @@
             @@ @@@@                           @@
              @@@                             @@
                  @@@@                       @
   @                  @@@@                 @@
   @                      @@@@@          @@
   @       @                   @@@@@   @@
   @@      #@                       @@@
    @        @
     @        @@
      @         @@@
       @@          @@@
         @              @
          @@
            @@
              @@@
                 @@@
                     @@@@
                             @@
""";

    public static void main(String[] args) throws IOException, InterruptedException {
        // WELCOME SCREEN
        System.out.println(bingo);
        printlnInteractive("Tara BINGO! (ENTER)");

        // PATTERN CREATION
        while (true) {
            System.out.print("Do you want to create a custom winning pattern? (y/n): ");
            String response = scanner.nextLine().toLowerCase().strip();
            if (response.equals("y")) {
                patternCreation();
                break;
            } else if (response == "n") {
                break;
            } else {
                System.out.println("Invalid input.");
            }
        }

        // HELP MODULE
        // playTutorial();

        // INITIALIZATION OF VARIABLES
        createBingoCardRepr();

        // MAIN GAME LOOP
        letsPlayBingo();
    }

    static void letsPlayBingo() throws IOException, InterruptedException {
        char letter;
        char randomNumberRepr;
        int randomNumber;
        String membership;

        cls();
        while (true) {
            do {
                randomNumberRepr = getNumberRepr(getRandomNumber(1, BINGOMAX + 1));
            } while (rolledNumbersRepr.contains(randomNumberRepr+""));

            randomNumber = randomNumberRepr - ASCIIMIN;
            membership = (bingoCardRepr.contains(randomNumberRepr+"")) ? "May " : "Wala ";

            if (randomNumber >= 1 && randomNumber <= 15) {
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

            updateBingoCardPattern();

            System.out.println(bingoWinningPatterns);
            System.out.println(bingoCardPattern);
            if (winningPatternContainsCardPattern()) {
                System.out.println("BINGO!!!");
                printBingoCardRepr();
                break;
            };

            // System.out.print(BingoShake);
            printBingoCardRepr();

            printlnInteractive("Taya taya...");
            printInteractive("Sa letra sang...");
            printInteractive(letter + "!");
            printInteractive(randomNumber + "!");
            System.out.println(membership + randomNumber + "!");

            rolledNumbersRepr += randomNumberRepr + " ";
            printInteractive("\nRoll again >>>");
            cls();
        }
    }

    static void createBingoCardRepr() {
        /*
        * bingoCardRepr is a one-directional string which includes five rows (separated by newline) of five characters.
         * We cannot naively store the card numbers as numerical values, or verify membership of single-digit numbers.
         * For example: We rolled a '1'. If we use bingoCardRepr.contains('1'), it will look for 1, 12, 13, ..., 21, 31, and so on.
         *
         * One technique I could think of is we could change the way we check the membership of rolled numbers.
         * But as of right now I cannot think of anything about this.
         *
         * What I came up with and ultimately chose was (Repr)esenting the rolled number into an ASCII character.
         * The number is always converted to its associated character, and vice versa.
         * We start the mapping at the 33rd character ('!') up to 107 ('k', 33 + 75 - 1).
         * The SPACE, is reserved to the middle FREE SQUARE of the card.
         *
         * But I am open to suggestions. Is there a better way of doing this?
         */
        char randNumberRepr;
        int min, max;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                 if (row == 2 && col == 2) {
                    bingoCardRepr += ' ';
                    continue;
                }
                min = col * 15 + 1;
                max = (col + 1) * 15 + 1;
                do {
                    randNumberRepr = getNumberRepr(getRandomNumber(min, max));
                } while (bingoCardRepr.contains(randNumberRepr+""));
                bingoCardRepr += randNumberRepr+"";
            }
            bingoCardRepr += '\n';
        }
    }

    static void printBingoCardRepr() {
        /*
         * Since bingoCardRepr is a 1-dimentional form, there is no need for nested loops.
         * I instead used conditionals to further control the printing of the card.
         *
         * TODO row and column separation
         */
        char currentNumRepr;
        int currentNum;

        System.out.println("B" + sep + "I" + sep + "N" + sep + "G" + sep + "O\n");
        for (int i = 0; i < bingoCardRepr.length(); i++) {
            currentNumRepr = bingoCardRepr.charAt(i);

            if (currentNumRepr == ' ') {
                System.out.print("FS" + sep);
            } else if (currentNumRepr == '\n') {
                System.out.println(currentNumRepr);
            } else {
                currentNum = getReprNumber(currentNumRepr);
                // enclose the number with parentheses if the number is already called out
                System.out.print(rolledNumbersRepr.contains(currentNumRepr+"") ? "(" + currentNum + ")" : currentNum);
                System.out.print(sep);
            }
        }
    }

    static int getRandomNumber(int min, int max) {
        // .nextInt((max-min) + min) --> range between min (inclusive) and max (exclusive)
        return rand.nextInt(max - min) + min;
    }

    static boolean horizontalCheck(){
        for (int i = 0; i < 5; i++){
            int j;
        }
        return false;
    }

    static char getNumberRepr(int number) {
        return (char) (number + ASCIIMIN);
    }

    static int getReprNumber(char repr) {
        return repr - ASCIIMIN;
    }

    static void cls() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    static void printlnInteractive(String s) {
        System.out.println(s + " (ENTER) ");
        scanner.nextLine();
    }

    static void printInteractive(String s) {
        System.out.print(s + " (ENTER) ");
        scanner.nextLine();
    }

    static void playTutorial() throws IOException, InterruptedException {
        // QUEENIELYN
        cls();
        printlnInteractive("Hello! Welcome to the BINGO tutorial!");
        printInteractive("Let's start with the mechanics.");
        printlnInteractive("BINGO is a game of chance.");
        printlnInteractive("Once every turn, a number is rolled randomly.");
        printlnInteractive("""
                On a BINGO card that looks something like this:
                12\t\t22\t\t43\t\t51\t\t72\n
                5\t\t23\t\t35\t\t57\t\t61\n
                9\t\t28\t\tB\t\t48\t\t69\n
                11\t\t29\t\t41\t\t49\t\t62\n
                6\t\t19\t\t42\t\t50\t\t65\n
                if the rolled number is present, that square gets marked.""");
        printlnInteractive("""
                The middle, denoted B, is a sort of free square.
                Consider it already marked.""");
        printlnInteractive("""
                The game is won if five (5) marks in a row is achieved,
                either vertically, horizontally, or diagonally.""");
        printlnInteractive("Good luck and have fun!");
        cls();
    }

    static void patternCreation() {
        /*
         * A `pattern` is a string of '*'s and '-', where '*' is a marked square, while '-' is the default.
         * All winning patterns, if there are multiple (to be implemented), are stored in a single `bingoWinningPatterns` String, separated by  ',' between.
         *
         * Player bingo cards are also converted into patterns, and we can simply iterate the cardPattern against the winningPatterns if the marks are matching.
         * Feeling proud...
         */

        // TOOL TUTORIAL
        patternCreationTutorial();
        String response;

        for (int i = 0; i < 25; i++) {
            if (i == 12) {
                bingoWinningPatterns += '*';
                continue;
            }

            System.out.println("B" + sep + "I" + sep + "N" + sep + "G" + sep + "O");
            for (int j = 0; j < 25; j++) {
                if (j < i) {
                    System.out.print(bingoWinningPatterns.charAt(j));
                } else if (j == i) {
                    System.out.print("[-]");
                } else if (j == 12) {
                    System.out.print("*");
                } else {
                    System.out.print("-");
                }

                if ((j+1) % 5 == 0) {
                    System.out.println('\n');
                } else {
                    System.out.print(sep);
                }
            }
            System.out.println();

            do {
                System.out.print("Mark current spot as winning pattern? (y/n): ");
                response = scanner.nextLine().toLowerCase().strip();
            } while (!(response.equals("y") || response.equals("n")));

            bingoWinningPatterns += (response.equals("y")) ? '*' : '-';
        }
        bingoWinningPatterns += ',';
    }

    static void updateBingoCardPattern() {
        bingoCardPattern = "";
        for (int i = 0; i < bingoCardRepr.length(); i++) {
            char currentChar = bingoCardRepr.charAt(i);
            if (currentChar == '\n') continue;
            bingoCardPattern += (rolledNumbersRepr.contains(currentChar+"")) ? '*' : '-';
        }
    }

    static boolean winningPatternContainsCardPattern() {
        boolean won = true;
        for (int i = 0; i < bingoCardPattern.length(); i++) {
            char currCard = bingoCardPattern.charAt(i);
            char currPatt = bingoWinningPatterns.charAt(i);
            if (currPatt == '*' && currCard != '*') return false;
        }
        return won;
    }

    static void patternCreationTutorial () {
        System.out.println("TODO");
    }

    static boolean rowCheck() {
        boolean completeMark = true;

        for (int i = 0; i < bingoCardRepr.length(); i++) {
            if (!rolledNumbersRepr.contains(bingoCardRepr.charAt(i) + "")) {
                completeMark = false;
                break;
            }
            if (bingoCardRepr.charAt(i) == '\n') continue;
        }
        System.out.println(completeMark);
        return completeMark;
    }
}
