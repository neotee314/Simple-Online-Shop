package com.neotee.ecommercesystem.solution.storageunit.application;

import com.neotee.ecommercesystem.domainprimitives.ZipCode;

public class test {
    public static void main(String[] args) {
        ZipCode client = (ZipCode) ZipCode.of("31463");
        ZipCode s52355 = (ZipCode) ZipCode.of("52355");//25
        ZipCode c76532 = (ZipCode) ZipCode.of("76532");//45
        ZipCode c97618 = (ZipCode) ZipCode.of("97618");//45
        ZipCode c89250 = (ZipCode) ZipCode.of("89250");//55
        System.out.println(client.difference(s52355));
        System.out.println(client.difference(c76532));
        System.out.println(client.difference(c97618));
        System.out.println(client.difference(c89250));

        ZipCode client0 = (ZipCode) ZipCode.of("02314");
        ZipCode su1 = (ZipCode) ZipCode.of("02313");
        ZipCode su2 = (ZipCode) ZipCode.of("02345");
        ZipCode su3 = (ZipCode) ZipCode.of("44923");
        System.out.println("02314 differece to 02313 ="+ client0.difference(su1));
        System.out.println("02314 differece to 02345 = "+ client0.difference(su2));
        System.out.println("02314 differece to 44923 = "+ client0.difference(su3));
    }
}
