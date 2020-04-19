package jcornaz.demo.kotlin.flappybird

import com.badlogic.gdx.Screen
import com.badlogic.gdx.physics.box2d.Box2D
import jcornaz.demo.kotlin.flappybird.screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.freetype.async.registerFreeTypeFontLoaders

class KotlinFlappyBirdGame : KtxGame<Screen>(), CoroutineScope by LibGdxScope() {

  private lateinit var assetStorage: AssetStorage

  override fun create() {
    KtxAsync.initiate()
    Box2D.init()

    assetStorage = AssetStorage().apply {
      registerFreeTypeFontLoaders()
    }

    launch {
      val bundle = AssetBundle.load(assetStorage)

      do {
        val mainScreen = MainScreen(bundle)

        show(StartingScreen(mainScreen, bundle.font))
        show(mainScreen)
        show(GameOverScreen(mainScreen, bundle.font))

      } while (isActive)
    }
  }

  private suspend inline fun <reified T : EndableScreen> show(screen: T) {
    addScreen(screen)
    setScreen<T>()
    screen.awaitEnd()
    removeScreen<T>()
  }

  override fun dispose() {
    cancel()
    assetStorage.dispose()
  }
}
