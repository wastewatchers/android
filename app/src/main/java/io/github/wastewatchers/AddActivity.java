package io.github.wastewatchers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    static final String TAG = "AddActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static float dpScale;


    protected ImageButton mTakePictureButton;
    protected LinearLayout mPictureLayout;
    protected TextView mDetailTextView;
    protected ConstraintLayout mDetailsContainer;
    protected Button mSubmitButton;
    protected EditText mProductNameEditText;
    protected EditText mManufacturerEditText;
    protected RatingBar mGradeRatingBar;
    protected EditText mVendorEditText;
    protected Spinner mPlasticTypeSpinner;
    protected EditText mWeightEditText;
    protected Spinner mRecyclableSpinner;

    protected List<Bitmap> pictures = new ArrayList<>();

    protected RequestQueue mQueue;

    protected String mUuid;

    protected String mEan = "1234567890123";

    protected final String IP = "10.42.0.1:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mEan = getIntent().getStringExtra("EAN");
        mQueue = Volley.newRequestQueue(getBaseContext());
        mUuid = UUID.randomUUID().toString();

        dpScale = getResources().getDisplayMetrics().density;

        mPictureLayout = findViewById(R.id.pictureLayout);
        mDetailsContainer = findViewById(R.id.detailsContainer);
        mProductNameEditText = findViewById(R.id.productNameEditText);
        mManufacturerEditText = findViewById(R.id.manufacturerEditText);
        mGradeRatingBar = findViewById(R.id.gradeRatingBar);
        mVendorEditText = findViewById(R.id.vendorEditText);
        mPlasticTypeSpinner = findViewById(R.id.plasticTypeSpinner);
        mWeightEditText = findViewById(R.id.weightEditText);
        mRecyclableSpinner = findViewById(R.id.recyclableSpinner);

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
                    mDetailTextView.setText("▼" + name);
                }else{
                    mDetailsContainer.setVisibility(View.VISIBLE);
                    mDetailTextView.setText("▲" + name);
                }
            }
        });

        mSubmitButton = findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productUrl = "http://" + IP + "/product/" + mEan + "?" +
                        "name=" + mProductNameEditText.getText() + "&" +
                        "manufacturer=" + mManufacturerEditText.getText();

                StringRequest productRequest = new StringRequest(Request.Method.PUT, productUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "That didn't work: " + error);
                            }
                });

                mQueue.add(productRequest);

                String ratingUrl = "http://" + IP + "/rating/" + mEan + "?" +
                        "uid=" + mUuid + "&" +
                        "grade=" + mGradeRatingBar.getNumStars() + "&" +
                        "vendor=" + mVendorEditText.getText() + "&" +
                        "ptype=" + mPlasticTypeSpinner.getSelectedItem().toString() + "&" +
                        "weight=" + mWeightEditText.getText() + "&" +
                        "recyclable=" + mRecyclableSpinner.getSelectedItem().toString().replace(' ', '_');

                StringRequest ratingRequest = new StringRequest(Request.Method.PUT, ratingUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "That didn't work: " + error);
                    }
                });

                mQueue.add(ratingRequest);

                for(Bitmap bmp : pictures) {
                    String pictuesUrl = "http://" + IP + "/product/" + mEan + "/image";
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    final byte[] pictureData = stream.toByteArray();

                    StringRequest pictureRequest = new StringRequest(Request.Method.PUT, pictuesUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "Response is: " + response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "That didn't work: " + error);
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            //TODO: change type
                            return "image/jpeg";
                        }

                        @Override
                        public byte[] getBody(){
                            return pictureData;
                        }
                    };

                    mQueue.add(pictureRequest);
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
            pictures.add(imageBitmap);

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
