# DJIFPV
A complete DJI FPV Demo project using the DJI Android SDK v2.4
This project is based on Android 5.1 and has been tested on NVIDIA SHELL.
Implemented the FPV function, taking photo, and taking video.


Point should be noted:
1. Fail to load lib error(cannot find "libGroudStation.so")
  http://forum.dev.dji.com/thread-31818-1-1.html OR
  add the .so files in DJI-SDK manually by creating a directory in your app named jniLibs and putting DJI-SDK-LIB/arm* in it.

2. A problem still need to be addressed
  The function DJIDrone.connectToDrone() seems to return ture no matter if the tablet is really connect to the remote controller   or not.

Summery: 
1. Apply a app code on dji website and add to AndroidManifest.xml in the application block
	    <meta-data
            android:name="com.dji.sdk.API_KEY"
            android:value="<YOUR KEY>" />
2. Start with DJIAoaActivity
	public class DJIAoaActivity extends AppCompatActivity {
    	private static boolean isStarted = false;

    	@Override
    	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_djiaoa);
	
	        if (isStarted) {
	            //Do nothing
	        } else {
	            isStarted = true;
	            ServiceManager.getInstance();
	            UsbAccessoryService.registerAoaReceiver(this);
	            Intent intent = new Intent(DJIAoaActivity.this, <YOUR MAIN ACTIVITY>.class);
	            startActivity(intent);
	        }
	
	        Intent aoaIntent = getIntent();
	        if (aoaIntent != null) {
	            String action = aoaIntent.getAction();
	            if (action == UsbManager.ACTION_USB_ACCESSORY_ATTACHED || action == Intent.ACTION_MAIN) {
	                Intent attachedIntent = new Intent();
	                attachedIntent.setAction(DJIUsbAccessoryReceiver.ACTION_USB_ACCESSORY_ATTACHED);
	                sendBroadcast(attachedIntent);
	            }
	        }
	        finish();
    }
}
3. Permission and features that may be used:
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
	<uses-permission android:name="android.permission.LOCATION_HARDWARE" />
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	
	 <uses-feature
		android:name="android.hardware.usb.UsbAccessory"
		android:required="false" />
	<uses-feature
		android:name="android.hardware.usb.UsbRequest"
		android:required="false" />
    	<uses-feature
		android:name="android.hardware.usb.UsbDeviceConnection"
		android:required="false" />
	<uses-feature
		android:name="android.hardware.usb.UsbDevice"
		android:required="false" />
	<uses-feature
		android:name="android.hardware.usb.UsbConfiguration"
		android:required="false" />
	<uses-feature
		android:name="android.hardware.usb.accessory"
		android:required="false" />
	<uses-feature
		android:name="android.hardware.usb.host"
		android:required="false" />
		
4. To activate the APP add this thread in the onCreate() method:
	new Thread() {
            public void run() {
                try {
                    DJIDrone.checkPermission(getApplicationContext(), new DJIGeneralListener() {
                        @Override
                        public void onGetPermissionResult(int result) {
                            if (result == 0) {
                                // show success
                                Log.e(TAG, "onGetPermissionResult =" + result);
                                Log.e(TAG,
                                        "onGetPermissionResultDescription=" + DJIError.getCheckPermissionErrorDescription(result));
                            } else {
                                // show errors
                                Log.e(TAG, "onGetPermissionResult =" + result);
                                Log.e(TAG,
                                        "onGetPermissionResultDescription=" + DJIError.getCheckPermissionErrorDescription(result));
                            }
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
		
4. Then set the DroneCode and initiate the SDK:
	DroneCode = (int) <YOUR DRONE TYPE>;
        onInitSDK(DroneCode);
		
			private void onInitSDK(int type) {
			switch (type) {
				case 0: {
					DJIDrone.initWithType(this.getApplicationContext(), DJIDroneType.DJIDrone_Vision);
					break;
				}
				case 1: {
					DJIDrone.initWithType(this.getApplicationContext(), DJIDroneType.DJIDrone_Inspire1);
					break;
				}
				case 2: {
					DJIDrone.initWithType(this.getApplicationContext(), DJIDroneType.DJIDrone_Phantom3_Advanced);
					break;
				}
				case 3: {
					DJIDrone.initWithType(this.getApplicationContext(), DJIDroneType.DJIDrone_M100);
					break;
				}
				default: {
					break;
				}
			}

		}
		
5. Call DJIDrone.connectToDrone() after calling onInitSDK() method (Tricky part, see note 2)

6. About the main activity:

	@Override
	
	 protected void onResume() {
		super.onResume();
		DJIDrone.getDjiMC().startUpdateTimer(1000); // Start the update timer for MC to update info
		ServiceManager.getInstance().pauseService(false);
	}

	@Override
	
	protected void onPause() {
		super.onPause();
		DJIDrone.getDjiMC().stopUpdateTimer(); // Stop the update timer for MC to update info
		ServiceManager.getInstance().pauseService(true);
	}

	// The following codes are used to exit FPVActivity when pressing the phone's "return" button twice.
    
	private static boolean first = false;
	private Timer ExitTimer = new Timer();

	class ExitCleanTask extends TimerTask {

	@Override
	
	public void run() {

		Log.e("ExitCleanTask", "Run in!!!! ");
		first = false;
		}
	}
	@Override
	
	// Tab return button twice to exit the app
    
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		// TODO Auto-generated method stub
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		Log.d(TAG,"onKeyDown KEYCODE_BACK");

		if (first) {
			first = false;
			finish();
		}
		else
		{
			first = true;
			Toast.makeText(MapsActivity.this, getText(R.string.press_again_exit), Toast.LENGTH_SHORT).show();
			ExitTimer.schedule(new ExitCleanTask(), 2000);
		}

		return true;
	        }
		return super.onKeyDown(keyCode, event);
	}
