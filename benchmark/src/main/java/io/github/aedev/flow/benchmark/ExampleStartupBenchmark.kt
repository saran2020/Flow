package io.github.aedev.flow.benchmark

import android.graphics.Point
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollFeed() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val metrics = context.resources.displayMetrics
        benchmarkRule.measureRepeated(
            packageName = "io.github.aedev.flow",
            compilationMode = CompilationMode.Full(),
            metrics = listOf(FrameTimingMetric()),
            iterations = 10,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait { intent ->
                    intent.putExtra("extra_skip_onboarding", true)
                }

                device.wait(
                    Until.hasObject(By.res("feed")),
                    5_000,
                )
                device.waitForIdle(1_500)
            },
            measureBlock = {
                val feed = device.findObject(By.res("feed")) ?: return@measureRepeated
                feed.setGestureMargin(device.displayWidth / 5)
                feed.drag(Point(feed.visibleCenter.x, -5000), 10000 * metrics.density.toInt())
                device.waitForIdle(2_000)
            },
        )
    }
}
