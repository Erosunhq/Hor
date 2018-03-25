package com.example.sunhq.hor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    List<String> PicList;
    GridView gridView;
    Button t;
    Button size;
    GetImagePath getImagePath;

    // 触摸图片缩放
    private ImageView imageView;
    private int screenWidth, screenHeight;//屏幕宽高
    public static Bitmap bitmap ;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private int mode = 0;
    private float distance;
    private float preDistance;
    private PointF mid = new PointF();//两指中点
    Context context;

    private Button back;

    //初始化圆点高宽,java代码中操作的宽高都是像素值，dp*density转成px
    int width = 40;
    int height = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        t = (Button) findViewById(R.id.t);
        size = (Button) findViewById(R.id.size);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImagePath = new GetImagePath("中式");
                setData();
                setGridView();
                // System.out.print(PicList);
            }
        });
        size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImagePath = new GetImagePath("80A");
                setData();
                setGridView();
            }
        });

        gridView = (GridView) findViewById(R.id.grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bitmap = BitmapFactory.decodeFile(PicList.get(position));
                Picasso.with(MainActivity.this)
                        .load(new File(PicList.get(position)))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .noFade()
                        .into(imageView);

                imageView.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                t.setVisibility(View.GONE);
                size.setVisibility(View.GONE);
            }
        });

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);   //使得imageView消失不见
                back.setVisibility(View.GONE);
                t.setVisibility(View.VISIBLE);
                size.setVisibility(View.VISIBLE);
            }
        });
        context = MainActivity.this;
        imageView = (ImageView) findViewById(R.id.detailView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);   //初始化
        center();//缩小后居中
        imageView.setImageMatrix(matrix);
        matrix.setScale(1f, 1f); //显示

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    // 单指触摸
                    case MotionEvent.ACTION_DOWN:
                        mode = 1;
                        //Toast.makeText(MainActivity.this,"单指点击了图片",Toast.LENGTH_SHORT).show();
                        /*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,height);
                        params.setMargins((int)event.getX(),(int)event.getY(),0,0);
                        view.setLayoutParams(params);*/
                        //添加单指拖动
                        break;
                    // 双指触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        preDistance = getDistance(event);
                        //当两指间距大于10时，计算两指中心点
                        if (preDistance > 10f) {
                            mid = getMid(event);
                            savedMatrix.set(matrix);
                            mode = 2;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = 0;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //当两指缩放，计算缩放比例
                        if (mode == 2) {
                            distance = getDistance(event);
                            if (distance > 10f) {
                                matrix.set(savedMatrix);
                                float scale = distance / preDistance;
                                matrix.postScale(scale, scale, mid.x, mid.y);//缩放比例和中心点坐标
                            }

                        }
                        break;
                }
                view.setImageMatrix(matrix);

                center();  //回弹，令图片居中
                return true;
            }
        });

    }

    /*获取两指之间的距离*/
    private float getDistance(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float distance = (float) Math.sqrt(x * x + y * y);//两点间的距离
        return distance;
    }

    /*使图片居中*/
    private void center() {
        Matrix m = new Matrix();
        m.set(matrix);
        //绘制图片矩形
        //这样rect.left，rect.right,rect.top,rect.bottom分别就就是当前屏幕离图片的边界的距离
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;

        //屏幕的宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm); //获取屏幕分辨率
        screenWidth = dm.widthPixels;  //屏幕宽度
        screenHeight = dm.heightPixels;  //屏幕高度

        //获取ActionBar的高度
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        //计算Y到中心的距离
        if (height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top - actionBarHeight;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < screenHeight) {
            deltaY = imageView.getHeight() - rect.bottom;
        }

        //计算X到中心的距离
        if (width < screenWidth) {
            deltaX = (screenWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < screenWidth) {
            deltaX = screenWidth - rect.right;
        }
        matrix.postTranslate(deltaX, deltaY);

    }

    /*取两指的中心点坐标*/
    public static PointF getMid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    private void setData(){
        PicList = getImagePath.getImagePathFromSD();
    }

    private void setGridView() {
        int size = PicList.size();

        int length = 100;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density + (size - 1)*30);   //计算宽度时一定要加上图片之间的间隔距离,这里设置的是30
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 重点
        gridView.setColumnWidth(itemWidth); // 重点
        gridView.setHorizontalSpacing(30); // 间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 重点

        GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),
                PicList);
        gridView.setAdapter(adapter);
    }


    public class GridViewAdapter extends BaseAdapter {

        Context context;
        List<String> list;

        public GridViewAdapter(Context _context, List<String> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = View.inflate(context,R.layout.list_item,null);
            }
            //ImageView imageView = (ImageView) convertView;

            Picasso.with(context)
                    .load(new File(PicList.get(position)))
                    .resize(200,200)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .noFade()
                    .into((ImageView) convertView);

            return convertView;
        }
    }
}
