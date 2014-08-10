package com.furdey.shopping.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.furdey.shopping.R;
import com.furdey.shopping.controllers.SocialController;

public class SocialMessageActivity extends BaseActivity {

	private EditText messageText;
	private TextView messagePreview;
	private Button saveButton;
	private Button cancelButton;
	private SocialController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_message);

		messageText = (EditText) findViewById(R.id.socialMessageText);
		messagePreview = (TextView) findViewById(R.id.socialMessagePreview);
		saveButton = (Button) findViewById(R.id.formButtonSave);
		cancelButton = (Button) findViewById(R.id.formButtonCancel);

		controller = new SocialController(this);
		messageText.setText(R.string.socialMessageBase);
		messagePreview.setText(controller.constructMessage(getString(R.string.socialMessageBase)));
		saveButton.setText(R.string.socialMessageSend);
		saveButton.setOnClickListener(controller.getSendButtonOnClickListener());
		cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

	public String getMessage() {
		return messagePreview.getText().toString();
	}
}
