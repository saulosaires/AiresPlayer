package com.airesplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airesplayer.AiresPlayerApp;
import com.airesplayer.Media;
import com.airesplayer.R;

import com.airesplayer.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Random;


public class AlbumFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<ItemListTwoLines> listEntity;

    public static AlbumFragment newInstance(List<ItemListTwoLines> listEntity) {

        AlbumFragment fragment = new AlbumFragment();
        fragment.init(listEntity);

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    public AlbumFragment() {}

    public void init(List<ItemListTwoLines> listEntity) {
        this.listEntity=listEntity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(getGridLayoutManager());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(new AlbumAdapter(listEntity));

    }

    private GridLayoutManager getGridLayoutManager(){

        GridLayoutManager manager=null;
        int collumPortrait=2;
        int collumLandScape=4;

        if(Utils.isScreenLarge(getContext())){
            collumPortrait=4;
            collumLandScape=6;
        }

        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){

            manager = new GridLayoutManager(getActivity(), collumPortrait);

        }else if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){

            manager = new GridLayoutManager(getActivity(), collumLandScape);

        }

        return manager;
    }

    public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>  {

        private List<ItemListTwoLines> listEntity;

        public AlbumAdapter(List<ItemListTwoLines> listEntity) {
            super();
            this.listEntity=listEntity;

        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup view, int i) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.card, view, false);
            ViewHolder viewHolder = new ViewHolder(v,view.getContext());


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            final ItemListTwoLines e = listEntity.get(i);

            if(e.getArtAlbum()!=null && !"".equals(e.getArtAlbum())){



                Picasso.with(getActivity())
                        .load(new File(e.getArtAlbum()))
                        .placeholder(R.drawable.ic_music_note_white_48dp)
                        .into(viewHolder.albumArt,
                                new com.squareup.picasso.Callback() {

                                    @Override
                                    public void onSuccess() {

                                       viewHolder.albumArt.setScaleType(ImageView.ScaleType.FIT_XY);

                                       Bitmap bitmap =  ((BitmapDrawable)viewHolder.albumArt.getDrawable()).getBitmap();

                                        Palette palette  = Palette.from(bitmap).generate();
                                        Palette.Swatch swatch = palette.getVibrantSwatch();

                                        if (swatch != null) {
                                            viewHolder.warpper.setBackgroundColor(swatch.getRgb());

                                            //viewHolder.title.setBackgroundColor(swatch.getRgb());
                                            viewHolder.title.setTextColor(swatch.getTitleTextColor());

                                            //viewHolder.subtitle.setBackgroundColor(swatch.getRgb());
                                            viewHolder.subtitle.setTextColor(swatch.getTitleTextColor());

                                        }

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
            }


            viewHolder.title.setText(e.getTitle());
            viewHolder.subtitle.setText(e.getSubTitle());

            viewHolder.albumArt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v,i);
                }
            });


            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v,i);
                }
            });

            viewHolder.subtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    handleClick(v,i);

                }
            });

            viewHolder.warpper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v,i);
                }
            });





        }


        @Override
        public int getItemCount() {

            if(listEntity!=null)
                return listEntity.size();

            return 0;
        }

        public void handleClick(View v ,int index) {


            ((AiresPlayerApp)getActivity().getApplication()).getService().play(index, Media.ALBUM.getTypeMedia());


        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView albumArt;
            public TextView title;
            public TextView subtitle;
            public LinearLayout warpper;
            Context context;

            public ViewHolder(View itemView,Context context) {
                super(itemView);
                this.context=context;
                albumArt = (ImageView)itemView.findViewById(R.id.album_art);
                subtitle = (TextView)itemView.findViewById(R.id.subtitle);
                title    = (TextView)itemView.findViewById(R.id.title);
                warpper  = (LinearLayout)itemView.findViewById(R.id.container);

            }
        }

    }


}
