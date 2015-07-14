package org.balazsbela.symbion.console.client;

public class ClientException extends Exception {

    public ClientException(String s) {
        super(s);
    }
    
    public ClientException(String s, Throwable t) {
        super(s, t);
    }
}
