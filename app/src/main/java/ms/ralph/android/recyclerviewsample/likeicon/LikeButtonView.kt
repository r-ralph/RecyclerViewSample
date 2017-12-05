package ms.ralph.android.recyclerviewsample.likeicon

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import kotlinx.android.synthetic.main.view_like_button.view.*
import ms.ralph.android.recyclerviewsample.R

class LikeButtonView : FrameLayout, View.OnClickListener {

    internal lateinit var ivStar: ImageView
    internal lateinit var vDotsView: DotsView
    internal lateinit var vCircle: CircleView

    private var isChecked: Boolean = false
    private var animatorSet: AnimatorSet? = null

    private var listener: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private lateinit var drawableStarOff: Drawable
    private lateinit var drawableStarOn: Drawable

    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.view_like_button, this, true)
        ivStar = view.ivStar
        vDotsView = view.vDotsView
        vCircle = view.vCircle

        drawableStarOff = IconDrawable(context, FontAwesomeIcons.fa_star).colorRes(R.color.gray).sizeDp(28)
        drawableStarOn = IconDrawable(context, FontAwesomeIcons.fa_star).colorRes(R.color.red).sizeDp(28)
        ivStar.setImageDrawable(drawableStarOff)
        setOnClickListener(this)
    }

    override fun onClick(v: View) {
        listener?.invoke()
    }

    fun setState(state: Boolean, animation: Boolean = true) {
        isChecked = state

        ivStar.setImageDrawable(if (isChecked) drawableStarOn else drawableStarOff)

        if (!animation) return

        if (animatorSet != null) {
            animatorSet!!.cancel()
        }

        if (isChecked) {
            ivStar.animate().cancel()
            ivStar.scaleX = 0f
            ivStar.scaleY = 0f
            vCircle.innerCircleRadiusProgress = 0f
            vCircle.outerCircleRadiusProgress = 0f
            vDotsView.currentProgress = 0f

            animatorSet = AnimatorSet()

            val outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
            outerCircleAnimator.duration = 250
            outerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

            val innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f)
            innerCircleAnimator.duration = 200
            innerCircleAnimator.startDelay = 200
            innerCircleAnimator.interpolator = DECCELERATE_INTERPOLATOR

            val starScaleYAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_Y, 0.2f, 1f)
            starScaleYAnimator.duration = 350
            starScaleYAnimator.startDelay = 250
            starScaleYAnimator.interpolator = OVERSHOOT_INTERPOLATOR

            val starScaleXAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_X, 0.2f, 1f)
            starScaleXAnimator.duration = 350
            starScaleXAnimator.startDelay = 250
            starScaleXAnimator.interpolator = OVERSHOOT_INTERPOLATOR

            val dotsAnimator = ObjectAnimator.ofFloat<DotsView>(vDotsView, DotsView.DOTS_PROGRESS, 0.0f, 1f)
            dotsAnimator.duration = 900
            dotsAnimator.startDelay = 50
            dotsAnimator.interpolator = ACCELERATE_DECELERATE_INTERPOLATOR

            animatorSet!!.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            )

            animatorSet!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    vCircle.innerCircleRadiusProgress = 0f
                    vCircle.outerCircleRadiusProgress = 0f
                    vDotsView.currentProgress = 0f
                    ivStar.scaleX = 1f
                    ivStar.scaleY = 1f
                }
            })

            animatorSet!!.start()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                ivStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).interpolator = DECCELERATE_INTERPOLATOR
                isPressed = true
            }

            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val isInside = x > 0 && x < width && y > 0 && y < height
                if (isPressed != isInside) {
                    isPressed = isInside
                }
            }

            MotionEvent.ACTION_UP -> {
                ivStar.animate().scaleX(1f).scaleY(1f).interpolator = DECCELERATE_INTERPOLATOR
                if (isPressed) {
                    performClick()
                    isPressed = false
                }
            }
        }
        return true
    }

    fun setOnClickListener(listener: () -> Unit) {
        this.listener = listener
    }

    companion object {
        private val DECCELERATE_INTERPOLATOR = DecelerateInterpolator()
        private val ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()
        private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)
    }
}