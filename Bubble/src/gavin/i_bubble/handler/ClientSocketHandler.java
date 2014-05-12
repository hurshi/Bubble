package gavin.i_bubble.handler;

import android.os.Handler;
import android.util.Log;
import gavin.i_bubble.activity.WiFiConnectionActivity;
import gavin.i_bubble.manager.ChatManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {

	private static final String TAG = "ClientSocketHandler";
	private Handler handler;
	private ChatManager chat;
	private InetAddress mAddress;

	public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
		this.handler = handler;
		this.mAddress = groupOwnerAddress;
	}

	@Override
	public void run() {
		Socket socket = new Socket();
		try {
			socket.bind(null);
			socket.connect(new InetSocketAddress(mAddress.getHostAddress(), WiFiConnectionActivity.SERVER_PORT), 5000);
			Log.d(TAG, "Launching the I/O handler");
			chat = new ChatManager(socket, handler);
			new Thread(chat).start();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}

	public ChatManager getChat() {
		return chat;
	}

}
