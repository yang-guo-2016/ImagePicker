package io.github.changjiashuai.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import io.github.changjiashuai.library.R;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/2/22 17:58.
 */

public class FolderPopUpWindow extends PopupWindow implements View.OnClickListener {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private ListView listView;
    private final View masker;
    private final View marginView;
    private int marginPx;

    public FolderPopUpWindow(Context context, BaseAdapter adapter) {
        super(context);
        final View view = View.inflate(context, R.layout.folder_popup_window, null);
        masker = view.findViewById(R.id.masker);
        masker.setOnClickListener(this);
        marginView = view.findViewById(R.id.margin);
        marginView.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);  //如果不设置，就是 AnchorView 的宽度
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setAnimationStyle(0);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int maxHeight = view.getHeight() * 6 / 8;
                int realHeight = listView.getHeight();
                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
                listParams.height = realHeight > maxHeight ? maxHeight : realHeight;
                listView.setLayoutParams(listParams);
                LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) marginView.getLayoutParams();
                marginParams.height = marginPx;
                marginView.setLayoutParams(marginParams);
                enterAnimator();
            }
        });
    }

    private void enterAnimator() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(masker, "alpha", 0, 1);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(listView, "translationY", listView.getHeight(), 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void exitAnimator() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(masker, "alpha", 1, 0);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(listView, "translationY", 0, listView.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FolderPopUpWindow.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
    }

    @Override
    public void dismiss() {
        exitAnimator();
    }

    public void setSelection(int selection) {
        listView.setSelection(selection);
    }

    public void setMargin(int marginPx) {
        this.marginPx = marginPx;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}