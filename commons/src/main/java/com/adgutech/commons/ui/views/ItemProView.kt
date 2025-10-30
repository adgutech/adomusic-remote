package com.adgutech.commons.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.adgutech.commons.R
import com.adgutech.commons.databinding.ViewItemProBinding

class ItemProView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        val binding = ViewItemProBinding.inflate(LayoutInflater.from(context), this, true)
        context.withStyledAttributes(attrs, R.styleable.ItemProView, 0, 0) {
            if (hasValue(R.styleable.ItemProView_proIcon)) {
                binding.icon.setImageDrawable(getDrawable(R.styleable.ItemProView_proIcon))
            }
            binding.title.text = getText(R.styleable.ItemProView_proTitle)

        }
    }
}