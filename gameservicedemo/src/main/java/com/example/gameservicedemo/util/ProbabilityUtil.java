package com.example.gameservicedemo.util;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/17:42
 * @Description:
 */
public class ProbabilityUtil {
    public static boolean getProbability(int chance){
        double v = Math.random() * 100;
        if (v < chance) {
            return true;
        }
        return false;
    }
}
