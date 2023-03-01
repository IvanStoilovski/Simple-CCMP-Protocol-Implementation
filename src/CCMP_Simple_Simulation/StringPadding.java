package CCMP_Simple_Simulation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
// This class is used to divide the message in 128-bit blocks, and if the last block isn't 128 bits, it is padded to exactly 128 bits.
public class StringPadding {
    private String message;
    public StringPadding(String s) {
        this.message = s;
    }

    // Divides the incoming string to 128-bit strings and puts them in a list
    public List<String> listDivide() {
        String s = message;
        List<String> list = new ArrayList<>();
        int numOfBits = s.getBytes(StandardCharsets.UTF_8).length;
        int counter = numOfBits / 16;
        for (int i = 0; i < counter; ++i) {
            if (i == 0)
                list.add(s.substring(i, 16));
            else {
                list.add(s.substring(i * 16, (i + 1) * 16));
            }
        }
        return list;
    }

    // This method pads the remaining packet to exactly 128 bits
    public String padd() {
        String s = message;
        String result;
        int number = s.getBytes(StandardCharsets.UTF_8).length / 16;
        if (s.getBytes(StandardCharsets.UTF_8).length % 16 == 0) {
            result = s;
        } else {
            String helper;
            result = s.substring(0, number * 16);
            helper = s.substring(number * 16);
            int i = helper.length();
            for (int j = i; j < 16; ++j) {
                helper += "0";
            }
            result += helper;
        }
        this.message = result;
        return message;
    }
}
