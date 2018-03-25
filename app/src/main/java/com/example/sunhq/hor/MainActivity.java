package com.example.sunhq.hor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    List<String> PicList;
    GridView gridView;
    Button t;
    Button size;
    GetImagePath getImagePath;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        t = (Button) findViewById(R.id.t);
        size = (Button) findViewById(R.id.size);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImagePath = new GetImagePath("back");
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
        imageView = (ImageView) findViewById(R.id.detailView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Picasso.with(MainActivity.this)
                        .load(new File(PicList.get(position)))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .noFade()
                        .into(imageView);
            }
        });

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
