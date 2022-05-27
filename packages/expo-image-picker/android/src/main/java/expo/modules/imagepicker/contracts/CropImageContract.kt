package expo.modules.imagepicker.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageOptions
import expo.modules.imagepicker.ImagePickerOptions
import expo.modules.imagepicker.MediaType
import expo.modules.imagepicker.PickingSource
import expo.modules.imagepicker.createOutputFile
import expo.modules.imagepicker.toBitmapCompressFormat
import expo.modules.imagepicker.toFileExtension

internal class CropImageContract(
  private val sourceUri: Uri,
  private val options: ImagePickerOptions,
  private val pickingSource: PickingSource
) : ImagePickerContract() {
//  TODO(@bbarthec): Preserve exif metadata when cropping
//  val exifDataHandler = ExifDataHandler(sourceUri)
//
//  class ExifDataHandler(private val uri: Uri) {
//    fun copyExifData(newUri: Uri, contentResolver: ContentResolver) {
//      if (uri == newUri) {
//        return
//      }
//      contentResolver.openInputStream(uri)?.use { input ->
//        val oldExif = ExifInterface(input)
//        newUri.path?.let {
//          val newExif = ExifInterface(it)
//          for (exifTag in ImagePickerConstants.EXIF_TAGS.values.flatten()) {
//            val value = oldExif.getAttribute(exifTag)
//            if (value != null &&
//              exifTag != ExifInterface.TAG_IMAGE_LENGTH &&
//              exifTag != ExifInterface.TAG_IMAGE_WIDTH &&
//              exifTag != ExifInterface.TAG_PIXEL_X_DIMENSION &&
//              exifTag != ExifInterface.TAG_PIXEL_Y_DIMENSION &&
//              exifTag != ExifInterface.TAG_ORIENTATION
//            ) {
//              newExif.setAttribute(exifTag, value)
//            }
//          }
//          try {
//            newExif.saveAttributes()
//          } catch (e: IOException) {
//            Log.w(ImagePickerConstants.TAG, "Couldn't save Exif data: ${e.message}", e)
//          }
//        }
//      }
//    }
//  }

  override fun createIntent(context: Context, input: Any?) = Intent(context, CropImageActivity::class.java).apply {
    // for [IMAGE_LIBRARY] we need to create a new file as up to this point we've been operating on the original media asset
    // for [CAMERA] we do not have to do it as it's already been created at the beginning of the picking process
    val needCreateNewFile = pickingSource == PickingSource.IMAGE_LIBRARY
    val mediaType = expo.modules.imagepicker.getType(context.contentResolver, sourceUri)

    val compressFormat = mediaType.toBitmapCompressFormat()

    val outputUri: Uri = if (needCreateNewFile) {
      createOutputFile(context.cacheDir, compressFormat.toFileExtension()).toUri()
    } else {
      sourceUri
    }

    putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundleOf(
      CropImage.CROP_IMAGE_EXTRA_SOURCE to sourceUri,
      CropImage.CROP_IMAGE_EXTRA_OPTIONS to CropImageOptions().apply {
        outputCompressFormat = compressFormat
        outputCompressQuality = (this@CropImageContract.options.quality * 100).toInt()
        this.outputUri = outputUri

        this@CropImageContract.options.aspect?.let { (x, y) ->
          aspectRatioX = x
          aspectRatioY = y
          fixAspectRatio = true
          initialCropWindowPaddingRatio = 0f
        }

        validate()
      }
    ))
  }

  override fun parseResult(resultCode: Int, intent: Intent?): ImagePickerContractResult {
    val result = intent?.getParcelableExtra<CropImage.ActivityResult?>(CropImage.CROP_IMAGE_EXTRA_RESULT)
    return if (resultCode == Activity.RESULT_CANCELED || result == null) ImagePickerContractResult.Cancelled()
    else ImagePickerContractResult.Success(MediaType.IMAGE to requireNotNull(result.uri))
  }
}
