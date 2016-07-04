package com.airesplayer.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.airesplayer.AiresPlayerApp;
import com.airesplayer.EditTagActivity;
import com.airesplayer.Media;
import com.airesplayer.PlayerService;
import com.airesplayer.R;

import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class MusicFragment extends Fragment {


    private List<ItemListTwoLines> listEntity;


    public static MusicFragment newInstance(List<ItemListTwoLines> listEntity) {
        MusicFragment fragment = new MusicFragment();


        fragment.init(listEntity);
        return fragment;
    }

    private void init(List<ItemListTwoLines> listEntity) {

        this.listEntity=listEntity;

    }

    public MusicFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList(view);
    }

    private void initList(View view){

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(new ListAdapter(listEntity));

    }

    private void reproduceNext(int position){


        AiresPlayerApp app = (AiresPlayerApp) getActivity().getApplication();

        app.getService().reproduceNext(listEntity.get(position));
    }

    private void addQueue(int position){


        AiresPlayerApp app = (AiresPlayerApp) getActivity().getApplication();

        app.getService().addToQueue(listEntity.get(position));
    }

    private void editTag(int position){

        int media = listEntity.get(position).getId();

        Intent intent = new Intent(getActivity(), EditTagActivity.class);

        Bundle b = new Bundle();

        b.putInt("id",media);

        intent.putExtras(b);

        startActivity(intent);

    }

    private void deleteMedia(final int position){

        final  int media = listEntity.get(position).getId();
        String title=listEntity.get(position).getTitle();
        String msg=getResources().getString(R.string.delete)+": "+title;


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AudioUtils.deleteMedia(getActivity(),media);
                        listEntity.remove(position);
                        initList(getView());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

         builder.create().show();

    }

    public class ListAdapter extends RecyclerView.Adapter<ViewHolder>  {

        private List<ItemListTwoLines> listEntity;

        public ListAdapter(List<ItemListTwoLines> listEntity) {
            super();
            this.listEntity=listEntity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup view, int i) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.row, view, false);
            ViewHolder viewHolder = new ViewHolder(v,view.getContext());


            return viewHolder;
        }

        @Override
        public void onBindViewHolder( ViewHolder viewHolder,  final int i) {
            final ItemListTwoLines e = listEntity.get(i);

            if(e.getArtAlbum()!=null && !"".equals(e.getArtAlbum())){

                viewHolder.albumArt.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.with(getActivity())
                       .load((e.getArtAlbum()))
                       .placeholder(R.drawable.ic_music_note_white_24dp)
                       .into(viewHolder.albumArt);

            }

            viewHolder.title.setText(e.getTitle());
            viewHolder.subtitle.setText(e.getSubTitle());

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

            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v,i);
                }
            });

            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = Utils.getPopUpMenu(getActivity(), v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch(item.getItemId()){

                                case R.id.reproduce_next:reproduceNext(i);break;
                                case R.id.add_queue:     addQueue(i);break;
                                case R.id.add_playlist:break;
                                case R.id.edit_tag:      editTag(i);break;
                                case R.id.delete:        deleteMedia(i);break;

                            }



                            return false;
                        }
                    });

                    popup.show();

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


            ((AiresPlayerApp)getActivity().getApplication()).getService().play(index, Media.MUSIC.getTypeMedia());


            }

        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView albumArt;
            public TextView title;
            public TextView subtitle;
            public ImageView more;
            public CardView card;
            Context context;

            public ViewHolder(View itemView,Context context) {
                super(itemView);
                this.context=context;
                albumArt = (ImageView)itemView.findViewById(R.id.album_art);
                more     = (ImageView)itemView.findViewById(R.id.more);
                subtitle = (TextView)itemView.findViewById(R.id.subtitle);
                title    = (TextView)itemView.findViewById(R.id.title);
                card     = (CardView)itemView.findViewById(R.id.card_view);

            }
        }

    }




