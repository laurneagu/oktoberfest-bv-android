package larc.ludiconprod.Utils.MyProfileUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by alex_ on 18.09.2017.
 */

public class Bar extends View {

    public Bar(Context context) {
        super(context);
    }

    public Bar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xffd4498b);

        int roundStart = 50;
        int hrs = roundStart / 2;

        canvas.drawArc(new RectF(0, 0, canvas.getWidth(), roundStart), 180, 180, true, p);
        canvas.drawRect(0, hrs, canvas.getWidth(), canvas.getHeight() - hrs, p);
        canvas.drawArc(new RectF(0, canvas.getHeight() - roundStart, canvas.getWidth(), canvas.getHeight()), 0, 180, true, p);
    }
}
