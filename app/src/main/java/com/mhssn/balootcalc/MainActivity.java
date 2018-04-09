package com.mhssn.balootcalc;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import custom.EditText;
import custom.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView arrow;
    private float arrowRotation = 0;
    private TextView timer;
    private int second;
    private int minute;
    private int team1, team2;
    private EditText them, us;
    private TextView reg1, reg2;
    private Button register;
    private Stack<Integer> stackT1,stackT2;
    private ImageButton more;
    private TextView sUs,sThem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        stackT1 = new Stack<>();
        stackT2 = new Stack<>();

        setTimer(timer);

        final PopupMenu popupMenu = new PopupMenu(this,more);
        popupMenu.getMenuInflater().inflate(R.menu.home_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case (R.id.newGame):
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage(R.string.new_game_msg);
                        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                newGame();
                            }
                        });
                        alert.show();
                        break;
                    case (R.id.back):
                        ScoreBack();
                        break;
                    case (R.id.screen):
                        menuItem.setChecked(!menuItem.isChecked());
                        if(menuItem.isChecked()){
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                        else{
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                        break;
                    case (R.id.randomizer):
                        Thread thread=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("test","hello");
                                Integer[] randomImgNum=fourRandomNumbers();
                                Log.d("Checking_Random_Numbers",randomImgNum[0]+" "+randomImgNum[1]+ " "+randomImgNum[2]+" "+randomImgNum[3]);
                            }
                        });
                        thread.start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });

        sThem = (TextView) findViewById(R.id.sThem);
        sUs = (TextView) findViewById(R.id.sUs);

        SharedPreferences sh = getSharedPreferences("object",MODE_PRIVATE);
        String json = sh.getString("object",null);
        if(json!=null){
            try {
                JSONObject jsonObject = new JSONObject(json);
                team1 = jsonObject.getInt("team1");
                team2 = jsonObject.getInt("team2");
                arrowRotation = jsonObject.getInt("arrowRotation");
                JSONArray jsonArray1 = jsonObject.getJSONArray("stack1");
                JSONArray jsonArray2 = jsonObject.getJSONArray("stack2");
                Log.d("hello",jsonArray1.toString());
                Log.d("hello",jsonArray1.toString());
                reg1.setText(jsonArray1.getString(0)+"");
                reg2.setText(jsonArray2.getInt(0)+"");
                for(int i = 1;i<jsonArray1.length();i++){
                    reg1.setText(reg1.getText()+"\n"+jsonArray1.getInt(i)+"");
                    reg2.setText(reg2.getText()+"\n"+jsonArray2.getInt(i)+"");
                    stackT1.push(jsonArray1.getInt(jsonArray1.length()-i-1));
                    stackT2.push(jsonArray1.getInt(jsonArray2.length()-i-1));
                }
                stackT1.push(jsonArray1.getInt(0));
                stackT2.push(jsonArray1.getInt(0));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            sUs.setText(team1+"");
            sThem.setText(team2+"");
            arrow.setRotation(arrowRotation);
        }
    }

    private void initializeViews() {
        arrow = (ImageView) findViewById(R.id.arrow);
        timer = (TextView) findViewById(R.id.timer);

        us = (EditText) findViewById(R.id.us);
        them = (EditText) findViewById(R.id.them);

        register = (Button) findViewById(R.id.register);
        reg1 = (TextView) findViewById(R.id.reg1);
        reg2 = (TextView) findViewById(R.id.reg2);


        more = (ImageButton) findViewById(R.id.more);
    }

    public void turnOn(View view) {
        arrow.animate().rotation(arrowRotation -= 90);
    }

    public void register(View view) {
        team1 += Integer.parseInt(us.getText().toString().equals("")?"0":us.getText().toString());
        team2 += Integer.parseInt(them.getText().toString().equals("")?"0":them.getText().toString());
        if(us.getText().toString().equals("") && them.getText().toString().equals("")){
            turnOn(null);
            return;
        }
        stackT1.push(team1);
        stackT2.push(team2);
        if (TextUtils.isEmpty(reg1.getText())) {
            reg1.setText(team1 + "");
            reg2.setText(team2 + "");
        } else {
            reg1.setText(reg1.getText() + "\n" + team1);
            reg2.setText(reg2.getText() + "\n" + team2);
        }
        us.setText("");
        them.setText("");
        if((team1>=152||team2>=152) && team1!=team2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(team2<team1)
                builder.setMessage(R.string.game_over1);
            else
                builder.setMessage(R.string.game_over2);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    newGame();
                }
            });
            builder.show();
        }
        turnOn(null);

        sUs.setText(team1+"");
        sThem.setText(team2+"");
    }

    private void newGame(){
        team1 = 0;
        team2 = 0;
        reg1.setText("");
        reg2.setText("");
        timer.setText("00:00");
        minute = 0;
        second = 0;
        stackT1 = new Stack<>();
        stackT2 = new Stack<>();
        sThem.setText("0");
        sUs.setText("0");
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("test",stackT1.toString());
        String json = "{team1:"+team1+",team2:"+team2+",arrowRotation:"+arrowRotation+",stack1:"+stackT1.toString()+",stack2:"+stackT2.toString()+"}";
        SharedPreferences sharedPreferences = getSharedPreferences("object",MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString("object",json);
        ed.commit();
        Log.d("hello","hi");
    }
    /*
        this method reset the score back one play ago, and reset the view new value.
     */
    private boolean ScoreBack() {
        if (stackT1.isEmpty())
            return false;
        stackT1.pop();
        stackT2.pop();
        if (!stackT1.isEmpty()) {
            team1 = stackT1.peek();
            team2 = stackT2.peek();
        } else {
            team1 = 0;
            team2 = 0;
        }
        sUs.setText(team1 + "");
        sThem.setText(team2 + "");
        int lastIndex1 = reg1.getText().toString().lastIndexOf('\n');
        int lastIndex2 = reg2.getText().toString().lastIndexOf('\n');
        if (lastIndex1 == -1) {
            reg1.setText("");
            reg2.setText("");
            return false;
        }
        reg1.setText(reg1.getText().toString().substring(0, lastIndex1));
        reg2.setText(reg2.getText().toString().substring(0, lastIndex2));
        return false;
    }


    public Integer[] fourRandomNumbers() {
        LinkedHashSet<Integer> randomNumSet=new LinkedHashSet<>();
        Random random=new Random(System.nanoTime());
        while (randomNumSet.size()<4){
            int i =random.nextInt(4);
            randomNumSet.add(i);
            Log.d("inside_Loop",randomNumSet.size() +" / "+i);
        }
        Integer i[] =new Integer[randomNumSet.size()];
        randomNumSet.toArray(i);
        return i;
    }
    public void setTimer(final TextView textView){
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (second % 59 == 0 && second != 0) {
                    second = 0;
                    minute++;
                } else {
                    second++;
                }

                String mSecond = second + "";
                String mMinute = minute + "";
                if (mSecond.length() == 1) {
                    mSecond = "0" + mSecond;
                }

                if (mMinute.length() == 1) {
                    mMinute = "0" + mMinute;
                }
                final String finalMMinute = mMinute;
                final String finalMSecond = mSecond;
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(finalMMinute + ":" + finalMSecond);
                    }
                });
            }
        }, 1000, 1000);

    }
}


