package utils

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log

import android.graphics.Paint.FILTER_BITMAP_FLAG
import java.io.*
import java.text.DecimalFormat


/**
 * Created on 10-10-2019.
 */
class ImageComprasser {

    companion object {

        fun saveBitmapToFile(context: Context, file: File): File? {
            try {

                // BitmapFactory options to downsize the image
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                o.inSampleSize = 6
                // factor of downsizing the image

                var inputStream = FileInputStream(file)
                //Bitmap selectedBitmap = null;
                BitmapFactory.decodeStream(inputStream, null, o)
                inputStream.close()

                // The new size we want to scale to
                val REQUIRED_SIZE = 75

                // Find the correct scale value. It should be the power of 2.
                var scale = 1
                while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                    scale *= 2
                }

                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                inputStream = FileInputStream(file)

                var selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
                inputStream.close()

                //      check the rotation of the image and display it properly
                val exif: ExifInterface
                try {
                    exif = ExifInterface(file.absolutePath)

                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0
                    )
                    Log.d("EXIF", "Exif: $orientation")
                    val matrix = Matrix()
                    if (orientation == 6) {
                        matrix.postRotate(90f)
                        Log.d("EXIF", "Exif: $orientation")
                    } else if (orientation == 3) {
                        matrix.postRotate(180f)
                        Log.d("EXIF", "Exif: $orientation")
                    } else if (orientation == 8) {
                        matrix.postRotate(270f)
                        Log.d("EXIF", "Exif: $orientation")
                    }
                    selectedBitmap = Bitmap.createBitmap(
                        selectedBitmap!!, 0, 0,
                        selectedBitmap.width, selectedBitmap.height, matrix,
                        true
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                // here i override the original image file
                val newFile = File(context.cacheDir, file.name)
                newFile.createNewFile()
                val outputStream = FileOutputStream(newFile)

                selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                return newFile
            } catch (e: Exception) {
                return null
            }
        }

        fun compressImage(context: Context, fileOriginal: File): File {

            val filePath = fileOriginal.absolutePath //getRealPathFromURI(context, imageUri)
            var scaledBitmap: Bitmap? = null

            val options = BitmapFactory.Options()

            //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            //      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(filePath, options)

            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

            //      max Height and width values of the compressed image is taken as 816x612
            val maxHeight = 1024f
            val maxWidth = 1024f
            var imgRatio = (actualWidth / actualHeight).toFloat()
            val maxRatio = maxWidth / maxHeight

            //      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }

            //      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

            //      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

            //      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            try {
                //          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()

            }

            try {
                scaledBitmap =
                    Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )

            //      check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(filePath)

                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0
                )
                Log.d("EXIF", "Exif: $orientation")
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap!!, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val newFile = File(context.cacheDir, fileOriginal.name)
            newFile.createNewFile()
            //val outputStream = FileOutputStream(newFile)

            var out: FileOutputStream? = null
            //val filename = getFilename()
            try {
                out = FileOutputStream(newFile)
                //          write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            Log.e("CompressSize", "Size:" + getFileSize(newFile))

            return newFile
        }

        private fun getRealPathFromURI(context: Context, contentURI: String): String {
            val contentUri = Uri.parse(contentURI)
            val cursor = context.contentResolver.query(contentUri, null, null, null, null)
            if (cursor == null) {
                return contentUri.path!!
            } else {
                cursor!!.moveToFirst()
                val index = cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                return cursor!!.getString(index)
            }
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }

            return inSampleSize
        }

        private val format = DecimalFormat("#.##")
        private val MiB = (1024 * 1024).toLong()
        private val KiB: Long = 1024

        fun getFileSize(file: File): String {

            require(file.isFile) { "Expected a file" }
            val length = file.length()

            if (length > MiB) {
                return format.format(length / MiB) + " MB"
            }
            return if (length > KiB) {
                format.format(length / KiB) + " KB"
            } else format.format(length) + " B"
        }

    }
}
