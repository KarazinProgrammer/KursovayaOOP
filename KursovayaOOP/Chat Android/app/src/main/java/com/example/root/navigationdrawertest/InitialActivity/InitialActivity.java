package com.example.root.navigationdrawertest.InitialActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.R;


public class InitialActivity extends AppCompatActivity {

    private SharedPreferences sPref;
    private GridView gvMain;
    private GridViewAdapter adapter;
    private ImageView resultImage;
    private Button btnOK;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Window window = this.getWindow();
        window.setLayout((int)(width*.8), (int)(height*.6));

        setFinishOnTouchOutside(false);

        sPref = getSharedPreferences("Info", MODE_PRIVATE);

        String nick = sPref.getString("nick", "");
        int img = sPref.getInt("img", 1);


        Intent intent = getIntent();
        final boolean unique = intent.getBooleanExtra("unique", true);

        if (!unique) {
            this.setTitle("Not unique nick!");
        }else{
            if(nick.equals("")) {
                setTitle(getTitle().toString() + "User!");
            }else{
                setTitle(getTitle().toString() + nick + "!");
            }
        }

        btnOK = (Button)findViewById(R.id.okBtn);
        resultImage =(ImageView)findViewById(R.id.resultImage);
        resultImage.setTag(img);
        int imgId = getResources().getIdentifier("img"+img , "drawable", getPackageName());
        resultImage.setImageResource(imgId);
        gvMain = (GridView)findViewById(R.id.gridView);
        editText = (EditText)findViewById(R.id.editNick);
        editText.setText(nick);
        adapter = new GridViewAdapter(getBaseContext());

        View.OnClickListener imageButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton ib = (ImageButton)view;
                resultImage.setImageDrawable(ib.getDrawable());
                resultImage.setTag(ib.getTag());
            }
        };

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nick = editText.getText().toString();
                if(nick.length()==0) {
                    editText.setError("Enter nick");
                }else if(!User.checkNickname(nick)){
                    editText.setError("a..z, A..Z, 0..9, _");
                }else if(!Translator.users.isUniqueNick(nick)){
                    editText.setError("Not unique nick!");
                } else {
                    int imgNumber = (int) resultImage.getTag();
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("nick", nick);
                    ed.putInt("img", imgNumber);
                    ed.apply();

                    Intent intent = new Intent();
                    intent.putExtra("nick", nick);
                    intent.putExtra("img", imgNumber);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    btnOK.performClick();
                }
                return false;
            }
        });

        adapter.setListener(imageButtonListener);
        gvMain.setAdapter(adapter);
        adjustGridView();
    }

    private void adjustGridView() {
        gvMain.setNumColumns(3);
        gvMain.setVerticalSpacing(5);
        gvMain.setColumnWidth(100);
        gvMain.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
    }
}
