package com.mei.wood.rx;


import androidx.fragment.app.FragmentActivity;

import com.mei.base.network.holder.SpiceLoadingHolder;


/**
 * Created by guoyufeng on 18/9/15.
 */
public interface MeiUiFrame extends SpiceLoadingHolder {

    FragmentActivity getActivity();
}
