package com.mei.userinfocomplement

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntRange
import androidx.fragment.app.commit
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.lightMode
import com.mei.orc.ext.transparentStatusBar
import com.mei.userinfocomplement.step.*
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.network.chick.user.ComplementUserInfoRequest
import launcher.Boom

/**
 *  Created by zzw on 2019-11-27
 *  Des:用户资料完善
 */


class UserInfoComplementActivity : MeiCustomActivity() {

    @Boom
    var loginUserId: Int = 0

    @Boom
    var sessionId: String = ""

    private var genderInt: Int = -1
    private var ageString: String = ""
    private var categoryIds = arrayListOf<Int>()
    private var initName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLauncher.bind(this)
        setContentView(FrameLayout(this).apply {
            id = R.id.user_info_complement_root
            setBackgroundColor(Color.WHITE)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        })

        transparentStatusBar()
        lightMode()

        step(0)
    }


    private fun step(@IntRange(from = 0, to = 3) step: Int) {
        supportFragmentManager.commit(allowStateLoss = true) {
            setCustomAnimations(R.anim.fragment_slide_right_in, R.anim.fragment_slide_left_out)
            val fragment = stepFragment(step)
            replace(R.id.user_info_complement_root, fragment)
        }
    }

    private fun stepFragment(@IntRange(from = 0, to = 3) pos: Int): ComplementBaseFragment = when (pos) {
        0 -> ComplementStepOneFragment().apply {
            genderBack = { sex ->
                genderInt = sex
                step(pos + 1)
            }
        }
        1 -> ComplementStepTwoFragment().apply {
            ageBack = { age ->
                ageString = age
                step(pos + 1)
            }
        }
        2 -> ComplementStepThreeFragment().apply {
            categoryBack = { ids, view ->
                categoryIds.clear()
                categoryIds.addAll(ids)
                if (complementStep == 3) {
                    updateUserData(view)
                } else {
                    step(pos + 1)
                }
            }
        }
        else -> ComplementStepFourFragment(loginUserId, sessionId).apply {
            nameBack = { name, view ->
                initName = name
                updateUserData(view)
            }
        }
    }

    override fun onBackPressed() {
    }

    private fun updateUserData(view: View) {
        showLoading(true)
        view.isEnabled = false
        apiSpiceMgr.executeToastKt(ComplementUserInfoRequest(loginUserId, sessionId).apply {
            gender = genderInt
            birthYear = ageString
            if (initName.isNotEmpty()) {
                nickname = initName
            }
            interestIds = categoryIds.joinToString(",")
        }, success = { resp ->
            if (resp.isSuccess) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(INFO_COMPLEMENT_STATE_KEY, true)
                })
                finish()
            }
        }, finish = {
            showLoading(false)
            view.isEnabled = true
        })
    }


}




