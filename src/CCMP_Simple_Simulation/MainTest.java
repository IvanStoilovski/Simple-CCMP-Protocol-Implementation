package CCMP_Simple_Simulation;

public class MainTest {
    public static void main(String[] args) {
        String secretKey = "thesecretkey";
        String header = "i+need_40+bytes_minimum+to_test_the_header";
        String bigger="Lorem Ipsum is simply dummy text of the printing and typesetting " +
                "industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen.";
        String MAC = "08-97-98-74-EE-9B";
        String ToS = "5";
        String PN = "563875";
        CCMPImplementationSimple impl = new CCMPImplementationSimple(PN, MAC, ToS, header, bigger, secretKey);
        System.out.println(impl.printProcess());
    }
}
