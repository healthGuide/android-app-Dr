package rkapoors.healthguide_dr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by KAPOOR's on 11-11-2017.
 */

public class recycleadapter extends RecyclerView.Adapter<recycleadapter.MyHoder>{

    List<checkrecorddata> list;
    Context context;

    public recycleadapter(List<checkrecorddata> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycle_items,parent,false);
        MyHoder myHoder = new MyHoder(view);


        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        checkrecorddata mylist = list.get(position);
        holder.dt.setText(mylist.getdt());
        holder.tm.setText(mylist.gettime());
        holder.comm.setText(mylist.getcomment());
        holder.gr.setText(mylist.getvalue());
        holder.dsg.setText(mylist.getdosage());
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;
            }
            else{

                arr=list.size();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return arr;

    }

    class MyHoder extends RecyclerView.ViewHolder{
        TextView dt,tm,comm,gr,dsg;


        public MyHoder(View itemView) {
            super(itemView);
            dt = (TextView) itemView.findViewById(R.id.dttv);
            tm = (TextView) itemView.findViewById(R.id.tmtv);
            comm = (TextView) itemView.findViewById(R.id.cmtv);
            gr = (TextView) itemView.findViewById(R.id.grtv);
            dsg = (TextView) itemView.findViewById(R.id.othercmtv);
        }
    }

}