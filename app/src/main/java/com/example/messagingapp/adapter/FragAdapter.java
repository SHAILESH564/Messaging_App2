package com.example.messagingapp.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.messagingapp.fragments.StatusFrag;
import com.example.messagingapp.fragments.chatfrag;

public class FragAdapter extends FragmentPagerAdapter {
    public FragAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return new chatfrag();
            case 1: return new StatusFrag();
            default:return new chatfrag();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title =null;
        if(position==0){
            title="CHATS";
        }
        if(position==1){
            title="STATUS";
        }

        return title;
    }
}
