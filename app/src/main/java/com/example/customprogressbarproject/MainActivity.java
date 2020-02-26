package com.example.customprogressbarproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<TestRange> ranges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomProgressView mCustomView = findViewById(R.id.custom_view);

        TestRange range1 = new TestRange(BigDecimal.ONE, BigDecimal.valueOf(0), BigDecimal.valueOf(999));
        TestRange range2 = new TestRange(BigDecimal.valueOf(2), BigDecimal.valueOf(1000), BigDecimal.valueOf(2999));
        TestRange range3 = new TestRange(BigDecimal.valueOf(3), BigDecimal.valueOf(3000), BigDecimal.valueOf(4999));
        TestRange range4 = new TestRange(BigDecimal.valueOf(3), BigDecimal.valueOf(5000), null);

        ranges.add(range1);
        ranges.add(range2);
        ranges.add(range3);
        ranges.add(range4);

        mCustomView.makeHighInterestLabels(ranges);
        mCustomView.progress = 1.5f;

        mCustomView.drawable = getResources().getDrawable(R.drawable.ic_done);

    }
}
