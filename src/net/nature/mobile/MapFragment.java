package net.nature.mobile;

import net.nature.mobile.model.Site;

import com.nutiteq.MapView;
import com.nutiteq.components.Bounds;
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
import com.nutiteq.style.LabelStyle;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.MarkerLayer;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MapFragment extends Fragment {

	private MapView mMapView;
	private MarkerLayer mMarkerLayer;
	private View mButtonCurrentPosition;	
	private Location mCurrentLocation;
	private double mLongitude;
	private double mLatitude;
	private Marker mMarker;
	private Bounds mBounds;
	


	public void setSite(Site site){
		if (site == null)
			return;

		RasterDataSource dataSource;
		Range zoomRange;
		if (site.getName().equalsIgnoreCase("aces")){
			dataSource = new PackagedRasterDataSource(new EPSG3857(), 19, 19, "aces_{zoom}_{x}_{y}", getActivity().getApplicationContext());
			mBounds = new Bounds(-106.8226, 39.197216, -106.820819, 39.194879);
			zoomRange = new Range(19, 19);
		}else if (site.getName().equalsIgnoreCase("umd")){			
			dataSource = new HTTPRasterDataSource(new EPSG3857(), 0, 19, "http://otile1.mqcdn.com/tiles/1.0.0/osm/{zoom}/{x}/{y}.png");
			mBounds = new Bounds(-76.956139, 38.998942, -76.933308, 38.977927);
			zoomRange = new Range(16, 19);
		}else if (site.getName().equalsIgnoreCase("cu")){			
			dataSource = new HTTPRasterDataSource(new EPSG3857(), 0, 19, "http://otile1.mqcdn.com/tiles/1.0.0/osm/{zoom}/{x}/{y}.png");
			mBounds = new Bounds(-105.277197, 40.015044, -105.259237, 40.000515);
			zoomRange = new Range(16, 19);		
		}else if (site.getName().equalsIgnoreCase("uncc")){			
			dataSource = new HTTPRasterDataSource(new EPSG3857(), 0, 19, "http://otile1.mqcdn.com/tiles/1.0.0/osm/{zoom}/{x}/{y}.png");
			mBounds = new Bounds(-80.743911, 35.315615, -80.723097, 35.29926);
			zoomRange = new Range(16, 19);
		}else {

			dataSource = new HTTPRasterDataSource(new EPSG3857(), 0, 19, "http://otile1.mqcdn.com/tiles/1.0.0/osm/{zoom}/{x}/{y}.png");
			mBounds = null;
			zoomRange = new Range(16, 19);			
		}
		
		RasterLayer mapLayer = new RasterLayer(dataSource, 0);
		mMapView.getLayers().setBaseLayer(mapLayer);
		mMapView.getConstraints().setZoomRange(zoomRange);
		mMapView.getConstraints().setRotatable(false);
		
		if (mBounds != null){
			MapPos topLeft = mMapView.getLayers().getBaseProjection().fromWgs84(mBounds.left, mBounds.top);
			MapPos bottomRight = mMapView.getLayers().getBaseProjection().fromWgs84(mBounds.right, mBounds.bottom);
			mMapView.getConstraints().setMapBounds(new Bounds(topLeft.x, topLeft.y,bottomRight.x, bottomRight.y));

			double centerLong = (mBounds.left + mBounds.right) / 2;
			double centerLat = (mBounds.top + mBounds.bottom) / 2;
			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(centerLong, centerLat));			
		}else{
			
			mMapView.getConstraints().setMapBounds(null);
		}
	}

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
		//		RasterDataSource dataSource = new PackagedRasterDataSource(new EPSG3857(), 19, 19, "aces_{zoom}_{x}_{y}", getActivity().getApplicationContext());
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

		//		double centerLong = (b.left + b.right) / 2;
		//		double centerLat = (b.top + b.bottom) / 2;
		//		mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(centerLong, centerLat));

		mMarkerLayer = new MarkerLayer(mapLayer.getProjection());
		mMapView.getLayers().addLayer(mMarkerLayer);

		//		mMapView.invalidate();

		mButtonCurrentPosition.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				doFocusMap(mLatitude, mLongitude);					
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
		//		double latitude = location.getLatitude();
		//		double longitude = location.getLongitude();
		if (mCurrentLocation == null){
			//			// if it's the first time acquiring a location, focus the map to the location
			//			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));
			setCurrentLocation(location.getLatitude(), location.getLongitude(), true);
		}else{
			setCurrentLocation(location.getLatitude(), location.getLongitude(), false);
		}
		mCurrentLocation = location;
	}

	public void addLandmarkMarker(double latitude, double longitude, String label){
		MapPos markerLocation = mMarkerLayer.getProjection().fromWgs84(longitude, latitude);
		Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.ic_marker);


		MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(0.5f).setColor(Color.WHITE).build();
		// define label what is shown when you click on marker
		Label markerLabel = new DefaultLabel("", label,
				LabelStyle.builder()
				.setDescriptionAlign(Align.LEFT)
				.setDescriptionFont(Typeface.create("Arial", Typeface.NORMAL), 48)
				.build());
		Marker marker = new Marker(markerLocation, markerLabel, markerStyle, mMarkerLayer);
		mMarkerLayer.add(marker);
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
			Label markerLabel = new DefaultLabel("", "Here",
					LabelStyle.builder()
					.setDescriptionAlign(Align.LEFT)
					.setDescriptionFont(Typeface.create("Arial", Typeface.NORMAL), 32)
					.build());

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

	void doFocusMap(double latitude, double longitude){
		boolean inBounds = mBounds == null || longitude >= mBounds.left && longitude <= mBounds.right && latitude >= mBounds.bottom && latitude <= mBounds.top;				
		if (inBounds)
			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));
		else{
			Toast.makeText(getActivity(), (String) "You are current ouside of the area.", 
					Toast.LENGTH_LONG).show();
		}

	}

	public void setCurrentLocation(double latitude, double longitude, boolean focus) {	
		//MapPos markerLocation = mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude);
		MapPos markerLocation = mMarkerLayer.getProjection().fromWgs84(longitude, latitude);
		mLatitude = latitude;
		mLongitude = longitude;
		if (mMarker == null){
			Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.cur_position);
			MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(1.0f).setColor(Color.WHITE).build();
			mMarker = new Marker(markerLocation, null, markerStyle, mMarkerLayer);
			mMarkerLayer.add(mMarker);
		}

		if (focus){			
			doFocusMap(latitude, longitude);
		}

		mMarker.setMapPos(markerLocation);		
		mMapView.invalidate();
	}
}
