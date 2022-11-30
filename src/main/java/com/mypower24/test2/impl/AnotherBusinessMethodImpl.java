/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.test2.impl;

import java.util.Objects;
import javax.ejb.Stateless;
import com.mypower24.test2.interfaces.IMoreBusinessMethods;

/**
 *
 * @author henry
 */
@Stateless
public class AnotherBusinessMethodImpl implements IMoreBusinessMethods {

    @Override
    public Boolean execAnotherBusinessMethod(String name) {
        if (Objects.equals("1234", name)) {
            return true;

        }
        return false;
    }

}
