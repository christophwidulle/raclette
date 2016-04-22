package de.chefkoch.raclette;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.chefkoch.raclette.routing.NavRequest;
import de.chefkoch.raclette.routing.NavRouteHandler;


/**
 * Created by christophwidulle on 01.10.15.
 */
public class RacletteLifecycleDelegate<V extends ViewModel, B extends ViewDataBinding> {

    private final Raclette raclette;

    protected V viewModel;
    protected B binding;
    protected final ViewModelBindingConfig<V> viewModelBindingConfig;
    private Context context;

    public RacletteLifecycleDelegate(Raclette raclette, ViewModelBindingConfig<V> viewModelBindingConfig) {
        this.raclette = raclette;
        this.viewModelBindingConfig = viewModelBindingConfig;
    }

    public View onCreateViewBinding(Activity activity) {
        binding = DataBindingUtil.setContentView(activity, viewModelBindingConfig.getLayoutResource());
        return binding.getRoot();
    }

    public View onCreateViewBinding(LayoutInflater inflater, @Nullable ViewGroup parent, boolean attachToParent) {
        binding = DataBindingUtil.inflate(inflater, viewModelBindingConfig.getLayoutResource(), parent, attachToParent);
        return binding.getRoot();
    }

    public void create(Activity activity, Bundle savedInstanceState) {
        create(activity, savedInstanceState, activity.getIntent() != null ? activity.getIntent().getExtras() : null);
    }

    public void create(Context context, Bundle savedInstanceState, Bundle extras) {
        checkViewBindung();
        Bundle params = null;
        if (extras != null) {
            params = ViewModel.Params.from(extras);
        }
        init(context, savedInstanceState, params);
        checkNavRequest(context, extras);
    }

    private void init(Context context, Bundle savedInstanceState, Bundle params) {
        //check for existing
        if (savedInstanceState != null) {
            String viewModelId = savedInstanceState.getString(ViewModel.EXTRA_VIEWMODEL_ID);
            if (viewModelId != null) {
                V viewModel = raclette.getViewModelManager().getViewModel(viewModelId);
                if (viewModel == null) {
                    throw new RacletteException("ViewModel not found but should be alive.");
                } else {
                    this.viewModel = viewModel;
                }
            }
        }
        if (viewModel == null) {
            viewModel = raclette.getViewModelManager().createViewModel(viewModelBindingConfig.getViewModelClass());
            viewModel.setNavigationController(raclette.getNavigationController());
            viewModel.injectParams(params);
            viewModel.onViewModelCreated(params);
        } else {
            viewModel.injectParams(params);
        }
        this.context = context;
        raclette.getContextManager().setCurrentContext(context);
        binding.setVariable(raclette.getViewModelBindingId(), viewModel);
        viewModel.onCreate(params);
    }

    private void checkNavRequest(Context context, Bundle extras) {
        NavRequest navRequest = NavRequest.from(extras);
        if (navRequest != null && context instanceof NavRouteHandler) {
            ((NavRouteHandler) context).onHandle(navRequest);
        }
    }

    public void onDestroy(Activity activity) {
        viewModel.onDestroy();
        if (activity.isFinishing()) {
            destroy();
        }
    }

    public void onDestroy(android.app.Fragment fragment) {
        viewModel.onDestroy();
        if (fragment.getActivity().isFinishing()) {
            destroy();
        } else if (fragment.isRemoving()) {
            //todo can be in backstack. check it.
            destroy();
        }
    }

    public void onStart() {
        raclette.getContextManager().setCurrentContext(context);
        viewModel.onStart();
    }

    public void onPause() {
        viewModel.onPause();
    }

    public void onResume() {
        viewModel.onResume();
    }

    public void onStop() {
        viewModel.onStop();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ViewModel.EXTRA_VIEWMODEL_ID, viewModel.getId());
    }

    protected void destroy() {
        raclette.getViewModelManager().delete(viewModel.getId());
        viewModel.onViewModelDestroyed();
    }

    private void checkViewBindung() {
        if (binding == null) throw new RacletteException("call onCreateViewBinding(...) before.");
    }

    public V viewModel() {
        return viewModel;
    }

    public B binding() {
        return binding;
    }


}
