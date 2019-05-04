package com.laqun.laqunserver;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<Integer> l = new ArrayList<>();
        l.add(0);
        for(int i=1; i<10; i++) {
            l.remove(0);
            l.add(i);
            System.out.println(l.get(0));
        }
    }
}
