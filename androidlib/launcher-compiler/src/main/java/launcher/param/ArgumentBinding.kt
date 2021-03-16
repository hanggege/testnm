package launcher.param

import com.squareup.javapoet.TypeName
import launcher.utils.camelCaseToUppercaseUnderscore

class ArgumentBinding(
        val name: String,
        val key: String,
        val paramType: ParamType,
        val typeName: TypeName,
        val isOptional: Boolean,
        val accessor: FieldAccessor
) {
    val fieldName: String by lazy { camelCaseToUppercaseUnderscore(name) + "_INTENT_KEY" }
}