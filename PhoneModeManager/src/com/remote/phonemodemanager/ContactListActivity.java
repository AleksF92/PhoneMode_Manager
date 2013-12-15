package com.remote.phonemodemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;



public class ContactListActivity extends Activity implements OnItemClickListener{

	private static Context context;
	List<String> name1 = new ArrayList<String>();
	List<String> phno1 = new ArrayList<String>();
	final DatabaseHelper dbHelper = new DatabaseHelper(this);
	MyAdapter ma ;
	Button select;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		ContactListActivity.context = getApplicationContext();
		getAllContacts(this.getContentResolver());
		ListView lv= (ListView) findViewById(R.id.lv);
		ma = new MyAdapter();
		lv.setAdapter(ma);
		lv.setOnItemClickListener(this); 
		lv.setItemsCanFocus(false);
		lv.setTextFilterEnabled(true);
		// adding
		
		select = (Button) findViewById(R.id.button1);
		select.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				List<String> checkedContactNames = new ArrayList<String>();
				List<String> checkedContactNumbers = new ArrayList<String>();
				Whitelist.clear(dbHelper);
				for(int i = 0; i < name1.size(); i++){
					if(ma.mCheckStates.get(i)==true){
						checkedContactNames.add(name1.get(i).toString());
						checkedContactNumbers.add(phno1.get(i).toString());
					}else{
					}
				}

				for(int i = 0; i < checkedContactNames.size(); i++){
					System.out.println("adding: " + checkedContactNames.get(i));
					String selName = checkedContactNames.get(i);
					String selNr = checkedContactNumbers.get(i);
					Whitelist selectedContact = new Whitelist(selName, selNr, 3);
					selectedContact.save(dbHelper);
				}
				finish();
			}       
		});
		checkmarkSaved();
		
	}
	
	public static Context getContactContext(){
		return ContactListActivity.context;
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		System.out.println("onItemClick called\n");
		ma.toggle(arg2);
	}

	public  void getAllContacts(ContentResolver cr) {

		System.out.println("getALlContacts Called");

		Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
			String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			name1.add(name);
			phno1.add(phoneNumber);
		}

		phones.close();
	}
	
	private void checkmarkSaved(){
		List<Whitelist> whList = Arrays.asList(Whitelist.getAll(dbHelper));
		for(int n = 0; n < phno1.size(); n++){
			String numToCheck = phno1.get(n);
			for(int c = 0; c < whList.size(); c++){
				if(numToCheck.equals(whList.get(c).getNr())){
					ma.setChecked(n, true);
				}
			}	
		}
	}


	class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
		private SparseBooleanArray mCheckStates;
		LayoutInflater mInflater;
		TextView tv1,tv;
		CheckBox cb;
		MyAdapter()
		{
			mCheckStates = new SparseBooleanArray(name1.size());
			mInflater = (LayoutInflater)ContactListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return name1.size();
		}

		@Override
		public Object getItem(int position) {
			System.out.println("get Item called\n");
			return position;
		}

		@Override
		public long getItemId(int position) {
			System.out.println("getItemId called\n");
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View vi=convertView;
			if(convertView==null)
				vi = mInflater.inflate(R.layout.row, null); 
			TextView tv= (TextView) vi.findViewById(R.id.textView1);
			tv1= (TextView) vi.findViewById(R.id.textView2);
			cb = (CheckBox) vi.findViewById(R.id.checkBox1);
			tv.setText(name1.get(position));
			tv1.setText(phno1.get(position));
			cb.setTag(position);
			cb.setChecked(mCheckStates.get(position, false));
			cb.setOnCheckedChangeListener(this);

			return vi;
		}
		public boolean isChecked(int position) {
			System.out.println("isChecked Called\n");
			return mCheckStates.get(position, false);
		}

		public void setChecked(int position, boolean isChecked) {
			System.out.println("setCheked called\n");
			mCheckStates.put(position, isChecked);
			System.out.println("hello...........");
			notifyDataSetChanged();
		}

		public void toggle(int position) {
			System.out.println("toggle called\n");
			setChecked(position, !isChecked(position));
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			System.out.println("item clicked " + isChecked);
			mCheckStates.put((Integer) buttonView.getTag(), isChecked);         
		}
		
	}   
}