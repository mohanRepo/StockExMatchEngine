package com.dse.model;

public interface Publisher<T> {

    void register(Subscriber<T> subscriber);

    void sendMessage(String topic, T message);

}
