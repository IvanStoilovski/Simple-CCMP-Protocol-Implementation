package CCMP_Simple_Simulation;

import java.util.List;
// This class simulates a crude and simple IP packet consisting of only a header and data parts.
// Both the header and data are represented as a list of strings for simplicity
public class SimpleIPPacketSimulation {
    List<String> header;
    List<String> data;

    public SimpleIPPacketSimulation(String header, String data) {
        StringPadding p_header = new StringPadding(header);
        p_header.padd();
        this.header = p_header.listDivide();
        StringPadding p_data = new StringPadding(data);
        p_data.padd();
        this.data = p_data.listDivide();
    }

}
