package com.dfjk.retouch.photo

import android.graphics.Bitmap
import android.graphics.Color

class RetouchEngine {

    fun processImage(source: Bitmap, mask: Bitmap): Bitmap {
        val result = source.copy(source.config, true)
        val width = source.width
        val height = source.height

        val sourcePixels = IntArray(width * height)
        val maskPixels = IntArray(width * height)
        val resultPixels = IntArray(width * height)

        source.getPixels(sourcePixels, 0, width, 0, 0, width, height)
        mask.getPixels(maskPixels, 0, width, 0, 0, width, height)
        result.getPixels(resultPixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val maskColor = maskPixels[idx]

                if (Color.alpha(maskColor) > 200 && Color.red(maskColor) > 200) {
                    val fillColor = sampleBoundaryColor(sourcePixels, width, height, x, y, maskPixels)
                    resultPixels[idx] = fillColor
                }
            }
        }

        val smoothPixels = gaussianBlur(resultPixels, width, height)
        result.setPixels(smoothPixels, 0, width, 0, 0, width, height)

        return result
    }

    private fun sampleBoundaryColor(
        pixels: IntArray,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        maskPixels: IntArray
    ): Int {
        val sampleRadius = 20
        var r = 0
        var g = 0
        var b = 0
        var count = 0

        for (dy in -sampleRadius..sampleRadius) {
            for (dx in -sampleRadius..sampleRadius) {
                val nx = x + dx
                val ny = y + dy

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    val idx = ny * width + nx
                    val maskColor = maskPixels[idx]

                    if (Color.alpha(maskColor) < 50) {
                        val color = pixels[idx]
                        r += Color.red(color)
                        g += Color.green(color)
                        b += Color.blue(color)
                        count++
                    }
                }
            }
        }

        if (count == 0) return Color.WHITE

        r /= count
        g /= count
        b /= count

        return Color.rgb(r, g, b)
    }

    private fun gaussianBlur(pixels: IntArray, width: Int, height: Int): IntArray {
        val result = pixels.copyOf()
        val kernelRadius = 2

        for (y in kernelRadius until height - kernelRadius) {
            for (x in kernelRadius until width - kernelRadius) {
                var r = 0
                var g = 0
                var b = 0
                var count = 0

                for (ky in -kernelRadius..kernelRadius) {
                    for (kx in -kernelRadius..kernelRadius) {
                        val idx = (y + ky) * width + (x + kx)
                        val color = pixels[idx]
                        r += Color.red(color)
                        g += Color.green(color)
                        b += Color.blue(color)
                        count++
                    }
                }

                r /= count
                g /= count
                b /= count

                result[y * width + x] = Color.rgb(r, g, b)
            }
        }

        return result
    }

    fun cloneArea(
        source: Bitmap,
        sourceX: Int,
        sourceY: Int,
        targetX: Int,
        targetY: Int,
        radius: Int
    ): Bitmap {
        val result = source.copy(source.config, true)

        for (dy in -radius..radius) {
            for (dx in -radius..radius) {
                val srcX = (sourceX + dx).coerceIn(0, source.width - 1)
                val srcY = (sourceY + dy).coerceIn(0, source.height - 1)
                val dstX = (targetX + dx).coerceIn(0, source.width - 1)
                val dstY = (targetY + dy).coerceIn(0, source.height - 1)

                val color = source.getPixel(srcX, srcY)
                result.setPixel(dstX, dstY, color)
            }
        }

        return result
    }
}