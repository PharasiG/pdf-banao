package io.github.zav.pdfbanao.feature.create.photos

/**
 * The most photos one PDF can be made from (PRD F1). This is also the Android photo picker's own
 * limit (`MediaStore.getPickImagesMaxLimit()`), so the picker can't be asked for more.
 */
internal const val MAX_PHOTOS = 100
