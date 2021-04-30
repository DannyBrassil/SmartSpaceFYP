package com.example.smartspace;

import com.google.android.gms.maps.model.LatLng;

public interface SortingStrategy {
     void sort(final SortingCallback myCallback);
     void sort(final LatLng latLng, final String id, final SortingCallback myCallback);
}
