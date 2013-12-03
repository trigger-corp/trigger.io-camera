``camera``: Foreground camera
=============================

The ``forge.camera`` namespace can be used to display a camera as part of your app. It is designed for use on older, low-memory Android devices.

This module is only available on Android, on iOS the [File and Camera Access](/modules/file/current/docs/index.html) will perform a similar function.

> ::Note:: This module can be used as a drop in replacement for [forge.file.getImage](/modules/file/current/docs/index.html#forgefilegetimageparams-success-error) on Android. Unlike that API, this module will display the camera as part of your app. This means the user will not have the option to select an image from the gallery, or perform advanced options such as zooming or changing other camera settings. However, on low memory devices this can be a more reliable method of capturing a photo.

This module uses file objects: it is recommended you process them using the [file module](/modules/file/current/docs/index.html).

##API

!method: forge.camera.getImage([params, ]success, error)
!param: params `object` an optional object of parameters
!param: success `function(file)` callback to be invoked when no errors occur (argument is the returned file)
!description: Returns a file object for a image taken using their camera. Images are always saved in your app's "pictures" directory. The file object can be handled using the [File and Camera access](/modules/file/current/docs/index.html) module.
!platforms: Android
!param: error `function(content)` called with details of any error which may occur

The optional parameters can contain any combination of the following:

- ``width``: The maximum height of the image when used, if the returned image is larger than this it will be automatically resized before display. The stored image will not be resized.
- ``height``: As ``width`` but sets a maximum height, both ``height`` and ``width`` can be set.

Returned files will be accessible to the app as long as they exist on the device.

##Permissions

On Android this module will add the ``WRITE_EXTERNAL_STORAGE`` and ``CAMERA`` permissions to your app, users will be prompted to accept this when they install your app.