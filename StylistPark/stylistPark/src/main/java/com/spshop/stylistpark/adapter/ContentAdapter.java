package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import java.util.List;

public class ContentAdapter extends ArrayAdapter<String> implements SectionIndexer {

    private List<String> mItem;

    public ContentAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);

        createSections();
    }

    private String[] mSections = null; // "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

    @Override
    public Object[] getSections() {
        return mSections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        char ch;
        for (int pos = 0; pos < getCount(); pos++) {
            ch = Character.toUpperCase(getItem(pos).charAt(0));
            if (ch == mSections[sectionIndex].charAt(0))
                return pos;
        }

        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        String item = getItem(position);
        char ch = item.charAt(0);

        for (int i = 0; i < mSections.length; i++)
            if (Character.toUpperCase(ch) == mSections[i].charAt(0))
                return i;

        return 0;
    }


    private void createSections() {

        mSections = new String[27];
        int index = 0;
        for (char i = 'A'; i <= 'Z'; i++)
            mSections[index++] = String.valueOf(i);
        mSections[26] = "#";

    }

}
