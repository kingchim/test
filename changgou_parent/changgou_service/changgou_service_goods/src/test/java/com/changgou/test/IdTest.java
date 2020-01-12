package com.changgou.test;

import com.changgou.util.IdWorker;

public class IdTest {
    public static void main(String[] args) {
        IdWorker idWorker = new IdWorker(1, 1);
        for (int i = 0; i <10000 ; i++) {
            long nextId = idWorker.nextId();
            System.out.println(nextId);
        }
    }
}
