package com.example.gameservicedemo.event;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/08/25/16:28
 * @Description:
 */
@FunctionalInterface
public interface EventHandler<E extends Event> {
    void handle(E event);
}
