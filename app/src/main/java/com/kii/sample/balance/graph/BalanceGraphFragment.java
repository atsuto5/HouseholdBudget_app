package com.kii.sample.balance.graph;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kii.cloud.storage.KiiObject;
import com.kii.sample.balance.Pref;
import com.kii.sample.balance.R;
import com.kii.sample.balance.kiiobject.Field;
import com.kii.sample.balance.list.BalanceListFragment;
import com.kii.sample.balance.title.TitleFragment;
import com.kii.util.KiiObjectParcel;
import com.kii.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Atsuto5 on 2016/08/07.
 */
public class BalanceGraphFragment extends Fragment {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd");
    private static final SimpleDateFormat DATE_FORMAT_WD = new SimpleDateFormat("yyyMMdd");
    private int all_expence;
    private int today_expence;
    private int day_before_expence;
    private int day_2before_expence;
    private int day_3before_expence;
    private int day_4before_expence;
    private int day_5before_expence;
    private int day_6before_expence;
    private int all_income;
    private int today_income;
    private int day_before_income;
    private int day_2before_income;
    private int day_3before_income;
    private int day_4before_income;
    private int day_5before_income;
    private int day_6before_income;
    Calendar cal;
    Date today;

    @Bind(R.id.g_toolbar)
    Toolbar mToolbar;

    public static BalanceGraphFragment newInstance() {
        return new BalanceGraphFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        ButterKnife.bind(this, root);

        Button btn =(Button)root.findViewById(R.id.list_btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ViewUtil.toNextFragment(getFragmentManager(),BalanceListFragment.newInstance(),false);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setTitle(R.string.balance_graph);
        activity.setSupportActionBar(mToolbar);

        createBarChart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        // clear token
        Pref.setStoredAccessToken(getActivity(), "");
        // next fragment
        ViewUtil.toNextFragment(getFragmentManager(), TitleFragment.newInstance(), false);
    }

    private void createBarChart() {
        BarChart barChart = (BarChart) getActivity().findViewById(R.id.bar_chart);
        barChart.setDescription("Household Budget");

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(true);
        barChart.setDrawGridBackground(true);
        barChart.setDrawBarShadow(false);
        barChart.setEnabled(true);

        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setDoubleTapToZoomEnabled(true);

        barChart.setDrawHighlightArrow(true);

        barChart.setScaleEnabled(true);

        barChart.getLegend().setEnabled(true);

        //X軸周り
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(0);

        barChart.setData(createBarChartData());

        barChart.invalidate();
        // アニメーション
        barChart.animateY(1500, Easing.EasingOption.EaseInBack);
        //barChart.setHighlightPerTapEnabled(true);

//        barChart.setClickable(true);
//        barChart.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Log.d("TAG", "TOUCHED IN : ");
//            }
//        });
    }

    // BarChartの設定
    private BarData createBarChartData() {

        cal = Calendar.getInstance();
        today = new Date();
        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        ArrayList<KiiObject> KiiValue = new ArrayList<KiiObject>();
        Bundle bundle = getArguments();

        if( null != bundle ) {
            KiiObjectParcel KiiParcel = bundle.getParcelable("KiiValue");
            KiiValue = KiiParcel.getKiiObject();
            //本日の費用を取得する
            for (KiiObject object : KiiValue) {
                if(DATE_FORMAT.format(today).equals(DATE_FORMAT.format(object.getCreatedTime()))) {
                    if (object.getInt(Field.TYPE)==Field.Type.INCOME){
                        today_income+= object.getInt(Field.AMOUNT);
                    }
                    if (object.getInt(Field.TYPE)==Field.Type.EXPENSE) {
                        today_expence += object.getInt(Field.AMOUNT);
                    }
                }
            }

            //2～7日前の費用を取得する
            for (int i=-1;-7<i;--i){
                cal.setTime(today);
                cal.add(Calendar.DAY_OF_MONTH,i);
                for (KiiObject object : KiiValue) {
                    if (DATE_FORMAT.format(cal.getTime()).equals(DATE_FORMAT.format(object.getCreatedTime()))) {
                        if (i == -1) {
                            if (object.getInt(Field.TYPE) == Field.Type.EXPENSE) {
                                day_before_expence += object.getInt(Field.AMOUNT);
                            }
                            if (object.getInt(Field.TYPE) == Field.Type.INCOME) {
                                day_before_income += object.getInt(Field.AMOUNT);
                            }
                        }
                        if (i == -2) {
                            if (object.getInt(Field.TYPE) == Field.Type.EXPENSE) {
                                day_2before_expence += object.getInt(Field.AMOUNT);
                            }
                            if (object.getInt(Field.TYPE) == Field.Type.INCOME) {
                                day_2before_income += object.getInt(Field.AMOUNT);
                            }
                        }
                        if (i == -3) {
                            if (object.getInt(Field.TYPE) == Field.Type.EXPENSE) {
                                day_3before_expence += object.getInt(Field.AMOUNT);
                            }
                            if (object.getInt(Field.TYPE) == Field.Type.INCOME) {
                                day_3before_income += object.getInt(Field.AMOUNT);
                            }
                        }
                        if (i == -4) {
                            if (object.getInt(Field.TYPE) == Field.Type.EXPENSE) {
                                day_4before_expence += object.getInt(Field.AMOUNT);
                            }
                            if (object.getInt(Field.TYPE) == Field.Type.INCOME) {
                                day_4before_income += object.getInt(Field.AMOUNT);
                            }
                        }
                        if (i == -5) {
                            if (object.getInt(Field.TYPE) == Field.Type.EXPENSE) {
                                day_5before_expence += object.getInt(Field.AMOUNT);
                            }
                            if (object.getInt(Field.TYPE) == Field.Type.INCOME) {
                                day_5before_income += object.getInt(Field.AMOUNT);
                            }
                        }
                        if (i == -6) {
                            if (object.getInt(Field.TYPE) == Field.Type.EXPENSE) {
                                day_6before_expence += object.getInt(Field.AMOUNT);
                            }
                            if (object.getInt(Field.TYPE) == Field.Type.INCOME) {
                                day_6before_income += object.getInt(Field.AMOUNT);
                            }
                        }
                    }
                }
            }
        }

        // X軸
        ArrayList<String> xValues = new ArrayList<>();

        //過去6日分の日付をX軸に追加
        for(int i = -6; i<0; i++){
            cal.setTime(today);
            cal.add(Calendar.DAY_OF_MONTH,i);
            xValues.add(DATE_FORMAT.format(cal.getTime()));
        }
        //終端に現在日付をX軸に追加
        xValues.add(DATE_FORMAT.format(today));

        // valueA
        ArrayList<BarEntry> valuesA = new ArrayList<>();
        valuesA.add(new BarEntry(day_6before_expence, 0));
        valuesA.add(new BarEntry(day_5before_expence, 1));
        valuesA.add(new BarEntry(day_4before_expence, 2));
        valuesA.add(new BarEntry(day_3before_expence, 3));
        valuesA.add(new BarEntry(day_2before_expence, 4));
        valuesA.add(new BarEntry(day_before_expence, 5));
        valuesA.add(new BarEntry(today_expence, 6));

        BarDataSet valuesADataSet = new BarDataSet(valuesA, "spend");
        valuesADataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);

        barDataSets.add(valuesADataSet);

        // valueB
        ArrayList<BarEntry> valuesB = new ArrayList<>();
        valuesB.add(new BarEntry(day_6before_income, 0));
        valuesB.add(new BarEntry(day_5before_income, 1));
        valuesB.add(new BarEntry(day_4before_income, 2));
        valuesB.add(new BarEntry(day_3before_income, 3));
        valuesB.add(new BarEntry(day_2before_income, 4));
        valuesB.add(new BarEntry(day_before_income, 5));
        valuesB.add(new BarEntry(today_income, 6));

        BarDataSet valuesBDataSet = new BarDataSet(valuesB, "income");
        valuesBDataSet.setColor(ColorTemplate.COLORFUL_COLORS[2]);

        barDataSets.add(valuesBDataSet);

        BarData barData = new BarData(xValues, barDataSets);
        return barData;
    }

//    @Override
//    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//        Log.d("TAG", Integer.toString(dataSetIndex));
//    }
//
//    @Override
//    public void onNothingSelected() {
//
//    }
}
