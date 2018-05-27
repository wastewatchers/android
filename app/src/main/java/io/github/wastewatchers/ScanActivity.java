package io.github.wastewatchers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                String url = "http://" + getString(R.string.serverIP) + "/rating/" + result.getContents() + "/count";
                Log.d("scan", "url: " + url);

                final Context context = this;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("scan", "Response is: " + response);

                            int count = Integer.parseInt(response);

                            if(count < getResources().getInteger(R.integer.minRatings))
                            {
                                startActivity(new Intent(context, AddActivity.class));
                            }
                            else
                            {
                                // start product view activity
                            }
                        }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("scan", "That didn't work: " + error);
                    }
                });

                queue.add(stringRequest);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
