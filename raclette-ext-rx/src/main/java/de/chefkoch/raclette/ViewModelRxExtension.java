package de.chefkoch.raclette;

import android.os.Bundle;
import de.chefkoch.raclette.ViewModelLifecycleState;
import de.chefkoch.raclette.routing.NavigationController;
import de.chefkoch.raclette.rx.RxNavigationControllerExt;
import de.chefkoch.raclette.rx.lifecycle.RxViewModelLifecycle;
import de.chefkoch.raclette.rx.lifecycle.ViewModelLifecycleProvider;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by christophwidulle on 25.06.16.
 */
public class ViewModelRxExtension implements ViewModelLifecycleProvider {

    private RxNavigationControllerExt extNavController;

    private final BehaviorSubject<ViewModelLifecycleState> lifecycleSubject = BehaviorSubject.create();

    public final Observable<ViewModelLifecycleState> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    public final <T> Observable.Transformer<T, T> bindUntilEvent(ViewModelLifecycleState event) {
        return RxViewModelLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxViewModelLifecycle.bind(lifecycleSubject);
    }

    @Override
    public <T> Observable.Transformer<T, T> bindUntilDestroy() {
        return bindUntilEvent(ViewModelLifecycleState.VIEWMODEL_DESTROY);
    }

    public RxNavigationControllerExt navigate() {
        return extNavController;
    }

    void setNavigationController(NavigationController navigationController) {
        this.extNavController = new RxNavigationControllerExt(navigationController);
    }

    //For UnitTests
    void setExtNavigationController(RxNavigationControllerExt extNavController) {
        this.extNavController = extNavController;
    }

    void updateState(ViewModelLifecycleState viewModelLifecycleState) {
        lifecycleSubject.onNext(viewModelLifecycleState);
    }
}
