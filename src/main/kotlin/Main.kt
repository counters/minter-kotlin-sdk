import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello from Kotlin")
            GlobalScope.launch {
                delay(3000)
                println("Hello from Kotlin Coroutines!")

            }
        }
    }
}