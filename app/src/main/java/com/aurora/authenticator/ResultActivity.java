package com.aurora.authenticator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ResultActivity extends Activity {

    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.token)
    TextView token;
    @BindView(R.id.auth)
    TextView auth;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.status)
    TextView status;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            String Email = intent.getStringExtra(MainActivity.AUTH_EMAIL);
            String Token = intent.getStringExtra(MainActivity.AUTH_TOKEN);
            retrieveAc2dmToken(Email, Token);
        }
    }

    private void retrieveAc2dmToken(final String Email, final String oAuthToken) {
        disposable.add(Observable.fromCallable(() -> new AC2DMTask()
                .getAC2DMResponse(Email, oAuthToken))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contentMap -> {
                    status.setText("SUCCESS");
                    email.setText("Email : " + contentMap.get("Email"));
                    token.setText("Token : " + contentMap.get("Token"));
                    auth.setText("Auth : " + contentMap.get("Auth"));
                    name.setText("Name : " + contentMap.get("firstName"));
                }, err -> {
                    status.setText("FAILED");
                    status.setTextColor(Color.RED);
                    Toast.makeText(this, "Failed to generate AC2DM Auth Token", Toast.LENGTH_LONG).show();
                }));
    }
}
