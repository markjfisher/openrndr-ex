package fish

import mu.KotlinLogging
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import kotlin.math.cos
import kotlin.math.sin

private val logger = KotlinLogging.logger {}

// simplified runner definition
fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        logger.info { "in program" }
        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/default.otf", 64.0)

        extend {
            drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.shade(0.2))
            drawer.image(image)

            drawer.fill = ColorRGBa.PINK
            drawer.circle(cos(seconds) * width / 2.0 + width / 2.0, sin(0.5 * seconds) * height / 2.0 + height / 2.0, 140.0)

            drawer.fontMap = font
            drawer.fill = ColorRGBa.WHITE
            drawer.text("OPENRNDR", width / 2.0, height / 2.0)
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

