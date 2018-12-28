package com.example.mattia.fotocamera.OCRManager;

public class OCRFactory {

  public TextRecognizer getTextRecognizer(Type mode){
    switch(mode){
        case TESSERECT: return useTesseract();
        case ML_KIT: return useMLKit();
    }
    return null;
  }

  private TextRecognizer useTesseract(){
    return new Tesseract();
  }

  private TextRecognizer useMLKit(){
    return new MlKit();
  }
}
