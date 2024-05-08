package com.example.androidapp.media


enum class MimeTypeEnum(private val type: String) {
    IMAGE("image"),
    VIDEO("video"),
    TEXT("text"),
    SOUND("audio");

    override fun toString(): String {
        return type
    }

    companion object {

        fun getMimeTypeEnumFromString(mimeTypeString: String): MimeTypeEnum? {
            for (mimeType in entries) {
                if (mimeTypeString.contains(mimeType.type)) {
                    return mimeType
                }
            }

            return null
        }
    }
}
