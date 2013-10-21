package se.cardador.water;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

    private ListView mList;
    private ArrayList<String> arrayList;
    private CustomAdapter mAdapter;
    private TCPClient mTcpClient;

    
	public final static String EXTRA_MESSAGE = "se.cardador.water.MESSAGE";
    public enum Status {
 	   hum,
 	   dry,
 	   sen0,
 	   sen1
 	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        arrayList = new ArrayList<String>();
        
        Button send = (Button)findViewById(R.id.send_button);
 
        //relate the listView from java to the one created in xml
        mList = (ListView)findViewById(R.id.list);
        mAdapter = new CustomAdapter(this, arrayList);
        mList.setAdapter(mAdapter);
        
        // connect to the server
        new connectTask().execute("");
        try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (Status state : Status.values()) {
            //sends the message to the server
            if (mTcpClient != null) {
                mTcpClient.sendMessage(state.toString());
            }
            //refresh the list
            mAdapter.notifyDataSetChanged();
            try {
				Thread.sleep(1100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Log.e("Startup", state.toString());
    	}
    
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            	//String message = editText.getText().toString();
                ////sends the message to the server
                //if (mTcpClient != null) {
                //    mTcpClient.sendMessage(message);
                //}
 
                ////refresh the list
                //mAdapter.notifyDataSetChanged();
                //editText.setText("");
                
                for (Status state : Status.values()) {
                	
                    //sends the message to the server
                    if (mTcpClient != null) {
                        mTcpClient.sendMessage(state.toString());
                    }
                    Log.e("Button", state.toString());
                    //refresh the list
                    mAdapter.notifyDataSetChanged();
                    try {
						Thread.sleep(1100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
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
	
    public class connectTask extends AsyncTask<String,String,TCPClient> {
    	 
        @Override
        protected TCPClient doInBackground(String... message) {
 
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();
 
            return null;
        }
 
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
 
            //in the arrayList we add the messaged received from server
            arrayList.add(values[0]);
            // notify the adapter that the data set has changed. This means that new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }
}
