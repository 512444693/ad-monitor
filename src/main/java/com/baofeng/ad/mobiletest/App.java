package com.baofeng.ad.mobiletest;

import com.baofeng.ad.mobiletest.server.MyServer;

public class App {
    public static void main( String[] args ) {
        MyServer.getInstance().start();
    }
}
