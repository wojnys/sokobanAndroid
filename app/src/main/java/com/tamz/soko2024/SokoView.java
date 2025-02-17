package com.tamz.soko2024;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

public class SokoView extends View {

    Bitmap[] bmp;

    int lW = 10;
    int lH = 10;

    int width;
    int height;

    int pY = 4;
    int pX = 7;
    private int starOverlayPosition = -1; // Position of star under the player, if any

    int[] level = LevelLoader.loadLevelFromJson(this.getContext(), "level1");
    public SokoView(Context context) {
        super(context);
        init(context);
    }

    public SokoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SokoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private long countOccurrences(int value) {
        return Arrays.stream(level).filter(num -> num == value).count();
    }

    void init(Context context) {
        bmp = new Bitmap[6];
        bmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        bmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        bmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.box);
        bmp[3] = BitmapFactory.decodeResource(getResources(), R.drawable.goal);
        bmp[4] = BitmapFactory.decodeResource(getResources(), R.drawable.hero);
        bmp[5] = BitmapFactory.decodeResource(getResources(), R.drawable.boxok);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / lW;
        height = h / lH;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        for (int y = 0; y < lH; y++) {
            for (int x = 0; x < lW; x++) {
                int index = y * lW + x;

                // Draw the star only if it’s not hidden under the player
                if (index == starOverlayPosition) {
                    canvas.drawBitmap(bmp[0], null, // draw empty tile in place of star if player is over it
                            new Rect(x * width, y * height, (x + 1) * width, (y + 1) * height), null);
                } else {
                    canvas.drawBitmap(bmp[level[index]], null,
                            new Rect(x * width, y * height, (x + 1) * width, (y + 1) * height), null);
                }

                // Draw the player on top if it's at the current position
                if (x == pX && y == pY) {
                    canvas.drawBitmap(bmp[4], null,
                            new Rect(x * width, y * height, (x + 1) * width, (y + 1) * height), null);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int touchX = (int) event.getX() / width;
            int touchY = (int) event.getY() / height;

            handlePlayerMovement(touchX, touchY);

            if(this.countOccurrences(3) == 0) {
                Toast.makeText(getContext(), "Congratulations! You won!", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void handlePlayerMovement(int touchX, int touchY) {
        int newX = pX, newY = pY;

        if (touchX == pX && touchY == pY) {
            Toast.makeText(getContext(), "This is your current position", Toast.LENGTH_SHORT).show();
            return;
        }

        if (touchX == pX && touchY == pY - 1) newY -= 1;
        else if (touchX == pX && touchY == pY + 1) newY += 1;
        else if (touchX == pX - 1 && touchY == pY) newX -= 1;
        else if (touchX == pX + 1 && touchY == pY) newX += 1;

        if (newX >= 0 && newX < lW && newY >= 0 && newY < lH) {
            int targetPos = level[newY * lW + newX];

            if (targetPos == 1) {
                Toast.makeText(getContext(), "Cannot move there!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (targetPos == 2 || targetPos == 5) {
                int boxNewX = newX + (newX - pX);
                int boxNewY = newY + (newY - pY);

                if (boxNewX >= 0 && boxNewX < lW && boxNewY >= 0 && boxNewY < lH) {
                    int boxTargetPos = level[boxNewY * lW + boxNewX];

                    if (boxTargetPos == 0 || boxTargetPos == 3) {
                        level[newY * lW + newX] = 0; // Clear the box old position
                        level[boxNewY * lW + boxNewX] = (boxTargetPos == 3) ? 5 : (targetPos == 5 ? 5 : 2); // Set to green box if on star

                        // Clear player pos
                        level[pY * lW + pX] = 0;

                        level[newY * lW + newX] = 4;
                        pX = newX;
                        pY = newY;

                        invalidate();

                    } else {
                        Toast.makeText(getContext(), "Cannot push the box there!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else {
                // clear player previous position
                level[pY * lW + pX] = 0;

                pX = newX;
                pY = newY;
                level[newY * lW + newX] = 4;

                invalidate();
            }
        } else {
            Toast.makeText(getContext(), "Cannot move there!", Toast.LENGTH_SHORT).show();
        }
    }

}

