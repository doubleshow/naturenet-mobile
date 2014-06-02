package net.nature.mobile;

import com.nutiteq.MapView;
import com.nutiteq.components.Color;
import com.nutiteq.components.Components;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Range;
import com.nutiteq.geometry.Marker;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.rasterdatasources.HTTPRasterDataSource;
import com.nutiteq.rasterdatasources.PackagedRasterDataSource;
import com.nutiteq.rasterdatasources.RasterDataSource;
import com.nutiteq.rasterlayers.RasterLayer;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.MarkerLayer;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MapFragment extends Fragment {

	private MapView mMapView;
	private MarkerLayer mMarkerLayer;
	private View mButtonCurrentPosition;	
	private Location mCurrentLocation;
	private double mLongitude;
	private double mLatitude;
	private Marker mMarker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_map, container, false);

		mMapView = (MapView)  view.findViewById(R.id.map_view);
		mButtonCurrentPosition = (View)  view.findViewById(R.id.map_button_mylocation);

		// define new configuration holder object
		mMapView.setComponents(new Components());

		// Define base layer. Here we use MapQuest open tiles which are free to use
		// Almost all online maps use EPSG3857 projection.
		// We use online data source for the tiles and the URL is given as template. 
//		RasterDataSource dataSource = new PackagedRasterDataSource(new EPSG3857(), 0, 19, "home_{zoom}_{x}_{y}", getActivity().getApplicationContext());
//		RasterLayer mapLayer = new RasterLayer(dataSource, 16);
		
		RasterDataSource dataSource = new HTTPRasterDataSource(new EPSG3857(), 0, 19, "http://otile1.mqcdn.com/tiles/1.0.0/osm/{zoom}/{x}/{y}.png");
//		MBTilesRasterDataSource dataSource = new MBTilesRasterDataSource(new EPSG3857(), 14, 19, "/sdcard/home.mbtiles", false, this);
		
		

		
		RasterLayer mapLayer = new RasterLayer(dataSource, 0);
		mMapView.getLayers().setBaseLayer(mapLayer);

		// do not allow to rotate map with two-finger touch. There is no rotation constraints by degrees, just on/off
		mMapView.getConstraints().setRotatable(false);

		// Set allowed zoom range and bounding box. 
		// Bounding box must be in layer projection units, so conversion is needed.
		mMapView.getConstraints().setZoomRange(new Range(19, 19));

		mMapView.setMapRotation(0f);
		// zoom - 0 = world, like on most web maps
		mMapView.setZoom(19.0f);
				
		mMarkerLayer = new MarkerLayer(mapLayer.getProjection());
		mMapView.getLayers().addLayer(mMarkerLayer);

		mButtonCurrentPosition.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(mLongitude, mLatitude));					
			}        	
		});		
		return view;		
	}
	
	public void setHomeButtonEnabled(boolean enabled){
		if (!enabled){
			mButtonCurrentPosition.setVisibility(View.INVISIBLE);
		}
	}


	@Override
	public void onStop() {
		//Stop the map - mandatory to avoid problems with app restart
		mMapView.stopMapping();
		super.onStop();
	}


	@Override
	public void onStart() {
		super.onStart();
		//Start the map - mandatory
		mMapView.startMapping();
	}

	public void setCurrentLocation(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		if (mCurrentLocation == null){
			// if it's the first time acquiring a location, focus the map to the location
			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));			
		}
		mCurrentLocation = location;
		setCurrentLocation(location.getLatitude(), location.getLongitude(), false);
	}
	
	public void setCurrentLocationCameraMarker(double latitude, double longitude, boolean focus) {	
		//MapPos markerLocation = mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude);
		MapPos markerLocation = mMarkerLayer.getProjection().fromWgs84(longitude, latitude);
		mLatitude = latitude;
		mLongitude = longitude;
		if (mMarker == null){
			// define marker style (image, size, color)
			Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.ic_camera);
			MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(0.5f).setColor(Color.WHITE).build();
			// define label what is shown when you click on marker
			Label markerLabel = new DefaultLabel("Here");//, "Here is a marker");
			//
			mMarker = new Marker(markerLocation, markerLabel, markerStyle, mMarkerLayer);
			mMarkerLayer.add(mMarker);
		}

		if (focus){
			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));
		}

		mMarker.setMapPos(markerLocation);		
		mMapView.invalidate();
	}

	public void setCurrentLocation(double latitude, double longitude, boolean focus) {	
		//MapPos markerLocation = mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude);
		MapPos markerLocation = mMarkerLayer.getProjection().fromWgs84(longitude, latitude);
		mLatitude = latitude;
		mLongitude = longitude;
		if (mMarker == null){
			// define marker style (image, size, color)
			Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.cur_position);
			MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(1.0f).setColor(Color.WHITE).build();
			// define label what is shown when you click on marker
			Label markerLabel = new DefaultLabel("Here");//, "Here is a marker");
			//
			mMarker = new Marker(markerLocation, markerLabel, markerStyle, mMarkerLayer);
			mMarkerLayer.add(mMarker);
		}

		if (focus){
			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));
		}

		mMarker.setMapPos(markerLocation);		
		mMapView.invalidate();
	}
}
