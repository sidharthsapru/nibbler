![Nibbler!](http://pkulak-nibbler-test.s3.amazonaws.com/nibbler.png)

Nibbler
=======

Nibbler is a simple image resizing proxy. The idea is to put it in front of a public store of source images and have
those resized/cropped on demand with simple GET requests. It's probably a good idea to put everything behind some kind
of caching CDN since it does take some time and resources to create a new image.

Every request path is passed through to the origin domain. The extension is stripped and used to determine the output
format (PNG and JPEG right now) and it's assumed that the origin is always PNG. Query string parameters are then used
to determine what operations to perform on the source image.

"width" and "height" parameters determine the bounding box for the image and will never be exceeded. However, images are
also never upscaled from the original, and may be _smaller_ than the bounding box. In that case, however, the ratio
of width to height will still be respected. An optional "bg" parameter determines the background color when it's needed.
The default is white for JPEGs and transparent for PNGs.

The "resize" parameter determines how the image is resized and cropped. There are four possible values.

(the examples below have been configured to use http://pkulak-nibbler-test.s3.amazonaws.com as the base URL, with
http://pkulak-nibbler-test.s3.amazonaws.com/bojack.png as the source image)

fit
---

Fit the image into the given box. Padding is added to center it.

    /bojack.png?width=500&height=500&resize=fit

<img src="http://pkulak-nibbler-test.s3.amazonaws.com/fit.png" width="250" height="250"/>

You can adjust the quality of JPEGs, on a 1 to 100 scale, with a default of 70.

    /bojack.jpg?width=500&height=500&resize=fit&quality=1

<img src="http://pkulak-nibbler-test.s3.amazonaws.com/fit_q1.jpg" width="250" height="250"/>

Or use BPG (this image and the one above are both 6K*).

    /bojack.bpg?width=500&height=500&resize=fit&quality=23

<img src="http://pkulak-nibbler-test.s3.amazonaws.com/bojack-bpg.png" width="250" height="250"/>

\* well, not really. There's no native BPG browser support right now, so it's a PNG _of_ the BPG output.

fit_width
---------

Fit the image into the given box with priority given to the width. Height will be cropped if necessary to keep it within
the requested height.

    /bojack.png?width=500&height=500&resize=fit_width

<img src="http://pkulak-nibbler-test.s3.amazonaws.com/fit_width.png" width="250" height="250"/>

fit_height
---------

Fit the image into the given box with priority given to the height. Width will be cropped if necessary to keep it within
the requested width.

    /bojack.png?width=500&height=500&resize=fit_height

<img src="http://pkulak-nibbler-test.s3.amazonaws.com/fit_height.png" width="196" height="250"/>

round
-----

Turn the image into a circle!

    /bojack.png?width=500&height=500&resize=round

<img src="http://pkulak-nibbler-test.s3.amazonaws.com/round.png" width="250" height="250"/>

Do you want a background that's not white or transparent? (Also notice that the return format has been changed to JPEG
by the new extension.

    /bojack.jpg?width=500&height=500&resize=round&bg=006341
    
<img src="http://pkulak-nibbler-test.s3.amazonaws.com/round_006341.jpg" width="250" height="250"/>