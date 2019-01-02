# Keepie Uppie
[![Keepie Uppie](readme/google-play.png)](https://play.google.com/store/apps/details?id=com.ernestoyaquello.keepieuppie)

Keepie Uppie is a casual indie Android game.

The goal of the game is to keep a ball up in the air for as long as possible by tapping on it. Each tap is a point, and the game ends whenever the ball touches the floor. The highest score is always saved and displayed, and some extra balls can be unlocked as certain scores are reached.

[![Keepie Uppie screenshots](https://github.com/ernestoyaquello/KeepieUppieGame/blob/master/readme/screenshots.png)](https://github.com/ernestoyaquello/KeepieUppieGame/blob/master/readme/screenshots.png)

Even though I only decided to publish at the end of 2018, I had actually finished its development in early 2017.

## Implementation details
### Game loop
The game loop is implemented with an infinite `while` loop that iterates a maximum of 60 times per second within an always running thread.

This loop takes care of updating all the values and triggering an asynchronous update of the game view by calling `postInvalidateOnAnimation()`.

### Game view
The game view is an extension of `View` in which the `onDraw()` method has been implemented to care of drawing the whole game onto the canvas over and over again.

### Appearance
To ensure the game looks the same in all screens and adapts to certain screen changes, the position, size and speed of all the assets are always relative to the size of the entire game view (most of these values are recalculated on each iteration of the game loop).

### State saving/restoration
There are four different states:

* Tutorial
* Start
* Play
* Game Over

and each one of them has several properties whose values get saved and restored whenever the user leaves the application and then goes back to it.

### Pause
The state saving/restoration mechanism is also used to allow the user to pause and resume the game.

## Disclaimer
The code is not polished and might look far from perfect, but please keep in mind that this was my first contact with Android game development and I was just doing a little experiment to try things out on my spare time.

## References
Even though many changes and improvements have been made to it, the basic game framework on top of which the game is implemented is based on the one that can be found [here](https://github.com/KamilSwojak/Ellio-android).

## License
```
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
