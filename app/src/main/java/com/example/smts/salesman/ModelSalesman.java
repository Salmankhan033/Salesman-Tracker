package com.example.smts.salesman;

import android.widget.EditText;

class ModelSalesman {

    String cnic_salesman;
    String email_salesman;
    String mob_salesman;
    String name_salesman;
    String password_salesman;
    String workingArea;

    public String getWorkingArea() {
        return workingArea;
    }

    public void setWorkingArea(String workingArea) {
        this.workingArea = workingArea;
    }

    public ModelSalesman(){

    }

    public String getCnic_salesman() {
        return cnic_salesman;
    }

    public void setCnic_salesman(String cnic_salesman) {
        this.cnic_salesman = cnic_salesman;
    }

    public String getName_salesman() {
        return name_salesman;
    }

    public void setName_salesman(String name_salesman) {
        this.name_salesman = name_salesman;
    }

    public String getMob_salesman() {
        return mob_salesman;
    }

    public void setMob_salesman(String mob_salesman) {
        this.mob_salesman = mob_salesman;
    }

    public String getEmail_salesman() {
        return email_salesman;
    }

    public void setEmail_salesman(String email_salesman) {
        this.email_salesman = email_salesman;
    }

    public String getPassword_salesman() {
        return password_salesman;
    }

    public void setPassword_salesman(String password_salesman) {
        this.password_salesman = password_salesman;
    }
}
