/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mei.widget.recyclerview.snaphelper;

import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Implementation of the {@link SnapHelper} supporting snapping in either vertical or horizontal
 * orientation.
 * <p>
 * The implementation will snap the center of the target child view to the center of
 * the attached {@link RecyclerView}. If you intend to change this behavior then override
 * {@link SnapHelper#calculateDistanceToFinalSnap}.
 */
public class LeftLinearSnapHelper extends SnapHelper {

    private static final float INVALID_DISTANCE = 1f;

    // Orientation holders are lazily created per LayoutManager.
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(layoutManager, targetView,
                    getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToStart(layoutManager, targetView,
                    getVerticalHelper(layoutManager));
        } else {
            out[1] = 0;
        }
        return out;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
                                      int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
        // deltaJumps sign comes from the velocity which may not match the order of children in
        // the LayoutManager. To overcome this, we ask for a vector from the LayoutManager to
        // get the direction.
        PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
        if (vectorForEnd == null) {
            // cannot get a vector for the given position.
            return RecyclerView.NO_POSITION;
        }

        int vDeltaJump, hDeltaJump;
        if (layoutManager.canScrollHorizontally()) {
            hDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getHorizontalHelper(layoutManager), velocityX, 0);
            int itemContainCount = layoutManager.getWidth() / getHorizontalHelper(layoutManager).getDecoratedMeasurement(currentView);
            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump;
            }
            if (hDeltaJump > itemContainCount) {
                hDeltaJump = itemContainCount;
            } else if (hDeltaJump < -itemContainCount) {
                hDeltaJump = -itemContainCount;
            }

        } else {
            hDeltaJump = 0;
        }
        if (layoutManager.canScrollVertically()) {
            vDeltaJump = estimateNextPositionDiffForFling(layoutManager,
                    getVerticalHelper(layoutManager), 0, velocityY);
            int itemContainCount = layoutManager.getHeight() / getHorizontalHelper(layoutManager).getDecoratedMeasurementInOther(currentView);
            if (vectorForEnd.y < 0) {
                vDeltaJump = -vDeltaJump;
            }
            if (vDeltaJump > itemContainCount) {
                vDeltaJump = itemContainCount;
            } else if (vDeltaJump < -itemContainCount) {
                vDeltaJump = -itemContainCount;
            }
        } else {
            vDeltaJump = 0;
        }

        int deltaJump = layoutManager.canScrollVertically() ? vDeltaJump : hDeltaJump;
        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION;
        }

        int targetPos = currentPosition + deltaJump;
        if (targetPos < 0) {
            targetPos = 0;
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1;
        }
        return targetPos;
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findStartView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    private int distanceToStart(@NonNull RecyclerView.LayoutManager layoutManager,
                                @NonNull View targetView, OrientationHelper holder) {
        if (checkLastItemNeedVisible(layoutManager, targetView, holder)) {
            return holder.getDecoratedEnd(targetView);
        }
        return holder.getDecoratedStart(targetView);
    }

    /**
     * Estimates a position to which SnapHelper will try to scroll to in response to a fling.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param holder        The {@link OrientationHelper} that is created from the LayoutManager.
     * @param velocityX     The velocity on the x axis.
     * @param velocityY     The velocity on the y axis.
     * @return The diff between the target scroll position and the current position.
     */
    private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager layoutManager,
                                                 OrientationHelper holder, int velocityX, int velocityY) {
        int[] distances = calculateScrollDistance(velocityX, velocityY);
        float distancePerChild = computeDistancePerChild(layoutManager, holder);
        if (distancePerChild <= 0) {
            return 0;
        }
        int distance =
                Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
        return (int) Math.round(distance / distancePerChild);
    }

    /**
     * Return the child view that is currently closest to the center of this parent.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param holder        The relevant {@link OrientationHelper} for the attached {@link RecyclerView}.
     * @return the child view that is currently closest to the center of this parent.
     */
    @Nullable
    private View findStartView(RecyclerView.LayoutManager layoutManager,
                               OrientationHelper holder) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View lastView = layoutManager.getChildAt(childCount - 1);
        if (checkLastItemNeedVisible(layoutManager, lastView, holder)) {
            return lastView;
        }

        View closestChild = null;
        int startest = Integer.MAX_VALUE;

        int index = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childStart = holder.getDecoratedStart(child);

            /** if child is more to start than previous closest, set it as closest  **/
            if (childStart < startest) {
                startest = childStart;
                closestChild = child;
                index = i;
            }
        }

        int width = holder.getDecoratedMeasurement(closestChild);
        int padingStart = Math.abs(holder.getDecoratedStart(closestChild));
        /** 首个item滑动到一半时分别处理 **/
        if ((width - padingStart) < width / 2 && childCount > index + 1) {
            closestChild = layoutManager.getChildAt(index + 1);
        }
        return closestChild;
    }

    /**
     * Computes an average pixel value to pass a single child.
     * <p>
     * Returns a negative value if it cannot be calculated.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param holder        The relevant {@link OrientationHelper} for the attached
     *                      {@link RecyclerView.LayoutManager}.
     * @return A float value that is the average number of pixels needed to scroll by one view in
     * the relevant direction.
     */
    private float computeDistancePerChild(RecyclerView.LayoutManager layoutManager,
                                          OrientationHelper holder) {
        View minPosView = null;
        View maxPosView = null;
        int minPos = Integer.MAX_VALUE;
        int maxPos = Integer.MIN_VALUE;
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return INVALID_DISTANCE;
        }

        for (int i = 0; i < childCount; i++) {
            View child = layoutManager.getChildAt(i);
            final int pos = layoutManager.getPosition(child);
            if (pos == RecyclerView.NO_POSITION) {
                continue;
            }
            if (pos < minPos) {
                minPos = pos;
                minPosView = child;
            }
            if (pos > maxPos) {
                maxPos = pos;
                maxPosView = child;
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE;
        }
        int start = Math.min(holder.getDecoratedStart(minPosView),
                holder.getDecoratedStart(maxPosView));
        int end = Math.max(holder.getDecoratedEnd(minPosView),
                holder.getDecoratedEnd(maxPosView));
        int distance = end - start;
        if (distance == 0) {
            return INVALID_DISTANCE;
        }
        return 1f * distance / ((maxPos - minPos) + 1);
    }


    private boolean checkLastItemNeedVisible(@NonNull RecyclerView.LayoutManager layoutManager,
                                             @NonNull View targetView, OrientationHelper holder) {
        int lastPosition = layoutManager.getPosition(targetView);
        int childEnd = holder.getTotalSpace() - holder.getDecoratedEnd(targetView);
        int width = holder.getDecoratedMeasurement(targetView);
        /**  最后一条item基本完全显示出来了 **/
        return layoutManager.getItemCount() == lastPosition + 1 && Math.abs(childEnd) < width / 6;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null || mHorizontalHelper.getLayoutManager() != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
