package com.example.mypegasus.oauthdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.api.AsyncBaiduRunner;
import com.baidu.api.Baidu;
import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView mTvAccessToken = null;
    private TextView mTvUserInfo = null;
    private TextView mTvUserInfoDetail = null;
    private Baidu mBaidu = null;
    private Gson mGson = null;

    String clientId = "dNF12tdr6kT869DfU9DA6Gtc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaidu = new Baidu(clientId, this);
        mGson = new Gson();
        setContentView(R.layout.activity_main);
        mTvAccessToken = (TextView) findViewById(R.id.tv_access_token);
        mTvUserInfo = (TextView) findViewById(R.id.tv_user_info);
        mTvUserInfoDetail = (TextView) findViewById(R.id.tv_user_info_detail);
    }

    public void OnClickOAuth(View v) {
        mBaidu.authorize(this, true, true, new BaiduDialog.BaiduDialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                refreshUI(mBaidu.getAccessToken());
            }

            @Override
            public void onBaiduException(BaiduException e) {
                refreshUI("exception");
            }

            @Override
            public void onError(BaiduDialogError baiduDialogError) {
                refreshUI("error");
            }

            @Override
            public void onCancel() {
                refreshUI("cancel");
            }
        });
    }

    public void OnClickGetUserInfo(View v) {
        String token = mBaidu.getAccessToken();
//        String token = "";
        if(TextUtils.isEmpty(token)) {
            Toast.makeText(MainActivity.this, "Token is null", Toast.LENGTH_SHORT).show();
        } else {
            final String url = "https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
            AsyncBaiduRunner runner = new AsyncBaiduRunner(mBaidu);
            runner.request(url, null, "GET", new AsyncBaiduRunner.RequestListener() {
                @Override
                public void onComplete(String json) {
                    refreshResultUI(json);
                }

                @Override
                public void onIOException(IOException e) {
                    refreshResultUI("onIOException");
                }

                @Override
                public void onBaiduException(BaiduException e) {
                    refreshResultUI("onBaiduException");
                }
            });
            /*new Thread(){
                @Override
                public void run() {
                    try {
                        final String jsonText = mBaidu.request(url, null, "GET");
//                        final UserEntity user = mGson.fromJson(jsonText, UserEntity.class);
                        final UserEntity user = mGson.fromJson(jsonText, new TypeToken<UserEntity>(){}.getType());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvUserInfo.setText(jsonText);
//                                mTvUserInfoDetail.setText(user.getUsername());
//                                mTvUserInfoDetail.setText(user.getName());
                                mTvUserInfoDetail.setText(mGson.toJson(user));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BaiduException e) {
                        e.printStackTrace();
                    }
                }
            }.start();*/
        }
    }

    private void refreshResultUI(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvUserInfoDetail.setText(msg);
            }
        });
    }

    private void refreshUI(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvAccessToken.setText(msg);
            }
        });
    }


    class UserEntity {
        private String userid;
        private String blood;
//        private String username;
        @SerializedName("username")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getBlood() {
            return blood;
        }

        public void setBlood(String blood) {
            this.blood = blood;
        }

        /*public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }*/
    }
}
