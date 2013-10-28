package se.cardador.water;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView mList;
	private ArrayList<String> arrayList;
	private CustomAdapter mAdapter;
	private TCPClient mTcpClient;
	private int mHum = 0;
	private int mDry = 0;

	public final static String EXTRA_MESSAGE = "se.cardador.water.MESSAGE";

	private enum Status {
		hum, dry, sen0, sen1, elap
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		arrayList = new ArrayList<String>();

		final Button send = (Button) findViewById(R.id.send_button);

		// relate the listView from java to the one created in xml
		mList = (ListView) findViewById(R.id.list);
		mAdapter = new CustomAdapter(this, arrayList);
		mList.setAdapter(mAdapter);

		// connect to the server
		new connectTask().execute("");

		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {	
				for (Status state : Status.values()) {

					// sends the message to the server
					if (mTcpClient != null) {
						mTcpClient.sendMessage(state.toString());
					}
					Log.e("Button", state.toString());
					// refresh the list
					mAdapter.notifyDataSetChanged();
					try {
						Thread.sleep(1200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class connectTask extends AsyncTask<String, String, TCPClient> {

		@Override
		protected TCPClient doInBackground(String... message) {

			// we create a TCPClient object and
			mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				// here the messageReceived method is implemented
				public void messageReceived(String message) {
					// this method calls the onProgressUpdate
					publishProgress(message);
				}
			});
			mTcpClient.run();

			return null;
		}

		@SuppressLint("DefaultLocale")
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			// in the arrayList we add the message received from server
			arrayList.add(values[0]);

			if (values[0].toLowerCase().contains("average")) {
				// Get only the value and cast to integer
				mHum = Integer.parseInt(values[0].split(" ")[1]);
			}
			if (values[0].toLowerCase().contains("dry")) {
				// Get only the value and cast to integer
				mDry = Integer.parseInt(values[0].split(" ")[1]);
				// Set the image since this is the last needed information
				ImageView imageHappy = (ImageView) findViewById(R.id.flower);

				if (mDry > mHum) {
					imageHappy.setImageResource(R.drawable.flower_sad);
				} else {
					imageHappy.setImageResource(R.drawable.flower);
				}
			}
			Log.e("Received: ", values[0].toString());
			// notify the adapter that the data set has changed. This means that
			// new message received
			// from server was added to the list
			mAdapter.notifyDataSetChanged();
		}
	}
}
