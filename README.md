# Simple-CCMP-Protocol-Simulation
### This is a crude and simple simulation of the CCMP (Counter Mode CBC-MAC Protocol), also known as WPA2 or RSN. This protocol is is an encryption protocol used in the IEEE 802.11 standard.
CCMP is an enhanced data cryptographic encapsulation mechanism designed for data confidentiality and based upon the Counter Mode with CBC-MAC (CCM mode) of the Advanced Encryption Standard (AES) standard. For more information about this protocol, click [here](https://en.wikipedia.org/wiki/CCMP_(cryptography)).

### ***A diagram of the protocol is shown [here](https://www.eetimes.com/wp-content/uploads/media-1097750-security-fig5.gif)***

This project is a simple and crude simulation of the aforementioned protocol. This simulation is achieved with **6 classes:** 

**1. CCMPImplementationSimple, which consists of 14 methods:**
  - **5 Setters/Getters.**
  - **A constructor with parameters.**
  - **byte[ ] xor**
    - A method that calculates and returns the logical **xor** of two byte arrays.
   - **byte[ ] MacHexConvert**
      - A method that converts the value of the **MAC** address to a byte array.
   - **void calculateNonce**
      - A method that calculates two versions of the **nonce** number: 
        - **nonce**: 128-bit nonce number without padding used in the process of calculating the **MIC** value.
        - **nonce_non_pad**: 104-bit nonce number without padding used in the process of encryption.
   - **byte[ ] MIC_calculation**
      - A method that calculates the **MIC** number and returns it's most important 64 bits.
   - **byte[ ] Encryption**
      - A method that combines the **nonce_non_pad** number with a counter which increments iteratively and uses it in the AES.encrypt method in order to encrypt the ***data*** part of the IP packet. At the end of the encrypted text, the calculated **MIC** number is concatenated.
   - **byte[ ] Decryption**
      - A method that combines the **nonce_non_pad** number with a counter which increments iteratively and uses it in the AES.decrypt method in order to decrypt the ***data*** part of the IP packet.
   - **void Verification**
      - A method that verifies the process by calculating a new **MIC** number based on the decrypted text and the already known header and compares the new **MIC** number with the existing **MIC** number. If these two numbers match, the packet is verified. If they dont match, an exception is thrown and the program terminates.
   - **String printProcess**
      - A method that prints the process step by step for better visualization.

**2. SimpleIPPacketSimulation**
  - This class simulates a crude and simple IP packet consisting of only a header and data parts. Both the header and data are represented as string lists for simplicity.

**3. AES**
  - This class uses the AES encryption and decryption algorithms provided in the javax.crypto.Cipher library
  
**4. StringPadding, which consists of 2 methods:**
  - **List<String> listDivide** 
    - A method that splits a single string into 128-bit sized blocks of strings and puts them into a list.
  - **String padd**
    - A method that takes the last remaining block of the original string, and pads it to exactly 128 bits if needed.
  
**5. NotVerifiableException**
  - An exception class.
  
**6. MainTest**
  - The main class used to run the program.
  
