package com.VideoCalling.sample.groupchatwebrtc.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.VideoCalling.sample.groupchatwebrtc.R;

import java.util.ArrayList;

/**
 * Created by Harishma Velagala on 01-01-2017.
 */
public class DownloadDocs  extends Fragment {
    RecyclerView recycler_view;
    ArrayList uploaded_doc_names=new ArrayList();
    ArrayList uploaded_doc_dates=new ArrayList();
    ArrayList uploaded_doc_sender=new ArrayList();
    VideocallAdapter videoCallAdapter;
    Typeface typeface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.docs_custom_tabs_layout, container, false);
        recycler_view= (RecyclerView) view.findViewById(R.id.recycler_view);
        uploaded_doc_sender.add("You");
        uploaded_doc_sender.add("You");
        uploaded_doc_sender.add("You");
        uploaded_doc_names.add("Document1");
        uploaded_doc_names.add("Document2");
        uploaded_doc_names.add("Document3");

        uploaded_doc_dates.add("12-Dec-2016");
        uploaded_doc_dates.add("13-Dec-2016");
        uploaded_doc_dates.add("14-Dec-2016");
        typeface= Typeface.createFromAsset(getActivity().getAssets(), "QuicksandRegular.ttf");
        videoCallAdapter = new VideocallAdapter(getActivity(), uploaded_doc_sender,uploaded_doc_names,uploaded_doc_dates);
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(recylerViewLayoutManager);
        recycler_view.setHasFixedSize(true);
        recycler_view.setAdapter(videoCallAdapter);
        return  view;
    }
    class VideocallAdapter extends RecyclerView.Adapter<VideocallAdapter.ViewHolder>{

        ArrayList callerName,videoName,callDate;
        Context context;
        View view1;
        ImageView contactImage;
        ViewHolder viewHolder1;
        TextView senderName,msg,sentDate;

        public VideocallAdapter(Context context1,ArrayList callerName,ArrayList videoName,ArrayList callDate){

            this.callerName=callerName;
            this.videoName=videoName;
            this.callDate=callDate;
            context = context1;
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public  TextView callerName,videoName,callDate;
            public ImageView contactImage;
            public ViewHolder(View v){

                super(v);
                contactImage= (ImageView) v.findViewById(R.id.contactImage);
                callerName = (TextView)v.findViewById(R.id.callerName);
                videoName = (TextView)v.findViewById(R.id.videoName);
                callDate = (TextView)v.findViewById(R.id.callDate);
            }
        }

        @Override
        public VideocallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            view1 = LayoutInflater.from(context).inflate(R.layout.videos_custom_layout,parent,false);

            viewHolder1 = new ViewHolder(view1);

            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position){

            holder.contactImage.setImageResource(R.drawable.docs);
            holder.callerName.setText(callerName.get(position).toString());
            holder.videoName.setText(videoName.get(position).toString());
            holder.callDate.setText(callDate.get(position).toString());
            holder.callerName.setTypeface(typeface);
            holder.videoName.setTypeface(typeface);
            holder.callDate.setTypeface(typeface);

        }

        @Override
        public int getItemCount(){

            return videoName.size();
        }
    }
}