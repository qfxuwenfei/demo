package com.kangjiu.xu.set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
public class ImmersedStatusbarUtils {
     
    /**
     * ��{@link Activity#setContentView}֮�����
     * 
     * @param activity
     *            Ҫʵ�ֵĳ���ʽ״̬����Activity
     * @param titleViewGroup
     *            ͷ���ؼ���ViewGroup,��Ϊnull,�������潫��״̬���ص�
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void initAfterSetContentView(Activity activity,
            View titleViewGroup) {
        if (activity == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (titleViewGroup == null)
            return;
        // ����ͷ���ؼ�ViewGroup��PaddingTop,��ֹ������״̬���ص�
        int statusBarHeight = getStatusBarHeight(activity);
        titleViewGroup.setPadding(0, statusBarHeight, 0, 0);
 
    }
 
    /**
     * ��ȡ״̬���߶�
     * 
     * @param context
     * @return
     */
    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
