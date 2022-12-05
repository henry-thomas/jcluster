/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2.impl;

import com.mypower24.test2.controller.DataInitializer;
import com.mypower24.test2.controller.entity.Dummy;
import javax.ejb.Stateless;
import com.mypower24.test2.interfaces.IMoreBusinessMethods;
import javax.inject.Inject;

/**
 *
 * @author henry
 */
@Stateless
public class AnotherBusinessMethodImpl implements IMoreBusinessMethods {

    @Inject
    DataInitializer data;

    @Override
    public Dummy execAnotherBusinessMethod(String name) {
        return data.getDataMap().get(name);
    }

    @Override
    public Object getLargeData() {
        return data.getBigData();
    }

}
