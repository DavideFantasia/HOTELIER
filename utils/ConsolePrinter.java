package utils;

/**
 * classe dedicata alla stampa sincronizzata sul stdOut
 */
public class ConsolePrinter {
    /**
     * stampa in maniera sincronizzata su stdOut
     * @param msg messaggio da stampare
     */
    public static synchronized void printToConsole(String msg){
        System.out.printf(msg);
        System.out.flush();
    }

    public static void mainMenu(){
        String title= ",--.  ,--. ,-----. ,--------.,------.,--.   ,--.,------.,------. \r\n" + //
                "|  '--'  |'  .-.  ''--.  .--'|  .---'|  |   |  ||  .---'|  .--. '\r\n" + //
                "|  .--.  ||  | |  |   |  |   |  `--, |  |   |  ||  `--, |  '--'.'\r\n" + //
                "|  |  |  |'  '-'  '   |  |   |  `---.|  '--.|  ||  `---.|  |\\  \\ \r\n" + //
                "`--'  `--' `-----'    `--'   `------'`-----'`--'`------'`--' '--'\n";

        String opzioni= "Per inserire un comando, digitare le parole chiave\n"+//
                        "[per parole con degli spazi, delimitare con degli \"...\"]\r\n" + //
                        "Opzioni di comando:\r\n" + //
                        "\tlogin <username> <password>\r\n" + //
                        "\tlogout\r\n" + //
                        "\texit\r\n" + //
                        "\tregister <username> <password>\r\n" + //
                        "\tsearchAllHotel <city>\r\n" + //
                        "\tsearchHotel \"<nome hotel>\" <city>\r\n" + //
                        "\tinsertReview \"<nome hotel>\" <city> <Global Rate> <cleaning> <position> <services> <quality>\r\n" + //
                        "\tshowMyBadge\r\n";
        printToConsole(ConsoleColor.CLEAR+ConsoleColor.GREEN+title+opzioni+ConsoleColor.RESET);
    }
}
