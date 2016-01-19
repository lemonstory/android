package com.xiaoningmeng;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenu.xlistview.XListView;
import com.xiaoningmeng.adapter.AlarmSoundSelectAdapter;
import com.xiaoningmeng.adapter.AlarmWeekSelectAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.WeekDay;
import com.xiaoningmeng.player.SoundManager;

public class AlarmSelectActivity extends BaseActivity implements OnClickListener{

	private XListView mListView;
	private int type;
	private BaseAdapter mAdapter;
	private List<WeekDay> weekDays;
	private int selectPos;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_address);
		type = getIntent().getIntExtra("alaramSelect", 0);
		
		setRightHeadText("");
		mListView = (XListView) findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		if(type == 0){
			setTitleName("重复");
			weekDays  = (List<WeekDay>) getIntent().getSerializableExtra("weekDays");
			mAdapter = new AlarmWeekSelectAdapter(this,weekDays);
		}else{
			setTitleName("铃声");
			selectPos = getIntent().getIntExtra("ringPosition", 0);
			mAdapter = new AlarmSoundSelectAdapter(this,selectPos);
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				if(type == 0){
					WeekDay weekDay = weekDays.get(pos);
					weekDay.setCheck(!weekDay.isCheck());
				}else{
					((AlarmSoundSelectAdapter)mAdapter).setSelectedPositon(pos);
					SoundManager.getInstance().start(AlarmSelectActivity.this, pos, SoundManager.SoundMode.ONCE);
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		//Slidr.attach(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_right:		
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	public void finish() {
		if(type == 0){
			Intent i = new Intent(this,AlarmClockActivity.class);
			i.putExtra("weekDays", (Serializable)weekDays);
			setResult(3, i);
		}else{
			 SoundManager.getInstance().end();
			 Intent i = new Intent(this,AlarmClockActivity.class);
			 i.putExtra("ringPosition", ((AlarmSoundSelectAdapter)mAdapter).getSelectedPositon());
			 setResult(4, i);
		}
		super.finish();
	}
}
