package io.github.wastewatchers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {
    static float dpScale;

    List<ColorDrawable> mImages;
    ColorDrawable mRating;
    String mName;
    String mRecycleable;
    String mPlasticType;
    String mWeight;
    List<String> mVendors;
    List<List<Pair<ColorDrawable, ColorDrawable>>> mAlternatives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        dpScale = getResources().getDisplayMetrics().density;

        mImages = Arrays.asList(
            new ColorDrawable(Color.BLUE),
            new ColorDrawable(Color.GREEN),
            new ColorDrawable(Color.YELLOW),
            new ColorDrawable(Color.RED));
        mRating = new ColorDrawable(Color.GREEN);
        mName = "IT'S TRASH";
        mRecycleable = "HELL YEAH BROTHA";
        mPlasticType = "YES";
        mWeight = "A METRIC SHITTON";
        mVendors = Arrays.asList("HERE", "THERE", "SOMEWHERE ELSE");
        mAlternatives = Arrays.asList(
            Arrays.asList(
                new Pair<>(new ColorDrawable(Color.MAGENTA), new ColorDrawable(Color.GREEN)),
                new Pair<>(new ColorDrawable(Color.MAGENTA), new ColorDrawable(Color.YELLOW)),
                new Pair<>(new ColorDrawable(Color.MAGENTA), new ColorDrawable(Color.RED)),
                new Pair<>(new ColorDrawable(Color.MAGENTA), new ColorDrawable(Color.RED))
            ),
            Arrays.asList(
                new Pair<>(new ColorDrawable(Color.CYAN), new ColorDrawable(Color.GREEN)),
                new Pair<>(new ColorDrawable(Color.CYAN), new ColorDrawable(Color.YELLOW)),
                new Pair<>(new ColorDrawable(Color.CYAN), new ColorDrawable(Color.YELLOW)),
                new Pair<>(new ColorDrawable(Color.CYAN), new ColorDrawable(Color.RED))
            ),
            Arrays.asList(
                new Pair<>(new ColorDrawable(Color.DKGRAY), new ColorDrawable(Color.GREEN)),
                new Pair<>(new ColorDrawable(Color.DKGRAY), new ColorDrawable(Color.GREEN)),
                new Pair<>(new ColorDrawable(Color.DKGRAY), new ColorDrawable(Color.GREEN)),
                new Pair<>(new ColorDrawable(Color.DKGRAY), new ColorDrawable(Color.YELLOW))
            )
        );

        // set images
        LinearLayout imageLayout = findViewById(R.id.images);
        for (int i = 0; i < mImages.size(); i++) {
            ColorDrawable drawable = mImages.get(i);
            ImageView img = new ImageView(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px(100), dp2px(80));
            if(i > 0)
                layoutParams.leftMargin = dp2px(8);

            img.setImageDrawable(drawable);
            img.setLayoutParams(layoutParams);

            imageLayout.addView(img);
        }

        // set rating
        ImageView ratingView = findViewById(R.id.ratingBar);
        ratingView.setImageDrawable(mRating);

        // set name
        TextView nameView = findViewById(R.id.productName);
        nameView.setText(mName);

        // set recycleable
        TextView recView = findViewById(R.id.recycleable);
        recView.setText(mRecycleable);

        // set plastic type
        TextView typeView = findViewById(R.id.plasticType);
        typeView.setText(mPlasticType);

        // set weight
        TextView weightView = findViewById(R.id.weight);
        weightView.setText(mWeight);

        // set vendors
        Spinner vendorsSpinner = findViewById(R.id.vendor);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mVendors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorsSpinner.setAdapter(adapter);
        vendorsSpinner.setOnItemSelectedListener(new itemSelectedListener());

        // set alternatives
        setAlternatives(0);

        // make detail text interactable
        final TextView detailView = findViewById(R.id.details);
        final TableLayout detailContainer = findViewById(R.id.detailsContainer);
        detailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = detailView.getText().toString();
                name = name.substring(1, name.length());

                if(detailContainer.getVisibility() == View.VISIBLE){
                    detailContainer.setVisibility(View.GONE);
                    detailView.setText("▼" + name);
                }else{
                    detailContainer.setVisibility(View.VISIBLE);
                    detailView.setText("▲" + name);
                }
            }
        });
    }

    private static int dp2px(int dp) {
        return (int) (dp * dpScale + 0.5f);
    }

    private class itemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            setAlternatives(pos);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

    private void setAlternatives(int index)
    {
        LinearLayout alternativesView = findViewById(R.id.alternatives);
        alternativesView.removeAllViews();

        List<Pair<ColorDrawable, ColorDrawable>> alternatives = mAlternatives.get(index);

        for (int i = 0; i < alternatives.size(); i++) {
            Pair<ColorDrawable, ColorDrawable> alternative = alternatives.get(i);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i > 0)
                layoutParams.leftMargin = dp2px(8);
            layout.setLayoutParams(layoutParams);

            ImageView img = new ImageView(this);
            LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(dp2px(100), dp2px(80));
            img.setImageDrawable(alternative.first);
            img.setLayoutParams(imgLayoutParams);

            ImageView rating = new ImageView(this);
            LinearLayout.LayoutParams ratingLayoutParams = new LinearLayout.LayoutParams(dp2px(100), dp2px(10));
            rating.setImageDrawable(alternative.second);
            rating.setLayoutParams(ratingLayoutParams);

            layout.addView(img);
            layout.addView(rating);

            alternativesView.addView(layout);
        }
    }
}