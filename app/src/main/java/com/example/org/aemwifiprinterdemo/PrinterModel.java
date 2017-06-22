package com.example.org.aemwifiprinterdemo;

/**
 * Created on 25/5/17.
 */

public class PrinterModel {

    String ipAddress, printeryType;
    boolean isKitchen, isBar, isCash;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isBar() {
        return isBar;
    }

    public void setBar(boolean bar) {
        isBar = bar;
    }

    public boolean isCash() {
        return isCash;
    }

    public void setCash(boolean cash) {
        isCash = cash;
    }

    public boolean isKitchen() {
        return isKitchen;
    }

    public void setKitchen(boolean kitchen) {
        isKitchen = kitchen;
    }

    public String getPrinteryType() {
        return printeryType;
    }

    public void setPrinteryType(String printeryType) {
        this.printeryType = printeryType;
    }
}
