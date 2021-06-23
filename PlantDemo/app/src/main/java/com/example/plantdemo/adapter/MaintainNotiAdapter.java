package com.example.plantdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantdemo.R;
import com.example.plantdemo.entity.Notify;

import org.litepal.LitePal;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by MXL on 2020/3/28
 * <br>类描述：<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class MaintainNotiAdapter extends RecyclerView.Adapter<MaintainNotiAdapter.MaintainNotiHolder> {

    List<Notify> notifis;
    Context mContext;
    public MaintainNotiAdapter(List<Notify> notifis, Context context){
        this.notifis=notifis;
        mContext=context;
    }
    @NonNull
    @Override
    public MaintainNotiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final MaintainNotiHolder holder=new MaintainNotiHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.notifi_item,parent,false));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.deleteAll(Notify.class);
                LitePal.deleteAll(Notify.class,"time=?",notifis.get(holder.getAdapterPosition()).getTime());
                reomveData(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaintainNotiHolder holder, int position) {
        holder.imageView.setImageResource(notifis.get(position).getImage_res());
        holder.textView.setText(notifis.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return notifis.size();
    }

    class MaintainNotiHolder  extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        Button stick;
        Button mark;
        Button delete;
        public MaintainNotiHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ima_maintain_notifi);
            textView=itemView.findViewById(R.id.tv_maintain_notifi);
            stick=itemView.findViewById(R.id.btn_main_notifi_stick);
            mark=itemView.findViewById(R.id.btn_main_notifi_mark);
            delete=itemView.findViewById(R.id.btn_main_notifi_delete);
        }
    }
    interface NotifyItemListener{
        void OnDelete();
    }
    /*
     * 适配器加入一条数据
     */
   public void  addData(Notify notifi){
        notifis.add(notifi);
        notifyItemChanged(notifis.size()-1);
    }
    /**
     * 从适配器删除一条数据
     */
    public void reomveData(int position){
        notifis.remove(position);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

}
