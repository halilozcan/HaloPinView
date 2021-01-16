<h1 align="center">HaloPinView</h1></br>
<p align="center">  
A library for entering pins through wiggle animator based on circle shapes
</p>
</br>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=17)
<a href="https://github.com/halilozcan"><img alt="Profile" src="https://img.shields.io/badge/github-halilozcan-blue"/></a>

</br>

![Sample Gif](https://github.com/halilozcan/HaloPinView/blob/master/arts/sample.gif)

XML
```xml
<com.halilozcan.halopinlib.HaloPinView
        android:id="@+id/pin_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:activePinColor="@color/design_default_color_secondary_variant"
        app:activePinRadius="8dp"
        app:animationDuration="1000"
        app:correctPin="1967"
        app:inActivePinColor="@color/design_default_color_primary_variant"
        app:inActivePinRadius="4dp"
        app:isAnimationActive="true"
        app:pinCount="4" />
```
</br>

Listen pin entering
```kotlin
pin_view.setOnPinFilledListener {
  // Do whatever you want
}
```
</br>

Enter pin (You can listen input changes)
```kotlin
pin_view.enterPin("1")
```
</br>

Delete pin

```kotlin
pin_view.deletePin()
```
</br>

Programatically Adding
```kotlin
val pinView = HaloPinView(context)
val pinConfig = PinConfig.Builder()
                  .inActivePinRadius(10f)
                  .activePinRadius(20f)
                  .animationDuration(1000L)
                  .animationActivationState(true)
                  .pinCount(4)
                  .inActivePinColor(Color.DKGRAY)
                  .activePinColor(Color.WHITE)
                  .correctPin("1967")
                  .build()
                  
pinView.setPinConfig(pinConfig)
```

## Find this repository useful? :heart:
__[follow](https://github.com/halilozcan)__ me for my next creations! 🤩

# License
```xml
Designed and developed by 2020 halilozcan (Halil ÖZCAN)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
