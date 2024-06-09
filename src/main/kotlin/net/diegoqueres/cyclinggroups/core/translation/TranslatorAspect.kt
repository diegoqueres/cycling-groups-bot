package net.diegoqueres.cyclinggroups.core.translation

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Aspect
@Component
class TranslatorAspect(
    private val translator: Translator = Translator()
) {

    @Around("@annotation(net.diegoqueres.cyclinggroups.core.translation.AutoTranslate)")
    fun aroundAutoTranslate(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature: MethodSignature = joinPoint.signature as MethodSignature
        val args = joinPoint.args

        val autoTranslate = methodSignature.method.getAnnotation(AutoTranslate::class.java)
        val argUpdate = autoTranslate.updateArg
        val argNamesToTranslate = autoTranslate.translateArgs.toSet()

        var update: Update? = null
        methodSignature.parameterNames.forEachIndexed { index, parameterName ->
            if (parameterName.equals(argUpdate)) {
                update = args[index] as Update
            }
        }

        val translatedArgs = args.mapIndexed { index, arg ->
            if (argNamesToTranslate.contains(methodSignature.parameterNames[index])) {
                if (arg is Array<*> && arg.all { it is String }) {
                    arg.map { translator.translateMessage(update, it as String) }.toTypedArray()
                } else {
                    translator.translateMessage(update, arg as String)
                }
            } else {
                arg
            }
        }.toTypedArray()

        val result = joinPoint.proceed(translatedArgs)
        return if (isVoidReturnType(methodSignature)) null else result
    }

    private fun isVoidReturnType(methodSignature: MethodSignature): Boolean {
        val returnType = methodSignature.returnType
        return returnType == Void.TYPE || returnType == Unit::class.java
    }

}