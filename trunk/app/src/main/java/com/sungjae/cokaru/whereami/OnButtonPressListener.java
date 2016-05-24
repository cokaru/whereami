package com.sungjae.cokaru.whereami;

/**
 * Created by sungjaefly on 2016-05-01.
 */
//http://manishkpr.webheavens.com/android-passing-data-between-fragments/
public interface OnButtonPressListener
{
    public void onSetLocation(double latitude,double longtitude,double airPressure);
}
