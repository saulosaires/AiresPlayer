package com.airesplayer.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
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

import com.airesplayer.lastFmApi.LastFmJsonUtil;
import com.airesplayer.lastFmApi.LastFmService;

import com.airesplayer.model.Image;
import com.airesplayer.model.ItemMedia;
import com.airesplayer.persistence.ImageDAO;
import com.airesplayer.util.Utils;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ArtistFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<ItemMedia> listEntity;


    public static ArtistFragment newInstance(List<ItemMedia> listEntity) {


        ArtistFragment fragment = new ArtistFragment();
        fragment.init(listEntity);

        Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    public ArtistFragment() {
    }

    public void init(List<ItemMedia> listEntity) {
        this.listEntity = listEntity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        mRecyclerView.setLayoutManager(getGridLayoutManager());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setAdapter(new ArtistAdapter(listEntity));


    }

    private GridLayoutManager getGridLayoutManager() {

        GridLayoutManager manager = null;
        int collumPortrait = 2;
        int collumLandScape = 4;

        if (Utils.isScreenLarge(getContext())) {
            collumPortrait = 4;
            collumLandScape = 6;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            manager = new GridLayoutManager(getActivity(), collumPortrait);

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            manager = new GridLayoutManager(getActivity(), collumLandScape);

        }

        return manager;
    }

    public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

        private List<ItemMedia> listEntity;
        private ImageDAO dao = new ImageDAO();

        public ArtistAdapter(List<ItemMedia> listEntity) {
            super();
            this.listEntity = listEntity;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup view, int i) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.card, view, false);
            ViewHolder viewHolder = new ViewHolder(v, view.getContext());

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            final ItemMedia e = listEntity.get(i);


            loadImage(viewHolder.albumArt,
                    viewHolder.title,
                    viewHolder.subtitle,
                    viewHolder.warpper,
                    e.getId(),
                    e.getTitle());


            viewHolder.title.setText(e.getTitle());
            viewHolder.subtitle.setText(e.getSubTitle());

            viewHolder.albumArt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v, i);
                }
            });


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

            viewHolder.warpper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick(v, i);
                }
            });


        }

        public void handleClick(View v, int index) {

            ((AiresPlayerApp) getActivity().getApplication()).doInit(index, Media.ARTIST.getTypeMedia(), true);


        }

        @Override
        public int getItemCount() {

            if (listEntity != null)
                return listEntity.size();

            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView albumArt;
            public TextView title;
            public TextView subtitle;
            public LinearLayout warpper;
            Context context;

            public ViewHolder(View itemView, Context context) {
                super(itemView);
                this.context = context;
                albumArt = (ImageView) itemView.findViewById(R.id.album_art);
                subtitle = (TextView) itemView.findViewById(R.id.subtitle);
                title = (TextView) itemView.findViewById(R.id.title);
                warpper = (LinearLayout) itemView.findViewById(R.id.container);

            }
        }


        private void loadImage(final ImageView albumArt,
                               final TextView title,
                               final TextView subtitle,
                               final LinearLayout warpper,
                               final int id,
                               final String artist) {

            Image img = dao.read(id);

            if(img==null || Utils.isEmpty(img.getImageUrl())){
                loadFromLastFm(albumArt, title, subtitle, warpper, id, artist);
            } else {
                picassoLoadImage(albumArt, title, subtitle, warpper, img.getImageUrl());
            }

        }

        private void loadFromLastFm(final ImageView albumArt,
                                    final TextView title,
                                    final TextView subtitle,
                                    final LinearLayout warpper,
                                    final int id,
                                    final String name) {

            LastFmService.CallBack callBack = new LastFmService.CallBack() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        String imageUrl = LastFmJsonUtil.parseSearchArtist(response);

                        if (!Utils.isEmpty(imageUrl)) {

                            dao.persist(new Image(id, imageUrl));
                            picassoLoadImage(albumArt, title, subtitle, warpper, imageUrl);

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

        private void picassoLoadImage(final ImageView albumArt,
                                      final TextView title,
                                      final TextView subtitle,
                                      final LinearLayout warpper,
                                      String uri) {

            albumArt.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso.with(getActivity())
                    .load(uri)
                    .resize(200, 150)
                    .into(albumArt,
                            new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {


                                    Bitmap bitmap = ((BitmapDrawable) albumArt.getDrawable()).getBitmap();

                                    Palette palette = Palette.from(bitmap).generate();
                                    Palette.Swatch swatch = palette.getVibrantSwatch();

                                    if (swatch != null) {
                                        warpper.setBackgroundColor(swatch.getRgb());

                                        //viewHolder.title.setBackgroundColor(swatch.getRgb());
                                        title.setTextColor(swatch.getTitleTextColor());

                                        //viewHolder.subtitle.setBackgroundColor(swatch.getRgb());
                                        subtitle.setTextColor(swatch.getTitleTextColor());

                                    }


                                }

                                @Override
                                public void onError() {

                                }
                            });

        }

    }
}
