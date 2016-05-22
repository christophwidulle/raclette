package de.chefkoch.raclette.android;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.util.AttributeSet;
import de.chefkoch.raclette.Updatable;
import de.chefkoch.raclette.UpdatableViewModel;

/**
 * Created by christophwidulle on 22.05.16.
 */
public class UpdatableViewComposition<T, V extends UpdatableViewModel<T>, B extends ViewDataBinding>
        extends ViewComposition<V, B>
        implements Updatable<T> {

    public UpdatableViewComposition(Context context) {
        super(context);
    }

    public UpdatableViewComposition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpdatableViewComposition(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void update(T item) {
        viewModel().update(item);
    }
}
