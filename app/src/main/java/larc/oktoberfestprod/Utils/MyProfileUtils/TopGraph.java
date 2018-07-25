package larc.oktoberfestprod.Utils.MyProfileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Random;

import larc.oktoberfestprod.R;

/**
 * Created by alex_ on 18.09.2017.
 */

public class TopGraph extends View implements Iterable {

    private static final Paint BLUE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint BLUE_TEXT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint WHITE = new Paint(Paint.ANTI_ALIAS_FLAG);
    static {
        BLUE.setStyle(Paint.Style.STROKE);
        BLUE.setColor(Color.BLUE);

        WHITE.setStyle(Paint.Style.FILL);
        WHITE.setColor(Color.WHITE);

        BLUE_TEXT.setStyle(Paint.Style.FILL);
        BLUE_TEXT.setColor(Color.BLUE);
        BLUE_TEXT.setTextAlign(Paint.Align.CENTER);
    }

    private int backLayoutId;
    private float[] xPoints;
    private float[] progress;
    private String[] text;
    private String[] randText;
    private float animationTime = 1f;
    private float animation = -1;
    private float randAnim;
    private float randAnimTime = 0.09f;

    public TopGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        BLUE_TEXT.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, context.getResources().getDisplayMetrics()));
        BLUE.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, context.getResources().getDisplayMetrics()));

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopGraph, 0, 0);
        int id = 0;
        try {
            id = a.getResourceId(R.styleable.TopGraph_backgroundLayout, 0);
            if (id == 0) {
                throw new RuntimeException("Set up the app:backgroundLayout attribute to the LinearLayout with the bars!");
            }
            this.backLayoutId = id;
        } finally {
            a.recycle();
        }
    }

    private final void configFromLayout(LinearLayout ll) {
        int size = ll.getChildCount();
        this.xPoints = new float[size];
        this.progress = new float[size];
        this.text = new String[size];
        this.randText = new String[size];
        RelativeLayout p;
        Bar b;
        for (int i = 0; i < size; ++i) {
            this.text[i] = "";
            this.randText[i] = "";
            p = (RelativeLayout) ll.getChildAt(i);
            this.xPoints[i] = p.getX();
            b = (Bar) p.getChildAt(0);
            this.xPoints[i] += b.getX();
            this.xPoints[i] += b.getWidth() / 2;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final LinearLayout ll = (LinearLayout) getRootView().findViewById(this.backLayoutId);
        this.configFromLayout(ll);
        ll.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Layout has happened here.

                        // Don't forget to remove your listener when you are done with it.
                        ll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        configFromLayout(ll);
                    }
                });
    }

    public void setText(int index, String text) throws IndexOutOfBoundsException {
        this.text[index] = text;
    }

    public void setProgress(int index, float progress) throws IndexOutOfBoundsException {
        this.progress[index] = progress;
    }

    private static final Random rand = new Random();
    public void iterate(float tpf) {
        if (this.animation >= this.animationTime) {
            return;
        }

        this.animation += tpf;
        if (this.animation > this.animationTime) {
            this.animation = this.animationTime;
            return;
        }

        this.randAnim -= tpf;
        if (this.randAnim <= 0) {
            this.randAnim += this.randAnimTime;
            for (int i = 0; i < this.randText.length; ++i) {
                this.randText[i] = "" + rand.nextInt(100);
            }
        }
    }
    private void drawLineBetween(Canvas c, PointF fp, PointF sp) {
        PointF cp = new PointF(fp.x + sp.x, fp.y + sp.y);
        cp.set(cp.x / 2, cp.y / 2);

        float w = cp.x - fp.x;
        float h = Math.abs(cp.y - fp.y);
        if (fp.y > sp.y) {
            PointF oc = new PointF(fp.x, cp.y);
            c.drawArc(new RectF(oc.x - w, oc.y - h, oc.x + w, oc.y + h), 0, 90, false, TopGraph.BLUE);

            oc.set(sp.x, cp.y);
            c.drawArc(new RectF(oc.x - w, oc.y - h, oc.x + w, oc.y + h), 180, 90, false, TopGraph.BLUE);
        } else {
            PointF oc = new PointF(fp.x, cp.y);
            c.drawArc(new RectF(oc.x - w, oc.y - h, oc.x + w, oc.y + h), 270, 90, false, TopGraph.BLUE);

            oc.set(sp.x, cp.y);
            c.drawArc(new RectF(oc.x - w, oc.y - h, oc.x + w, oc.y + h), 90, 90, false, TopGraph.BLUE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.animation <= 0) {
            return;
        }

        final int w = super.getWidth();
        final int h = super.getHeight();

        float labelDist = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, super.getResources().getDisplayMetrics());
        float circleDim = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, super.getResources().getDisplayMetrics());
        float circleRad = circleDim / 2;

        float x;
        float y;
        for (int i = 0; i < this.xPoints.length - 1; ++i) {
            x = this.xPoints[i];
            y = this.progress[i] / 100;
            if (i % 2 == 0) {
                y = y * this.animation / this.animationTime;
            } else {
                y += (1 - y) * (1 - this.animation / this.animationTime);
            }
            y = 1 - y;
            y = (h - circleDim) * y + circleRad;

            PointF fp = new PointF(x, y);
            x = this.xPoints[i + 1];
            y = this.progress[i + 1] / 100;
            if (i % 2 == 1) {
                y = y * this.animation / this.animationTime;
            } else {
                y += (1 - y) * (1 - this.animation / this.animationTime);
            }
            y = 1 - y;
            y = (h - circleDim) * y + circleRad;
            PointF sp = new PointF(x, y);
            this.drawLineBetween(canvas, fp, sp);
        }

        String tx;
        for (int i = 0; i < this.xPoints.length; ++i) {
            x = this.xPoints[i];
            tx = this.text[i];
            y = this.progress[i] / 100;
            if (i % 2 == 0) {
                y = y * this.animation / this.animationTime;
            } else {
                y += (1 - y) * (1 - this.animation / this.animationTime);
            }
            y = 1 - y;
            y = (h - circleDim - BLUE.getStrokeWidth()) * y + circleRad + BLUE.getStrokeWidth() / 2;
            RectF rf = new RectF(x - circleRad, y - circleRad, x + circleRad, y + circleRad);


            canvas.drawOval(rf, TopGraph.WHITE);
            canvas.drawOval(rf, TopGraph.BLUE);

            if (this.animation < this.animationTime) {
                tx = this.randText[i];
            }

            canvas.drawText(tx, x + labelDist, y + BLUE.getTextSize() / 2, BLUE_TEXT);
        }
    }
}
