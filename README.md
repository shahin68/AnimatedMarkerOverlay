# MarkerOverlay
**Animate map markers with overlay projection compatible with clustering**

### What can you do with this package?

You can animate lot's of markers however you want.



## Usage

Add the layouts on top of your map view

```xml
<fragment
        android:id="@+id/map_view"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
        
<com.shahin.overlay.CanvasOverlay
        android:id="@+id/canvas_overlay"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:markerColor="@color/black"
        app:markerMaximumRadius="48dp"
        app:markerMinimumRadius="0dp"/>

<com.shahin.overlay.BitmapOverlay
        android:id="@+id/bitmap_overlay"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:maximumSize="48dp"
        app:minimumSize="0dp"
        app:minimumSizeInheritFromSrc="true"
        app:src="@drawable/ic_marker"/>
```

*CanvasOverlay* attributes:

* `markerColor` to set marker color

* `markerMaximumRadius` to set maximum radius

* `markerMinimumRadius` to set minumum radius


*BitmapOverlay* attributes:

* `maximumSize` to set maximum size

* `minimumSize` to set minimum size

* `minimumSizeInheritFromSrc` set `true` if your want to inherit minimum size from the bitmap itself

* `src` to set our image (Note: .png or . jpg - not .xml drawable)


**In your View For adding markers:**

```kotlin
val projection = Projection(mMap.projection.toScreenLocation(latLng),latLng)

binding.canvasOverlay.set(projection) // to add one marker drawn
                            
binding.bitmapOverlay.set(projection) // to add one bitmap drawn
```

**In your View For moving markers:**

```kotlin
mMap.setOnCameraMoveListener {
   binding.canvasOverlay.move(projection) // to move one marker drawn
   binding.bitmapOverlay.move(projection) // to move one bitmap drawn                 
}

mMap.setOnCameraMoveListener {
   projections.forEach { // to move list of markers
      binding.canvasOverlay.move(it)
      binding.bitmapOverlay.move(it)
   }                
}
```

Canvas drawings are cheap but use carefully and inspect the performace thoroughly.

And,

**Do't forget to pause the animators when view isn't visible:**

```kotlin
override fun onResume() {
   super.onResume()
   binding.canvasOverlay.start()
   binding.bitmapOverlay.start()
}

override fun onStop() {
   super.onStop()
   binding.canvasOverlay.pause()
   binding.bitmapOverlay.pause()
}
```
