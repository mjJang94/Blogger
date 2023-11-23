package com.mj.blogger.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.collect
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.common.ktx.toast
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.compose.MainComposeActivity
import com.mj.blogger.ui.compose.MainComposeActivity.Modify
import com.mj.blogger.ui.login.LoginActivity
import com.mj.blogger.ui.main.presentation.MainScreen
import com.mj.blogger.ui.post.PostDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.mj.blogger.ui.main.MainViewModel.UserEvent as Event

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var repository: Repository

    companion object {
        const val EXTRA_MODIFY_DATA = "EXTRA_MODIFY_DATA"

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val composeResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.fetchPostingData()
            }
        }

        val detailResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                when (val intent = result.data) {
                    null -> viewModel.fetchPostingData()
                    else -> {
                        val data = intent.parcelable<Modify>(EXTRA_MODIFY_DATA)
                        composeResult.launch(MainComposeActivity.contract(this, data))
                    }
                }
            }
        }

        viewModel.composeEvent.collect(this) {
            composeResult.launch(MainComposeActivity.contract(this))
        }

        viewModel.openDetailEvent.collect(this) { item ->
            detailResult.launch(PostDetailActivity.contract(this, item))
        }

        viewModel.invalidateEvent.collect(this) { event ->
            val msg = when (event) {
                Event.LOGOUT -> getString(R.string.setting_logout_complete)
                Event.RESIGN -> getString(R.string.setting_resign_complete)
                Event.INVALIDATE -> getString(R.string.main_invalidate_access)
            }
            toast(msg)
            LoginActivity.start(this)
            finish()
        }

        setContent {
            BloggerTheme {
                MainScreen(presenter = viewModel)
            }
        }
    }
}

