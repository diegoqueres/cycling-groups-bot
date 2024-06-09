package net.diegoqueres.cyclinggroups.core.translation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoTranslate(
    val updateArg: String = "update",
    val translateArgs: Array<String> = []
)