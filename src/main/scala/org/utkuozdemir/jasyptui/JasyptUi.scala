package org.utkuozdemir.jasyptui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafxml.core.{FXMLView, NoDependencyResolver}

/**
  * @author <a href="mailto:utku@mobilabsolutions.com">Utku Ozdemir</a>
  */
object JasyptUi extends JFXApp {
  val resource = getClass.getResource("JasyptUi.fxml")

  val root = FXMLView(resource, NoDependencyResolver)

  stage = new PrimaryStage() {
    title = "Jasypt Encrypt/Decrypt Tool"
    scene = new Scene(root)
  }

  stage.setMinHeight(500)
  stage.setMinWidth(720)

  stage.icons.add(new Image("saru.png"))
}