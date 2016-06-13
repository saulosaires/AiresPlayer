package com.airesplayer.fragment;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.airesplayer.AiresPlayerApp;
import com.airesplayer.PlayerService;
import com.airesplayer.R;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class QueueFragment extends Fragment  {


    private List<ItemListTwoLines> listEntity;

    LinearLayoutManager linear;

    public static QueueFragment newInstance(List<ItemListTwoLines> listEntity,int centerX, int centerY) {
        QueueFragment fragment = new QueueFragment();

        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);

        fragment.init(listEntity);

        fragment.setArguments(args);
        return fragment;
    }

    private void init(List<ItemListTwoLines> listEntity) {

        this.listEntity=listEntity;

    }

    public QueueFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queue_list, container, false);

        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int cx = getArguments().getInt("cx");
                int cy = getArguments().getInt("cy");

                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int) Math.hypot(right, bottom);

                Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                reveal.setInterpolator(new DecelerateInterpolator(2f));
                reveal.setDuration(700);
                reveal.start();
            }
        });



        return rootView;
    }

    /**
     * Get the animator to unreveal the circle
     *
     * @param cx center x of the circle (or where the view was touched)
     * @param cy center y of the circle (or where the view was touched)
     * @return Animator object that will be used for the animation
     */
    public Animator prepareUnrevealAnimator(float cx, float cy) {
        int radius = getEnclosingCircleRadius(getView(), (int) cx, (int) cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(getView(), (int) cx, (int) cy, radius, 0);
        anim.setInterpolator(new AccelerateInterpolator(2f));
        anim.setDuration(700);
        return anim;
    }

    /**
     * To be really accurate we have to start the circle on the furthest corner of the view
     *
     * @param v  the view to unreveal
     * @param cx center x of the circle
     * @param cy center y of the circle
     * @return the maximum radius
     */
    private int getEnclosingCircleRadius(View v, int cx, int cy) {
        int realCenterX = cx + v.getLeft();
        int realCenterY = cy + v.getTop();
        int distanceTopLeft = (int) Math.hypot(realCenterX - v.getLeft(), realCenterY - v.getTop());
        int distanceTopRight = (int) Math.hypot(v.getRight() - realCenterX, realCenterY - v.getTop());
        int distanceBottomLeft = (int) Math.hypot(realCenterX - v.getLeft(), v.getBottom() - realCenterY);
        int distanceBottomRight = (int) Math.hypot(v.getRight() - realCenterX, v.getBottom() - realCenterY);

        Integer[] distances = new Integer[]{distanceTopLeft, distanceTopRight, distanceBottomLeft,
                distanceBottomRight};

        return Collections.max(Arrays.asList(distances));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initList(view);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_SELECTED);

        getActivity().registerReceiver(new ServiceReceiver(), intentFilter);

    }

    private void initList(View view){

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        LinearLayoutManager linear=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linear);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(new ListAdapter(listEntity));


    }

    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(PlayerService.ACTION_SELECTED)) {

                initList(getView());

            }
        }

    }

    public class ListAdapter extends RecyclerView.Adapter<ViewHolder>  {

        private List<ItemListTwoLines> listEntity;
        private int current=0;

        public ListAdapter(List<ItemListTwoLines> listEntity) {
            super();
            this.listEntity=listEntity;
            AiresPlayerApp app = (AiresPlayerApp) getActivity().getApplication();

            current = app.getService().getCurrentIndex();
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup view, int i) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.row_player, view, false);
            ViewHolder viewHolder = new ViewHolder(v,view.getContext());


            return viewHolder;
        }

        @Override
        public void onBindViewHolder( ViewHolder viewHolder,  final int i) {
            final ItemListTwoLines e = listEntity.get(i);

            if(e.getArtAlbum()!=null && !"".equals(e.getArtAlbum())){


                Picasso.with(getActivity())
                       .load(new File(e.getArtAlbum()))
                       .placeholder(R.drawable.ic_music_note_white_24dp)
                       .into(viewHolder.albumArt);

            }

            viewHolder.title.setText(e.getTitle());
            viewHolder.subtitle.setText(e.getSubTitle());

            if(i==current){
                viewHolder.warper.setBackgroundColor(getResources().getColor(R.color.yellow));
            }

        }


        @Override
        public int getItemCount() {

            if(listEntity!=null)
                return listEntity.size();

            return 0;
        }



        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView albumArt;
            public TextView title;
            public TextView subtitle;
            public ImageView more;
            public View warper;

            Context context;

            public ViewHolder(View itemView,Context context) {
                super(itemView);
                this.context=context;
                albumArt = (ImageView)itemView.findViewById(R.id.album_art);
                more     = (ImageView)itemView.findViewById(R.id.more);
                subtitle = (TextView)itemView.findViewById(R.id.subtitle);
                title    = (TextView)itemView.findViewById(R.id.title);
                warper   = (View) itemView.findViewById(R.id.warper);

            }
        }

    }




