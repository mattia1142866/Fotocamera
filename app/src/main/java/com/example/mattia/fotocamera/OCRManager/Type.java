package com.example.mattia.fotocamera.OCRManager;

public enum Type {
    TESSERECT(0),
    ML_KIT(1);

    private int type;
    Type(int type){
        this.type = type;
    }

    int getType(){
        return this.type;
    }
}
