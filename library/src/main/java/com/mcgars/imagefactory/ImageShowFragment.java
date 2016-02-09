package com.mcgars.imagefactory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcgars.imagefactory.objects.IThumb;
import com.mcgars.imagefactory.objects.Thumb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 19.08.2015.
 */
public class ImageShowFragment extends Fragment {
    private ViewPager viewPager;
    public static final String POSITION = "position";
    public static final String LIST_STRING = "list_string";
    public static final String IS_THUMB = "is_thumb";
    int position;
    private List<IThumb> listValues = new ArrayList<>();

    public static Fragment newInstance(Bundle extras) {
        return newInstance(extras, true);
    }

    public static ImageShowFragment newInstance(Bundle extras, boolean isThumb) {
        ImageShowFragment frag = new ImageShowFragment();
        extras.putBoolean(IS_THUMB, isThumb);
        frag.setArguments(extras);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_imagefactory_image, null);
        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParans();
    }

    protected void initParans() {
        Bundle extra = getArguments();
        if(extra!=null) {
            position = extra.getInt(POSITION);
            convertData(extra.getStringArrayList(LIST_STRING));
        }
    }

    protected void convertData(List<String> stringArrayList) {
        if(stringArrayList!=null) {
            for (String url : stringArrayList) {
                listValues.add(new Thumb(url,url));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PagerImageController controller = new PagerImageController(getActivity(), viewPager);
        controller.setList(position, listValues, getArguments().getBoolean(IS_THUMB, true));
    }

}
