/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
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

package com.bk.widgets.actionbar;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

public class ActionBar extends RelativeLayout implements OnClickListener {
    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    private ImageView mIconView;
    private TextView mTitleView;
    private LinearLayout mActionsView;

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        mIconView = (ImageView) mBarView.findViewById(R.id.actionbar_icon);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ActionBar);
        CharSequence title = a.getString(R.styleable.ActionBar_title);
        if (title != null) {
            setTitle(title);
        }
        a.recycle();
    }

    public void setIcon(int resId) {
        mIconView.setImageResource(resId);
        mIconView.setVisibility(View.VISIBLE);
    }

    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    /**
     * Set the enabled state of the progress bar.
     *
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE},
     *            or {@link View#GONE}.
     */
//   public void setProgressBarVisibility(int visibility)
//   {
//      mProgress.setVisibility(visibility);
//   }
//
//   /**
//    * Returns the visibility status for the progress bar.
//    *
//    * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE},
//    *            or {@link View#GONE}.
//    */
//   public int getProgressBarVisibility()
//   {
//      return mProgress.getVisibility();
//   }

    /**
     * Function to set a click listener for Title TextView
     *
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction();
        }
    }

    /**
     * Adds a list of {@link Action}s.
     *
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     *
     * @param action the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     *
     * @param action the action to add
     * @param index  the position at which to add the action
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(createActionView(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     *
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     *
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mActionsView.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     *
     * @return action count
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }

    public View getActionView(String tag) {
        for (int child = 0; child < mActionsView.getChildCount(); child++) {
            View actionView = mActionsView.getChildAt(child);
            Action action = (Action) actionView.getTag();
            if (tag.equals(action.getTag()))
                return actionView;
        }
        return null;
    }

    public void enableAction(String tag, boolean enabled) {
        View actionView = getActionView(tag);
        if (actionView != null) {
            actionView.setEnabled(enabled);
        }
    }


    /**
     * Inflates a {@link View} with the given {@link Action}.
     *
     * @param action the action to inflate
     * @return a view
     */
    private View createActionView(Action action) {
        View view = action.createView(mInflater, mActionsView);
        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }

    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        public View createView(LayoutInflater inflater, ViewGroup root);

        public void performAction();

        public String getTag();
    }

    public static abstract class AbstractAction implements Action {
        private ActionListener listener;
        private String tag;

        protected AbstractAction(String tag) {
            this.tag = tag;
        }

        protected AbstractAction() {
            this.tag = Integer.toHexString(super.hashCode());
        }

        public ActionListener getListener() {
            return listener;
        }

        public void setListener(ActionListener listener) {
            this.listener = listener;
        }

        @Override
        public void performAction() {
            if (listener != null) {
                listener.onAction();
            }
        }

        public String getTag() {
            return tag;
        }
    }

    public static interface ActionListener {
        void onAction();
    }

    public static class ImageAction extends AbstractAction {
        private int mDrawableId;

        public ImageAction(int drawable,ActionBar.ActionListener actionListener) {
            this.mDrawableId = drawable;
            setListener(actionListener);
        }

        public ImageAction(String tag,int drawable,ActionBar.ActionListener actionListener) {
            super(tag);
            this.mDrawableId = drawable;
            setListener(actionListener);
        }


        @Override
        public View createView(LayoutInflater inflater, ViewGroup root) {
            ImageButton view = (ImageButton) inflater.inflate(R.layout.actionbar_imgitem, root, false);
            view.setImageResource(mDrawableId);
            return view;
        }
    }

    public static class TextAction extends AbstractAction {
        private int mTextId;

        public TextAction(int textId) {
            this.mTextId = textId;
        }

        public TextAction(int textId, ActionBar.ActionListener actionListener) {
            this.mTextId = textId;
            setListener(actionListener);
        }

        public TextAction(String tag, int textId, ActionBar.ActionListener actionListener) {
            super(tag);
            this.mTextId = textId;
            setListener(actionListener);
        }

        @Override
        public View createView(LayoutInflater inflater, ViewGroup root) {
            Button view = (Button) inflater.inflate(R.layout.actionbar_textitem, root, false);
            view.setText(mTextId);
            return view;
        }
    }


//   public static class AnimationAction extends AbstractAction
//   {
//      private int mAnimId;
//
//      public AnimationAction(int animId)
//      {
//         this.mAnimId = animId;
//      }
//
//
//      @Override
//      public View getView(LayoutInflater inflater,ViewGroup root)
//      {
//         ImageButton view = (ImageButton) inflater.inflate(R.layout.actionbar_imgitem, root, false);
//
////         view.setBackgroundResource(mAnimId);
////         Drawable background = view.getBackground();
//
//         return view;
//      }
//   }


}
