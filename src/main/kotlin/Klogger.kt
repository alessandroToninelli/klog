import kotlin.concurrent.thread

interface Logger {

    fun debug(msg: ()-> Any?)

    fun error(msg: () -> Any?)

    fun trace(trowable: Throwable?, msg: (() -> Any?) ? = null)
}

internal object Klogger: Logger {

    override fun debug(msg: () -> Any?) {
        System.out.println(makeMsg(msg))
    }

    override fun error(msg: () -> Any?) {
        System.err.println(makeMsg(msg))
    }

    override fun trace(trowable: Throwable?, msg: (() -> Any?)?) {
        val log = msg?.invoke() ?: ""
        System.err.println(makeMsg({ log }))
        trowable?.printStackTrace(System.err)
    }

    private fun makeMsg(msg: (() -> Any?)?): String{

        val index = with(Thread.currentThread().stackTrace){
            this.indexOfFirst { it.methodName == "makeMsg"}
        }

        val stacktrace = Thread.currentThread().stackTrace[index+2]

        val classname = stacktrace.className
        val methodname = stacktrace.methodName
        val line = stacktrace.lineNumber
        return "LOGGER: $classname.$methodname():$line - ${msg?.invoke()}"
    }

}

val klog: Logger = Klogger
