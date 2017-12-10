package rkapoors.healthguide_dr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash extends Activity {      //extend AppCompatActivity to display title bar

    ImageView splimg;
    TextView spltxt, spltxt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splimg=(ImageView)findViewById(R.id.splimg);
        spltxt=(TextView)findViewById(R.id.spltxt);
        spltxt1=(TextView)findViewById(R.id.spltxt1);
        spltxt.setVisibility(View.INVISIBLE);
        spltxt1.setVisibility(View.INVISIBLE);
        final Animation fadein = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_out);
        final Animation fadein1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_out1);

        splimg.startAnimation(fadein);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                spltxt.setVisibility(View.VISIBLE);
                spltxt1.setVisibility(View.VISIBLE);
                spltxt.startAnimation(fadein1);
                spltxt1.startAnimation(fadein1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadein1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

}
