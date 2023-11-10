package com.pathfoss.vivoxia.general;

import android.graphics.Canvas;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class RoundBarChartRenderer extends BarChartRenderer {

    // Create constructor method
    public RoundBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    // Create method to initialize buffers
    @Override
    public void initBuffers() {

        BarData barData = mChart.getBarData();
        mBarBuffers = new BarBuffer[barData.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            IBarDataSet set = barData.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4 * (set.isStacked() ? set.getStackSize() : 1),
                    barData.getDataSetCount(), set.isStacked());
        }
    }

    // Create method to draw chart data set
    protected void drawDataSet(Canvas c, @NonNull IBarDataSet dataSet, int index) {

        initBuffers();

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setBarWidth(mChart.getBarData().getBarWidth());
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        int mRadius = Units.convertDPtoPX(20);
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    else
                        c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom(), mShadowPaint);
                }

                mRenderPaint.setColor(dataSet.getColor(j / 4));
                if (mRadius > 0)
                    c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]), mRadius, mRadius, mRenderPaint);
                else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
            }
        } else {

            mRenderPaint.setColor(dataSet.getColor());

            for (int j = 0; j < buffer.size(); j += 4) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], mRenderPaint);
                }

                if (mRadius > 0)
                    c.drawRoundRect(new RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]), mRadius, mRadius, mRenderPaint);
                else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }
}