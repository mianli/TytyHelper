package com.mli.crown.tytyhelper.tools;

/**
 * Created by crown on 2017/6/6.
 */

public class Global {

    public static MessageController handler;

    public Global() {

    }

    public static void init() {
        handler = new MessageController();
    }

}
