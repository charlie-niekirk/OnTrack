package me.cniekirk.ontrack.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // Start default activity for your app
            startActivityAndWait()

            device.waitAndFindObject(By.text("Search for Trains"), 10_000)

            // 2. Scroll through recent searches if they exist
            // The fake data includes 3 recent searches
//            device.wait(
//                Until.hasObject(By.text("Recent Searches")),
//                2_000
//            )?.let {
//                // Scroll the recent searches list
//                val recentSearchesList = device.findObject(By.scrollable(true))
//                recentSearchesList?.let {
//                    it.setGestureMargin(device.displayWidth / 5)
//                    it.scroll(androidx.test.uiautomator.Direction.DOWN, 1.0f)
//                }
//            }
//
//            // 3. Click on station selection to trigger station search
//            // This will load fake stations from FakeStationsRepository
            device.findObject(By.text("Trains departing from"))?.click()
            device.waitForIdle()
//
//            // Wait for station list to appear
//            device.wait(
//                Until.hasObject(By.text("London Paddington")),
//                3_000
//            )
//
//            // 4. Scroll through the station list
//            val stationList = device.findObject(By.scrollable(true))
//            stationList?.let {
//                it.setGestureMargin(device.displayWidth / 5)
//                // Scroll down through fake stations
//                it.scroll(androidx.test.uiautomator.Direction.DOWN, 0.5f)
//                device.waitForIdle()
//                // Scroll back up
//                it.scroll(androidx.test.uiautomator.Direction.UP, 0.3f)
//            }
//
//            // 5. Select a station (Reading)
//            device.findObject(By.text("Reading"))?.click()
//            device.waitForIdle()
//
//            // 6. Back on home screen, click search to view services
//            // This will load fake train services from FakeRealtimeTrainsRepository
//            device.findObject(By.text("Search"))?.click()
//            device.waitForIdle()
//
//            // 7. Wait for service list to load
//            device.wait(
//                Until.hasObject(By.textContains("SERVICE_")),
//                3_000
//            )
//
//            // 8. Scroll through the service list
//            val serviceList = device.findObject(By.scrollable(true))
//            serviceList?.let {
//                it.setGestureMargin(device.displayWidth / 5)
//                // Scroll down through services
//                it.scroll(androidx.test.uiautomator.Direction.DOWN, 0.8f)
//                device.waitForIdle()
//                // Scroll back up
//                it.scroll(androidx.test.uiautomator.Direction.UP, 0.5f)
//                device.waitForIdle()
//            }
//
//            // 9. Click on a service to view details
//            device.findObject(By.textContains("SERVICE_"))?.click()
//            device.waitForIdle()
//
//            // Wait for service details to load
//            device.wait(
//                Until.hasObject(By.text("Great Western Railway")),
//                3_000
//            )
//
//            // 10. Navigate back to home
//            device.pressBack()
//            device.waitForIdle()
//            device.pressBack()
//            device.waitForIdle()
//
//            // 11. Test another journey: Click on a recent search
//            device.findObject(By.text("London Paddington"))?.click()
//            device.waitForIdle()
//
//            // Wait for service list
//            device.wait(
//                Until.hasObject(By.textContains("SERVICE_")),
//                3_000
//            )

            // Done - this exercises the main user journeys with fake data
        }
    }
}