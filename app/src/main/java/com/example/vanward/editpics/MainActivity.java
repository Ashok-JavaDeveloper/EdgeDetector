package com.example.vanward.editpics;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int x, y;
    private ImageView imageView;
    private EditText actionText;
    private Dialog dialog;
    private Bitmap origionalBitmap;
    private Bitmap editedBitmap;
    private Button clearAll, back, detectEdge;
    private Button save;
    private TextView textView;
    private static final int MENU_ADD = Menu.FIRST;
//    private static final int MENU_EDIT = Menu.FIRST + 1;
//    private static final int MENU_DELETE = Menu.FIRST + 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        clearAll = (Button) findViewById(R.id.clearAll);
        save = (Button) findViewById(R.id.save);
        back = (Button) findViewById(R.id.back);
        detectEdge = (Button) findViewById(R.id.detectEdge);
        textView = (TextView) findViewById(R.id.axis);

        origionalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.room3);

        detectEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!OpenCVLoader.initDebug()) {
                    Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
                } else {
                    Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
                    detectEdges(origionalBitmap);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(origionalBitmap);
            }
        });

//        BitmapFactory.Options dimensions = new BitmapFactory.Options();
//        dimensions.inJustDecodeBounds = true;
//        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.layout, dimensions);
//        imageView.setImageResource(R.drawable.layout);
//        int height = dimensions.outHeight;
//        int width =  dimensions.outWidth;
//        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
//        android.view.ViewGroup.LayoutParams layoutParams = layout.findViewById(R.id.imageView).getLayoutParams();
//        Log.e("width",""+width);
//        layoutParams.width = width;
//        layoutParams.height = height;
//        Log.e("height",""+height);
//        imageView.setLayoutParams(layoutParams);
//        imageView.setBackgroundColor(Color.CYAN);
//        imageView.requestLayout();
//        setContentView(imageView);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    x = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();
                    textView.setText(x + "-" + y);
                }
                return false;
            }
        });

        imageView.setLongClickable(true);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openContextMenu(imageView);
                return true;
            }
        });
        registerForContextMenu(imageView);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAll.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                imageView.setImageBitmap(origionalBitmap);
                editedBitmap = null;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAll.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                origionalBitmap = editedBitmap;
                SaveBitmapToStorage(origionalBitmap);
                editedBitmap = null;
            }
        });
    }

    private void detectEdges(Bitmap origionalBitmap) {
        Mat image = new Mat();
        Bitmap bitmap = origionalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bitmap, image);
        Mat grayImage = new Mat();
//image.size(), CvType.CV_8UC1
        Mat edges = new Mat(image.size(), CvType.CV_8U);
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.blur(grayImage, edges, new Size(3,3));                  //extra

//        double threshValue = Imgproc.threshold(image, grayImage, 0, 255, Imgproc.THRESH_OTSU);
        double thresholdValue = calculateThreshValue(grayImage);

//        int threshValue = calculateThreshValue(edges);
        Log.e("thresholdValue",thresholdValue+"");
        Imgproc.Canny(grayImage, edges, 50, 170);
//        Log.e("edges", edges.toString());

//        List<MatOfPoint> countours = new ArrayList<>();
//        Imgproc.findContours(edges,countours,hierarchy, Imgproc.MORPH_ERODE,Imgproc.CHAIN_APPROX_SIMPLE);
//        Log.e("countours", countours.toString());

//        Mat lines = new Mat(edges.size(), CvType.CV_8UC1);
//        Imgproc.HoughLinesP(edges, lines, 2, 2, 10);
//        Log.e("lines", lines.toString());

//        Mat dest = new Mat();
//        Core.add(dest, Scalar.all(0), dest);
//
//        edges.copyTo(dest,edges);
//        edges = doBackgroundRemoval(dest);


//        Imgproc.cvtColor(dest, dest, Imgproc.COLOR_BGR2HSV);
//        List<Mat> matList = new ArrayList<>();
//        Core.split(dest, matList);
//
//        Mat hist_hue = new Mat();
//        double average = 0;
//        Imgproc.calcHist(matList, new MatOfInt(0), new Mat(), hist_hue, new MatOfInt(), new MatOfFloat(0, 179));
//        for (int h = 0; h < 180; h++)
//            average += (hist_hue.get(h, 0)[0] * h);
//        average = average / dest.size().height / dest.size().width;
//
//
//        Imgproc.threshold(matList.get(0), dest, average, 179.0, Imgproc.THRESH_BINARY);
//        Imgproc.blur(dest, dest, new Size(5, 5));
//        Imgproc.dilate(dest, dest, new Mat(), new Point(-1, -1), 1);
//        Imgproc.erode(dest, dest, new Mat(), new Point(-1, -1), 3);
//        Imgproc.threshold(dest, dest, 3, 179.0, Imgproc.THRESH_BINARY);
//        Mat newMat = new Mat(dest.size(),CvType.CV_8UC3, new Scalar(255, 255, 255));


        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }

    private double calculateThreshValue(Mat frame)
    {
        Mat hsvImg = new Mat();
        Mat thresholdImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(hsvImg, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);
        double histValue = this.getHistAverage(hsvImg, hsvPlanes.get(0));
        Log.e("histValue",histValue+"");
        return Imgproc.threshold(hsvPlanes.get(0), thresholdImg, histValue, 255, Imgproc.THRESH_OTSU);
    }

    private Mat doBackgroundRemoval(Mat frame)
    {
        // init
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;
//        if (this.inverse.isSelected())
//           int thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(hsvImg, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        double threshValue = this.getHistAverage(hsvImg, hsvPlanes.get(0));

        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);

        Imgproc.blur(thresholdImg, thresholdImg, new Size(5, 5));

        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);

        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC1, new Scalar(255, 255, 255));
        frame.copyTo(foreground, thresholdImg);

        return foreground;
    }

    private double getHistAverage(Mat hsvImg, Mat hueValues)
    {
        // init
        double average = 0.0;
        Mat hist_hue = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        hue.add(hueValues);

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++)
        {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (hist_hue.get(h, 0)[0] * h);
        }

        // return the average hue of the image
        return average / hsvImg.size().height / hsvImg.size().width;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_ADD, Menu.NONE, "Add text");
//        menu.add(0, MENU_EDIT, Menu.NONE, "Edit text");
//        menu.add(0, MENU_DELETE, Menu.NONE, "Remove text");
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_pop_up);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(item.getTitle());

        Button actionButton = (Button) dialog.findViewById(R.id.actionButton);
        actionText = (EditText) dialog.findViewById(R.id.actionText);

        actionButton.setOnClickListener(this);

        dialog.show();

        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        dialog.dismiss();
        clearAll.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        DrawView drawView = new DrawView(this);
        Canvas canvas;
        if (editedBitmap == null) {
            editedBitmap = origionalBitmap.copy(Bitmap.Config.ARGB_8888, true);
//            drawView.setBitmap(actionText.getText().toString(), x, y);
//            canvas = new Canvas(editedBitmap);
//            drawView.draw(canvas);
//            editedBitmap = drawTextToBitmap(MainActivity.this, origionalBitmap.copy(Bitmap.Config.ARGB_8888, true), actionText.getText().toString());
        }
//        else {
            drawView.setBitmap(actionText.getText().toString(), x, y);
            canvas = new Canvas(editedBitmap);
            drawView.draw(canvas);
//            editedBitmap = drawTextToBitmap(MainActivity.this, editedBitmap, actionText.getText().toString());
//        }

        imageView.setImageBitmap(editedBitmap);
    }

//    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String mText) {
//        try {
//            Resources resources = mContext.getResources();
//            float scale = resources.getDisplayMetrics().density;
//
//            Canvas canvas = new Canvas(bitmap);
//            // new antialised Paint
//            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setDither(true);
//            paint.setFilterBitmap(true);
//            paint.setColor(Color.BLACK);
//            paint.setTextSize(20);
//
//            Rect bounds = new Rect();
//            paint.getTextBounds(mText, 0, mText.length(), bounds);
//            canvas.getClipBounds();
//            canvas.drawText(mText, x, y, paint);
//            return bitmap;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    void SaveBitmapToStorage(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "saved" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Saved-" + n + ".jpg";
        File file = new File(dir, fname);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            fos.flush();
        } catch (Exception e) {
            Log.e("Expression", "Error, " + e);
        }
    }
}

class DrawView extends View {
    private String mText;
    Context context;
    private int x, y;
    private Rect bounds;
    private Paint paint;
    private View textView;

    public DrawView(Context context) {
        super(context);
        this.context = context;
        bounds = new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setColor(ContextCompat.getColor(context, R.color.colorCyan));
        textView = new TextView(context);
        Log.e("called in construtor", "touch");
    }

    void setBitmap(String mText, int x, int y) {
        this.mText = mText;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("called", "touch");
        int x_axis = getLeft();
        int y_axis = getTop();
        ((TextView) textView).setText("Edited");
        Log.e("touch event on view", x_axis + " " + y_axis);
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.getTextBounds(mText, 0, mText.length(), bounds);
        canvas.getClipBounds(bounds);
        ((TextView) textView).setText(mText);
        ((TextView) textView).setTextSize(25);
        ((TextView) textView).setTextColor(ContextCompat.getColor(context, R.color.colorCyan));
        // you have to enable setDrawingCacheEnabled, or the getDrawingCache will return null
        textView.setDrawingCacheEnabled(true);

        // we need to setup how big the view should be..which is exactly as big as the canvas
        textView.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY));

        // assign the layout values to the textview
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

        // draw the bitmap from the drawingcache to the canvas
        canvas.drawBitmap(textView.getDrawingCache(), x, y, paint);

        // disable drawing cache
        textView.setDrawingCacheEnabled(false);
//        canvas.drawText(mText, x, y, paint);

    }

   /* @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.e("called","onTouch");
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            x = (int) motionEvent.getX();
            y = (int) motionEvent.getY();
            Log.e("axis",x +"&"+y);
        }

        return true;
    }*/
}