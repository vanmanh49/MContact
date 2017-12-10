package com.vm.mcontact.model;

import java.util.List;

/**
 * Created by VanManh on 09-Dec-17.
 */

public class Contact {
    private String name;
    private String phoneNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Contact(String name, String phoneNum) {

        this.name = name;
        this.phoneNum = phoneNum;
    }

    public boolean checkValidate() {
        if (this.getName().equals("") || this.phoneNum.equals("")) {
            return false;
        }
        if (this.getName() == null || this.phoneNum == null) {
            return false;
        }
        return true;
    }

    public boolean checkContact(List<Contact> list) {
        for (Contact c : list) {
            if (this.getName().equals(c.getName()) && this.getPhoneNum().equals(c.getPhoneNum())) {
                return true;
            }
        }
        return false;
    }
}
