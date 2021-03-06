package android.me.p2psip.activity;

import io.vov.vitamio.LibsChecker;
import android.content.Intent;
import android.me.p2psip.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ConfigActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Init Media Play lib
		 */
		if (!LibsChecker.checkVitamioLibs(this))
			return;

		setContentView(R.layout.activity_config);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
