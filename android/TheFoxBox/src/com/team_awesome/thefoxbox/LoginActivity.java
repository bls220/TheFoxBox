package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.provider.LoaderHelper;
import com.team_awesome.thefoxbox.provider.LoaderHelper.Callback;
import com.team_awesome.thefoxbox.provider.LoginInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener, Callback<Boolean> {

	Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		(button=(Button)this.findViewById(R.id.login_button)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		TextView s = ((TextView)findViewById(R.id.login_name));
		LoaderHelper.init(new LoginInfo(s.getText().toString()));
		
		LoaderHelper.ping(this);
		button.setEnabled(false);
	}

	@Override
	public void done(Boolean ret) {
		if (ret) {
			startActivity(new Intent(this, MainActivity.class));
			// I think this will prevent the user from going back into this.
			finish();
		} else {
			err(new Exception("Could not log in"));
		}
	}

	@Override
	public void err(Exception ex) {
		Toast.makeText(this, "Could not log in: " + ex, Toast.LENGTH_LONG).show();
		button.setEnabled(true);
	}
}
