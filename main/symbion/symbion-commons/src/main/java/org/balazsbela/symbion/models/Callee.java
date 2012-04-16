package org.balazsbela.symbion.models;

public class Callee {
    String className;
    String methodName;
    String methodDesc;
    String source;
    int line;

    public Callee(String cName, String mName, String mDesc, String src, int ln) {
        className = cName; methodName = mName; methodDesc = mDesc; source = src; line = ln;
    }
}

