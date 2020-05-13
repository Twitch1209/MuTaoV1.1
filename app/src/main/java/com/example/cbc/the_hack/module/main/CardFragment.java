package com.example.cbc.the_hack.module.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.cbc.library.base.BaseFragment;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.entity.NewPoem;
import com.example.cbc.the_hack.entity.Poem;
import com.example.cbc.the_hack.module.adapter.CardStackAdapter;
import com.example.cbc.the_hack.module.feed.PublishActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;


public class CardFragment extends BaseFragment implements CardStackListener {

    @BindView(R.id.card_stack_view)
    CardStackView cardStackView;

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private boolean isReceived = false;
    private List<NewPoem> poemList = new ArrayList<>();
    private int cardPos=0;

    public CardFragment(String response) {
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(response).getAsJsonArray();
        if (array != null) {
            for (JsonElement poem : array) {
                poemList.add(new Gson().fromJson(poem, NewPoem.class));
            }
            isReceived = true;
        }
    }

    public static CardFragment newInstance(String response) {
        CardFragment fragment = new CardFragment(response);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_fragment, container, false);
        ButterKnife.bind(this, view);
        manager = new CardStackLayoutManager(this.getContext(), this);
        adapter = new CardStackAdapter(poemList);
        setupCardStackView();

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,
                                HomeFragment.newInstance("home", getContext()))
                        .commit();
            }
        });

        //按下按钮跳转到发布界面，并且复制古诗
        view.findViewById(R.id.social_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                int circular = cardPos % poemList.size();
                NewPoem poem = poemList.get(circular);
                intent.putExtra("poem",poem);
                startActivity(intent);
            }
        });


        return view;
    }

    void setupCardStackView() {
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);

    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {

    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
        cardPos=position;
    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}
