package com.worthlesscog.utils

import java.io.File
import java.nio.file.Files
import java.security.{MessageDigest, NoSuchAlgorithmException}

import org.apache.commons.codec.binary.{Base64, Hex}

import scala.util.{Failure, Success, Try}

object HashRename {

    object Format extends Enumeration {
        val base36, base64, raw = Value
        def valueOf(v: String) = values find { _.toString == v }
    }

    var dedupe = false
    var digest: MessageDigest = _
    var extensions = true
    var format = Format.base36
    var hash = "MD5"
    var paths = List.empty[String]
    var recursive = false

    def extension(f: File) =
        f.getName.lastIndexOf('.') match {
            case -1 => ""
            case n  => f.getName.substring(n)
        }

    def rename(f: File) = {
        val bs = Files.readAllBytes(f.toPath)
        val h = digest.digest(bs)
        val stub = format match {
            case Format.base36 => BigInt(h).toString(36).filter('-' !=).toUpperCase
            case Format.base64 => Base64.encodeBase64URLSafeString(h)
            case Format.raw    => Hex.encodeHexString(h)
        }
        val ext = if (extensions) extension(f) else ""
        val newName = stub + ext
        if (f.getName != newName) {
            val file = new File(f.getParent, newName)
            if (file exists) {
                if (!dedupe)
                    println(s"Can't rename $f - file exists $file")
                else if (f delete)
                    println(s"Deleted $f")
                else
                    println(s"Delete failed for $f")
            } else if (!(f renameTo file))
                println(s"Rename $f to $file failed")
        }
    }

    def walk(path: File): Unit = {
        println(path)
        if (path isDirectory)
            path.listFiles.sorted foreach { f =>
                if (f isFile)
                    rename(f)
                else if (recursive)
                    walk(f)
            }
        else
            rename(path)
    }

    def walk(path: String): Unit =
        walk(new File(path))

    def main(args: Array[String]): Unit = {
        val status = for {
            _ <- parseArgs(args toList)
            _ <- prep()
            s <- run()
        } yield s
        println(status merge)
    }

    private def parseArgs(args: List[String]): Either[String, Unit] =
        args match {
            case "-dedupe" :: tail =>
                dedupe = true
                parseArgs(tail)

            case "-extensions" :: tail =>
                extensions = false
                parseArgs(tail)

            case "-format" :: f :: tail =>
                Format.valueOf(f) match {
                    case Some(fmt) =>
                        format = fmt
                        parseArgs(tail)

                    case _ =>
                        Left(s"Unknown format - $f")
                }

            case "-hash" :: h :: tail =>
                hash = h
                parseArgs(tail)

            case "-recursive" :: tail =>
                recursive = true
                parseArgs(tail)

            case path :: tail =>
                paths = path :: paths
                parseArgs(tail)

            case Nil =>
                Right(())
        }

    def prep() =
        Try(digest = MessageDigest.getInstance(hash)) match {
            case Failure(_: NoSuchAlgorithmException) =>
                Left(s"No such algorithm - $hash")

            case Success(_) =>
                Right(())
        }

    def run() = {
        paths foreach walk
        Right("Ok")
    }

}
