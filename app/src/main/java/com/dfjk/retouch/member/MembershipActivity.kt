package com.dfjk.retouch.member

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dfjk.retouch.R

class MembershipActivity : AppCompatActivity() {

    private lateinit var switchMember: Switch
    private lateinit var tvStatus: TextView
    private lateinit var btnUpgrade: Button
    private lateinit var membershipStore: MembershipStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        membershipStore = MembershipStore(this)

        switchMember = findViewById(R.id.switchMember)
        tvStatus = findViewById(R.id.tvStatus)
        btnUpgrade = findViewById(R.id.btnUpgrade)

        updateUI()

        switchMember.setOnCheckedChangeListener { _, isChecked ->
            membershipStore.setMember(isChecked)
            updateUI()
            Toast.makeText(
                this,
                if (isChecked) "已开启会员" else "已关闭会员",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnUpgrade.setOnClickListener {
            switchMember.isChecked = true
            Toast.makeText(this, "会员已激活", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val isMember = membershipStore.isMember()
        switchMember.isChecked = isMember

        tvStatus.text = if (isMember) {
            "✓ 会员已激活\n• 无广告体验\n• 无限高清导出\n• 无限视频处理"
        } else {
            "○ 免费版\n• 观看广告使用高级功能\n• 单次高清导出解锁 1 小时"
        }
    }
}