package com.dse.model;

public interface Subscriber<T> {

    void onMessage(String topic, T message);
}
