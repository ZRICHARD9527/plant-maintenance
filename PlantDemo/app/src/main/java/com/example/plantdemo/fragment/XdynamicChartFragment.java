package com.example.plantdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.plantdemo.R;
import com.example.plantdemo.entity.Data;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by MXL on 2020/12/20
 * <br>类描述：<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class XdynamicChartFragment extends LazyBaseFragment{

        public  static  String default_path="/AndroidExcelDemo";
        private static final String TAG = "ChartFragment2";
        private  int numberOfLines = 1;         //线条的数量
        private  int maxNumberOfLines = 4;     //最大的线条数据
        private int numberOfPoints = 0;     //当前点的数量
        private LineChartView chart;        //显示线条的自定义View
        private LineChartData data;          // 折线图封装的数据类
        Data[][] randomNumbersTab = new Data[maxNumberOfLines][numberOfPoints]; //二维数组，线的数量和点的数量
        List<List<PointValue>> pointvalues=new ArrayList<>();
        List< List<PointValue>> averData=new ArrayList<>();
        ArrayList<Float> sum =new ArrayList<>();
        private boolean hasAxes = true;       //是否有轴，x和y轴
        private boolean hasAxesNames = true;   //是否有轴的名字
        private boolean hasLines = true;       //是否有线（点和点连接的线）
        private boolean hasPoints = true;       //是否有点（每个值的点）
        //style
        private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
        private boolean isFilled = false;                //是否是填充
        private boolean hasLabels = false;               //每个点是否有名字
        private boolean isCubic = false;                 //是否是立方的，线条是直线还是弧线
        private boolean hasLabelForSelected = true;       //每个点是否可以选择（点击效果）
        private boolean pointsHaveDifferentColor;           //线条的颜色变换
        private boolean hasGradientToTransparent = false;      //是否有梯度的透明
        private float y_max=0;                   //y最大值
        private int x_max=0;                   //x最大值
        private Axis axisX;                  //x轴
        private  Axis axisY;                 //y轴
        private List<AxisValue> mAxisXValues; //x轴数据
//        private  int index;

        public  XdynamicChartFragment(){
//            this.index=index;
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_chart,container,false);
            return  view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            initData();
            initView();
            //  generateValues();
            generateData();
            // chart.setViewportCalculationEnabled(false);
            // chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩，默认是全方位缩放
            // resetViewport(-1,-1,-1,-1);   //设置折线图的显示大小
            initViewport();
        }

        @Override
        public View initView() {
            initChartview();
            return null;
        }

        @Override
        public void initData() {
            y_max=0;
            x_max=numberOfPoints;  //初始值赋值
            generateValues();
            for(int i=0;i<numberOfLines;i++) { //根据线条数生成平均值线
                List<PointValue> pointValues=new ArrayList<>();
                sum.add(new Float(0));
                for(int j=0;j<numberOfPoints;j++) {
                    sum.set(i,new Float(sum.get(i)+randomNumbersTab[i][j].getValue()));
                    //   sum.get(i)= sum.get(i)randomNumbersTab[i][j].getValue(); //计算总数
                    float t=((float)(sum.get(i).intValue()))/(j+1);  //平均值
                    pointValues.add(new PointValue(j,t));
                }
                averData.add(pointValues);
            }
        }

        @Override
        protected void lazyLoad() {

        }
        /**
         * 设置四条线条的数据
         */
        private void generateValues() {
            for (int i = 0; i < maxNumberOfLines; ++i) {
                for (int j = 0; j < numberOfPoints; ++j) {
                    randomNumbersTab[i][j] = new Data();  //
                }
            }
        }
        /**
         * 配置数据
         * 设置每条线
         * 把数据加入控件
         */
        private void generateData() {
            x_max=0;
            pointvalues.clear();
            //存放线条对象的集合
            List<Line> lines = new ArrayList<Line>();
            //把数据设置到线条上面去
            for (int i = 0; i < numberOfLines; ++i) {//遍历每条线
                List<PointValue> values = new ArrayList<PointValue>();  //存放数据点的list
                pointvalues.add(values);
                for (int j = 0; j < numberOfPoints; ++j) {
                    Data data=randomNumbersTab[i][j];
                    values.add(new PointValue(j,Float.valueOf(data.getValue())));
                    //更新最值
                    y_max= Math.max(y_max,Float.valueOf(data.getValue()));
                }
                //设置样式
                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);  //每条线的颜色
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
//            line.setHasGradientToTransparent(hasGradientToTransparent); /
                if (pointsHaveDifferentColor) {  //有颜色变化
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line); //加入线
                lines.add(getAverLine(i));//加入每条线的均值线
            }

            data = new LineChartData(lines);
            //设置x轴
            if (hasAxes) {
                axisX = new Axis();
                axisY = new Axis().setHasLines(true); //x数据
                mAxisXValues = new ArrayList<AxisValue>();
                //初始化x轴长度等于最长线的长度
                int maxlen=0;
                for(int i=0;i<numberOfLines;i++){
                    maxlen=Math.max(maxlen,pointvalues.get(i).size());
                }
                for(int i=0;i<maxlen;i++){
                    mAxisXValues.add(new AxisValue(i).setLabel(i+"s"));
                }
                axisX.setValues(mAxisXValues);
//            // 这样添加y轴坐标 就可以固定y轴的数据
//            List<AxisValue> values = new ArrayList<>();
//            for(int i = 0; i < 5; i++) {
//                AxisValue value = new AxisValue(i*40);
//                values.add(value);
//            }
//            axisY.setValues(values);
                if (hasAxesNames) {
                    axisX.setTextColor(Color.BLACK);//设置x轴字体的颜色
                    axisX.setMaxLabelChars(12);//最多设置几个x轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
                    axisY.setTextColor(Color.BLACK);//设置y轴字体的颜色
                    axisX.setName("数值");
                    axisY.setName("时间");
                }
                //设置xy轴方向
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }
            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);
        }

        /**
         * 得到均值的线
         * @return
         */
        private Line getAverLine(int index){
            averData.get(index).clear();
            sum.set(index,0f);
            Line line=new Line(averData.get(index));
            line.setColor(ChartUtils.COLOR_RED);  //红色
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            return  line;
        }
        /**
         * 触摸监听类
         */
        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        }

        /**
         * 加监听事件
         */
        private void initEvent() {
            chart.setOnValueTouchListener(new ValueTouchListener());
        }


        public void initViewport(){
            Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = y_max;
            //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
            chart.setMaximumViewport(v);
            v.left = 0;
            v.right= 12;
            chart.setCurrentViewport(v);
        }
        /**
         * 设置视口
         * @param top 上部视口 设置<0 则用当前数据最大值 否则重新设置
         * @param right 右端视口 设置<0 则用当前数据最大值 否则用初始数据值
         */
        private void resetViewport(int top,int left,int bottom,int right) {
            // Reset viewport height range to (0,100)
//        final Viewport v = new Viewport(chart.getMaximumViewport());
//        v.bottom = 0;
//        v.top = 100;  //顶部数据范围
//        v.left = 0;
//        v.right = numberOfPoints - 1; //右边数据范围
//        chart.setCurrentViewport(v);
//        v.bottom=0;
//        v.top=y_max;
//        v.right=x_max;
//        chart.setMaximumViewport(v); //最大数据范围
            Viewport v = new Viewport(chart.getMaximumViewport());
            Log.d(TAG, "resetViewport: 视口 left="+v.left+"right="+v.right+"top="+v.top+"bottom="+v.bottom);
            //  v.left=0;
            v.bottom=0;
            v.top = 100;
            //  v.right=12;
            //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
            chart.setMaximumViewport(v);
            if(left<0){
                v.left = 0;
            }else {
                v.left=left;
            }
            if(right<0){
                v.right= 12;
            }else {
                v.right=right;
            }
            chart.setCurrentViewport(v);
        }
        /**
         * 初始化chartview
         */
        private void initChartview(){
            chart=getView().findViewById(R.id.chart_view); //绑定控件
            //设置行为属性，支持缩放、滑动以及平移
            chart.setInteractive(true);
//        chart.setZoomType(ZoomType.HORIZONTAL);
//        chart.setMaxZoom((float) 4);//最大放大比例
            chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            chart.setVisibility(View.VISIBLE);
        }

        /**
         * 动态替换数据
         */
        public  void replaceData(int index,List<Data> datas){
            pointvalues.get(index).clear();
            for (int i = 0; i < datas.size(); i++) {
                pointvalues.get(index).add(new PointValue(i, Float.valueOf(datas.get(i).getValue())));
                //更新x轴
                mAxisXValues.add(new AxisValue(i).setLabel(i+"s"));
                //更新均值
                sum.set(index,new Float(sum.get(index)+datas.get(i).getValue()));
                float t=((float)(sum.get(index).intValue()))/(i+1);  //平均值
                averData.get(index).add(new PointValue(i,t));
            }
            int len=datas.size();
            x_max=len; //更新最大值
            chart.setLineChartData(data);
            resetViewport(-1,len-12>0?len-12:0,-1,len);
//        resetViewport(-1,-1);
        }
        /**
         * 动态添加数据
         */
        public  void addData(int index,Data datas){
            Log.d(TAG, "addData: ");
            int len=pointvalues.get(index).size(); //获取当前长度
            Log.d(TAG, "addData: 当前长度"+len);
            pointvalues.get(index).add(new PointValue(len,Float.valueOf(datas.getValue())));
            //更新x轴
            mAxisXValues.add(new AxisValue(len).setLabel(len+"s"));
            //更新均值线
            sum.set(index,new Float(sum.get(index)+Float.valueOf(datas.getValue())));
            float t=((float)(sum.get(index)))/(len+1);  //平均值
            averData.get(index).add(new PointValue(len,t));
            chart.setLineChartData(data);
            x_max++; //更新最大值
            resetViewport(-1,len-12>0?len-12:0,-1,len);
        }
        /**
         * 动态添加数据
         */
        public  void addDatas(int index,List<Data> datas){
            int len=pointvalues.get(index).size(); //获取当前长度
            for (int i = 0; i < datas.size(); i++)
                pointvalues.get(index).add(new PointValue(len+i,Float.valueOf(datas.get(i).getValue())));
            chart.setLineChartData(data);
            resetViewport(-1,len-12,-1,len);
        }

    /**
     * 重新开始获取
     */
    public void restart(){
        generateData();
    }
}
