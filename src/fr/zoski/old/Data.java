package fr.zoski.old;

import java.io.Serializable;

/**
 * Created by gael on 30/12/15.
 */
public class Data implements Serializable {
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
