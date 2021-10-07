package com.example.refresh_selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class FragmentMap extends Fragment implements MapView.MapViewEventListener {
    private String MarkerName;//마커 장소이름
    private double Latitude;//위도
    private double Longitude;//경도
    private int Tag;//마커 태그?
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_map, container, false);
        MapView mapView = new MapView(getActivity());

        //지도를 현재위치에 있는 장소로 띄어줌
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        mapView.addPOIItem(setMarker(MarkerName,Latitude,Longitude,Tag));//서버에서 가져온 위도,경도,마커이름 //#태그번호는 임의로 지정?
//        setMarker(mapView);

        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);//xml에 지도넣기
        mapViewContainer.addView(mapView); 
        return v;
    }

    private MapPOIItem setMarker(String markerName, double latitude, double longitude, int tag) {
        //마커객체 생성
        MapPOIItem marker = new MapPOIItem();

        //맵 포인트 위도경도 설정 나중에 서버에서 제공하는 위도 ,경도로 설정
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude,longitude);//36.369015338611675, 127.34685766817087 충남대학교 좌표
        marker.setItemName(markerName);//마커이름
        marker.setTag(tag);//마커 태그?
        marker.setMapPoint(mapPoint);//마커를 위치에 표시
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        return marker;
        //mapView.addPOIItem(marker);//마커 표시
    }

    //MapView.MapViewEventListener 구현
    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint){

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}