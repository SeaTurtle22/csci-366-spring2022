package edu.montana.csci.csci366.strweb.ops;

import java.util.concurrent.CountDownLatch;

/**
 * This class is should calculate the length of each line and replace the line in the
 * array with a string representation of its length
 */
public class LineLengthTransformer {
    String[] _lines;

    public LineLengthTransformer(String strings) {
        _lines = strings.split("\n"); //Split up the text-box contents by newline
    }

    public String toLengths() {
        CountDownLatch latch = new CountDownLatch(_lines.length); //creates a latch that ensures that the main thread will wait until every line is processed
        for (int i = 0; i < _lines.length; i++) {
            String line = _lines[i];
            LineLengthCalculator lineLengthCalculator = new LineLengthCalculator(i, latch); //creates a new runnable to process a single line
            new Thread(lineLengthCalculator).start(); //assigns the runnable to a new thread
        }
        try {
            latch.await(); //waits until every line is processed
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return String.join("\n", _lines);
    }

    class LineLengthCalculator implements Runnable {
        private final int index;
        private final CountDownLatch latch;

        public LineLengthCalculator(int index, CountDownLatch latch) {
            this.index = index;
            this.latch = latch;

        }

        public void run() {
            _lines[index] = String.valueOf(_lines[index].length()); //sets the string to the length of the string
            latch.countDown(); //decreases the latch once the line is processed, ensuring that eventually the main thread will start again
        }
    }

}
