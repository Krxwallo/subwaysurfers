package gg.norisk.subwaysurfers.client.renderer

import gg.norisk.subwaysurfers.SubwaySurfers.logger
import net.coderbot.iris.Iris
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object ShaderManager {
    fun init() {
    }

    fun loadCurvedShader() {
        Iris.getIrisConfig().setShaderPackName("subwaysurfers_shader.zip")
        Iris.getIrisConfig().setShadersEnabled(true)
        Iris.getIrisConfig().save()
        Iris.reload()
    }

    fun initShader() {
        if (Iris.getShaderpacksDirectory().none {
                it?.toFile()?.name.equals("subwaysurfers_shader.zip")
            }) {
            logger.info("Unzip Shader Pack, this can crash on mac or linux :P")
            extractZip(javaClass.getResourceAsStream("/shaderpacks.zip"), Iris.getShaderpacksDirectory().toFile())
        }
    }


    private fun extractZip(zipStream: InputStream, folder: File) {
        if (!folder.exists()) {
            folder.mkdir()
        }

        ZipInputStream(zipStream).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry

            while (zipEntry != null) {
                if (zipEntry.isDirectory) {
                    zipEntry = zipInputStream.nextEntry
                    continue
                }

                val newFile = File(folder, zipEntry.name)
                File(newFile.parent).mkdirs()

                FileOutputStream(newFile).use {
                    zipInputStream.copyTo(it)
                }
                zipEntry = zipInputStream.nextEntry
            }

            zipInputStream.closeEntry()
        }
    }
}