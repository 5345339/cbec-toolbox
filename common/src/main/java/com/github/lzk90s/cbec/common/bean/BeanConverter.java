package com.github.lzk90s.cbec.common.bean;

import com.google.common.base.Converter;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.*;

public class BeanConverter<A1, A2> extends Converter<A1, A2> {
    private Class<A1> a1Class;
    private Class<A2> a2Class;

    public BeanConverter(Class<A1> a1Class, Class<A2> a2Class){
        this.a1Class = a1Class;
        this.a2Class = a2Class;
    }

    @Override
    public A2 doForward(A1 a1) {
        try {
            A2 a2 = a2Class.newInstance();
            BeanUtils.copyProperties(a1, a2);
            return a2;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public A1 doBackward(A2 a2) {
        try {
            A1 a1 = a1Class.newInstance();
            BeanUtils.copyProperties(a2, a1);
            return a1;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
