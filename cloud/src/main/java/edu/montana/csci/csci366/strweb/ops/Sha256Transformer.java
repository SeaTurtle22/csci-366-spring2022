package edu.montana.csci.csci366.strweb.ops;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class is should calculate the SHA 256 hexadecimal hash for each line and replace the line in the
 * array with that hash.
 *
 * It should do so using the ThreadPoolExecutor created below.
 */
public class Sha256Transformer {
    String[] _lines;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public Sha256Transformer(String strings) {
        _lines = strings.split("\n");
    }

    public String toSha256Hashes() {
        CountDownLatch latch = new CountDownLatch(_lines.length); //Set up our latch to make sure everything is done before moving on
        for (int i = 0; i < _lines.length; i++) {
            Sha256Computer sha256Computer = new Sha256Computer(i,latch); //Create a new instance of the SHA256 runnable to deal with one line of code
            executor.execute(sha256Computer); //Adds the runnable to the work pool that the 10 threads will work on. In this case there are a fixed 10 threads.
        }
        try {
            latch.await(); //Waits until every line is hashed to continue
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return String.join("\n", _lines); //returns the concatenation of every string in the array, each one on a newline
    }

    class Sha256Computer implements Runnable {
        private  final int index;
        private final CountDownLatch latch;

        public Sha256Computer(int index, CountDownLatch latch){
            this.index = index;
            this.latch = latch;
        }

        public void run(){
            MessageDigest digest = null;
            try{
                String originalString = _lines[index];
                digest = MessageDigest.getInstance("SHA-256"); //creates digest to hash our string later
                byte[] encodedhash = digest.digest(
                        originalString.getBytes(StandardCharsets.UTF_8)
                );//Creates a byte array that stores the result of hashing out original string
                _lines[index] = bytesToHex(encodedhash); //Convert the byte array to a hex string, and store it back to the lines array
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            latch.countDown(); //Decrements latch to ensure main thread continues once every line is hashed
        }

        private  String bytesToHex(byte[] hash) { //This whole thing converts byte arrays to equivalent hex string.
            StringBuilder hexString = new StringBuilder((2*hash.length));
            for(int i = 0; i < hash.length; i++){
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');//Adds leading zero if single digit to maintain overall place-value correctness
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }

}
