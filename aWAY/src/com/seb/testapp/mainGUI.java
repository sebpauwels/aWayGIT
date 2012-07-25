/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seb.testapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import java.io.IOException;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author spauwels1
 */
public class mainGUI extends MapActivity implements LocationListener
{

    private MapView mapView;
    private MapController mc;
    private MyLocationOverlay myLocation = null;
    private LocationManager lm;
    private double lat = 0;
    private double lng = 0;
    private GeoPoint p;
    private GeoPoint pClicked;
    private Double[] positions;
    private Projection projection;
    private List<Overlay> mapOverlays;
    private RelativeLayout menuNewPoint;
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Boolean targeting = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main_gui);
        Log.d("aWAY_DEBUG", "entering geoloc !");

        mapView = (MapView) this.findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mc = mapView.getController();
        mc.setZoom(17);

        menuNewPoint = (RelativeLayout) findViewById(R.id.menuNewPoint);
        menuNewPoint.setVisibility(RelativeLayout.GONE);

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);


        myLocation = new MyLocationOverlay(getApplicationContext(), mapView);
        mapView.getOverlays().add(myLocation);
        myLocation.enableMyLocation();

        myLocation.enableCompass();

        myLocation = new MyLocationOverlay(getApplicationContext(), mapView);
        myLocation.runOnFirstFix(new Runnable()
        {

            public void run()
            {
                mc.animateTo(myLocation.getMyLocation());
                mc.setZoom(17);
            }
        });

        mapOverlays = mapView.getOverlays();
        projection = mapView.getProjection();
        mapOverlays.add(new MyOverlay(positions));

        final Button btMapSat = (Button) findViewById(R.id.bt_mapSat);

        btMapSat.setOnClickListener(new Button.OnClickListener()
        {

            public void onClick(View v)
            {
                if (mapView.isSatellite())
                {
                    mapView.setSatellite(false);
                    btMapSat.setText("Satellite    ");
                }
                else
                {
                    mapView.setSatellite(true);
                    btMapSat.setText("Map    ");
                }

            }
        });
        
        final Button btTarget = (Button) findViewById(R.id.bt_Div);

        btTarget.setOnClickListener(new Button.OnClickListener()
        {

            public void onClick(View v)
            {
                targeting = !targeting;
                if (targeting)
                {
                    btTarget.setBackgroundResource(R.drawable.target2);
                }
                else
                {
                    btTarget.setBackgroundResource(R.drawable.target);
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location)
    {
        lat = location.getLatitude();
        lng = location.getLongitude();

        p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
        mc.animateTo(p);
        mc.setCenter(p);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider)
    {
// TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
    }

    class MyOverlay extends Overlay
    {

        public MyOverlay(Double[] positions)
        {
        }

        public void draw(Canvas canvas, MapView mapv, boolean shadow)
        {
            super.draw(canvas, mapView, shadow);

            //---translate the GeoPoint to screen pixels---
            try
            {
                Point screenPts = new Point();
                mapView.getProjection().toPixels(pClicked, screenPts);

                //---add the marker---
                Bitmap bmp = BitmapFactory.decodeResource(
                        getResources(), R.drawable.pin);
                canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
            }
            catch (Exception e)
            {
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView)
        {
            //---when  user lifts his finger---
            if (event.getAction() == 1 && targeting)
            {
                menuNewPoint.setVisibility(RelativeLayout.VISIBLE);
                pClicked = mapView.getProjection().fromPixels(
                        (int) event.getX(),
                        (int) event.getY());
                Log.i("aWAY_DEBUG",
                        pClicked.getLatitudeE6() / 1E6 + ","
                        + pClicked.getLongitudeE6() / 1E6);

                Geocoder geoCoder = new Geocoder(
                        getBaseContext(), Locale.getDefault());
                try
                {
                    Log.i("aWAY_DEBUG", "retriving address...");
                    List<Address> addresses = geoCoder.getFromLocation(
                            pClicked.getLatitudeE6() / 1E6,
                            pClicked.getLongitudeE6() / 1E6, 1);

                    String add = "";
                    if (addresses.size() > 0)
                    {
                        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();
                                i++)
                        {
                            add += addresses.get(0).getAddressLine(i) + "\n";
                        }
                    }
                    Log.i("aWAY_DEBUG", add);
                    TextView textNewPoint = (TextView) findViewById(R.id.testAdress);
                    textNewPoint.setText(add);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                return true;
            }
            else
            {
                menuNewPoint.setVisibility(RelativeLayout.GONE);
                return false;
            }
        }
    }
}
