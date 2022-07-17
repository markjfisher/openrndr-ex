package fish

import mu.KotlinLogging
import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorHSLa
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.color.ColorXSLa
import org.openrndr.color.ColorXSVa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import org.openrndr.shape.Rectangle
import kotlin.math.cos
import kotlin.math.sin

private val logger = KotlinLogging.logger {}

// simplified runner definition
fun main() = application {
    configure {
        width = 770
        height = 672
        title = "Mark's first demo"
        windowResizable = true
    }

    program {
        extend {
            drawer.stroke = null

            // -- draw hsv swatches
            for (j in 0..7) {
                for (i in 0..31) {
                    drawer.fill = ColorHSVa(360 * (i / 31.0), 0.7, 0.125 + j / 8.0).toRGBa()
                    drawer.rectangle(35.0 + (700 / 32.0) * i, 32.0 + j * 16.0, (700 / 32.0), 16.0)
                }
            }

            // -- draw hsl swatches
            drawer.translate(0.0, 160.0)
            for (j in 0..7) {
                for (i in 0..31) {
                    drawer.fill = ColorHSLa(360 * (i / 31.0), 0.7, 0.125 + j / 9.0).toRGBa()
                    drawer.rectangle(35.0 + (700 / 32.0) * i, 32.0 + j * 16.0, (700 / 32.0), 16.0)
                }
            }

            // -- draw xsv (Kuler) swatches
            drawer.translate(0.0, 160.0)
            for (j in 0..7) {
                for (i in 0..31) {
                    drawer.fill = ColorXSVa(360 * (i / 31.0), 0.7, 0.125 + j / 8.0).toRGBa()
                    drawer.rectangle(35.0 + (700 / 32.0) * i, 32.0 + j * 16.0, (700 / 32.0), 16.0)
                }
            }

            // -- draw xsl (Kuler) swatches
            drawer.translate(0.0, 160.0)
            for (j in 0..7) {
                for (i in 0..31) {
                    drawer.fill = ColorXSLa(360 * (i / 31.0), 0.7, 0.125 + j / 9.0, 1.0).toRGBa()
                    drawer.rectangle(35.0 + (700 / 32.0) * i, 32.0 + j * 16.0, (700 / 32.0), 16.00)
                }
            }
        }
    }
}

// You can do it like this, but the above is lighter, and you just need to refer to the class with "Kt" added to end of this file name, e.g. "fish.ExampleAppKt"
//class ExampleApp {
//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            application {
//                configure {
//                    width = 768
//                    height = 576
//                }
//        }
//    }
//}

