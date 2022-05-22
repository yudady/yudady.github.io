package tk.tommy.npe;

/**
 * -XX:+ShowCodeDetailsInExceptionMessages
 * Enables printing of improved NullPointerException messages. When an application throws a NullPointerException,
 * the option enables the JVM to analyze the program's bytecode instructions to determine precisely
 * which reference is null, and describes the source with a null-detail message.
 * The null-detail message is calculated and returned by NullPointerException.getMessage(),
 * and will be printed as the exception message along with the method, filename, and line number.
 *
 * By default, this option is enabled.
 */
public class NullPointExceptionMessageTest {

    public static void main(String[] args) {
        Npe npe = new Npe();
        npe.getName();
    }

    static public class Npe {

        public String[] name;

        public String getName() {
            return name[0];
        }


    }
}
