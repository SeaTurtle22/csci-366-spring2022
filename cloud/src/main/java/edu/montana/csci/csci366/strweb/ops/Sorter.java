package edu.montana.csci.csci366.strweb.ops;

import java.util.Arrays;
import java.util.Collections;

/**
 * This class is a simple sorter that implements sorting a strings rows in different ways
 */
public class Sorter {
    private final String _strings;

    public Sorter(String strings) {
        _strings = strings;
    }

    public String sort() {
        String[] split = _strings.split("[\n]|[\r\n]"); //split input by newline (with windows support)
        Arrays.sort(split); //sort with built-in method
        return String.join("\n", split); //Staple the list together into a string and send it back
    }

    public String reverseSort() {
        String[] split = _strings.split("[\n]|[\r\n]"); //split input by newline (with windows support)
        Arrays.sort(split, Collections.reverseOrder()); //sort with built-in method and built-in reverse comparitor
        return String.join("\n", split); //Staple the list together into a string and send it back
    }

    public String parallelSort() {
        String[] split = _strings.split("[\n]|[\r\n]"); //split input by newline (with windows support)
        Arrays.parallelSort(split); //sort with built-in more efficent parallel method
        return String.join("\n", split); //Staple the list together into a string and send it back
    }
}
