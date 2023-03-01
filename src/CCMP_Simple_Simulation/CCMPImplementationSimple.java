package CCMP_Simple_Simulation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CCMPImplementationSimple {
    SimpleIPPacketSimulation packet;
    String pn;
    String SourceMAC;
    String QoS;
    String header;
    String payload;
    byte[] MIC;
    String key;
    byte[] nonce_Non_Padd;
    byte[] nonce;
    byte [] encrypted;
    public byte[] getMIC() {
        return MIC;
    }

    public void setMIC(byte[] MIC) {
        this.MIC = MIC;
    }

    public void setNonce_Non_Padd(byte[] nonce_Non_Padd) {
        this.nonce_Non_Padd = nonce_Non_Padd;
    }

    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }
    public void setEncrypted(List<Byte> b)
    {
        byte[] pom=new byte[b.size()];
        for(int i=0;i<b.size();++i)
            pom[i]=b.get(i);
        this.encrypted=pom;
    }
    public CCMPImplementationSimple(String PN, String mac, String qos, String head, String data, String secretKey) {
        this.pn = PN;
        this.SourceMAC = mac;
        this.QoS = qos;
        this.header = head;
        this.payload = data;
        this.packet = new SimpleIPPacketSimulation(header, payload);
        this.key = secretKey;
        this.nonce_Non_Padd = new byte[13];
        this.nonce = new byte[16];
    }
    // Calculates and returns the logical xor of two byte arrays.
    public byte[] xor(byte[] b1, byte[] b2) {
        byte[] br = new byte[b1.length];
        for (int i = 0; i < b1.length; ++i) {
            br[i] = (byte) (b1[i] ^ b2[i]);
        }
        return br;
    }

    //Converts the MAC address from String to a byte array
    public byte[] MacHexConvert() {
        // Extracts only the Alphanumeric values of the MAC address
        String[] macAdd = SourceMAC.split("-");
        byte[] macAddressBytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            Integer hexV = Integer.parseInt(macAdd[i], 16);
            macAddressBytes[i] = hexV.byteValue();
        }
        return macAddressBytes;
    }
    // Calculates the nonce number which is later used when calculating the MIC number in the process of encryption and decryption
    public void calculateNonce() throws IOException {
        ByteArrayOutputStream outputStream_p = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream_np = new ByteArrayOutputStream();
        String tos = QoS + "000";
        byte[] nonce;
        byte[] nonce_non_pad;
        //nonce calculation with padding for MIC calculation
        outputStream_p.write(pn.getBytes(StandardCharsets.UTF_8));
        outputStream_p.write(MacHexConvert());
        outputStream_p.write(tos.getBytes(StandardCharsets.UTF_8));
        nonce = outputStream_p.toByteArray();
        setNonce(nonce);
        // nonce calculation without padding for MIC calculation when encrypting
        outputStream_np.write(pn.getBytes(StandardCharsets.UTF_8));
        outputStream_np.write(MacHexConvert());
        outputStream_np.write(QoS.getBytes(StandardCharsets.UTF_8));
        nonce_non_pad = outputStream_np.toByteArray();
        setNonce_Non_Padd(nonce_non_pad);
    }
    // Calculates the MIC
    public byte[] MIC_calculation(List<String> header,List<String> data) {
        byte[] mic_p = new byte[16];
        byte[] mic_i;
        byte[] end = new byte[8];
        for (int i = 0; i <header.size(); ++i) {
            if (i == 0) {
                mic_i = xor(AES.encrypt(nonce, key), header.get(0).getBytes(StandardCharsets.UTF_8));
            } else {
                mic_i = xor(mic_p, header.get(i).getBytes(StandardCharsets.UTF_8));
            }
            mic_p = AES.encrypt(mic_i, key);
        }
        for (String datum : data) {
            mic_i = xor(mic_p, datum.getBytes(StandardCharsets.UTF_8));
            mic_p = AES.encrypt(mic_i, key);
        }
        System.arraycopy(mic_p, 0, end, 0, 7);
        return end;
    }
    public byte[] Encryption() {
        List<Byte> pom_main = new ArrayList<>();
        List<Byte> pom_d = new ArrayList<>();
        byte[] ctrPreload=nonce_Non_Padd;
        int counter=0;
        for (int i = 0; i < packet.data.size(); ++i) {
            byte[] ctr_temp;
            // counter padding check
            if(counter<=9) {
                ctr_temp = (counter + "00").getBytes(StandardCharsets.UTF_8);
            }
            else {
                ctr_temp = (counter + "0").getBytes(StandardCharsets.UTF_8);
            }
            byte[] c = new byte[ctrPreload.length + ctr_temp.length];
            System.arraycopy(ctrPreload, 0, c, 0, ctrPreload.length);
            System.arraycopy(ctr_temp, 0, c, ctrPreload.length, ctr_temp.length);
            byte[] a1 = AES.encrypt(c, key);
            byte[] a2 = packet.data.get(i).getBytes(StandardCharsets.UTF_8);
            byte[] a3 = xor(a1, a2);
            for (Byte b : a3) {
                pom_d.add(b);
            }
            ++counter;
        }
        setEncrypted(pom_d);
        setMIC(MIC_calculation(packet.header,packet.data));
        for (Byte b : MIC)
            pom_d.add(b);
        pom_main.addAll(pom_d);
        byte[] enc = new byte[pom_main.size()];
        for (int i = 0; i < pom_main.size(); ++i) {
            enc[i] = pom_main.get(i);
        }
        return enc;
    }
    public byte[] Decryption()
    {
        byte[] ctrPreload=nonce_Non_Padd;
        ArrayList<Byte> lista=new ArrayList<>();
        int counter=0;
        byte [] dec_data=new byte[encrypted.length];
        List<byte[]>list=new ArrayList<>();
        int n=encrypted.length/16;
        for(int i=0;i<n;i++)
        {
            byte [] pom= Arrays.copyOfRange(encrypted,i*16,i*16+16);
            list.add(pom);
        }
        for (byte[] bytes : list) {
            byte[] ctr_temp;
            // counter padding check
            if (counter <= 9) {
                ctr_temp = (counter + "00").getBytes(StandardCharsets.UTF_8); //possible change
            } else {
                ctr_temp = (counter + "0").getBytes(StandardCharsets.UTF_8);
            }
            byte[] c = new byte[ctrPreload.length + ctr_temp.length];
            System.arraycopy(ctrPreload, 0, c, 0, ctrPreload.length);
            System.arraycopy(ctr_temp, 0, c, ctrPreload.length, ctr_temp.length);
            byte[] a1 = AES.encrypt(c, key);
            byte[] pom = (xor(a1, bytes));
            for (byte b : pom)
                lista.add(b);
            ++counter;
        }
        for(int i=0;i<lista.size();++i)
            dec_data[i]=lista.get(i);
        return dec_data;
    }
    public String printProcess()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Encrypting...").append("\n");
        byte [] enc=Encryption();
       // byte [] mic= impl.getMIC();
        sb.append("Printing the MIC: ").append("\n");
        for (byte b:MIC)
            sb.append(String.format("%02X ", b));
        sb.append("\n");
        sb.append("Printing the encrypted data+MIC: ").append("\n");
        for(byte b:enc)
            sb.append(String.format("%02X ", b));
        sb.append("\n");
        sb.append("Decrypting...").append("\n");
        //byte []transformed=Decryption();
        String str2= new String(Decryption(),StandardCharsets.UTF_8);
        sb.append("Printing the decrypted plaintext from the data: ").append("\n");
        sb.append(str2).append("\n");
        return sb.toString();
    }
}
