package sg.com.smartmediasoft.storeclient;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.widget.ImageView;

public class StoreApplication extends Application {

	private static ImageLoader mImageLoader;
	private static DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_action_cloud) // resource or
															// drawable
			.showImageForEmptyUri(R.drawable.ic_action_cloud) // resource or
																// drawable
			.resetViewBeforeLoading(true) // default
			.cacheInMemory(true) // default
			.cacheOnDisk(true) // default
			.build();

	@Override
	public void onCreate() {
		super.onCreate();

		// Create global configuration and initialize ImageLoader with this
		// configuration
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(config);

		GCMIntentService.register(this);
	}

	public static void loadImage(String url, ImageView imageView) {
		mImageLoader.displayImage(url, imageView, mOptions);
	}

}
