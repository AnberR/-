package a5i5j.com.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zgr on 2018/8/4.
 */

public class netView extends View {
    private int count = 5;//五边形
    private int layerCount = 5;//层数
    private float angle;//每条边对应的圆心角
    private  int centerX;//圆心X
    private  int centerY;//圆心Y
    private float radius;//半径
    private Paint polygonPaint;//边框
    private Paint linePaint;//连线
    private Paint txtPaint;//文字
    private Paint circlePaint;//圆点
    private Paint regionColorPaint;//覆盖区域
    private Paint getRegionColorLinesPaint;//描边
    private Double[] percents = {0.91,0.8,0.56,0.72,0.4};
    private String[] titles = {"品质","生活","医疗","运动","交通"};
    private int valueRadius = 8;
    private List<TouchArea> touchAreaList = new ArrayList<TouchArea>(count);
    private int clickTextPosition;
    private boolean hasAdd;

    public netView(Context context) {
        super(context);
        init(context);
    }

    public netView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public netView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        angle = (float) (Math.PI * 2 / count);
        polygonPaint = new Paint();
        polygonPaint.setColor(ContextCompat.getColor(context, R.color.radarPolygonColor));
        polygonPaint.setAntiAlias(true);
        polygonPaint.setStyle(Paint.Style.STROKE);
        polygonPaint.setStrokeWidth(4f);

        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.radarPolygonColor));
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);

        txtPaint = new Paint();
        txtPaint.setColor(ContextCompat.getColor(context, R.color.radarPolygonColor));
        txtPaint.setAntiAlias(true);
        txtPaint.setStyle(Paint.Style.STROKE);
        txtPaint.setTextSize(40);

        circlePaint = new Paint();
        circlePaint.setColor(ContextCompat.getColor(context, R.color.circle));
        circlePaint.setAntiAlias(true);

        regionColorPaint = new Paint();
        regionColorPaint.setColor(ContextCompat.getColor(context, R.color.Lines));
        regionColorPaint.setStyle(Paint.Style.FILL);
        regionColorPaint.setAntiAlias(true);

        getRegionColorLinesPaint = new Paint();
        getRegionColorLinesPaint.setColor(ContextCompat.getColor(context, R.color.Lines));
        getRegionColorLinesPaint.setAntiAlias(true);
        getRegionColorLinesPaint.setStyle(Paint.Style.STROKE);
        getRegionColorLinesPaint.setStrokeWidth(6f);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(h, w) / 2 * 0.7f;
        centerX = w / 2;
        centerY = h / 2;
        //一旦size发生改变，重新绘制
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);//绘制正五边形
        drawLines(canvas);//绘制连线
        drawText(canvas);//绘制文字
        drawRegion(canvas);//绘制覆盖区域
        drawRegionLines(canvas);//描边
    }

    /**
     * 描边
     * @param canvas
     */
    private void drawRegionLines(Canvas canvas) {
        Path path = new Path();
        float r = radius / layerCount;//每层的间距
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                path.moveTo(centerX, (float) (centerY - r - (radius - r) * percents[i]));
            } else {
                float x = (float) (centerX + Math.sin(angle * i) * (percents[i] * (radius - r) + r));
                float y = (float) (centerY - Math.cos(angle * i) * (percents[i] * (radius - r) + r));
                path.lineTo(x, y);

            }
        }
        path.close();
        canvas.drawPath(path, getRegionColorLinesPaint);
    }

    /**
     * 绘制覆盖区域
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        float r = radius / layerCount;//每层的间距
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                path.moveTo(centerX, (float) (centerY - r - (radius - r) * percents[i]));
                //绘制圆点
                if (clickTextPosition == 1) {
                    canvas.drawCircle(centerX,(float) (centerY - r - (radius - r) * percents[i]),valueRadius,regionColorPaint);
                } else {
                }
                //canvas.drawCircle(centerX,(float) (centerY - r - (radius - r) * percents[i]),valueRadius,regionColorPaint);
            } else {
                float x = (float) (centerX + Math.sin(angle * i) * (percents[i] * (radius - r) + r));
                float y = (float) (centerY - Math.cos(angle * i) * (percents[i] * (radius - r) + r));
                path.lineTo(x, y);
                if (clickTextPosition == i+1){
                    canvas.drawCircle(x,y,valueRadius,regionColorPaint);
                }

            }
        }
        path.close();
        regionColorPaint.setStyle(Paint.Style.STROKE);
        regionColorPaint.setAlpha(100);
        regionColorPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, regionColorPaint);
    }

    /**
     * 绘制文字
     * @param canvas
     */
    private void drawText(Canvas canvas) {

       for (int i = 0;i <count;i++){
           //获取到雷达图最外边的坐标
           float x = (float) (centerX + Math.sin(angle * i) * (radius + 12));
           float y = (float) (centerY - Math.cos(angle * i) * (radius + 12));
           if (angle * i == 0){
               if (!hasAdd){
                   TouchArea touchArea1 = new TouchArea();
                   touchArea1.setL(x - radius / 3);
                   touchArea1.setR(x + radius / 3);
                   touchArea1.setT(y - radius / 3);
                   touchArea1.setB(y + radius / 3);
                   touchAreaList.add(touchArea1);

               }

               //第一个文字位于顶角正上方
               txtPaint.setTextAlign(Paint.Align.CENTER);

               txtPaint.setTextAlign(Paint.Align.LEFT);
               if (clickTextPosition == 1) {
                   txtPaint.setColor(Color.RED);
               } else {
                   txtPaint.setColor(Color.BLACK);
               }
               canvas.drawText(titles[i],x,y,txtPaint);
           }else if(angle * i >= 0 && angle *i < Math.PI/2){
               if (!hasAdd){
                   TouchArea touchArea2 = new TouchArea();
                   touchArea2.setL((x + 18)- radius / 3);
                   touchArea2.setR((x + 18)+ radius / 3);
                   touchArea2.setT((y + 10) - radius / 3);
                   touchArea2.setB((y + 10) + radius / 3);
                   touchAreaList.add(touchArea2);

               }

               if (clickTextPosition == 2) {
                   txtPaint.setColor(Color.RED);
               } else {
                   txtPaint.setColor(Color.BLACK);
               }
               //微调
               canvas.drawText(titles[i], x + 18, y + 10, txtPaint);
           }else if (angle * i >= Math.PI / 2 && angle * i < Math.PI){
               //最右下的文字获取到文字的长、宽，按文字长度百分比向左移
               String txt = titles[i];
               Rect bounds = new Rect();
               txtPaint.getTextBounds(txt,0,txt.length(),bounds);
               float height = bounds.bottom - bounds.top;
               float width = txtPaint.measureText(txt);

               if (!hasAdd){
                   TouchArea touchArea3 = new TouchArea();
                   touchArea3.setL((x - width * 0.4f)-radius / 3);
                   touchArea3.setR((x - width * 0.4f)+radius / 3);
                   touchArea3.setT((y + height + 18)-radius / 3);
                   touchArea3.setB((y + height + 18)+radius / 3);
                   touchAreaList.add(touchArea3);

               }

               if (clickTextPosition == 3) {
                   txtPaint.setColor(Color.RED);
               } else {
                   txtPaint.setColor(Color.BLACK);
               }
               canvas.drawText(txt,x - width * 0.4f ,y + height + 18,txtPaint);
           }else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2){

               //同理最左下的文字获取到文字的长、宽，按文字长度百分比向左移
               String txt = titles[i];
               Rect bounds = new Rect();
               txtPaint.getTextBounds(txt, 0, txt.length(), bounds);
               float width = txtPaint.measureText(txt);
               float height = bounds.bottom - bounds.top;


               if (!hasAdd){
                   TouchArea touchArea4 = new TouchArea();
                   touchArea4.setL((x - width * 0.6f)- radius / 3);
                   touchArea4.setR((x - width * 0.6f)+ radius / 3);
                   touchArea4.setT((y + height + 18)- radius / 3);
                   touchArea4.setB((y + height + 18)+ radius / 3);
                   touchAreaList.add(touchArea4);

               }

               if (clickTextPosition == 4) {
                   txtPaint.setColor(Color.RED);
               } else {
                   txtPaint.setColor(Color.BLACK);
               }
               canvas.drawText(txt, x - width * 0.6f, y + height + 18, txtPaint);
           }else if (angle * i >= 3 * Math.PI / 2 && angle * i < 2 * Math.PI){
               //文字向左移动
               String txt = titles[i];
               float width = txtPaint.measureText(txt);
               if (!hasAdd){
                   TouchArea touchArea5 = new TouchArea();
                   touchArea5.setL((x - width - 18)-radius / 3);
                   touchArea5.setR((x - width - 18)+radius / 3);
                   touchArea5.setT((y + 10)-radius / 3);
                   touchArea5.setB((y + 10)+radius / 3);
                   touchAreaList.add(touchArea5);
               }
               if (clickTextPosition == 5) {
                   txtPaint.setColor(Color.RED);
               } else {
                   txtPaint.setColor(Color.BLACK);
               }
               canvas.drawText(txt, x - width - 18, y + 10, txtPaint);
           }

       }
        hasAdd = true;
    }

    /**
     * 绘制连线
     * @param canvas
     */
    private void drawLines(Canvas canvas) {
        float r = radius / layerCount;
        for (int i = 0 ; i < count ; i++){
            //起始坐标
            float startX = centerX;
            float startY = centerY;
            //末端坐标
            float endX = (float) (centerX + Math.sin(angle * i ) * radius);
            float endY = (float) (centerY - Math.cos(angle * i ) * radius);
            canvas.drawLine(startX,startY,endX,endY,linePaint);
        }
    }

    /**
     * 绘制五边形
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float r = radius / layerCount;
        for (int i = 1;i <= layerCount;i++){
            float curR = r * i;//当前所在层的半径
            path.reset();
            for (int j = 0;j < count;j++){
                if (j == 0){//每一层第一点坐标
                    path.moveTo(centerX,centerY - curR);
                }else {//顺时针记录其余顶角的点坐标
                    float x = (float) (centerX + Math.sin(angle * j) * curR);
                    float y = (float) (centerY - Math.cos(angle * j) * curR);
                    path.lineTo(x,y);
                }
            }
            path.close();
            canvas.drawPath(path,polygonPaint);
        }

    }

    //设置几边形
    public void setCount(int count){
        this.count = count;
        angle = (float) (Math.PI * 2/count);
        invalidate();
    }
    //设置层数
    public void setLayerCount(int layerCount){
        this.layerCount = layerCount;
        invalidate();
    }

    //设置颜色
    public void setTitle( int clickTextPosition){
        this.clickTextPosition = clickTextPosition;
        invalidate();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//                case MotionEvent.ACTION_UP:
//                    int x = (int) event.getX();
//                    int y = (int) event.getY();
//                    for (int i = 0; i < touchAreaList.size(); i++) {
//                        if (x < touchAreaList.get(i).getR() && touchAreaList.get(i).getL() < x && y < touchAreaList.get(i).getB() && touchAreaList.get(i).getT() < y) {
//                           // Log.d("qclqcl", "点击了 " + titles.get(i));
//                            clickTextPosition = i + 1;
//                            postInvalidate();//重新绘制
//                        }
//                    }
//                    break;
//                default:
//                    break;
//        }
//        return true;
//    }

    //用来存放点击区域
    class TouchArea {
        float l;
        float t;
        float r;
        float b;

        public float getL() {
            return l;
        }

        public void setL(float l) {
            this.l = l;
        }

        public float getT() {
            return t;
        }

        public void setT(float t) {
            this.t = t;
        }

        public float getR() {
            return r;
        }

        public void setR(float r) {
            this.r = r;
        }

        public float getB() {
            return b;
        }

        public void setB(float b) {
            this.b = b;
        }
    }
}
