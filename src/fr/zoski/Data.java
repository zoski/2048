package fr.zoski;

/**
 * Created by gael on 30/12/15.
 */
public class Data {
    /*
    Data container
    to be transformed as a byte[] and send throw the network
     */

    private String txt;
    private Grid g;

    public Data(String txt, Grid g) {
        this.txt = txt;
        this.g = g;
    }


}
