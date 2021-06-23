package com.example.plantdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantdemo.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by MXL on 2020/12/17
 * <br>类描述：<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class XfixedChartFragment extends LazyBaseFragment{
    private static final String TAG = "XfixedChartFragment";
    private LineChartView chartTop; //折线图
    LineChartData data; //折线图数据类
    List<Line> lines; //所有的线条
    private Axis axisY; //Y轴
    private Axis axisX; //X轴
    String[] dataX ;//x分度值
    float[] dataY ; //y轴分度值
    private  float[] potX; //数据点x坐标
    private  float[] potY;//数据点y坐标
    private List<PointValue> mPointValues = new ArrayList<PointValue>(); //样例数据点
    private float minY ;//Y轴坐标最小值
    private float maxY ;//Y轴坐标最大值
    //线段常量
    ValueShape valueShape=ValueShape.CIRCLE; //;//折线图上每个数据点的形状  这里是圆形
    boolean isCubic=false;//曲线是否平
    int strokeWidth=1;   //线条的粗细，默认是3
    boolean isFilled=true;//是否填充曲线的面积
    boolean hasLabels=true;//曲线的数据坐标是否加上备注
    boolean hasLabelsOnlyForSelected=false;//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
    int decimalDigitsNumber=2;//设置显示小数点位数
    boolean hasLines=true;//是否用直线显示。如果为false 则没有曲线只有点显示
    boolean hasPoints=true;//是否显示圆点 如果为false 则没有原点只有点显示
    int pointRadius=3;  //点半径
    String color="#5E8BDF"; //线段颜色
    //数据常量
    int valueLabelBackgroundColor=Color.TRANSPARENT;
    int valueLabelTextSize=6;
    int valueLabelsTextColor=Color.WHITE;  //此处设置坐标点旁边的文字颜色


    public XfixedChartFragment (){};
    /**
     * 工厂方法
     * @param dataX X坐标分度值 如时间序列 [1h,2h,3h]
     * @param dataY Y坐标分度值
     * @return
     */
    public  static  XfixedChartFragment newInstance(String[] dataX,float[] dataY,float potX[],float potY[]){
        XfixedChartFragment fragment=new XfixedChartFragment();
        Bundle bundle=new Bundle();
        bundle.putStringArray("dataX",dataX);
        bundle.putFloatArray("dataY",dataY);
        bundle.putFloatArray("potX",potX);
        bundle.putFloatArray("potY",potY);
        fragment.setArguments(bundle);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chart,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
    }

    @Override
    public View initView() {
        chartTop=getView().findViewById(R.id.chart_view);
        initChart();
        return null;
    }

    @Override
    public void initData() {
      //XY轴的分度值数据
      this.dataX=getArguments().getStringArray("dataX");
      this.dataY=getArguments().getFloatArray("dataY");
      //初始化数据
      this.potX=getArguments().getFloatArray("potX");
      this.potY=getArguments().getFloatArray("potY");
      mPointValues=new ArrayList<>();
      for(int i=0;i<potX.length;i++) mPointValues.add(new PointValue(potX[i],potY[i]));
      //得到min和max
      minY=min(potY);
      maxY=max(potY);
    }

    @Override
    protected void lazyLoad() {

    }

    /**
     * 装填XY轴
     */
    private void initAxisXY(){
        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
        List<AxisValue> values = new ArrayList<>();
        for (int i = 0; i <dataX.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(dataX[i]));
        }
        //X坐标轴
        axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
//	    axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setTextColor(Color.BLACK);//黑色
        axisX.setTextSize(12);//设置字体大小
        axisX.setMaxLabelChars(0); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        axisX.setHasLines(true); //x 轴分割线
        //  Log.d(TAG, "initLineChart: xsize"+mAxisXValues.size());

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        axisY = new Axis();  //Y轴
        axisY.setTextColor(Color.BLACK);
        axisY.setName("");//y轴标注
        axisY.setTextSize(13);//设置字体大小
        axisY.setHasLines(true);
        axisY.setMaxLabelChars(6);//max label length, for example 60
        // 这样添加y轴坐标 就可以固定y轴的数据
        for(int i = 0; i < dataY.length; i++){
            AxisValue value = new AxisValue(dataY[i]);
            values.add(value);
        }
        axisY.setValues(values);
    }


    /**
     * 添加新的线条
     * @param pointValues 数据点
     * @param color 颜色
     */
    public void  addLine(List<PointValue> pointValues,String color){
        Line line = new Line(pointValues).setColor(Color.parseColor(color));  //折线的颜色
        //常量设置
        line.setShape(valueShape);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(isCubic);//曲线是否平
//	    line.setStrokeWidth(3);//线条的粗细，默认是3
        line.setFilled(isFilled);//是否填充曲线的面积
        line.setHasLabels(hasLabels);//曲线的数据坐标是否加上备注
        line.setFormatter(new SimpleLineChartValueFormatter(decimalDigitsNumber));//设置显示小数点
//		line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(hasLines);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(hasPoints);//是否显示圆点 如果为false 则没有原点只有点显示
        line.setStrokeWidth(strokeWidth);
        line.setPointRadius(pointRadius);
        lines.add(line);
        //同步适配器  这里如果开发多条线时可以使用
      //  data.setLines(lines);
      //  chartTop.setLineChartData(data);
    }

    /**
     * 对ChartView进行初始化
     * 在此之前需要对坐标轴数据进行填充 dataX dataY
     */
    public void initChart(){
        initAxisXY();
        lines=new ArrayList<>();
        addLine(mPointValues,color); //默认一条线
        data = new LineChartData();  //设置data
        data.setValueLabelBackgroundColor(Color.TRANSPARENT);
        data.setValueLabelBackgroundAuto(true);
        data.setValueLabelBackgroundEnabled(true);
        data.setValueLabelTextSize(10);
        data.setValueLabelsTextColor(Color.WHITE);  //此处设置坐标点旁边的文字颜色
        data.setLines(lines);
        data.setAxisXBottom(axisX); //默认X底部 y左边
        data.setAxisYLeft(axisY);
        //charttop全局设置 视口设置
        //设置行为属性，支持缩放、滑动以及平移
        chartTop.setInteractive(true);
        chartTop.setZoomType(ZoomType.HORIZONTAL);
        chartTop.setMaxZoom((float) 4);//最大放大比例
        chartTop.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chartTop.setLineChartData(data);
        chartTop.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(chartTop.getMaximumViewport());
        v.bottom = minY;
        v.top = maxY;
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
        chartTop.setMaximumViewport(v);
        v.left = 0;
        v.right= 12;
        chartTop.setCurrentViewport(v);
    }

    private float min(float[] dataY){
        float min =99999999;
        for(int i=0;i<dataY.length;i++){
            min=Math.min(min,dataY[i]);
        }
        return  min;
    }
    private float max(float[] dataY){
        float max =-1;
        for(int i=0;i<dataY.length;i++){
            max=Math.max(max,dataY[i]);
        }
        return  max;
    }

    /**
     * 更换数据
     * @param position
     * @param potX
     * @param potY
     */
    public  void replaceLine(int position,float[] potX,float[] potY){
        List<PointValue> values=new ArrayList<>();
        for(int i=0;i<potX.length;i++) {
            values.add(new PointValue(potX[i], potY[i]));
            Log.d(TAG, "replaceLine: "+potX[i]+" "+potY[i]);
        }
        lines.clear();
        addLine(values,color);
        data.setLines(lines);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        chartTop.setLineChartData(data);
        Viewport v = new Viewport(chartTop.getMaximumViewport());
//        minY=min(potY); //重新获取最值
//        maxY=max(potY);
//        v.bottom = minY;
//        v.top = maxY;
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,这不是我想要的
        chartTop.setMaximumViewport(v);
        v.left = 0;
        v.right=potX.length<12?potX.length:12;
        chartTop.setCurrentViewport(v);
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     *
     */
//    private void initAxisXY(){
//
//    }
}
