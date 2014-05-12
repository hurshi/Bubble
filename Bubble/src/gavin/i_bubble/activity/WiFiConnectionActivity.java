package gavin.i_bubble.activity;

import gavin.i_bubble.R;
import gavin.i_bubble.broadcast_receiver.WiFiDirectBroadcastReceiver;
import gavin.i_bubble.handler.ClientSocketHandler;
import gavin.i_bubble.handler.GroupOwnerSocketHandler;
import gavin.i_bubble.manager.ChatManager;
import gavin.i_bubble.manager.DataManage;
import gavin.i_bubble.service.WiFiP2pService;
import gavin.i_bubble.utils.ChatManagerUtil;
import gavin.i_bubble.utils.WiFiDirectServicesList;
import gavin.i_bubble.utils.WiFiDirectServicesList.DeviceClickListener;
import gavin.i_bubble.utils.WiFiDirectServicesList.WiFiDevicesAdapter;
import gavin.i_bubble.view.WiFiChatFragment;
import gavin.i_bubble.view.WiFiChatFragment.MessageTarget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class WiFiConnectionActivity extends Activity implements OnClickListener, DeviceClickListener, Handler.Callback, MessageTarget, ConnectionInfoListener {

	public static void startActivity(Context context) {
		Intent i = new Intent(context, WiFiConnectionActivity.class);
		context.startActivity(i);
	}

	public static final String TAG = "Bubble";

	// TXT RECORD properties
	public static final String TXTRECORD_PROP_AVAILABLE = "available";
	public static final String SERVICE_INSTANCE = "_Bubble";
	public static final String SERVICE_REG_TYPE = "_presence._tcp";

	public static final int MESSAGE_READ = 0x400 + 1;
	public static final int MY_HANDLE = 0x400 + 2;
	private WifiP2pManager manager;

	public static final int SERVER_PORT = 4545;

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WifiP2pDnsSdServiceRequest serviceRequest;

	private Handler handler = new Handler(this);
	private WiFiChatFragment chatFragment;
	private WiFiDirectServicesList servicesList;

	private TextView statusTxtView;
	private Button btnSearch;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wifi_connection);
		findViews();
		init();
	}

	private void findViews() {
		btnSearch = (Button) findViewById(R.id.button_connect);
	}

	private void init() {
		statusTxtView = (TextView) findViewById(R.id.status_text);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);

		btnSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btnSearch) {
			startRegistrationAndDiscovery();
			servicesList = new WiFiDirectServicesList();
			getFragmentManager().beginTransaction().add(R.id.container_root, servicesList, "services").commit();
			btnSearch.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onRestart() {
		Fragment frag = getFragmentManager().findFragmentByTag("services");
		if (frag != null) {
			getFragmentManager().beginTransaction().remove(frag).commit();
		}
		super.onRestart();
	}

	@Override
	protected void onStop() {
		if (manager != null && channel != null) {
			manager.removeGroup(channel, new ActionListener() {

				@Override
				public void onFailure(int reasonCode) {
					Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
				}

				@Override
				public void onSuccess() {
				}

			});
		}
		super.onStop();
	}

	/**
	 * Registers a local service and then initiates a service discovery
	 */
	private void startRegistrationAndDiscovery() {
		Map<String, String> record = new HashMap<String, String>();
		record.put(TXTRECORD_PROP_AVAILABLE, "visible");
		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		manager.addLocalService(channel, service, new ActionListener() {
			@Override
			public void onSuccess() {
				appendStatus("Added Local Service");
			}

			@Override
			public void onFailure(int error) {
				appendStatus("Failed to add a service");
			}
		});
		discoverService();
	}

	private void discoverService() {
		manager.setDnsSdResponseListeners(channel, new DnsSdServiceResponseListener() {
			@Override
			public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
				if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
					WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager().findFragmentByTag("services");
					if (fragment != null) {
						WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment.getListAdapter());
						WiFiP2pService service = new WiFiP2pService();
						service.device = srcDevice;
						service.instanceName = instanceName;
						service.serviceRegistrationType = registrationType;
						adapter.add(service);
						adapter.notifyDataSetChanged();
						Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
					}
				}

			}
		}, new DnsSdTxtRecordListener() {

			/**
			 * A new TXT record is available. Pick up the advertised buddy name.
			 */
			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice device) {
				Log.d(TAG, device.deviceName + " is " + record.get(TXTRECORD_PROP_AVAILABLE));
			}
		});

		// After attaching listeners, create a service request and initiate
		// discovery.
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		manager.addServiceRequest(channel, serviceRequest, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Added service discovery request");
			}

			@Override
			public void onFailure(int arg0) {
				appendStatus("Failed adding service discovery request");
			}
		});
		manager.discoverServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Service discovery initiated");
			}

			@Override
			public void onFailure(int arg0) {
				appendStatus("Service discovery failed");

			}
		});
	}

	@Override
	public void connectP2p(WiFiP2pService service) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = service.device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest, new ActionListener() {

				@Override
				public void onSuccess() {
				}

				@Override
				public void onFailure(int arg0) {
				}
			});

		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Connecting to service");
			}

			@Override
			public void onFailure(int errorCode) {
				appendStatus("Failed connecting to service");
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_READ:
			byte[] readBuf = (byte[]) msg.obj;
			String readMessage = new String(readBuf, 0, msg.arg1);
			// float data;
			// Log.i("接收到得数据:", readMessage);
			// if (readMessage.length() > 6) {
			// data = 0;
			// } else {
			// data = Float.parseFloat(readMessage);
			// }
			DataManage.getInstance().dataUtils(readMessage);
			readMessage = "";
			break;

		case MY_HANDLE:
			Object obj = msg.obj;
			(chatFragment).setChatManager((ChatManager) obj);
			ChatManagerUtil.getInstance().setChatManager((ChatManager) obj);

		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		Log.i("onPause", "WiFiConnectionActivity暂停");
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		Thread handler = null;
		/*
		 * The group owner accepts connections using a server socket and then spawns a client socket for every client. This is handled by {@code GroupOwnerSocketHandler}
		 */

		if (p2pInfo.isGroupOwner) {
			ChatManagerUtil.getInstance().setIsGroupOwner(true);
			Log.d(TAG, "Connected as group owner");
			try {
				handler = new GroupOwnerSocketHandler(((MessageTarget) this).getHandler());
				handler.start();
			} catch (IOException e) {
				// Log.d(TAG, "Failed to create a server thread - " +
				// e.getMexssage());
				return;
			}
		} else {
			Log.d(TAG, "Connected as peer");
			handler = new ClientSocketHandler(((MessageTarget) this).getHandler(), p2pInfo.groupOwnerAddress);
			handler.start();
		}
		chatFragment = WiFiChatFragment.getInstance();
		getFragmentManager().beginTransaction().replace(R.id.container_root, chatFragment).commit();
		statusTxtView.setVisibility(View.GONE);
	}

	public void appendStatus(String status) {
		String current = statusTxtView.getText().toString();
		statusTxtView.setText(current + "\n" + status);
	}

}
