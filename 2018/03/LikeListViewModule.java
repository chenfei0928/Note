import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 为ViewGroup填充子View工具类
 * Created by MrFeng on 2017/3/1.
 */
public class LikeListViewModule {
    /**
     * 将bean列表设置给容器ViewGroup里
     */
    public static <Bean, VG extends ViewGroup> void inject(
            @NonNull VG viewGroup, @Nullable Iterable<Bean> beanIterable, @NonNull Adapter<VG, Bean> adapter) {
        if (beanIterable == null) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        // 目标ViewGroup的view集合
        List<View> views = new ArrayList<>(viewGroup.getChildCount());
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            views.add(childAt);
        }
        // 对view和bean列表进行迭代
        Iterator<View> viewIterator = views.iterator();
        Iterator<Bean> beanIterator = beanIterable.iterator();

        while (viewIterator.hasNext()) {
            View next = viewIterator.next();
            // 判断当前迭代到的view
            if (!adapter.isView(next)) {
                // 如果不是目标itemView
                viewGroup.removeView(next);
                viewIterator.remove();
            } else if (beanIterator.hasNext()) {
                // 如果是目标itemView，并且有一个要适配到的bean
                next.setVisibility(View.VISIBLE);
                adapter.onBindView(next, beanIterator.next());
            } else {
                // 如果没有要适配到的bean，隐藏当前行
                next.setVisibility(View.GONE);
            }
        }
        // 如果还有bean需要显示，实例化View，适配数据，并将Binding缓存
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        while (beanIterator.hasNext()) {
            View view = adapter.onCreateView(inflater, viewGroup);
            viewGroup.addView(view);
            adapter.onBindView(view, beanIterator.next());
        }
    }

    public interface Adapter<VG, Bean> {
        @NonNull
        View onCreateView(@NonNull LayoutInflater inflater, @NonNull VG parent);

	/**
	 * 确定已在列表中出现的View是否是可以被正确填充数据的子View
	 * 该方法用于安全检查
	 */
        boolean isView(@NonNull View view);

        void onBindView(@NonNull View view, @NonNull Bean bean);
    }
}
