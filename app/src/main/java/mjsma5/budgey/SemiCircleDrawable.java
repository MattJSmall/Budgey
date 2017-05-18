package mjsma5.budgey;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by Matts on 17/05/2017.
 */

public class SemiCircleDrawable extends Drawable {

    private Paint paint;
    private RectF rectF;
    private int color;
    private Direction angle;

    public enum Direction
    {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public SemiCircleDrawable() {
        this(Color.BLUE, Direction.LEFT);
    }

    public SemiCircleDrawable(int color, Direction angle) {
        this.color = color;
        this.angle = angle;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        rectF = new RectF();
    }

    public int getColor() {
        return color;
    }

    /**
     * A 32bit color not a color resources.
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        Rect bounds = getBounds();

        if(angle == Direction.LEFT || angle == Direction.RIGHT)
        {
            canvas.scale(2, 1);
            if(angle == Direction.RIGHT)
            {
                canvas.translate(-(bounds.right / 2), 0);
            }
        }
        else
        {
            canvas.scale(1, 2);
            if(angle == Direction.BOTTOM)
            {
                canvas.translate(0, -(bounds.bottom / 2));
            }
        }


        rectF.set(bounds);

        if(angle == Direction.LEFT)
            canvas.drawArc(rectF, 90, 180, true, paint);
        else if(angle == Direction.TOP)
            canvas.drawArc(rectF, -180, 180, true, paint);
        else if(angle == Direction.RIGHT)
            canvas.drawArc(rectF, 270, 180, true, paint);
        else if(angle == Direction.BOTTOM)
            canvas.drawArc(rectF, 0, 180, true, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // Has no effect
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        return 0;
    }

}
