package com.airesplayer.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.airesplayer.R;

import com.airesplayer.lastFmApi.LastFmJsonUtil;
import com.airesplayer.lastFmApi.LastFmService;
import com.airesplayer.model.Image;
import com.airesplayer.model.ItemMedia;
import com.airesplayer.persistence.ImageDAO;
import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MusicFragment extends Fragment {


    private List<ItemMedia> listMusic;

    public static MusicFragment newInstance(List<ItemMedia> listEntity) {
        MusicFragment fragment = new MusicFragment();


        fragment.init(listEntity);
        return fragment;
    }

    private void init(List<ItemMedia> listMusic) {

        this.listMusic = listMusic;

    }

    public MusicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList(view);
    }

    private void initList(View view) {

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(new ListAdapter(listMusic));

    }

    private void reproduceNext(int position) {


        AiresPlayerApp app = (AiresPlayerApp) getActivity().getApplication();

        app.reproduceNext(position);
    }


    private void editTag(int position) {

        int media = listMusic.get(position).getId();

        Intent intent = new Intent(getActivity(), EditTagActivity.class);

        Bundle b = new Bundle();

        b.putInt("id", media);

        intent.putExtras(b);

        startActivity(intent);

    }

    private void deleteMedia(final int position) {

        final int media = listMusic.get(position).getId();
        String title = listMusic.get(position).getTitle();
        String msg = getResources().getString(R.string.delete) + ": " + title;


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AudioUtils.deleteMedia(getActivity(), media);
                        listMusic.remove(position);
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

    public class ListAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<ItemMedia> listEntity;
        private ImageDAO dao = new ImageDAO();

        public ListAdapter(List<ItemMedia> listEntity) {
            super();
            this.listEntity = listEntity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup view, int i) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.row, view, false);
            ViewHolder viewHolder = new ViewHolder(v, view.getContext());


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            final ItemMedia e = listEntity.get(i);

            loadImage(viewHolder.albumArt, e.getId(), e.getSubTitle());

            viewHolder.title.setText(e.getTitle());
            viewHolder.subtitle.setText(e.getSubTitle());

            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v, i);
                }
            });

            viewHolder.subtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    handleClick(v, i);

                }
            });

            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v, i);
                }
            });

            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = Utils.getPopUpMenu(getActivity(), v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.reproduce_next:
                                    reproduceNext(i);
                                    break;
                                case R.id.edit_tag:
                                    editTag(i);
                                    break;
                                case R.id.delete:
                                    deleteMedia(i);
                                    break;

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

            if (listEntity != null)
                return listEntity.size();

            return 0;
        }


        public void handleClick(View v, int index) {

            ((AiresPlayerApp) getActivity().getApplication()).doInit(index, Media.MUSIC.getTypeMedia(), true);

        }



    private void loadImage(final ImageView albumArt, final int id, final String name) {


        Image img = dao.read(id);

        if(img==null || Utils.isEmpty(img.getImageUrl())){
            loadFromLastFm(albumArt, id, name);
        } else {
            picassoLoadImage(albumArt, img.getImageUrl());
        }


    }

    private void loadFromLastFm(final ImageView albumArt, final int id, final String name) {

        LastFmService.CallBack callBack = new LastFmService.CallBack() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    String imageUrl = LastFmJsonUtil.parseSearchArtist(response);

                    if (!Utils.isEmpty(imageUrl)) {
                        dao.persist(new Image(id, imageUrl));

                        picassoLoadImage(albumArt, imageUrl);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.print(error);

            }

        };


        LastFmService.searchArtist(name, callBack);

    }

    private void picassoLoadImage(final ImageView albumArt, String uri) {

        albumArt.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.with(getActivity())
                .load(uri)
                .resize(200, 150)
                .into(albumArt);

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




