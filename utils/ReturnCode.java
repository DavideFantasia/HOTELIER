package utils;

public enum ReturnCode {
    SUCCESS,
    USER_ALREADY_PRESENT_ERROR,
    HASHING_ERROR,
    NOT_LOGGEDIN_ERROR,
    NO_SUCH_HOTEL_ERROR,
    NO_SUCH_CITY_ERROR,
    WRONG_INPUT_ERROR,
    WRONG_PASSWORD_ERROR,
    CONNECTION_CLOSED,
    UNKNOW_ERROR;

    @Override
    public String toString() {
        switch (this) {
            case SUCCESS:
                return ConsoleColor.GREEN+this.name()+ConsoleColor.RESET;
            case CONNECTION_CLOSED:
                return ConsoleColor.MAGENTA+this.name()+ConsoleColor.RESET;
            default:
                return ConsoleColor.RED+this.name()+ConsoleColor.RESET;
        }
    }
}
