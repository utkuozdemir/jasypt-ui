package org.utkuozdemir.jasyptui

import java.io.{File, PrintWriter, StringWriter}
import java.lang.Thread.UncaughtExceptionHandler
import java.nio.file.StandardOpenOption._
import java.nio.file._
import java.util.Properties

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.registry.AlgorithmRegistry
import resource._

import scala.collection.JavaConverters._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control._
import scalafx.scene.input.{Clipboard, ClipboardContent, DataFormat}
import scalafxml.core.macros.sfxml

/**
  * @author <a href="mailto:utku@mobilabsolutions.com">Utku Ozdemir</a>
  */
@sfxml
class JasyptUiPresenter(
                         private val input: TextArea,
                         private val pasteFromClipboard: Button,
                         private val password: PasswordField,
                         private val algorithm: ChoiceBox[String],
                         private val savePassword: CheckBox,
                         private val output: TextArea,
                         private val copyToClipboard: Button,
                         private val encrypt: RadioButton,
                         private val decrypt: RadioButton,
                         private val logs: TextArea,
                         private val help: Button,
                         private val doIt: Button
                       ) {

  Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler {
    override def uncaughtException(t: Thread, e: Throwable): Unit = {
      val sw = new StringWriter()
      e.printStackTrace(new PrintWriter(sw))
      logs.text = sw.toString
      logs.scrollTop = Double.MaxValue
    }
  })

  val DEFAULT_ALGORITHM = "PBEWITHMD5ANDTRIPLEDES"
  val CONFIG_FILE_NAME = ".jasypt-ui.properties"

  val PASSWORD = "password"
  val SAVE_PASSWORD = "save.password"
  val ALGORITHM = "algorithm"
  val ENCRYPT = "encrypt"

  private val systemClipboard: Clipboard = Clipboard.systemClipboard

  private val pbeAlgorithms = AlgorithmRegistry.getAllPBEAlgorithms.asScala.map(_.toString).toSeq

  algorithm.items_=(ObservableBuffer(pbeAlgorithms))

  algorithm.value = DEFAULT_ALGORITHM
  encrypt.selected = true

  loadState()

  def pasteFromClipboard(event: ActionEvent) {
    if (systemClipboard.hasString) {
      input.text = systemClipboard.content.getString
    }
  }

  def copyToClipboard(event: ActionEvent) {
    Option(output.getText).foreach(s => systemClipboard.content_=(ClipboardContent((DataFormat.PlainText, s))))
  }

  def doIt(event: ActionEvent) {
    if (Option(input.text.value).exists(_.trim.nonEmpty) && Option(password.text.value).exists(_.trim.nonEmpty)) {
      val encryptor = new StandardPBEStringEncryptor()
      encryptor.setPassword(password.getText)
      encryptor.setAlgorithm(algorithm.getValue)

      output.text = if (encrypt.selected.value) {
        encryptor.encrypt(input.getText)
      } else {
        encryptor.decrypt(input.getText)
      }
      saveState()
    }
  }

  def help(event: ActionEvent) {
    throw new UnsupportedOperationException("You are asking for too much...")
  }

  def saveState(): Unit = {
    val props = new Properties()
    props.put(ALGORITHM, algorithm.getValue)
    props.put(ENCRYPT, encrypt.selected.value.toString)
    props.put(SAVE_PASSWORD, savePassword.isSelected.toString)
    if (savePassword.isSelected) {
      props.put(PASSWORD, password.getText)
    }

    import resource._
    if (!Files.exists(configFilePath)) {
      Files.createFile(configFilePath)
    }

    for (os <- managed(Files.newOutputStream(configFilePath, WRITE))) {
      props.store(os, null)
    }
  }

  def configFilePath: Path = {
    val jarPath: Path = Paths.get(classOf[JasyptUiPresenter].getProtectionDomain.getCodeSource.getLocation.toURI)
    Paths.get(jarPath.getParent.toAbsolutePath.toString + File.separator + CONFIG_FILE_NAME)
  }

  def loadState(): Unit = {
    val props = new Properties()
    val path: Path = configFilePath
    if (Files.exists(path)) {
      for (is <- managed(Files.newInputStream(path, READ))) {
        props.load(is)
      }

      Option(props.getProperty(ALGORITHM)).foreach(algorithm.value = _)
      Option(props.getProperty(SAVE_PASSWORD)).foreach(sp => savePassword.selected = sp.toBoolean)
      Option(props.getProperty(ENCRYPT)).foreach(e => {
        encrypt.selected = e.toBoolean
        decrypt.selected = !e.toBoolean
      }
      )
      Option(props.getProperty(PASSWORD)).foreach(password.text = _)
    }
  }

}