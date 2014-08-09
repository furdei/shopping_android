package com.furdey.shopping.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.furdey.engine.android.controllers.OnCursorLoadListener;
import com.furdey.shopping.R;
import com.furdey.shopping.controllers.BaseFilterController;

public abstract class BaseFilterActivity extends BaseActivity {

	private BaseFilterController<?, ?> controller;

	private EditText filterEdit;

	private Boolean filteringInProgress;
	private Boolean filterAgain;
	private String filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_filter_list);
		filterEdit = (EditText) findViewById(R.id.filterListFilterEdit);
		filteringInProgress = false;
		filterAgain = false;

		filter = getIntent().getExtras().getString(BaseFilterController.FILTER_PARAM_NAME);
		filterEdit.setText(filter);

		controller.registerCursorLoadListener(new OnCursorLoadListener() {
			@Override
			public void onLoadComplete(Cursor cursor) {
				onCursorLoadComplete(cursor);

				synchronized (filteringInProgress) {
					filteringInProgress = false;
				}

				if (filterAgain)
					onFilter();
			}
		});

		filterEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d("BaseFilterActivity", "afterTextChanged");
				filter = s.toString();
				onFilter();
			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		filter = filterEdit.getText().toString();
		onFilter();
	}

	public BaseFilterController<?, ?> getController() {
		return controller;
	}

	public void setController(BaseFilterController<?, ?> controller) {
		this.controller = controller;
	}

	public abstract void onCursorLoadComplete(Cursor cursor);

	private final void onFilter() {
		Log.d("BaseFilterActivity", "onFilter");
		synchronized (filteringInProgress) {
			if (!filteringInProgress) {
				filteringInProgress = true;
				filterAgain = false;
				controller.setFilter(filter);
				controller.refreshCursor();
			} else
				filterAgain = true;
		}
	}

}
