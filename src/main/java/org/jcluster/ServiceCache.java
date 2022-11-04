package org.jcluster;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mypower24.smd.rar.lib;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// *
// * @author henry
// */
//public class ServiceCache {
//
//    private List<IDummy> services;
//
//    public ServiceCache() {
//        services = new ArrayList<IDummy>();
//    }
//
//    public IDummy getService(String serviceName) {
//
//        for (IDummy service : services) {
//            if (service.getName().equalsIgnoreCase(serviceName)) {
//                System.out.println("Returning cached  " + serviceName + " object");
//                return service;
//            }
//        }
//        return null;
//    }
//
//    public void addService(IDummy newService) {
//        boolean exists = false;
//
//        for (IDummy service : services) {
//            if (service.getName().equalsIgnoreCase(newService.getName())) {
//                exists = true;
//            }
//        }
//        if (!exists) {
//            services.add(newService);
//        }
//    }
//}
