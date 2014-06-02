package net.nature.mobile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.activeandroid.Model;
import com.google.common.collect.Lists;

import retrofit.RetrofitError;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import net.nature.mobile.rest.NatureNetRestAdapter;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class SurveyActivity extends Activity {

	static final String OUTPUT_SURVEY_TEXT = "survey.text";	
	protected static final String TAG = SurveyActivity.class.getName();
	private CheckBox checkbox1;
	private CheckBox checkbox2;
	private CheckBox checkbox3;
	private CheckBox checkbox4;
	private Button next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_survey);	
		checkbox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkbox2 = (CheckBox) findViewById(R.id.checkBox2);
		checkbox3 = (CheckBox) findViewById(R.id.checkBox3);

		next = (Button) findViewById(R.id.next_button);		
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

				String surveyText = "";
				for (CheckBox ch : Lists.newArrayList(checkbox1,checkbox2,checkbox3)){
					if (ch.isChecked()){
						surveyText += ch.getText().toString() + "; ";
					}
				}
				Intent result = new Intent();
				result.putExtra(OUTPUT_SURVEY_TEXT, surveyText);
				setResult(RESULT_OK, result);
				finish();
			}
		});
	}
}
