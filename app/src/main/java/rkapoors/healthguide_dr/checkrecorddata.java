package rkapoors.healthguide_dr;

/**
 * Created by KAPOOR's on 11-11-2017.
 */

public class checkrecorddata {
    public String dt;
    public String time;
    public String comment;
    public String value;
    public String dosage;

    public checkrecorddata(){
        //default constructor for calls to DataSnapshot
    }

    public checkrecorddata(String time, String comment, String value, String dosage) {
        this.time = time;
        this.value = value;
        this.comment=comment;
        this.dosage=dosage;
    }

    public String getdt() {
        return dt;
    }
    public void setdt(String dt) {
        this.dt = dt;
    }

    public String gettm(){
        return time;
    }
    public void settm(String tm){
        this.time=tm;
    }

    public String getcomment(){
        return comment;
    }
    public void setcomment(String cm){
        this.comment=cm;
    }

    public String getglucoreading(){
        return value;
    }
    public void setglucoreading(String gr){
        this.value=gr;
    }

    public String getothercm(){
        return dosage;
    }
    public void setothercm(String ocm){
        this.dosage=ocm;
    }
}

