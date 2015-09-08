package basti.coryphaei.com.wheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bowen on 2015/9/7.
 */
public class WheelView extends View {

    //控件大小
    private float controlWidth;
    private float controlHeight;

    //单位格的高度
    private float unitHeight=50;
    //正常情况下字体大小
    private float normalFont = (float) 14.0;
    //选中情况下字体大小
    private float selectedFont = (float) 22.0;
    //显示的item个数
    private int  itemNumber = 7;
    //正常情况下字体的颜色
    private int normalColor = 0xff303030;
    //选中状态下字体的颜色
    private int selectedColor = 0xffff0000;
    //蒙版高度
    private float maskHeight = (float)48.0;
    //是否允许为空
    private boolean noEmpty = true;
    //线的颜色
    private int lineColor = 0xff303030;
    //线的高度
    private float lineWHeight = 2f;
    //是否可选
    private Boolean isEnable = true;

    //正在更改数据，防止ConcurrentModificationException错误
    private boolean isClearing = false;
    //是否正在滑动中
    private boolean isScorlling = false;
    //按下的时间和y坐标
    private int downY = 0;
    private long downTime = 0;

    //短促移动时间
    private int goonTime = 200;
    //短促移动距离
    private int goonDistance = 100;
    //移动距离
    private static final int MOVE_NUMBER = 5;
    //刷新界面
    private static final int REFRESH_VIEW = 0x001;

    private ArrayList<ItemObject> itemList =  new ArrayList<>();
    private ArrayList<String> dataList = new ArrayList<>();
    private Paint linePaint;
    private OnSelectListener onSelectListener = null;


    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置自定义控件
        initAttr(context, attrs);
        //初始化数据
        initData();
    }

    //自定义控件
    private void initAttr(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.WheelView);

        unitHeight = ta.getDimension(R.styleable.WheelView_unitHeight,32);
        normalFont = ta.getDimension(R.styleable.WheelView_normalTextSize,14.0f);
        selectedFont = ta.getDimension(R.styleable.WheelView_selectedTextSize, 22.0f);
        itemNumber = ta.getInt(R.styleable.WheelView_itemNumber, 7);
        maskHeight = ta.getDimension(R.styleable.WheelView_maskHeight, 48);
        noEmpty = ta.getBoolean(R.styleable.WheelView_noEmpty, true);
        lineWHeight = ta.getDimension(R.styleable.WheelView_lineHeight, 2f);
        lineColor = ta.getColor(R.styleable.WheelView_lineColor, 0xff303030);
        isEnable = ta.getBoolean(R.styleable.WheelView_isEnable, true);
        normalColor = ta.getColor(R.styleable.WheelView_normalTextColor, 0xff303030);
        selectedColor = ta.getColor(R.styleable.WheelView_selectedTextColor,0xffff0000);
        ta.recycle();
        controlHeight = itemNumber*unitHeight;
    }

    //初始化数据
    private void initData() {
        itemList.clear();

        isClearing = true;

        ItemObject itemObject;

        for (int i = 0;i<dataList.size();i++){
            itemObject = new ItemObject();
            itemObject.id = i;
            itemObject.itemText = dataList.get(i);
            itemObject.x = 0;
            itemObject.y = (int) (i*unitHeight);
            itemList.add(itemObject);
        }
        isClearing = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawList(canvas);
    }

    //绘制文字
    //注意synchronized用法
    private synchronized void drawList(Canvas canvas) {

        if (isClearing)
        {
            return;
        }

        try {
            for (ItemObject itemObject:itemList){
                itemObject.drawself(canvas);
            }
        }catch (Exception e){

        }
    }

    //画线
    private void drawLine(Canvas canvas) {

        if (linePaint == null){
            linePaint = new Paint();
            linePaint.setColor(lineColor);
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(lineWHeight);
        }

        canvas.drawLine(0, controlHeight / 2 - unitHeight / 2 + 2, controlWidth, controlHeight / 2 - unitHeight / 2 + 2, linePaint);
        canvas.drawLine(0, controlHeight / 2 + unitHeight / 2-2, controlWidth, controlHeight / 2 + unitHeight / 2-2, linePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        controlWidth = getWidth();
        if (controlWidth != 0){
            setMeasuredDimension(getWidth(), (int) (itemNumber*unitHeight));
            controlWidth = getWidth();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setData(ArrayList<String> data){
        dataList  = data;
        initData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable) return true;

        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isScorlling = true;
                downTime = System.currentTimeMillis();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(y-downY);
                break;
            case MotionEvent.ACTION_UP:
                //移动距离
                int move = Math.abs(y-downY);

                //判断短时间内移动的距离
                /*if (System.currentTimeMillis() -downTime < goonTime&&
                        move>goonDistance){
                    goonMove(y - downY);
                }else{
                    actonUp(y-downY);
                }*/

                actonUp(y-downY);
                noEmpty();
                isScorlling = false;
                break;
        }

        return true;
    }

    //设置不能为空
    private void noEmpty() {

        if(!noEmpty){
            return;
        }

        for (ItemObject itemObject : itemList){
            if(itemObject.isSelected()){
                return ;
            }
        }
        int move = (int) itemList.get(0).moveToSelected();
        if (move<0) {
            defaultMove(move);
        }else {
            defaultMove((int) itemList.get(itemList.size()-1).moveToSelected());
        }

        for (ItemObject itemObject : itemList){
            if (itemObject.isSelected()){
                if(onSelectListener != null){
                    onSelectListener.endSelect(itemObject.id,itemObject.itemText);
                    break;
                }
            }
        }
    }

    private void defaultMove(int move) {

        for (ItemObject itemObject : itemList){
            itemObject.newY(move);
        }

        Message message = Message.obtain();
        message.what = REFRESH_VIEW;
        handler.sendMessage(message);

    }

    //移动
    private void actionMove(int move) {

        for (ItemObject itemObject : itemList){
            itemObject.move(move);
        }
        invalidate();
    }

    //松开以后
    private void actonUp(int move) {
        int newMove = 0;
        if (move>0){
            //手指向上滑动
            for (int i = 0;i<itemList.size();i++){
                if (itemList.get(i).isSelected()){
                        newMove = (int) itemList.get(i).moveToSelected();
                    if (onSelectListener != null){
                        onSelectListener.endSelect(itemList.get(i).id, itemList.get(i).itemText);
                    }
                    break;
                }
            }
        }else {
            //手指向下滑动
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).isSelected()) {
                    newMove = (int) itemList.get(i).moveToSelected();
                    if (onSelectListener != null)
                        onSelectListener.endSelect(itemList.get(i).id,
                                itemList.get(i).itemText);
                    break;
                }
            }
        }
        for (ItemObject item : itemList){
            item.newY(move+0);
        }
        slowMove(newMove);
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);
    }

    //缓慢移动
    private synchronized void slowMove(final int newMove) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //判断正负
                int m = newMove >0 ? newMove : newMove * (-1);
                int i = newMove >0 ? 1 : (-1);
                //移动速度
                int speed = 1;
                while(true){
                    m = m - speed;
                    if (m <= 0){
                        for(ItemObject item:itemList){
                            item.newY(m*i);
                        }
                        Message message = Message.obtain();
                        message.what = REFRESH_VIEW;
                        handler.sendMessage(message);
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    for (ItemObject item : itemList){
                        item.newY(speed*i);
                    }
                    Message message = Message.obtain();
                    message.what = REFRESH_VIEW;
                    handler.sendMessage(message);

                    try{
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (ItemObject itemObject :itemList){
                    if (itemObject.isSelected()){
                        if (onSelectListener != null){
                            onSelectListener.endSelect(itemObject.id,itemObject.itemText);
                        }
                        break;
                    }
                }
            }
        }).start();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case REFRESH_VIEW:
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    //设置监听
    public void setOnSelectListener(OnSelectListener mListener){
        onSelectListener = mListener;
    }



    //继续移动一段距离
    private synchronized void goonMove(int move) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                int distance = 0;

                while (distance < unitHeight * MOVE_NUMBER){
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    distance += 10;

                }


            }
        }).start();

    }

    public void resetData(ArrayList<String> data1) {
        setData(data1);
        invalidate();
    }

    public void setDefault(int index) {

        if (index>itemList.size()) return;

        float move = itemList.get(index).moveToSelected();
        defaultMove((int) move);

    }

    public interface OnSelectListener{
        public void endSelect(int id,String text);

        public void selecting(int id,String text);
    }

    //自定义类itemObject
    private class ItemObject {

        public int id = 0;

        public String itemText = "";

        public int x = 0;

        public int y = 0;

        public int move = 0;

        public Paint textPaint = null;

        public Rect textRect = null;

        public ItemObject() {
        }

        public void drawself(Canvas canvas){
            if (textPaint == null){
                textPaint = new Paint();
                textPaint.setAntiAlias(true);
            }

            if (textRect == null){
                textRect = new Rect();
            }
            //被选中
            if (isSelected()){
                textPaint.setColor(selectedColor);
                float distanceFromSelect=moveToSelected();
                //计算当前字体大小
                float textSize = normalFont+(selectedFont-normalFont)*(1-distanceFromSelect/unitHeight);
                textPaint.setTextSize(textSize);
            }else {
                //未被选中
                textPaint.setColor(normalColor);
                textPaint.setTextSize(normalFont);
            }

            textPaint.getTextBounds(itemText,0,itemText.length(),textRect);
            //判断是否在可见范围内
            if (!isVisible()){
                //不可见 直接返回
                return;
            }

            // 绘制内容
            canvas.drawText(itemText, x + controlWidth / 2 - textRect.width()
                            / 2, y + move + unitHeight / 2 + textRect.height() / 2,
                    textPaint);
        }

        private float moveToSelected() {

            return (controlHeight/2-unitHeight/2)-y-move;
        }

        //判断是否在可见范围内
        private boolean isVisible() {
            //两种情况 一种字已经滑到最下，或者字体还在上边还未显示
            if (y+move>controlHeight||y+move+unitHeight/2+textRect.height()/2<0){
                return false;
            }else
                return  true;
        }

        public boolean isSelected(){

/*            Log.i(y+move+"",controlHeight/2-unitHeight/2+2+"");
            Log.i(y+move+"",controlHeight/2+unitHeight/2-2+"");
            Log.i(y+move+unitHeight+"",controlHeight/2-unitHeight/2+2+"");
            Log.i(y+move+unitHeight+"",controlHeight/2+unitHeight/2-2+"");
            Log.i(y + move+"",controlHeight / 2 - unitHeight / 2 + 2+"");
            Log.i(y + move + unitHeight+"",controlHeight / 2
                    + unitHeight / 2 - 2+"");
            Log.i("1","----------------------------");*/
            if (((y+move)>=(controlHeight/2-unitHeight/2+2))&&
                    ((y+move)<=(controlHeight/2+unitHeight/2-2))){

                return true;
            }if (((y+move+unitHeight) >= controlHeight/2 - unitHeight/2+2)&&
                    ((y+move+unitHeight)<=controlHeight/2+unitHeight/2-2))
            {
                return  true;
            }
            //这一段的意义没看懂
            if ((y + move) <= controlHeight / 2 - unitHeight / 2 + 2
                    && (y + move + unitHeight) >= controlHeight / 2
                    + unitHeight / 2 - 2){
                return true;
            }
            return false;
        }

        public void newY(int newMove) {
            move = 0;
            y = y+newMove;
        }

        public void move(int move) {

            this.move = move;

        }
    }

}
