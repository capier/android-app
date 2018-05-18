package one.mixin.android.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.MaskTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import one.mixin.android.util.StringSignature

const val ANIMATION_DURATION_SHORTEST = 260L

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, SHOW_IMPLICIT)
}

fun View.fadeIn() {
    this.fadeIn(ANIMATION_DURATION_SHORTEST)
}

fun View.fadeIn(duration: Long) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    ViewCompat.animate(this).alpha(1f).setDuration(duration).setListener(null).start()
}

fun View.fadeOut() {
    this.fadeOut(ANIMATION_DURATION_SHORTEST)
}

fun View.fadeOut(duration: Long) {
    this.alpha = 1f
    ViewCompat.animate(this).alpha(0f).setDuration(duration).setListener(object : ViewPropertyAnimatorListener {
        override fun onAnimationStart(view: View) {
            view.isDrawingCacheEnabled = true
        }

        override fun onAnimationEnd(view: View) {
            view.visibility = View.INVISIBLE
            view.alpha = 0f
            view.isDrawingCacheEnabled = false
        }

        override fun onAnimationCancel(view: View) {}
    })
}

fun View.translationX(value: Float) {
    this.translationX(value, ANIMATION_DURATION_SHORTEST)
}

fun View.translationX(value: Float, duration: Long) {
    ViewCompat.animate(this).setDuration(duration).translationX(value).start()
}

fun View.translationY(value: Float) {
    this.translationY(value, ANIMATION_DURATION_SHORTEST)
}

fun View.translationY(value: Float, duration: Long) {
    ViewCompat.animate(this).setDuration(duration).translationY(value).start()
}

fun View.animateWidth(form: Int, to: Int) {
    this.animateWidth(form, to, one.mixin.android.extension.ANIMATION_DURATION_SHORTEST)
}

fun View.animateWidth(form: Int, to: Int, duration: Long) {
    val anim = ValueAnimator.ofInt(form, to)
    anim.addUpdateListener { valueAnimator ->
        layoutParams.width = valueAnimator.animatedValue as Int
        requestLayout()
    }
    anim.duration = duration
    anim.start()
}

fun View.animateHeight(form: Int, to: Int) {
    this.animateHeight(form, to, one.mixin.android.extension.ANIMATION_DURATION_SHORTEST)
}

fun View.animateHeight(form: Int, to: Int, duration: Long) {
    val anim = ValueAnimator.ofInt(form, to)
    anim.addUpdateListener { valueAnimator ->
        layoutParams.height = valueAnimator.animatedValue as Int
        requestLayout()
    }
    anim.duration = duration
    if (to == 0 || form == 0) {
        anim.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                if (to == 0) {
                    this@animateHeight.visibility = GONE
                }
            }

            override fun onAnimationStart(animation: Animator?) {
                if (form == 0) {
                    this@animateHeight.visibility = VISIBLE
                }
            }
        })
    }
    anim.start()
}

fun View.round(radius: Float) {
    this.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }
    this.clipToOutline = true
}

fun View.round(radius: Int) {
    round(radius.toFloat())
}

fun EditText.showCursor() {
    this.requestFocus()
    this.isCursorVisible = true
}

fun EditText.hideCursor() {
    this.clearFocus()
    this.isCursorVisible = false
}

fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
) = LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)!!

fun TextView.timeAgo(str: String) {
    text = str.timeAgo(context)
}

fun TextView.timeAgoClock(str: String) {
    text = str.timeAgoClock()
}

fun TextView.timeAgoDate(str: String) {
    text = str.timeAgoDate(context)
}

fun ImageView.loadImage(uri: String?, width: Int, height: Int) {
    val multi = MultiTransformation(CropTransformation(width, height))
    Glide.with(this).load(uri).apply(bitmapTransform(multi).dontAnimate()).into(this)
}

fun ImageView.loadImage(uri: String?, requestListener: RequestListener<Drawable?>) {
    Glide.with(this).load(uri).listener(requestListener).into(this)
}

fun ImageView.loadGif(uri: String?, requestListener: RequestListener<GifDrawable?>? = null) {
    if (requestListener != null) {
        Glide.with(this).asGif().load(uri).listener(requestListener).into(this)
    } else {
        Glide.with(this).load(uri).into(this)
    }
}

fun ImageView.loadImage(uri: String?, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank()) {
        if (holder != null) {
            setImageResource(holder)
        }
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions().dontAnimate()).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().placeholder(holder).dontAnimate()).into(this)
    }
}

fun ImageView.loadImageUseMark(uri: String?, @DrawableRes holder: Int, @DrawableRes mark: Int? = null) {
    when {
        uri.isNullOrBlank() -> setImageResource(holder)
        mark == null -> loadImage(uri, holder)
        else -> Glide.with(this).load(uri).apply(RequestOptions().centerCrop().transform(MaskTransformation(mark))
            .signature(StringSignature("$uri$mark"))
            .placeholder(holder).dontAnimate()
        ).into(this)
    }
}

fun ImageView.loadImage(uri: ByteArray?, width: Int, height: Int, @DrawableRes mark: Int? = null) {
    val multi = MultiTransformation(CropTransformation(width, height))
    if (mark == null) {
        Glide.with(this).load(uri).apply(RequestOptions().centerCrop().transform(multi).dontAnimate()).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().centerCrop().transform(multi).transform(MaskTransformation(mark))
            .dontAnimate())
            .into(this)
    }
}

fun ImageView.loadCircleImage(uri: String?, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank()) {
        if (holder != null) {
            setImageResource(holder)
        }
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions().circleCrop()).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().placeholder(holder).circleCrop()).into(this)
    }
}

fun ImageView.loadRoundImage(uri: String?, radius: Int, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank() && holder != null) {
        setImageResource(holder)
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(bitmapTransform(RoundedCornersTransformation(radius, 0))).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().transform(RoundedCornersTransformation(radius, 0))
            .placeholder(holder))
            .into(this)
    }
}