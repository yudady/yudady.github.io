package tk.tommy.jdk8;

public class Example {

    private String mainLoop() {
        return new Dcevm("123").getName();
    }

    public static void main(String[] args) throws InterruptedException {
        Example example = new Example();
        while (true) {
            System.out.println("[LOG] = " + example.mainLoop());;
            Thread.sleep(1000);
        }

    }


}

