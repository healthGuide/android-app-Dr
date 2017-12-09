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

    public String gettime(){
        return time;
    }
    public void settime(String time){
        this.time=time;
    }

    public String getcomment(){
        return comment;
    }
    public void setcomment(String comment){
        this.comment=comment;
    }

    public String getvalue(){
        return value;
    }
    public void setvalue(String value){this.value=value;}

    public String getdosage(){
        return dosage;
    }
    public void setdosage(String dosage){
        this.dosage=dosage;
    }
}

