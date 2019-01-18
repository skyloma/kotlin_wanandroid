package com.hao.easy.wechat.ui.fragment

import android.os.Bundle
import android.view.View
import com.hao.easy.base.common.RefreshResult
import com.hao.easy.base.ui.BaseListFragment
import com.hao.easy.base.ui.WebActivity
import com.hao.easy.wechat.R
import com.hao.easy.wechat.di.component
import com.hao.easy.wechat.model.Article
import com.hao.easy.wechat.ui.adapter.CommonArticleAdapter
import com.hao.easy.wechat.viewmodel.WechatArticleViewModel
import javax.inject.Inject

/**
 * @author Yang Shihao
 * @date 2018/11/18
 */
class WechatArticleFragment : BaseListFragment<Article, WechatArticleViewModel>() {

    companion object {
        private const val AUTHOR_ID = "AUTHOR_ID"
        fun instance(authorId: Int): WechatArticleFragment {
            val fragment = WechatArticleFragment()
            val bundle = Bundle()
            bundle.putInt(AUTHOR_ID, authorId)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var adapter: CommonArticleAdapter

    override fun getLayoutId() = R.layout.wechat_fragment_wechat_article

    override fun initInject() {
        component().inject(this)
    }

    override fun isLazy() = true

    override fun initView() {
        adapter.showAuthor = false
        super.initView()
    }

    override fun initData() {
        arguments?.apply {
            viewModel.authorId = getInt(AUTHOR_ID, 409)
        }
        super.initData()
        lifecycle.addObserver(viewModel)
    }

    override fun adapter() = adapter

    override fun itemClicked(view: View, item: Article, position: Int) {
        when (view.id) {
            R.id.tvLink -> {
                context?.apply {
                    var title = item.title.replace(Regex("<[^>]+>"), "")
                    WebActivity.start(this, title, item.projectLink)
                }
            }
            R.id.ivFav -> {
                if (item.collect) {
                    viewModel.cancelCollect(item, position)
                } else {
                    viewModel.collect(item, position)
                }
            }
            else -> {
                context?.apply {
                    var title = item.title.replace(Regex("<[^>]+>"), "")
                    WebActivity.start(this, title, item.link)
                }
            }
        }
    }

    override fun refreshFinished(result: RefreshResult) {
        super.refreshFinished(result)
        var weChatFragment = parentFragment as WechatFragment
        weChatFragment.refreshFinished()
    }

    fun refresh() {
        viewModel.invalidate()
    }
}