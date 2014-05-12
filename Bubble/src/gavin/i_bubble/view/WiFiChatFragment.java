package gavin.i_bubble.view;

import gavin.i_bubble.R;
import gavin.i_bubble.manager.ChatManager;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("ValidFragment")
public class WiFiChatFragment extends Fragment implements OnClickListener {
	private ChatManager chatManager;
	private View view;
	private Button btnSend;

	private volatile static WiFiChatFragment uniqueInstance;

	private WiFiChatFragment() {
	}

	public static WiFiChatFragment getInstance() {
		if (uniqueInstance == null) {
			synchronized (WiFiChatFragment.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new WiFiChatFragment();
				}
			}
		}
		return uniqueInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chat, container, false);
		btnSend = (Button) view.findViewById(R.id.button_startgame);
		btnSend.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v == btnSend) {
			// String str = "12";
			// DataManage.getInstance().setData(true, Integer.parseInt(str));//
			// 刷新自己的数据
			// chatManager.write(str.getBytes());// 发送数据
			// PlayingGameActivity.startActivity(getActivity());
			PlayingGameFragment playGameFragment = new PlayingGameFragment();
			getActivity().getFragmentManager().beginTransaction().replace(R.id.container_root, playGameFragment).commit();

		}
	}

	public Context getContext() {
		return getActivity();
	}

	public interface MessageTarget {
		public Handler getHandler();
	}

	public void setChatManager(ChatManager obj) {
		chatManager = obj;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

}
