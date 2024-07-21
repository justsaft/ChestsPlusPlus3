package com.jamesdpeters.chestsplusplus.util.annotation

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

class PartiesAPIAvailable : Condition {

    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        return try {
            Class.forName("com.alessiodp.parties.api.interfaces.PartiesAPI")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

}