package com.hiennguyen.image_picker.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.hiennguyen.image_picker.ui.ImagePickerActivity

abstract class BaseFragment<VB : ViewBinding, Intent: ViewIntent, State: ViewState> : Fragment() {

    protected abstract val viewModel: BaseViewModel<Intent, State>
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        initData()
        bindComponent()
        bindEvent()
        bindData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showLoading(isShow: Boolean) {
        (activity as? ImagePickerActivity)?.showLoading(isShow)
    }

    protected abstract fun State.toUI()
    protected open fun observeState() {
        if (viewModel.state.hasObservers()) viewModel.resetState()
        viewModel.state.observe(viewLifecycleOwner, { it.toUI() } )
    }

    protected open fun initData() {}
    protected open fun bindData() {}
    protected open fun bindComponent() {}
    protected open fun bindEvent() {}
}