package com.bottlerocket.domod;

/**
 * Represents various components in the framework to make decisions off of component type,
 * for example different logging based on component
 *
 * Created by ford.arnett on 6/3/19
 */
public enum ComponentType {
    mockServer("mockserver"),
    dataLoader("dataloader"),
    bash("bash"),
    proguard("proguard"),
    reporter("reporter"),
    unknown("unrecognized framework component");


    String type;
    ComponentType(String type) {
        this.type = type;
    }
}
