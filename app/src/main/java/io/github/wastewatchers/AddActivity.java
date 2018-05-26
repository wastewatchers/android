package io.github.wastewatchers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static float dpScale;

    protected ImageButton mTakePictureButton;
    protected LinearLayout mPictureLayout;
    protected TextView mDetailTextView;
    protected ConstraintLayout mDetailsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dpScale = getResources().getDisplayMetrics().density;

        mPictureLayout = findViewById(R.id.pictureLayout);
        mDetailsContainer = findViewById(R.id.detailsContainer);

        mTakePictureButton = findViewById(R.id.takePictureButton);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });


        mDetailTextView = findViewById(R.id.detailsTextView);
        mDetailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mDetailTextView.getText().toString();
                name = name.substring(1, name.length());
                if(mDetailsContainer.getVisibility() == View.VISIBLE){
                    mDetailsContainer.setVisibility(View.GONE);
                    mDetailTextView.setText("▲" + name);
                }else{
                    mDetailsContainer.setVisibility(View.VISIBLE);
                    mDetailTextView.setText("▼" + name);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            ImageView newImageView = new ImageView(this);
            newImageView.setImageBitmap(imageBitmap);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );

            layoutParams.setMargins(0,0, 8, 0);
            mPictureLayout.addView(newImageView, mPictureLayout.getChildCount() - 1, layoutParams);


        }

    }

    private static int dp2px(int dp) {
        return (int) (dp * dpScale + 0.5f);
    }
}
